package com.lingyun.library.brightmoon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lingyun.library.brightmoon.database.entity.Position
import com.lingyun.library.brightmoon.retrofit.RetrofitManager
import com.lingyun.library.brightmoon.service.PositionService
import com.lingyun.library.brightmoon.viewmodule.BaseScopViewModule
import kotlinx.coroutines.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    lateinit var viewModule: BaseScopViewModule
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate")
        setContentView(R.layout.activity_main)

        viewModule = BaseScopViewModule(this)
    }


    fun loadPositions() {
        //show loading
        Timber.d("load positions")
        launch {
            val positions = AppConfig.appDatabase.positionDao()
                .listPositions(0, 100)
            //finish loading
            Timber.d("load positions finish")
        }
    }

    fun loadPositionWithTimeout() {
        Timber.d("loadPositionWithTimeout")

        launch {
            val result = withTimeoutOrNull(10 * 1000L) {
                AppConfig.appDatabase.positionDao()
                    .listPositions(0, 100)
            }

            when {
                result == null -> {
                    Timber.d("load positions timeout")
                }
                else -> {
                    Timber.d("load positions success")
                }
            }

            Timber.d("loadPositionWithTimeout finish")
        }
    }


    fun uploadPosition() {
        Timber.d("uploadPositions")

        launch {

            try {
                val result = RetrofitManager.getService(PositionService::class.java)
                    .updatePosition(Position()).await()

                if (result.result == true) {
                    Timber.d("upload success")
                } else {
                    Timber.d("upload failed:${result.message}")
                }
            } catch (e: Exception) {
                Timber.d("upload failed")
                Timber.e(e)
            }

        }

    }

    fun uploadPositionTimeout() {
        Timber.d("uploadPositionTimeout")

        launch {

            try {
                val result = withTimeoutOrNull(20 * 1000L) {
                    RetrofitManager.getService(PositionService::class.java)
                        .updatePosition(Position()).await()
                }
                when {
                    result == null -> {
                        Timber.d("timeout")
                    }
                    result.result == true -> {
                        Timber.d("success")
                    }
                    else -> {
                        Timber.d("failed")
                    }
                }

            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> {
                        //scop is cancel
                        Timber.d("upload failed because by scop cancel")
                        throw  e
                    }
                    else -> {
                        Timber.d("upload failed because by http exection")
                    }
                }
                Timber.e(e)
            }

        }
    }


    fun loadByViewModule() {
        val channel = viewModule.loadPosition()

        launch {
            for (value in channel) {
                Timber.e("load progress:${value}")
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy")
        viewModule.onDestroy()
        try {
            cancel()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}
