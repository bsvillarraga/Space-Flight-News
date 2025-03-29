package com.bsvillarraga.spaceflightnews.data.model

import com.google.gson.annotations.SerializedName

data class EventDto(
    @SerializedName("event_id")
    val eventId: String,
    val provider: String,
)