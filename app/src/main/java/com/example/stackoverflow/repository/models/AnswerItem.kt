package com.example.stackoverflow.repository.models

import com.fasterxml.jackson.annotation.JsonProperty

data class AnswerItem(
    val owner: Owner,
    @JsonProperty("is_accepted") val isAccepted: Boolean,
    val score: Int,
    @JsonProperty("last_activity_date") val lastActivityDate: Long,
    @JsonProperty("creation_date") val creationDate: Long,
    @JsonProperty("answer_id") val answerId: Long,
    @JsonProperty("question_id") val questionId: Long,
    @JsonProperty("content_license") val contentLicense: String? = null,
    val body: String
)