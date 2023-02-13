package com.evport.businessapp.data.bean

import androidx.annotation.Keep

class Family {
}

//{“homePk” : “”, homeName:””}
@Keep
data class FamilyList(
    var homePk:String,
    var img:String?=null,
    var homeName:String
){
}