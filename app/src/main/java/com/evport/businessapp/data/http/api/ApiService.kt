package com.evport.businessapp.data.http.api

import com.evport.businessapp.data.bean.*
import com.evport.businessapp.data.http.networkmanager.Resource
import io.reactivex.Observable
import retrofit2.http.*
import kotlin.collections.ArrayList


/**
 * 网络请求路由,详见接口文档 （没有接口文档）
 */
interface ApiService {

    // 登录
    @POST("login/validateApp")
    fun usersAuth(@Body user: User): Observable<Resource<User>>

    //
    @GET("ocpp/index/getDomain")
    fun getUrl(): Observable<Resource<String>>

    // 校验
    @GET("ocpp/user/validAppUsername")
    fun checkName(@QueryMap(encoded = false) map: Map<String, String>): Observable<Resource<User>>

    // 注册
    @POST("app/user/updateSelfInfo")
    fun sinUp(@Body user: User): Observable<Resource<User>>

    // 忘记密码
    @POST("app/user/forgetPassword")
    fun forgetPassword(@Body user: User): Observable<Resource<User>>

    // 发送邮件验证码
    @POST("app/user/getCode")
    fun getCode(@Body user: User): Observable<Resource<String>>

    // 发送邮件验证码
    @POST("/ocpp/user/validRegistrationCode")
    fun getValidRegistrationCode(@Body user: User): Observable<Resource<String>>

    // 发送邮件验证码 -- 改密码
    @GET("app/user/getEmailCode")
    fun getCodeResetpwd(@QueryMap(encoded = false) map: Map<String, String>): Observable<Resource<String>>


    //
    @POST("authen/login/logout")
    fun logout(@Body user: User): Observable<Resource<User>>


    // 改密码
    @POST("ocpp/user/changePassword")
    fun changePassword(@Body changePassword: ChangePassword): Observable<Resource<ChangePassword>>


    // 上传头像
    @POST("pcApp/index/uploadAvatar")
    fun updateAvatar(@Body body: Image): Observable<Resource<User>>


    // feedback
    @POST("pcApp/feedback/commit")
    fun feedbackCommit(@Body body: FeedbackCommit): Observable<Resource<Any>>


    // feedback
    @POST("pcApp/feedbackReply/replyFeedback")
    fun feedbackReply(@Body body: FeedbackReplyCommit): Observable<Resource<Any>>

    //获取未读消息
    @POST("pcApp/user/getMessageInfo")
    fun getMessageInfo(@Body body: MessageData): Observable<Resource<MessageInfo>>


    //已读反馈回复
    @POST("pcApp/feedbackReply/check")
    fun feedbackReplyCheck(@Body body: MessageData): Observable<Resource<String>>

    //已读系统消息
    @POST("pcApp/user/checkSystemMessage")
    fun checkSystemMessage(@Body body: MessageData): Observable<Resource<String>>

    //已读回复
    @POST("pcApp/commentsReply/check")
    fun commentsReplyCheck(@Body body: MessageData): Observable<Resource<String>>

    //修改用户信息
//    @POST("ocpp/user/updateInfo")
//    fun updateInfo(@Body body: User): Observable<Resource<String>>


    //修改用户信息
    @POST("ocpp/user/v2/updateInfo")
    fun updateInfo(@Body body: User): Observable<Resource<String>>

//    ---------------------------

    //
    @POST("evie/chargeBoxUserEvie/settingCharger")
    fun chargeSet(@Body chargeSetting: ChargeSetting): Observable<Resource<ChargeSetting>>


    //
    @GET("evie/chargeBoxUserEvie/getCharger")
    fun getChargeSettingList(): Observable<Resource<MutableList<ChargeSetting>>>

    //
    @POST("evie/chargeBoxUserEvie/bindFamily")
    fun bindFamily(@Body body: RequestScan): Observable<Resource<Any>>


    //
    @POST("evie/chargeBoxUserEvie/unbindFamily")
    fun unBindFamily(@Body body: RequestScan): Observable<Resource<Any>>


// ------


//    @POST("pcApp/chargingOrder/stats")
//    fun stats(@Body requestStats: RequestStats): Observable<Resource<StatsDataResp>>

