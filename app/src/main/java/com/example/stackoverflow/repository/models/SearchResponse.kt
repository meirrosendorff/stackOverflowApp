package com.example.stackoverflow.repository.models

data class SearchResponse(
    val items: List<Question>,
    val has_more: Boolean,
    val quota_remaining: Int? = null
)
