package com.lingyun.library.brightmoon.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.lingyun.library.brightmoon.database.entity.Position

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
@Dao
interface PositionDao {

    @Query("select * from position where timestamp > :startTime limit :limit")
    suspend fun listPositions(startTime: Long, limit: Int): List<Position>

}