    @POST("ev365/transaction/stats")
    fun stats(@Body requestStats: RequestStats): Observable<Resource<StatsDataResp>>

    @POST("pcApp/index/search")
    fun homeSearch(@Body requestStats: HomeSearch): Observable<Resource<List<StationListBean>>>


    @POST("pcApp/index/stationDetail")
    fun stationDetail(@Body stationPk: StationListBean): Observable<Resource<StationDetailBean>>


    @POST("pcApp/comment/getStationComment")
    fun getStationComment(@Body commentpk: StationCommentReq): Observable<Resource<List<Comment>>>

    @POST("pcApp/commentsReply/getCommentReply")
    fun getCommentReply(@Body commentpk: Comment): Observable<Resource<List<Comment>>>

    @POST("pcApp/commentsReply/getReplyDetail")
    fun getReplyDetail(@Body commentsReplyRootPk: Comment): Observable<Resource<ReplyListItem>>

    @POST("pcApp/commentsReply/commit")
    fun addRep(@Body commit: CommitComment): Observable<Resource<Any>>

    @POST("pcApp/commentsReply/check")
    fun getReplyCheck(@Body commentsReplyPk: Comment): Observable<Resource<Any>>

    @POST("pcApp/comment/delete")
    fun delCOmment(@Body commentsReplyPk: Comment): Observable<Resource<Any>>

    @POST("pcApp/commentsReply/delete")
    fun delReply(@Body commentsReplyPk: Comment): Observable<Resource<Any>>

    //收藏
    @POST("pcApp/collection/add")
    fun collectionAdd(@Body collectionAdd: CollectionAdd): Observable<Resource<Any>>

    @POST("pcApp/collection/delete")
    fun collectionDelete(@Body collectionAdd: CollectionAdd): Observable<Resource<Any>>

    @POST("pcApp/collection/getCollection")
    fun getCollection(@Body collectionAdd: getCollectReq): Observable<Resource<CollectList>>

    @POST("pcApp/comment/addComment")
    fun addComment(@Body add: CommentAdd): Observable<Resource<Any>>


//    评论

    //
//    @POST("pcApp/chargingOrder/record")
//    fun records(@Body requestRecord: RequestRecord): Observable<Resource<MutableList<RecordResp>>>
    @POST("ev365/transaction/record")
    fun records(@Body requestRecord: RequestRecord): Observable<Resource<MutableList<RecordResp>>>

    //
    @POST("pcApp/chargingOrder/orderDetail")
    fun recordsDetail(@Body requestRecord: RequestRecordDetail): Observable<Resource<RecordDetailResp>>


    //
    @POST("evie/evieTransaction/authorize")
    fun scan(@Body requestScan: RequestScan): Observable<Resource<Boolean>>

    //
    @POST("pcApp/index/getConnectorInfo")
    fun scanV2(@Body requestScan: RequestScan): Observable<Resource<ConnectorDetail>>


    //
    @GET("evie/chargeBoxUserEvie/getAuthoCharger")
    fun getChargePoint(): Observable<Resource<MutableList<ChargeSetting>>>


    //
    @POST("evie/evieTransaction/connectorInfo")
    fun getChargegun(@Body requestScan: RequestScan): Observable<Resource<MutableList<GunResp>>>

    @POST("pcApp/index/connectorDetail")
    fun connectorDetail(@Body Pk: Connector): Observable<Resource<ConnectorDetail>>


    /****** 充电相关***/

//          定时启动
    @POST("evie/chargingScheduleEvie/chargingSchedule")
    fun setChargingSchedule(@Body body: RequestCharge): Observable<Resource<String>>


    //          修改定时启动
    @POST("ocpp/chargingScheduleEvie/update")
    fun setChargingScheduleUpdate(@Body body: RequestCharge): Observable<Resource<String>>


    //          启动定时启动
    @POST("evie/chargingScheduleEvie/start")
    fun setChargingScheduleStart(@Body body: RequestCharge): Observable<Resource<String>>

