package com.example.stackoverflow.repository.stackoverflowRepository.interfaces

import com.example.stackoverflow.repository.models.Question
import com.example.stackoverflow.repository.models.StackoverflowResult

interface StackOverflowRepository {
    suspend fun searchQuestions(query: String): StackoverflowResult<List<Question>>
}