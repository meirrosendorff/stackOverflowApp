package com.example.stackoverflow.repository.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Owner(
    val reputation: Int,
    @JsonProperty("user_id") val userId: Long,
    @JsonProperty("user_type") val userType: String,
    @JsonProperty("profile_image") val profileImage: String? = null,
    @JsonProperty("display_name") val displayName: String,
    val link: String,
    @JsonProperty("accept_rate") val acceptRate: Int? = null
)
