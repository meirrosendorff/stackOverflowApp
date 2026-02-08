package com.example.stackoverflow.repository.stackoverflowRepository.implementations

import com.example.stackoverflow.repository.models.SearchResponse
import com.example.stackoverflow.repository.models.StackoverflowResult
import com.example.stackoverflow.repository.stackoverflowApi.interfaces.StackoverflowApi
import com.example.stackoverflow.repository.stackoverflowRepository.interfaces.StackOverflowRepository

class StackOverflowRepositoryImplementation(private val api: StackoverflowApi): StackOverflowRepository {
    override suspend fun searchQuestions(query: String, page: Int): StackoverflowResult<SearchResponse> {
        return try {
            val response = api.searchQuestions(query, page)
            StackoverflowResult.Success(response)
        } catch (e: Exception) {
            StackoverflowResult.Error(e)
        }
    }
}