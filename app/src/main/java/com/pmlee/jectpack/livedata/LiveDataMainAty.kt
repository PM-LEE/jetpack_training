package com.pmlee.jectpack.livedata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.pmlee.jectpack.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class LiveDataMainAty : AppCompatActivity() {
    //注入ViewModel
    private val model:NameViewModel by viewModels<NameViewModel>()
    private var i = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model.currentName.observe(this, Observer {
            tv_title.text = it
        })
    }

    fun postValue(view: View) {
//        model.currentName.value = "${++i}"

        LiveDataEventBus.postValue(1,"zxcccccc${++i}")
        Thread(Runnable {

            while (true){
                Thread.sleep(5000)
                LiveDataEventBus.postValue(1,"zxcccccc${++i}")
                Log.e("LiveDataMainAty","post in thread")
            }

        }).start()
    }

    fun goToSecond(view: View) {
        startActivity(Intent(this,LiveBus1Aty::class.java))
    }
}
