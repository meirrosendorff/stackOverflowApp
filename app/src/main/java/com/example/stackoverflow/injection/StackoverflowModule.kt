package com.example.stackoverflow.injection

import com.example.stackoverflow.repository.stackoverflowApi.interfaces.StackoverflowApi
import com.example.stackoverflow.repository.stackoverflowRepository.implementations.StackOverflowRepositoryImplementation
import com.example.stackoverflow.repository.stackoverflowRepository.interfaces.StackOverflowRepository
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object StackoverflowModule {

    @Provides
    @Singleton
    fun provideObjectMapper(): ObjectMapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategies.LOWER_CAMEL_CASE)

    @Provides
    @Singleton
    fun provideRetrofit(mapper: ObjectMapper): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.stackexchange.com/2.2/")
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .build()

    @Provides
    @Singleton
    fun provideStackOverflowApi(retrofit: Retrofit): StackoverflowApi =
        retrofit.create(StackoverflowApi::class.java)

    @Provides
    @Singleton
    fun providesStackoverflowRepository(api: StackoverflowApi): StackOverflowRepository =
        StackOverflowRepositoryImplementation(api)
}