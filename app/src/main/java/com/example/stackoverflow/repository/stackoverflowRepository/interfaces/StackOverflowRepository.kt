package com.example.stackoverflow.repository.stackoverflowRepository.interfaces

import com.example.stackoverflow.repository.models.AnswerResponse
import com.example.stackoverflow.repository.models.SearchResponse
import com.example.stackoverflow.repository.models.StackoverflowResult

interface StackOverflowRepository {
    suspend fun searchQuestions(query: String, page: Int = 1): StackoverflowResult<SearchResponse>
    suspend fun fetchAnswers(questionId: String): StackoverflowResult<AnswerResponse>
}