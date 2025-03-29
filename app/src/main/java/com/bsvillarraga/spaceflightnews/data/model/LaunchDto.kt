package com.bsvillarraga.spaceflightnews.data.model

import com.google.gson.annotations.SerializedName

data class LaunchDto(
    @SerializedName("launch_id")
    val launchId: String,
    val provider: String,
)