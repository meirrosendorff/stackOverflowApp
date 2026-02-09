package com.example.stackoverflow.repository.models

import com.fasterxml.jackson.annotation.JsonProperty


data class AnswerResponse(
    val items: List<AnswerItem>,
    @JsonProperty("has_more") val hasMore: Boolean,
)
