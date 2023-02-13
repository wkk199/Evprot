package com.evport.businessapp.data.bean

import androidx.annotation.Keep
import com.evport.businessapp.App
import com.evport.businessapp.utils.DateUtil
import com.evport.businessapp.utils.diviceTypeTextcolor
import com.evport.businessapp.utils.homeDiviceTypebgcolor


//homeName	String	必须;
//note	String	必须;
//img	String	可选; base64编码的图片
//maxPower	String	必须; 单位kw, 保留一位小数; 家庭最大功率
//reservedPower	String	必须; 单位kw, 保留一位小数; 家庭预留功率
//members	List<String>	可选; app用户的主键集合;家庭成员
@Keep
data class FamilyAddBean (
    var homePk:String,
    var homeName:String,
    var note:String?=null,
    var img:String?=null,
    var maxPower:String,
    var reservedPower:String,
    var members:List<String>?=null
        )
@Keep
data class FamilyBean (
    var homePk:String?=null,
    var homeSettingPk:String?=null,
    var homeCreatorUserPk:String?=null,
    var homeName:String?=null,
    var note:String?=null,
    var img:String?=null,
    var homeMaxPower:String?=null,
    var homeReservedPower:String?=null,
    var members:ArrayList<FamilyMemberBean>?=null
        )
//homeMemberPk	String	家庭设置主键
//userName	String	家庭主键
//avatarUrl	String	家庭名字
@Keep
data class FamilyMemberBean (
    var homeMemberPk:String?=null,
    var userName:String?=null,
    var email:String?=null,
    var userPk:String?=null,
    var avatarUrl:String?=null
        )
@Keep
data class HomePkBean (
    var homePk:String?=null,
    var chargeBox:String?=null,
    var homeChargeBoxPk:String?=null
    )
@Keep
data class EmailBean (
    var email:String?=null,//修改时必须
    var homeMemberPk:String?=null,//删除时必须
    var homePk:String?=null//修改新增时必须
    )
@Keep
data class PowerUpBean (
    var homeSettingPk:String?=null,
    var maxPower:String?=null,
    var reservedPower:String?=null
)
//homePk	String	家庭主键
//homeChargeBoxPk	String	家庭充电桩主键
//chargeBoxId	String	充电桩ID
//status	String	0:设置失败,1:设置成功,2:等待设置
//connectors	List<JSON>	充电枪
@Keep
data class HomeStationBean (
    var homePk:String?=null,
    var homeChargeBoxPk:String?=null,
    var chargeBoxId:String?=null,
    var status:String?=null,
    var connectors:ArrayList<HomeDeviceBean>?=null
)
//connectorPk	String	充电枪主键
//type	String	充电枪类型
//power	String	充电枪功率
//status	String	充电枪状态
@Keep
data class HomeDeviceBean (
    var status:String?=null,
    var power:String?=null,
    var type:String?=null,
    var homeSchedule: AddSchedule?=null,
    var connectorPk:String?=null
){
    fun bgColor(): Int {
        return App.appContext.resources.getColor(status.homeDiviceTypebgcolor())
    }

    fun textColor(): Int {
        return App.appContext.resources.getColor(status.diviceTypeTextcolor())
    }

    fun btnStr():String{
        return if (homeSchedule==null||homeSchedule?.homeSchedulePk==null){
            "Start"
        }else{
            "Charge now"
        }
    }
}

@Keep
data class AddSchedule (
    var connectorPk:String?=null,//新增
    var homeSchedulePk:String?=null,//删除修改
    var transactionPk:String?=null,//删除修改
    var userPk:String?=null,
    var startTime:String?=null,
    var stopTime:String?=null
){
    fun stopTimeUtc2Str():String?{
        return if (stopTime.isNullOrEmpty())
            "--"
        else
            DateUtil.UTCToString(stopTime)
//        return DateUtil.UTCToString(stopTime)
//        return stopTime
    }
    fun stopTimeUtc():String?{
          return  DateUtil.UTCToString(stopTime)
    }
    fun startTimeUtc2Str():String{
        return if (startTime.isNullOrEmpty())
            "--"
        else
            DateUtil.UTCToString(startTime)
    }
    fun startTimeUtc():String{
            return  DateUtil.UTCToString(startTime)
    }

}
@Keep
data class NameVo (  var name:String?=null,//新增
var random:String ?=null
)