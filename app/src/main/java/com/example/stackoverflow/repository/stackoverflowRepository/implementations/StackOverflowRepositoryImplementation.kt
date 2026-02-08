package com.example.stackoverflow.repository.stackoverflowRepository.implementations

import com.example.stackoverflow.repository.models.Question
import com.example.stackoverflow.repository.models.StackoverflowResult
import com.example.stackoverflow.repository.stackoverflowApi.interfaces.StackoverflowApi
import com.example.stackoverflow.repository.stackoverflowRepository.interfaces.StackOverflowRepository

class StackOverflowRepositoryImplementation(private val api: StackoverflowApi): StackOverflowRepository {
    override suspend fun searchQuestions(query: String): StackoverflowResult<List<Question>> {
        return try {
            val response = api.searchQuestions(query)
            StackoverflowResult.Success(response.items)
        } catch (e: Exception) {
            StackoverflowResult.Error(e)
        }
    }
}