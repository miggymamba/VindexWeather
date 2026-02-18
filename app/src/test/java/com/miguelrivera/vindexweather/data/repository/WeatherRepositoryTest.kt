package com.miguelrivera.vindexweather.data.repository

import androidx.room.withTransaction
import com.miguelrivera.vindexweather.core.common.Result
import com.miguelrivera.vindexweather.data.local.database.WeatherDatabase
import com.miguelrivera.vindexweather.data.local.database.dao.WeatherDao
import com.miguelrivera.vindexweather.data.remote.WeatherApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class WeatherRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var weatherApi: WeatherApi
    private val database: WeatherDatabase = mockk()
    private val weatherDao: WeatherDao = mockk(relaxed = true)

    private lateinit var repository: WeatherRepositoryImpl

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // 1. Setup Real Retrofit with Fake Server
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        weatherApi = retrofit.create(WeatherApi::class.java)

        // 2. Mock Room Database Transaction behavior
        // Since 'withTransaction' is a static extension function,
        // it should be mocked to simply execute the lambda passed to it immediately.
        mockkStatic("androidx.room.RoomDatabaseKt")
        val transactionLambda = slot<suspend() -> Any>()
        coEvery {
            database.withTransaction (capture(transactionLambda))
        } coAnswers {
            transactionLambda.captured.invoke()
        }

        every { database.weatherDao } returns weatherDao

        // 3. Init Repository
        repository = WeatherRepositoryImpl(weatherApi, database, testDispatcher)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        unmockkStatic("androidx.room.RoomDatabaseKt")
    }

    @Test
    fun `syncWeather parses JSON correctly and inserts into database`() = runTest {
        // Arrange: A simplified valid JSON response (2.5 API format)
        val jsonResponse = """
            {
              "cod": "200",
              "message": 0,
              "cnt": 1,
              "list": [
                {
                  "dt": 1661871600,
                  "main": {
                    "temp": 296.76,
                    "feels_like": 296.98,
                    "temp_min": 296.76,
                    "temp_max": 297.87,
                    "pressure": 1015,
                    "humidity": 69
                  },
                  "weather": [{"id": 500, "main": "Rain", "description": "light rain", "icon": "10d"}],
                  "clouds": {"all": 100},
                  "wind": {"speed": 0.62, "deg": 349},
                  "visibility": 10000,
                  "pop": 0.32,
                  "sys": {"pod": "d"},
                  "dt_txt": "2022-08-30 15:00:00"
                }
              ],
              "city": {
                "id": 3163858,
                "name": "Zocca",
                "coord": {"lat": 44.34, "lon": 10.99},
                "country": "IT",
                "population": 4593,
                "timezone": 7200,
                "sunrise": 1661834187,
                "sunset": 1661882248
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        // Act
        val result = repository.syncWeather(44.34, 10.99)

        // Assert
        assertTrue("Expected Result.Success but got $result", result is Result.Success)

        // Verify Data Layer interactions
        coVerify(exactly = 1) { weatherDao.clearAll() }
        coVerify(exactly = 1) { weatherDao.upsertWeather(any()) }
    }

    @Test
    fun `syncWeather handles 404 Server Error gracefully`() = runTest {
        // Arrange
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("{}"))

        // Act
        val result = repository.syncWeather(0.0, 0.0)

        // Assert
        assertTrue("Expected Result.Error but got $result", result is Result.Error)

        // Critical: Ensure app did NOT wipe the cache on a failed sync
        coVerify(exactly = 0) { weatherDao.clearAll() }
    }
}