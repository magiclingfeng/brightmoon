package com.lingyun.library.brightmoon.log

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

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
class CrashReportingTree: Timber.Tree() {

    @SuppressLint("LogNotTimber")
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val tagStr = tag ?:getTag()
        getTag()
        val threadName = Thread.currentThread().name
        var pri  = ""
        when(priority){
            Log.VERBOSE->{
                pri = "V"
                Log.v(tagStr,"[$threadName] $message")
            }
            Log.DEBUG->{
                pri = "D"
                Log.d(tagStr,"[$threadName] $message")
            }
            Log.INFO->{
                pri = "I"
                Log.i(tagStr,"[$threadName] $message")
            }
            Log.WARN->{
                pri = "W"
                Log.w(tagStr,"[$threadName] $message")
            }
            Log.ERROR->{
                pri = "E"
                Log.e(tagStr,"[$threadName] $message")
            }
            Log.ASSERT->{
                pri = "ASSET"
                Log.wtf(tagStr,"[$threadName] $message")
            }
        }
        val currentTime = System.currentTimeMillis()
        val dayFormat = SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS", Locale.getDefault())
        val msg = "${dayFormat.format(currentTime)} $pri  [$threadName] $message\r\n"
        LogFileManager.saveLog(msg)
    }

    private val MAX_TAG_LENGTH = 23
    private val CALL_STACK_INDEX = 6
    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

    /**
     * Extract the tag which should be used for the message from the `element`. By default
     * this will withScope the class name without any anonymous class suffixes (e.g., `Foo$1`
     * becomes `Foo`).
     *
     *
     * Note: This will not be called if a [manual tag][.tag] was specified.
     */
    protected fun createStackElementTag(element: StackTraceElement): String? {
        var tag = element.className
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        tag = tag.substring(tag.lastIndexOf('.') + 1)
        // Tag length limit was removed in API 24.
        return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tag
        } else tag.substring(0, MAX_TAG_LENGTH)
    }

    internal fun getTag(): String? {
        // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
        // because Robolectric runs them on the JVM but on Android the elements are different.
        val stackTrace = Throwable().stackTrace
        if (stackTrace.size <= CALL_STACK_INDEX) {
            throw IllegalStateException(
                "Synthetic stacktrace didn't have enough elements: are you using proguard?"
            )
        }

//        stackTrace.forEach {
//            Log.e("TAG",it.className)
//        }
        return createStackElementTag(stackTrace[CALL_STACK_INDEX])
    }

}