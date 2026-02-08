package com.example.stackoverflow.repository.stackoverflowApi.interfaces

import com.example.stackoverflow.repository.models.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StackoverflowApi {
    @GET("search/advanced")
    suspend fun searchQuestions(
        @Query("title") title: String,
        @Query("page") page: Int = 1,
        @Query("pagesize") pageSize: Int = 20,
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "activity",
        @Query("site") site: String = "stackoverflow",
        @Query("filter") filter: String = "withbody"
    ): SearchResponse

//    // Get answers for a specific question
//    @GET("2.2/questions/{questionId}/answers")
//    suspend fun getAnswers(
//        @Path("questionId") questionId: Long,
//        @Query("order") order: String = "desc",
//        @Query("sort") sort: String = "activity",
//        @Query("site") site: String = "stackoverflow",
//        @Query("filter") filter: String = "withbody"
//    ): AnswersResponse
}