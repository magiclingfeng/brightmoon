package com.lingyun.library.brightmoon.retrofit

import android.app.Application
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/*
* Created by mc_luo on 2019-06-19.
* Copyright (c) 2019 The LingYun Authors. All rights reserved.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

object RetrofitManager {

    private lateinit var baseRetrofit: Retrofit

    fun initBaseRetrofit() {
        val protocols = ArrayList<Protocol>()
        protocols.add(Protocol.HTTP_1_1)
        protocols.add(Protocol.HTTP_2)
        val builder = OkHttpClient.Builder()
            .protocols(protocols)
            //                .retryOnConnectionFailure(true)
            .connectTimeout(RetrofigConfig.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(RetrofigConfig.READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(RetrofigConfig.WRITE_TIMEOUT, TimeUnit.MILLISECONDS)

        baseRetrofit = Retrofit.Builder()
            .client(builder.build())
            .baseUrl(RetrofigConfig.BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    fun <T> getService(service: Class<T>): T {
        return baseRetrofit.create(service)
    }
}