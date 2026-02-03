package com.miguelrivera.vindexweather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    @SerialName("list") val list: List<WeatherDto>,
    @SerialName("city") val city: CityDto,
    @SerialName("cnt") val count: Int
)

/**
 * Represents the city information from the API response.
 *
 * @property sunrise Time of sunrise, unix, UTC. Useful for calculating Day/Night UI themes.
 * @property sunset Time of sunset, unix, UTC.
 */
@Serializable
data class CityDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("coord") val coord: CoordDto,
    @SerialName("country") val country: String,
    @SerialName("population") val population: Long = 0,
    @SerialName("timezone") val timezone: Long,
    @SerialName("sunrise") val sunrise: Long,
    @SerialName("sunset") val sunset: Long
)

@Serializable
data class CoordDto(
    @SerialName("lat") val lat: Double,
    @SerialName("lon") val lon: Double
)

/**
 * Represents a specific forecast time entry.
 *
 * @property pop Probability of precipitation. Values vary between 0 and 1 (0% to 100%).
 */
@Serializable
data class WeatherDto(
    @SerialName("dt") val dt: Long,
    @SerialName("main") val main: MainDto,
    @SerialName("weather") val weather: List<WeatherDescriptionDto>,
    @SerialName("clouds") val clouds: CloudsDto,
    @SerialName("wind") val wind: WindDto,
    @SerialName("visibility") val visibility: Int,
    @SerialName("pop") val pop: Double,
    @SerialName("sys") val sys: SysDto,
    @SerialName("dt_txt") val dtTxt: String
)

/**
 * Main weather parameters.
 *
 * @property feelsLike This temperature parameter accounts for the human perception of weather. Unit Default: Kelvin, Metric: Celsius.
 * @property pressure Atmospheric pressure on the sea level, hPa.
 */
@Serializable
data class MainDto(
    @SerialName("temp") val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    @SerialName("temp_min") val tempMin: Double,
    @SerialName("temp_max") val tempMax: Double,
    @SerialName("pressure") val pressure: Int,
    @SerialName("sea_level") val seaLevel: Int = 0,
    @SerialName("grnd_level") val grndLevel: Int = 0,
    @SerialName("humidity") val humidity: Int,
    @SerialName("temp_kf") val tempKf: Double = 0.0
)

@Serializable
data class WeatherDescriptionDto(
    @SerialName("id") val id: Int,
    @SerialName("main") val main: String,
    @SerialName("description") val description: String,
    @SerialName("icon") val icon: String
)

@Serializable
data class CloudsDto(
    @SerialName("all") val all: Int
)

/**
 * Wind conditions.
 *
 * @property deg Wind direction, degrees (meteorological).
 * @property gust Wind gust. Nullable because the API omits this field if there are no significant gusts.
 */
@Serializable
data class WindDto(
    @SerialName("speed") val speed: Double,
    @SerialName("deg") val deg: Int,
    @SerialName("gust") val gust: Double? = null
)

@Serializable
data class SysDto(
    @SerialName("pod") val pod: String
)