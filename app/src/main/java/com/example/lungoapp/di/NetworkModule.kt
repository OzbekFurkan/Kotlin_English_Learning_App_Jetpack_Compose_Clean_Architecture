package com.example.lungoapp.di

import android.util.Log
import com.example.lungoapp.data.manager.UserManager
import com.example.lungoapp.data.remote.*
import com.example.lungoapp.services.TranslationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(userManager: UserManager): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val token = userManager.getToken()
            Log.d("AuthInterceptor", "Token: $token")
            
            if (token != null) {
                val newRequest = request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                Log.d("AuthInterceptor", "Added Authorization header: Bearer $token")
                chain.proceed(newRequest)
            } else {
                Log.d("AuthInterceptor", "No token available")
                chain.proceed(request)
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // 10.0.2.2 is the special IP address to access the host machine from Android emulator
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWordApi(retrofit: Retrofit): WordApi {
        return retrofit.create(WordApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTranslationService(retrofit: Retrofit): TranslationService {
        return retrofit.create(TranslationService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTranslateService(retrofit: Retrofit): TranslateService {
        return retrofit.create(TranslateService::class.java)
    }
} 