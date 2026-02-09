package com.example.stackoverflow.repository.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    val tags: List<String>,
    val owner: Owner,
    @JsonProperty("is_answered") val isAnswered: Boolean? = null,
    @JsonProperty("view_count") val viewCount: Int,
    @JsonProperty("answer_count") val answerCount: Int,
    val score: Int,
    @JsonProperty("last_activity_date") val lastActivityDate: Long,
    @JsonProperty("creation_date") val creationDate: Long,
    @JsonProperty("last_edit_date") val lastEditDate: Long? = null,
    @JsonProperty("question_id") val questionId: Long,
    @JsonProperty("content_license") val contentLicense: String? = null,
    val link: String? = null,
    val title: String,
    val body: String
) : Parcelable
