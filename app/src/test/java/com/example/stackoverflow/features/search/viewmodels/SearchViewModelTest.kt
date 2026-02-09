package com.example.stackoverflow.features.search.viewmodels

import app.cash.turbine.test
import com.example.stackoverflow.repository.models.Owner
import com.example.stackoverflow.repository.models.Question
import com.example.stackoverflow.repository.models.SearchResponse
import com.example.stackoverflow.repository.models.StackoverflowResult
import com.example.stackoverflow.repository.stackoverflowRepository.interfaces.StackOverflowRepository
import com.example.stackoverflow.utils.interfaces.DateUtils
import com.example.stackoverflow.utils.interfaces.NetworkConnectivityChecker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var repository: StackOverflowRepository
    private lateinit var networkChecker: NetworkConnectivityChecker
    private lateinit var dateUtils: DateUtils
    private lateinit var viewModel: SearchViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        networkChecker = mockk()
        dateUtils = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `formatDate returns formatted date string`() {
        // Given
        val timestamp = 1704067200L // Jan 1, 2024 00:00:00 UTC
        val expectedDate = Date(timestamp * 1000)
        every { dateUtils.createDate(timestamp * 1000) } returns expectedDate
        viewModel = SearchViewModel(repository, networkChecker, dateUtils)

        // When
        val result = viewModel.formatDate(timestamp)

        // Then
        assert(result.contains("2024"))
        verify { dateUtils.createDate(timestamp * 1000) }
    }

    @Test
    fun `onQueryChange updates query state and resets pagination`() = runTest {
        // Given
        val mockResponse = SearchResponse(
            items = emptyList(),
            has_more = false,
            quota_remaining = 299
        )
        every { networkChecker.isNetworkAvailable() } returns true
        coEvery { repository.searchQuestions(any(), any()) } returns StackoverflowResult.Success(mockResponse)
        viewModel = SearchViewModel(repository, networkChecker, dateUtils)
        val newQuery = "kotlin"

        // When
        viewModel.query.test {
            assertEquals("", awaitItem()) // Initial value
            viewModel.onQueryChange(newQuery)
            assertEquals(newQuery, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        // Then
        viewModel.questions.test {
            assertEquals(emptyList<Question>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchQuestions loads questions successfully`() = runTest {
        // Given
        val searchQuery = "kotlin"
        val mockOwner = Owner(
            
            reputation = 1000,
            userId = 456,
            userType = "registered",
            profileImage = "https://example.com/image.jpg",
            displayName = "Test User",
            link = "https://stackoverflow.com/users/456"
        )
        val mockQuestions = listOf(
            Question(
                tags = listOf("kotlin", "android"),
                owner = mockOwner,
                isAnswered = true,
                viewCount = 100,
                answerCount = 5,
                score = 10,
                lastActivityDate = 1704067200L,
                creationDate = 1704067200L,
                lastEditDate = null,
                questionId = 1L,
                contentLicense = "CC BY-SA 4.0",
                link = "https://stackoverflow.com/questions/1",
                title = "Test Question",
                body = "Test body"
            )
        )
        val mockResponse = SearchResponse(
            items = mockQuestions,
            has_more = false,
            
            quota_remaining = 299
        )

        every { networkChecker.isNetworkAvailable() } returns true
        coEvery { repository.searchQuestions(searchQuery, 1) } returns StackoverflowResult.Success(mockResponse)
        viewModel = SearchViewModel(repository, networkChecker, dateUtils)

        // When
        viewModel.onQueryChange(searchQuery)
        advanceTimeBy(350) // Wait for debounce delay

        // Then
        viewModel.questions.test {
            assertEquals(mockQuestions, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.isLoading.test {
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchQuestions shows no internet dialog when network unavailable`() = runTest {
        // Given
        val searchQuery = "kotlin"
        every { networkChecker.isNetworkAvailable() } returns false
        viewModel = SearchViewModel(repository, networkChecker, dateUtils)

        // When
        viewModel.onQueryChange(searchQuery)
        advanceTimeBy(350) // Wait for debounce delay

        // Then
        viewModel.showNoInternetDialog.test {
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.isLoading.test {
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchQuestions handles error response`() = runTest {
        // Given
        val searchQuery = "kotlin"
        val errorMessage = "Network error"
        every { networkChecker.isNetworkAvailable() } returns true
        coEvery { repository.searchQuestions(searchQuery, 1) } returns 
            StackoverflowResult.Error(Exception(errorMessage))
        viewModel = SearchViewModel(repository, networkChecker, dateUtils)

        // When
        viewModel.onQueryChange(searchQuery)
        advanceTimeBy(350) // Wait for debounce delay

        // Then
        viewModel.error.test {
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.isLoading.test {
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadMore appends questions when has more is true`() = runTest {
        // Given
        val searchQuery = "kotlin"
        val mockOwner = Owner(
            
            reputation = 1000,
            userId = 456,
            userType = "registered",
            profileImage = "https://example.com/image.jpg",
            displayName = "Test User",
            link = "https://stackoverflow.com/users/456"
        )
        val firstPageQuestions = listOf(
            Question(
                tags = listOf("kotlin"),
                owner = mockOwner,
                isAnswered = true,
                viewCount = 100,
                answerCount = 5,
                score = 10,
                lastActivityDate = 1704067200L,
                creationDate = 1704067200L,
                lastEditDate = null,
                questionId = 1L,
                contentLicense = "CC BY-SA 4.0",
                link = "https://stackoverflow.com/questions/1",
                title = "Question 1",
                body = "Body 1"
            )
        )
        val secondPageQuestions = listOf(
            Question(
                tags = listOf("kotlin"),
                owner = mockOwner,
                isAnswered = true,
                viewCount = 100,
                answerCount = 5,
                score = 10,
                lastActivityDate = 1704067200L,
                creationDate = 1704067200L,
                lastEditDate = null,
                questionId = 2L,
                contentLicense = "CC BY-SA 4.0",
                link = "https://stackoverflow.com/questions/2",
                title = "Question 2",
                body = "Body 2"
            )
        )
        val firstResponse = SearchResponse(
            items = firstPageQuestions,
            has_more = true,
            
            quota_remaining = 299
        )
        val secondResponse = SearchResponse(
            items = secondPageQuestions,
            has_more = false,
            
            quota_remaining = 298
        )

        every { networkChecker.isNetworkAvailable() } returns true
        coEvery { repository.searchQuestions(searchQuery, 1) } returns StackoverflowResult.Success(firstResponse)
        coEvery { repository.searchQuestions(searchQuery, 2) } returns StackoverflowResult.Success(secondResponse)
        viewModel = SearchViewModel(repository, networkChecker, dateUtils)

        // When
        viewModel.onQueryChange(searchQuery)
        advanceTimeBy(350) // Wait for debounce
        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.questions.test {
            val questions = awaitItem()
            assertEquals(2, questions.size)
            assertEquals("Question 1", questions[0].title)
            assertEquals("Question 2", questions[1].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadMore does not load when query is too short`() = runTest {
        // Given
        val shortQuery = "ab"
        every { networkChecker.isNetworkAvailable() } returns true
        viewModel = SearchViewModel(repository, networkChecker, dateUtils)

        // When
        viewModel.onQueryChange(shortQuery)
        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.questions.test {
            assertEquals(emptyList<Question>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(exactly = 0) { networkChecker.isNetworkAvailable() }
    }

    @Test
    fun `loadMore shows no internet dialog when network unavailable`() = runTest {
        // Given
        val searchQuery = "kotlin"
        val mockResponse = SearchResponse(
            items = emptyList(),
            has_more = true,
            
            quota_remaining = 299
        )
        every { networkChecker.isNetworkAvailable() } returnsMany listOf(true, false)
        coEvery { repository.searchQuestions(searchQuery, 1) } returns StackoverflowResult.Success(mockResponse)
        viewModel = SearchViewModel(repository, networkChecker, dateUtils)

        // When
        viewModel.onQueryChange(searchQuery)
        advanceTimeBy(350)
        viewModel.loadMore()

        // Then
        viewModel.showNoInternetDialog.test {
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dismissNoInternetDialog hides dialog`() = runTest {
        // Given
        every { networkChecker.isNetworkAvailable() } returns false
        viewModel = SearchViewModel(repository, networkChecker, dateUtils)
        viewModel.onQueryChange("kotlin")
        advanceTimeBy(350)

        // When
        viewModel.dismissNoInternetDialog()

        // Then
        viewModel.showNoInternetDialog.test {
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
