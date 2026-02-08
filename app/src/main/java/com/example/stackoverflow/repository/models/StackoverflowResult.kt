package com.example.stackoverflow.repository.models

sealed interface StackoverflowResult<out T> {
    data class Success<T>(val data: T) : StackoverflowResult<T>
    data class Error(val throwable: Throwable) : StackoverflowResult<Nothing>
}