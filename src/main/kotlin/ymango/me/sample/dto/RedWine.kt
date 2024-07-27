package ymango.me.sample.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RedWine(
    val winery: String,
    val wine: String,
    @SerialName("rating")
    val ratingTest: Rating,
    val location: String,
    val image: String,
    val id: Int
)

@Serializable
data class Rating(
    val average: String,
    val reviews: String
)
