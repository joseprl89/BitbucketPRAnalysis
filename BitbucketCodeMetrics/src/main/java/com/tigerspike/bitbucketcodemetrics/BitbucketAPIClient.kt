package com.tigerspike.bitbucketcodemetrics

import com.google.gson.*
import com.tigerspike.bitbucketcodemetrics.retrofit.APIDefinition
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class BitbucketAPIClient(
    val baseURL: String = "https://api.bitbucket.org/2.0/",
    val debug: Boolean
) {

    val apiDefinition: APIDefinition

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(createOkHTTPClient(debug))
            .addConverterFactory(GsonConverterFactory.create(gson()))
            .build()

        apiDefinition = retrofit.create(APIDefinition::class.java)
    }

    private fun createOkHTTPClient(debug: Boolean): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level =
            if (debug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC

        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private fun gson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(ZonedDateTime::class.java, object : JsonDeserializer<ZonedDateTime> {
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
