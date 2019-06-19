package com.lingyun.library.brightmoon.retrofit

import android.app.Application

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
object RetrofigConfig {
    lateinit var APP: Application
    lateinit var BASEURL: String

    val CONNECT_TIMEOUT = 20 * 1000L
    val WRITE_TIMEOUT = 10 * 1000L
    val READ_TIMEOUT = 30 * 1000L

    fun initRetrofit(application: Application, baseUrl: String) {
        APP = application
        BASEURL = baseUrl
        RetrofitManager.initBaseRetrofit()
    }
}