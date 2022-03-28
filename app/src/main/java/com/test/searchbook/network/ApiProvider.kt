package com.test.searchbook.network

import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@Singleton
class ApiProvider @Inject constructor() {

    companion object {
        const val BOOK_API_HOST = "https://api.itbook.store/1.0/"
    }

    private fun getRetrofitApiClient(url: String): Retrofit {
        val clientBuilder = createOkHttpClientBuilder()
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .client(clientBuilder.build())
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    private fun createOkHttpClientBuilder(): OkHttpClient.Builder {
        val trustAllCerts = arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {

            }

            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

        })

        val sslContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, java.security.SecureRandom())
        }

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0])
            .readTimeout(30 * 1000L, TimeUnit.MICROSECONDS)
            .writeTimeout(30 * 1000L, TimeUnit.MICROSECONDS)
            .connectTimeout(30 * 1000L, TimeUnit.MICROSECONDS)
    }

    fun <T> createApi(service: Class<T>, url: String): T {
        return getRetrofitApiClient(url).create(service)
    }
}