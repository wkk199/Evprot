package com.evport.businessapp.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.evport.businessapp.data.bean.EventBean
import java.util.concurrent.ConcurrentHashMap

class LiveBus private constructor() {

    private val busMap by lazy { ConcurrentHashMap<Class<*>, BusLiveData<*>>() }

    private fun <T> bus(clazz: Class<T>) = busMap.getOrPut(clazz) { BusLiveData<T>() }

    fun <T> with(clazz: Class<T>) = bus(clazz) as BusLiveData<T>

    // 同步发送事件
    fun post(value: EventBean) {
        with(EventBean::class.java).setValue(value)
    }

    // 异步发送事件
    fun postAsyn(value: EventBean) {
        with(EventBean::class.java).postValue(value)
    }

    // 订阅事件
    fun observeEvent(licOwner: LifecycleOwner, observer: Observer<EventBean>) {
        return with(EventBean::class.java).observe(licOwner, observer)
    }

    fun observeForeverEvent(observer: Observer<EventBean>) {
        return with(EventBean::class.java).observeForever(observer)
    }

    // 订阅粘性事件
    fun observerStickyEvent(licOwner: LifecycleOwner, observer: Observer<EventBean>) {
        return with(EventBean::class.java).observe(licOwner, observer, true)
    }

    fun observeForeverStickyEvent(observer: Observer<EventBean>) {
        return with(EventBean::class.java).observeForever(observer, true)
    }


    fun removeObserver(observer: Observer<EventBean>) {
        with(EventBean::class.java).removeObserver(observer)
    }

    class BusLiveData<T> : MutableLiveData<T>() {

        var mVersion = START_VERSION

        var mStickyEvent: T? = null


        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            observe(owner, observer as Observer<T>, false)
        }


        fun observe(owner: LifecycleOwner, observer: Observer<T>, sticky: Boolean) {
            super.observe(owner, ObserverWrapper(observer, this, sticky))
        }

        fun observeForever(observer: Observer<T>, sticky: Boolean) {
            super.observeForever(ObserverWrapper(observer, this, sticky))
        }


        override fun setValue(value: T) {
            mVersion++
            super.setValue(value)
        }

        override fun postValue(value: T) {
            mVersion++
            super.postValue(value)
        }

        fun setValueSticky(value: T) {
            mStickyEvent = value
            setValue(value)
        }

        fun postValueSticky(value: T) {
            mStickyEvent = value
            postValue(value)
        }

        fun removeSticky() {
            mStickyEvent = null
        }
    }

    private class ObserverWrapper<T>(val observer: Observer<T>, val liveData: BusLiveData<T>, val sticky: Boolean) :
        Observer<T> {

        // 通过标志位过滤旧数据
        private var mLastVersion = liveData.mVersion

        override fun onChanged(t: T?) {

            if (mLastVersion >= liveData.mVersion) {
                // 回调粘性事件
                if (sticky && liveData.mStickyEvent != null) {
                    observer.onChanged(liveData.mStickyEvent)
                }
                return
            }
            mLastVersion = liveData.mVersion

            observer.onChanged(t)
        }
    }

    companion object {

        private const val START_VERSION = -1

        @Volatile
        private var instance: LiveBus? = null

        @JvmStatic
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: LiveBus().also { instance = it }
        }

    }

}