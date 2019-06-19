package com.lingyun.library.brightmoon.viewmodule

import com.lingyun.library.brightmoon.AppConfig
import com.lingyun.library.brightmoon.database.entity.Position
import com.lingyun.library.brightmoon.retrofit.RetrofitManager
import com.lingyun.library.brightmoon.service.PositionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import timber.log.Timber


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

class BaseScopViewModule(coroutineScope: CoroutineScope) :
    CoroutineScope by CoroutineScope(coroutineScope.coroutineContext + Dispatchers.IO) {


    fun onCreate(){
        Timber.e("onCreate")
    }

    fun loadPosition(): Channel<String> {
        val channel = Channel<String>()

        launch {
            channel.send("start")
            try {
                AppConfig.appDatabase.positionDao().listPositions(0, 100)
            } finally {
                channel.send("finish")
            }
        }

        return channel
    }

    fun uploadPosition(position: Position): Channel<String> {
        val channel = Channel<String>()
        launch {
            channel.send("start")

            try {
                RetrofitManager.getService(PositionService::class.java)
                    .updatePosition(position).await()
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                channel.offer("finish")
            }

        }

        return channel
    }

    fun onDestroy() {
        Timber.d("onDestroy")

        try {
            cancel()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}