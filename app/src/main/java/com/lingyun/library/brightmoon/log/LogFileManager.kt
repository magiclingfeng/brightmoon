package com.lingyun.library.brightmoon.log

import android.os.Environment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

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
object LogFileManager {

    var saveFileCount:Int = 20
    var rollingTime = 1*60*60*1000L
    var fileNameFormat = "yyyyMMdd"
    val dirPath: String = (Environment.getExternalStorageDirectory().absolutePath + File.separator + "brightmoon"+File.separator+"logs")//保存路径

    private val logMsgQueue = ConcurrentLinkedQueue<String>()
    private var isStart = AtomicBoolean(false)
    private var logRunnable :LogRunnable? = null

    fun tryStart(){
        if (isStart.compareAndSet(false,true)){
            logRunnable?.cancel()
            logRunnable =LogRunnable(dirPath,fileNameFormat)
            Thread(logRunnable).start()
            GlobalScope.launch {
                delay(rollingTime)
                Timber.d("GlobalScope to rolling log")
                tryStart()
            }
        }
    }

    fun saveLog(message:String){
        logMsgQueue.add(message)
        tryStart()
    }


    private class LogRunnable(private val dirPath: String, private val logFileFormat: String) : Runnable {
        private var fos: FileOutputStream? = null
        @Volatile
        private var cancle:Boolean = false

        init {
            try {
                checkToCreateLogFile()
            } catch (e: Exception) {
                Timber.e(e)
            }

        }

        @Throws(Exception::class)
        private fun checkToCreateLogFile() {
            val dir = File(dirPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val currentTime = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat(logFileFormat, Locale.getDefault())
            val file = File(dirPath, dateFormat.format(currentTime) + ".log")

            if (!file.exists()) {
                fos?.use {
                    it.flush()
                }
                val success = file.createNewFile()
                if (success){
                    checkToDeleteLogFile()
                }
                Timber.e("create new file:${file.absolutePath} success:$success")
                fos = FileOutputStream(file, true)
            } else if (fos == null) {
                fos = FileOutputStream(file, true)
            }
        }

        private fun checkToDeleteLogFile(){
            val dir = File(dirPath)
            if (dir.exists()){
                val files = dir.listFiles()
                if (files.size> saveFileCount){
                    files.sortBy { it.name }
                    val toDeleteFile = files.slice(0..files.size-saveFileCount)
                    toDeleteFile.forEach {
                        Timber.e("delete file:${it.name}")
                        it.delete()
                    }
                }
            }
        }

        fun cancel(){
            cancle = true
            Timber.e("cancel log")
        }

        override fun run() {
            while (!cancle) {
                try {
                    val msg:String? = logMsgQueue.poll()
                    msg?.let {
                        fos!!.write(it.toByteArray(Charsets.UTF_8))
                    }?:{
                        Thread.sleep(200)
                    }()
                } catch (e: Exception) {
                    try {
                        Thread.sleep(2*1000)
                        checkToCreateLogFile()
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                    Timber.e(e)
                }
            }

            try {
                fos?.use {
                    it.flush()
                }
            }catch (e:java.lang.Exception){
                Timber.e(e)
            }
        }
    }
}