package com.tigerspike.bitbucketcodemetrics.api

import com.google.gson.*
import com.tigerspike.bitbucketcodemetrics.api.retrofit.APIDefinition
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.ZonedDateTime

class BitbucketAPIClient(
    baseURL: String = "https://api.bitbucket.org/2.0/",
    val debug: Boolean,
    credentials: Credentials?
) {

    val apiDefinition: APIDefinition

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(createOkHTTPClient(debug, credentials))
            .addConverterFactory(GsonConverterFactory.create(gson()))
            .build()

        apiDefinition = retrofit.create(APIDefinition::class.java)
    }

    private fun createOkHTTPClient(debug: Boolean, credentials: Credentials?): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level =
            if (debug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC

        return OkHttpClient.Builder()
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    if (credentials == null) return chain.proceed(chain.request())

                    val newRequest = chain.request().newBuilder()
                        .header("Authorization", credentials.basic())
                        .build()

                    return chain.proceed(newRequest)
                }
            })
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private fun gson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                ZonedDateTime::class.java,
                object : JsonDeserializer<ZonedDateTime> {
                    @Throws(JsonParseException::class)
                    override fun deserialize(
                        json: JsonElement,
                        type: Type,
                        jsonDeserializationContext: JsonDeserializationContext
                    ): ZonedDateTime {
                        return ZonedDateTime.parse(json.asJsonPrimitive.asString)
                    }
                })
            .create()
    }
}
