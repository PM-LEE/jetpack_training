package com.pmlee.jectpack.livedata

import android.util.SparseArray
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * 基于LiveData实现的消息总线
 *
 * LiveData默认的实现有粘性消息的效果
 */
object LiveDataEventBus {

    private val busMapper:SparseArray<MutableLiveData<Any>> by lazy {
        SparseArray<MutableLiveData<Any>>()
    }

    /**
     * 通过键值获取到LiveData对象
     */
    fun <T> with(key: Int):MutableLiveData<T>?{
        return busMapper[key] as? MutableLiveData<T>
    }

    /**
     * 注册监听者
     */
    fun <T> register(lifecycleOwner: LifecycleOwner,observer: Observer<in T>,key: Int){
        var tempLiveData = busMapper[key]
        if (tempLiveData != null) {
            tempLiveData.observe(lifecycleOwner,observer as Observer<Any>)
        }else{
            busMapper.put(key, MutableLiveData<Any>())
        }
    }

    /**
     * 在主线程线程发送消息
     */
    fun setValue(key: Int,value:Any){
        busMapper[key]?.value = value
    }

    /**
     * 在子线程中发送数据
     * 消息会进入队列
     * 最终还是在主线程中回调
     */
    fun postValue(key: Int,value: Any){
        busMapper[key]?.postValue(value)
    }
}