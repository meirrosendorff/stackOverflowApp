package com.example.stackoverflow.features.details.viewmodels

import app.cash.turbine.test
import com.example.stackoverflow.repository.models.AnswerItem
import com.example.stackoverflow.repository.models.AnswerResponse
import com.example.stackoverflow.repository.models.Owner
import com.example.stackoverflow.repository.models.StackoverflowResult
import com.example.stackoverflow.repository.stackoverflowRepository.interfaces.StackOverflowRepository
import com.example.stackoverflow.utils.interfaces.DateUtils
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionDetailViewModelTest {

    private lateinit var repository: StackOverflowRepository
    private lateinit var dateUtils: DateUtils
    private lateinit var viewModel: QuestionDetailViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        dateUtils = mockk()
        viewModel = QuestionDetailViewModel(repository, dateUtils)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `formatDate returns formatted date with time`() {
        // Given
        val timestamp = 1704067200L // Jan 1, 2024 00:00:00 UTC
        val expectedDate = Date(timestamp * 1000)
        every { dateUtils.createDate(timestamp * 1000) } returns expectedDate

        // When
        val result = viewModel.formatDate(timestamp)

        // Then
        assertTrue(result.contains("2024"))
        assertTrue(result.contains("at"))
        verify { dateUtils.createDate(timestamp * 1000) }
    }

    @Test
    fun `toTimeAgo returns Just now for recent time`() {
        // Given
        val now = 1704067200000L
        val timestamp = now / 1000 // Same time in seconds
        every { dateUtils.getCurrentTimeMillis() } returns now

        // When
        val result = viewModel.toTimeAgo(timestamp)

        // Then
        assertEquals("Just now", result)
        verify { dateUtils.getCurrentTimeMillis() }
    }

    @Test
    fun `toTimeAgo returns minutes ago for time within hour`() {
        // Given
        val now = 1704067200000L // Current time in millis
        val thirtyMinutesAgo = (now / 1000) - (30 * 60) // 30 minutes ago in seconds
        every { dateUtils.getCurrentTimeMillis() } returns now

        // When
        val result = viewModel.toTimeAgo(thirtyMinutesAgo)

        // Then
        assertEquals("30 minutes ago", result)
        verify { dateUtils.getCurrentTimeMillis() }
    }

    @Test
    fun `toTimeAgo returns hours ago for time within day`() {
        // Given
        val now = 1704067200000L
        val threeHoursAgo = (now / 1000) - (3 * 60 * 60) // 3 hours ago in seconds
        every { dateUtils.getCurrentTimeMillis() } returns now

        // When
        val result = viewModel.toTimeAgo(threeHoursAgo)

        // Then
        assertEquals("3 hours ago", result)
        verify { dateUtils.getCurrentTimeMillis() }
    }

    @Test
    fun `toTimeAgo returns Yesterday for one day ago`() {
        // Given
        val now = 1704067200000L
        val oneDayAgo = (now / 1000) - (24 * 60 * 60) // 1 day ago in seconds
        every { dateUtils.getCurrentTimeMillis() } returns now

        // When
        val result = viewModel.toTimeAgo(oneDayAgo)

        // Then
        assertEquals("Yesterday", result)
        verify { dateUtils.getCurrentTimeMillis() }
    }

    @Test
    fun `toTimeAgo returns days ago for time within month`() {
        // Given
        val now = 1704067200000L
        val fiveDaysAgo = (now / 1000) - (5 * 24 * 60 * 60) // 5 days ago in seconds
        every { dateUtils.getCurrentTimeMillis() } returns now

        // When
        val result = viewModel.toTimeAgo(fiveDaysAgo)

        // Then
        assertEquals("5 days ago", result)
        verify { dateUtils.getCurrentTimeMillis() }
    }

    @Test
    fun `toTimeAgo returns months ago for time within year`() {
        // Given
        val now = 1704067200000L
        val threeMonthsAgo = (now / 1000) - (90 * 24 * 60 * 60) // ~3 months ago in seconds
        every { dateUtils.getCurrentTimeMillis() } returns now

        // When
        val result = viewModel.toTimeAgo(threeMonthsAgo)

        // Then
        assertEquals("3 months ago", result)
        verify { dateUtils.getCurrentTimeMillis() }
    }

    @Test
    fun `toTimeAgo returns years ago for old time`() {
        // Given
        val now = 1704067200000L
        val twoYearsAgo = (now / 1000) - (730 * 24 * 60 * 60) // 2 years ago in seconds
        every { dateUtils.getCurrentTimeMillis() } returns now

        // When
        val result = viewModel.toTimeAgo(twoYearsAgo)

        // Then
        assertEquals("2 years ago", result)
        verify { dateUtils.getCurrentTimeMillis() }
    }

    @Test
    fun `toTimeAgo returns Just now for future time`() {
        // Given
        val now = 1704067200000L
        val futureTime = (now / 1000) + 1000 // Future time in seconds
        every { dateUtils.getCurrentTimeMillis() } returns now

        // When
        val result = viewModel.toTimeAgo(futureTime)

        // Then
        assertEquals("Just now", result)
        verify { dateUtils.getCurrentTimeMillis() }
    }

    @Test
    fun `toTimeAgo returns singular form for one unit`() {
        // Given
        val now = 1704067200000L
        val oneMinuteAgo = (now / 1000) - 60 // 1 minute ago
        every { dateUtils.getCurrentTimeMillis() } returns now

        // When
        val result = viewModel.toTimeAgo(oneMinuteAgo)

        // Then
        assertEquals("1 minute ago", result)
        verify { dateUtils.getCurrentTimeMillis() }
    }

    @Test
    fun `loadAnswers fetches answers successfully`() = runTest {
        // Given
        val questionId = 12345L
        val mockOwner = Owner(
            
            reputation = 1000,
            userId = 456,
            userType = "registered",
            profileImage = "https://example.com/image.jpg",
            displayName = "Test User",
            link = "https://stackoverflow.com/users/456"
        )
        val mockAnswers = listOf(
            AnswerItem(
                owner = mockOwner,
                isAccepted = true,
                score = 10,
                lastActivityDate = 1704067200L,
                creationDate = 1704067200L,
                answerId = 1L,
                questionId = questionId,
                contentLicense = "CC BY-SA 4.0",
                body = "Test answer"
            )
        )
        val mockResponse = AnswerResponse(
            items = mockAnswers,
            hasMore = false
        )

        coEvery { repository.fetchAnswers(questionId.toString()) } returns 
            StackoverflowResult.Success(mockResponse)

        // When
        viewModel.loadAnswers(questionId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.answers.test {
            assertEquals(mockAnswers, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.isLoading.test {
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.error.test {
            assertEquals(null, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadAnswers handles error response`() = runTest {
        // Given
        val questionId = 12345L
        val errorMessage = "Network error"
        coEvery { repository.fetchAnswers(questionId.toString()) } returns 
            StackoverflowResult.Error(Exception(errorMessage))

        // When
        viewModel.loadAnswers(questionId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.error.test {
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.answers.test {
            assertEquals(emptyList<AnswerItem>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.isLoading.test {
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadAnswers handles error response with null message`() = runTest {
        // Given
        val questionId = 12345L
        coEvery { repository.fetchAnswers(questionId.toString()) } returns 
            StackoverflowResult.Error(Exception())

        // When
        viewModel.loadAnswers(questionId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.error.test {
            assertEquals("Failed to load answers", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadAnswers sets loading state during fetch`() = runTest {
        // Given
        val questionId = 12345L
        val mockResponse = AnswerResponse(
            items = emptyList(),
            hasMore = false
        )
        coEvery { repository.fetchAnswers(questionId.toString()) } returns 
            StackoverflowResult.Success(mockResponse)

        // When & Then
        viewModel.isLoading.test {
            assertEquals(false, awaitItem()) // Initial state
            viewModel.loadAnswers(questionId)
            assertEquals(true, awaitItem()) // Loading state
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(false, awaitItem()) // Final state
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadAnswers clears previous error before fetching`() = runTest {
        // Given
        val questionId = 12345L
        val mockResponse = AnswerResponse(
            items = emptyList(),
            hasMore = false
        )

        coEvery { repository.fetchAnswers("999") } returns 
            StackoverflowResult.Error(Exception("Previous error"))
        coEvery { repository.fetchAnswers(questionId.toString()) } returns 
            StackoverflowResult.Success(mockResponse)

        // When
        viewModel.loadAnswers(999L)
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.loadAnswers(questionId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.error.test {
            val currentError = awaitItem()
            assertEquals(null, currentError)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
