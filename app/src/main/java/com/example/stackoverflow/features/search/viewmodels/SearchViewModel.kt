package com.example.stackoverflow.features.search.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stackoverflow.repository.models.Question
import com.example.stackoverflow.repository.models.StackoverflowResult
import com.example.stackoverflow.repository.stackoverflowRepository.interfaces.StackOverflowRepository
import com.example.stackoverflow.utils.interfaces.DateUtils
import com.example.stackoverflow.utils.interfaces.NetworkConnectivityChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: StackOverflowRepository,
    private val networkChecker: NetworkConnectivityChecker,
    private val dateUtils: DateUtils
): ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _showNoInternetDialog = MutableStateFlow(false)
    val showNoInternetDialog: StateFlow<Boolean> = _showNoInternetDialog

    private var currentPage = 1
    private var hasMore = true
    private var isLoadingMore = false

    init {
        viewModelScope.launch {
            query
                .debounce(DEBOUNCE_DELAY)
                .distinctUntilChanged()
                .filter { it.length >= MIN_SEARCH_LENGTH }
                .collect { q ->
                    searchQuestions(q)
                }
        }
    }

    fun onQueryChange(text: String) {
        _query.value = text
        currentPage = 1
        hasMore = true
        _questions.value = emptyList()
    }


    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(dateUtils.createDate(timestamp * 1000))
    }

    private suspend fun searchQuestions(searchQuery: String) {
        if (!networkChecker.isNetworkAvailable()) {
            _showNoInternetDialog.value = true
            _isLoading.value = false
            return
        }

        _isLoading.value = true
        _error.value = null

        when (val result = repository.searchQuestions(searchQuery, 1)) {
            is StackoverflowResult.Success -> {
                _questions.value = result.data.items
                hasMore = result.data.has_more
                currentPage = 1
            }
            is StackoverflowResult.Error -> {
                _error.value = result.throwable.message
            }
            else -> {}
        }
        _isLoading.value = false
    }

    fun loadMore() {
        if (isLoadingMore || !hasMore || _query.value.length < MIN_SEARCH_LENGTH) return

        if (!networkChecker.isNetworkAvailable()) {
            _showNoInternetDialog.value = true
            return
        }

        isLoadingMore = true
        _isLoading.value = true
        viewModelScope.launch {
            val nextPage = currentPage + 1
            when (val result = repository.searchQuestions(_query.value, nextPage)) {
                is StackoverflowResult.Success -> {
                    _questions.value = _questions.value + result.data.items
                    hasMore = result.data.has_more
                    currentPage = nextPage
                }
                is StackoverflowResult.Error -> {
                    _error.value = result.throwable.message
                }
                else -> {}
            }
            isLoadingMore = false
            _isLoading.value = false
        }
    }

    fun dismissNoInternetDialog() {
        _showNoInternetDialog.value = false
    }

    private companion object {
        const val DEBOUNCE_DELAY = 300L
        const val MIN_SEARCH_LENGTH = 3
    }
}