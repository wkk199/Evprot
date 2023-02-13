package com.evport.businessapp.data.http.networkmanager

import androidx.annotation.Keep

/**
 * 封装处理后的数据，将一次网络请求分类
 */
@Keep
class Resource<T > private constructor(var code: String,  val data: T?
                                       ,val success:Boolean,val message:String) {

    companion object {
        const val SUCCESS: String = "SUCCESS"
        const val LOADING: String = "LOADING"
        const val ERROR: String = "ERROR"
//
//        fun <T > success(@NonNull result: T): Resource<T> {
//            return Resource(SUCCESS, result,null)
//        }
//
//        fun <T > loading(): Resource<T> {
//            return Resource(LOADING, null,null)
//        }

        fun <T > error(error: String): Resource<T> {

            return Resource(ERROR,null,success = false,message = error)
        }
    }

    fun isSuccess(): Boolean {
        return this.success
    }

    fun getResult(): T? {
        return data
    }

    fun getErrorMsg(): String? {
        return message
    }

}


