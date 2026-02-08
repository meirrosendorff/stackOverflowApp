package com.example.stackoverflow.search.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stackoverflow.repository.models.Question
import com.example.stackoverflow.repository.models.StackoverflowResult
import com.example.stackoverflow.repository.stackoverflowRepository.interfaces.StackOverflowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(repository: StackOverflowRepository): ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    fun onQueryChange(text: String) {
        _query.value = text
    }

    val results: StateFlow<StackoverflowResult<List<Question>>> =
        query
            .debounce(DEBOUNCE_DELAY)
            .distinctUntilChanged()
            .filter { it.length >= MIN_SEARCH_LENGTH }
            .flatMapLatest { q ->
                flow {
                    emit(StackoverflowResult.Loading)
                    val result = repository.searchQuestions(q)
                    emit(result)
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(SHARING_DELAY),
                StackoverflowResult.Success(emptyList())
            )

    private companion object {
        const val DEBOUNCE_DELAY = 300L
        const val MIN_SEARCH_LENGTH = 3
        const val SHARING_DELAY = 5000L
    }
}