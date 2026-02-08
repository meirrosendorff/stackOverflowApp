package com.example.stackoverflow.repository.models

sealed interface StackoverflowResult<out T> {
    object Loading : StackoverflowResult<Nothing>
    data class Success<T>(val data: T) : StackoverflowResult<T>
    data class Error(val throwable: Throwable) : StackoverflowResult<Nothing>
}