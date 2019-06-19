package com.lingyun.library.brightmoon.service

import com.lingyun.library.brightmoon.database.entity.Position
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.POST

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

interface PositionService {

    @POST("position/upload")
    fun updatePosition(@Body body: Position):Deferred<BaseResponse<Boolean>>
}