    //          启动定时启动
    @POST("evie/chargingScheduleEvie/delete")
    fun setChargingScheduleDelete(@Body body: RequestCharge): Observable<Resource<String>>

    //          进入正在充电界面接口
    @POST("pcApp/transaction/charging")
    fun getCheckTransactions(@Body b: ReqDefault): Observable<Resource<MutableList<CheckTransaction>>>

    //          进入正在充电界面接口 pk
    @POST("pcApp/transaction/charging")
    fun getCheckTransactions(@Body b: ReqDefaultPk): Observable<Resource<MutableList<CheckTransaction>>>

    //充电中能源自动切换和能源手动修改
    @POST("evie/chargingScheduleEvie/autoSwitch")
    fun setAutoSwitch(@Body body: RequestChargeChange): Observable<Resource<String>>


    @POST("pcApp/transaction/remoteStart")
    fun remoteStartPc(@Body body: RequestChargeChange): Observable<Resource<RemoteStartRep>>

    @POST("pcApp/transaction/remoteStop")
    fun remoteStopPc(@Body body: RequestChargeChange): Observable<Resource<Any>>

    @POST("pcApp/stripeCustomer/getEphemeralKey")
    fun getEphemeralKey(@Body body: GetEphemeralKey): Observable<Resource<Any>>

    //远程启动
    //远程启动
    @POST("pcApp/transaction/remoteStart")
    fun remoteStart(@Body body: RequestChargeChange): Observable<Resource<String>>


    //远程启动
    @POST("pcApp/transaction/remoteStop")
    fun remoteStop(@Body body: RequestChargeChange): Observable<Resource<String>>


    /******/
//远程启动
    @GET("pcApp/index/getOperator")
    fun getOperator(): Observable<Resource<ArrayList<SocketType>>>


    //=====noti
    @POST("pcApp/comment/getComments")
    fun getNotiComments(@Body body: StationCommentReq): Observable<Resource<List<Comment>>>

    @POST("pcApp/commentsReply/getReplyList")
    fun getNotiReply(@Body body: StationCommentReq): Observable<Resource<List<ReplyDetail>>>


    @POST("pcApp/feedback/getFeedback")
    fun getNotiFeedback(@Body body: StationCommentReq): Observable<Resource<List<Feedback>>>

    @POST("pcApp/feedbackReply/deleteFeedback")
    fun delNotiFeedback(@Body body: StationCommentReq): Observable<Resource<Any>>


    @POST("pcApp/feedbackReply/getDetail")
    fun getNotiFeedbackDetail(@Body body: StationCommentReq): Observable<Resource<FeedBackDataBean>>

    @POST("pcApp/index/getSysMessage")
    fun getNotiSys(@Body body: StationCommentReq): Observable<Resource<List<NotiSys>>>

    @POST("pcApp/index/getSysMessageDetail")
    fun getNotiSysDetail(@Body body: SysDetailReq): Observable<Resource<NotiSys>>

    @GET("pcApp/transaction/cardPackage")
    fun getCard(): Observable<Resource<List<MyCard>>>

    @POST("pcApp/alipay/recharge")
    fun recharge(@Body body: RechargeReq): Observable<Resource<String>>

    @POST("pcApp/wxpay/recharge")
    fun wxRecharge(@Body body: RechargeReq): Observable<Resource<WxPayBean>>


    //      家庭功能-=-
    @POST("home/home/addHome")
    fun addNewFamily(@Body body: FamilyAddBean): Observable<Resource<Any>>

    @POST("home/home/verify")
    fun verifyEmail(@Body emailBean: EmailBean): Observable<Resource<User>>

    @POST("home/homeMember/addMember")
    fun addMember(@Body emailBean: EmailBean): Observable<Resource<User>>

    @POST("home/homeMember/delete")
    fun delMember(@Body emailBean: EmailBean): Observable<Resource<Any>>

    @GET("home/homeMember/getHomeList")
    fun getHomeList(): Observable<Resource<ArrayList<FamilyList>>>

