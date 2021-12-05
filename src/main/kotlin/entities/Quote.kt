package entities

import com.google.gson.annotations.SerializedName

data class Quote(
    @SerializedName("Global Quote")
    val globalQuote: GlobalQuote
)
