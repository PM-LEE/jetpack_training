package com.pmlee.jectpack.livedata

import android.util.Log
import android.util.SparseArray
import androidx.arch.core.internal.SafeIterableMap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.lang.Exception

/**
 * 基于LiveData实现的消息总线
 *
 * LiveData默认的实现有粘性消息的效果
 *
 * @author liyunshuang
 *
 */
object LiveDataEventBus {

    private val busMapper: SparseArray<MutableLiveData<Any>> by lazy {
        SparseArray<MutableLiveData<Any>>()
    }

    /**
     * 通过键值获取到LiveData对象
     */
    fun <T> with(key: Int): MutableLiveData<T>? {
        return busMapper[key] as? MutableLiveData<T>
    }

    /**
     * 注册监听者
     * @param lifecycleOwner 拥有生命周期的控件
     * @param key 消息的键值
     * @param needReceiveLastChange 在注册的时候接收发送者上一次的事件
     */
    fun <T> register(
        lifecycleOwner: LifecycleOwner
        , observer: Observer<in T>
        , key: Int
        , needReceiveLastChange: Boolean? = false
    ) {
        var tempLiveData = busMapper[key]
        if (tempLiveData == null) {
            tempLiveData = MutableLiveData<Any>()
            busMapper.put(key, tempLiveData)
        }

        tempLiveData.observe(lifecycleOwner, observer as Observer<Any>)

        if (needReceiveLastChange != true)
            syncChangedVersion(tempLiveData, observer)
    }

    /**
     * 同步观察者与被观察者的版本号
     */
    private fun syncChangedVersion(mutableLiveData: MutableLiveData<Any>, observer: Observer<Any>) {
        try {
            var liveDataClazz = MutableLiveData::class.java
            var liveDataFiled = liveDataClazz.superclass?.getDeclaredField("mVersion")
            liveDataFiled?.isAccessible = true
            var liveDataVersion = liveDataFiled?.get(mutableLiveData) ?: -1
            liveDataClazz.superclass?.getDeclaredField("mObservers")?.run {
                isAccessible = true
                var map = get(mutableLiveData) as? SafeIterableMap<*, *>
                SafeIterableMap::class.java.getDeclaredMethod("get", Any::class.java).run {
                    isAccessible = true
                    (invoke(map, observer) as? Map.Entry<*, *>)?.run {
                        var mapper = this.value
                        this.value.run {
                            javaClass.superclass?.getDeclaredField("mLastVersion")?.run {
                                isAccessible = true
                                set(mapper, liveDataVersion)
                            }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("LiveDataEventBus", e.toString())
        }
    }

    /**
     * 在主线程线程发送消息
     */
    fun setValue(key: Int, value: Any) {
        busMapper[key]?.value = value
    }

    /**
     * 在子线程中发送数据
     * 消息会进入队列
     * 最终还是在主线程中回调
     */
    fun postValue(key: Int, value: Any) {
        busMapper[key]?.postValue(value)
    }
}