    @GET("home/home/getHomePk")
    fun getHomePk(): Observable<Resource<String>>

    @POST("home/homeChargeBox/get")
    fun getHomeChargeBox(@Body homePkBean: HomePkBean): Observable<Resource<List<HomeStationBean>>>

    @POST("home/homeChargeBox/bind")
    fun bindCharge(@Body homePkBean: HomePkBean): Observable<Resource<Any>>

    @POST("home/homeChargeBox/unbind")
    fun unBindCharge(@Body homePkBean: HomePkBean): Observable<Resource<Any>>

    @POST("home/home/delete")
    fun delHome(@Body homePkBean: HomePkBean): Observable<Resource<Any>>

    @POST("home/homeSetting/get")
    fun getSettingHome(@Body homePkBean: HomePkBean): Observable<Resource<FamilyBean>>

    @POST("home/home/update")
    fun homeUpdate(@Body homePkBean: FamilyBean): Observable<Resource<Any>>

    @POST("home/homeSetting/updateSetting")
    fun powerUpdate(@Body homePkBean: PowerUpBean): Observable<Resource<Any>>


    //远程启动
    @POST("home/homeChargeBox/remoteStart")
    fun remoteStartHome(@Body connectorPk: RequestChargeChange): Observable<Resource<RemoteStartRep>>

    @POST("home/homeSchedule/addSchedule")
    fun addSchedule(@Body connectorPk: AddSchedule): Observable<Resource<Any>>

    @POST("home/homeSchedule/updateSchedule")
    fun updateSchedule(@Body homeSchedulePk: AddSchedule): Observable<Resource<Any>>

    @POST("home/homeSchedule/delete")
    fun deleteSchedule(@Body homeSchedulePk: AddSchedule): Observable<Resource<Any>>

    @POST("pcApp/user/getAccountBalance")
    fun getAccountBalance(@Body homeSchedulePk: NameVo): Observable<Resource<String>>

    //可退款余额
    @POST("pcApp/appRefund/getRefundableAmount")
    fun getRefundableAmount(@Body homeSchedulePk: NameVo): Observable<Resource<String>>

    //退款
    @POST("pcApp/appRefund/refund")
    fun refund(@Body rechargeReq: RechargeReq): Observable<Resource<String>>

    //获取服务器版本号
    @POST("pcApp/index/getAndroidVersion")
    fun getAndroidVersion(@Body homeSchedulePk: NameVo): Observable<Resource<String>>

    //获取服务器版本号
    @POST("pcApp/index/getAndroidInstallPackage")
    fun getAndroidInstallPackage(@Body homeSchedulePk: NameVo): Observable<Resource<String>>

    //获取订单列表
    @POST("ocpp/chargingOrderInvoice/getInvoiceAbleList")
    fun getInvoiceAbleList(@Body homeSchedulePk: InvoiceVo): Observable<Resource<ArrayList<InvoicListBean>>>

    @POST("ocpp/chargingOrderInvoice/getCompanyInfo")
    fun getCompanyInfo(@Body homeSchedulePk: CompanyNameVO): Observable<Resource<ArrayList<CompanyBean>>>

    @POST("ocpp/chargingOrderInvoice/getCompanyInfo")
    fun getCompanyInfoDefult(@Body homeSchedulePk: DefultBean): Observable<Resource<ArrayList<CompanyBean>>>

    //开票接口
    @POST("ocpp/chargingOrderInvoice/createInvoice")
    fun createInvoice(@Body homeSchedulePk: CreateInvoiceVo): Observable<Resource<String>>

    //获取订单列表
    @POST("ocpp/chargingOrderInvoice/getCreatedInvoiceHistoryList")
    fun getCreatedInvoiceHistoryList(@Body homeSchedulePk: InvoiceHistoryVo): Observable<Resource<ArrayList<InvoiceHistoryBean>>>

    //删除用户
    @POST("ocpp/user/deleteUser")
    fun deleteUser(@Body body: DeleteUserReq): Observable<Resource<String>>

}

