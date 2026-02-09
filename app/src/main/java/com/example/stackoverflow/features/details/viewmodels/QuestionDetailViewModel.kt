package com.example.stackoverflow.features.details.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stackoverflow.repository.models.AnswerItem
import com.example.stackoverflow.repository.models.StackoverflowResult
import com.example.stackoverflow.repository.stackoverflowRepository.interfaces.StackOverflowRepository
import com.example.stackoverflow.utils.interfaces.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class QuestionDetailViewModel @Inject constructor(
    private val repository: StackOverflowRepository,
    private val dateUtils: DateUtils
) : ViewModel() {

    private val _answers = MutableStateFlow<List<AnswerItem>>(emptyList())
    val answers: StateFlow<List<AnswerItem>> = _answers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAnswers(questionId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.fetchAnswers(questionId.toString())) {
                is StackoverflowResult.Success -> {
                    _answers.value = result.data.items
                }
                is StackoverflowResult.Error -> {
                    _error.value = result.throwable.message ?: "Failed to load answers"
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd yyyy 'at' HH:mm", Locale.getDefault())
        return sdf.format(dateUtils.createDate(timestamp * 1000))
    }

    fun toTimeAgo(time: Long): String {
        val now = dateUtils.getCurrentTimeMillis()
        val time = time * 1000
        val diff = now - time

        if (diff < 0) return "Just now"

        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
            hours < 24 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            days == 1L -> "Yesterday"
            days < 30 -> "$days day${if (days > 1) "s" else ""} ago"
            days < 365 -> {
                val months = days / 30
                "$months month${if (months > 1) "s" else ""} ago"
            }
            else -> {
                val years = days / 365
                "$years year${if (years > 1) "s" else ""} ago"
            }
        }
    }
}