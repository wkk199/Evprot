package com.evport.businessapp.data.http.networkmanager

/**
 * 自定义的异常类型
 */

 class ApiException(override val message: String?, val code: String) : RuntimeException()