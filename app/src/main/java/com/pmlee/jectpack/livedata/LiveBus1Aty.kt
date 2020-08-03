package com.pmlee.jectpack.livedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.pmlee.jectpack.R

class LiveBus1Aty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_bus1_aty)
        LiveDataEventBus.register<String>(this, Observer {
            Toast.makeText(this.applicationContext,"1111111 $it",0).show()
        },1)
    }
}
