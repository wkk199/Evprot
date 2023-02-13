package com.evport.businessapp.ws

import android.app.*
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.evport.businessapp.BuildConfig
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.MessageWrap
import com.evport.businessapp.data.config.Configs
import com.kunminx.architecture.utils.SPUtils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake

import org.jetbrains.anko.toast
import org.json.JSONObject
import java.net.URI
import java.util.concurrent.TimeUnit


/**
 *
 *  author : JiangKun
 *  e-mail : jiangkuikui001@gmail.com
 *  time   : 2021/12/01
 *  desc   : WebSocket 服务
 *
 */
class WsService : Service() {
    private var wsClient: WebSocketClient? = null
    private val mBinder = WsClientBinder()

    private val NOTIFICATION_CHANNEL_NAME = "ws_notification"
    private var notificationManager: NotificationManager? = null
    private var isCreateChannel = false

    companion object {
        var isStop = false
    }

    override fun onCreate() {
        super.onCreate()

        startForeground(1, buildNotification())
        stopForeground(true)

        initWebSocket()

        GlobalScope.launch(Dispatchers.IO) {
            while (!isStop) {
                if (wsClient != null) {
                    if (wsClient!!.isClosed)
                        reconnectWs()
                } else {
                    initWebSocket()
                    reconnectWs()
                }
                delay(10 * 1000L)
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, buildNotification())
        stopForeground(true)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initWebSocket() {
        val wsUrl = Configs.WEB_BASE_URL.plus("platform/") + SPUtils.getInstance()
            .getString(Configs.EMAIL) + "_EVPORT_Websocket"


        Log.e("hm---", wsUrl)
        wsClient = object : WebSocketClient(URI(wsUrl)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.e("hm---socket", "ws onOpen")
            }

            override fun onMessage(message: String?) {
                Log.e("hm---socket", message!!)
                EventBus.getDefault().post(MessageWrap(1, message));

            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.e("hm---socket", "ws onClose")
//                Thread { reconnect() }.start()
            }

            override fun onError(ex: Exception?) {
                Log.e("hm---socket", "ws onError" + ex!!.message)
            }
        }
        connect()
    }

    private fun isJsonStr(rawString: String): Boolean {
        return try {
            JSONObject(rawString)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onDestroy() {
        closeConnect()
        super.onDestroy()
    }

    /**
     * 发送消息
     */
    fun sendMsg(msg: String) {
        if (null != wsClient) {
            Log.e("hm---socket", msg)
            try {
                wsClient?.send(msg)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * TODO 连接ws
     *
     */
    fun connect() {
        try {
            wsClient?.connectBlocking(5, TimeUnit.SECONDS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * TODO 断开连接
     */
    private fun closeConnect() {
        try {
            wsClient?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * TODO 重连
     */
    private fun reconnectWs() {
        try {
            //重连
            wsClient?.reconnectBlocking()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    //用于Activity和service通讯
    internal inner class WsClientBinder : Binder() {
        val service: WsService
            get() = this@WsService
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        closeConnect()
    }


    private fun buildNotification(): Notification {
        var builder: Notification.Builder? = null
        var notification: Notification? = null
        if (Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager =
                    application.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            }
            val channelId: String = application.packageName
            if (!isCreateChannel) {
                val notificationChannel = NotificationChannel(
                    channelId,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableLights(true) //是否在桌面icon右上角展示小圆点
                notificationChannel.lightColor = Color.BLUE //小圆点颜色
                notificationChannel.setShowBadge(true) //是否在久按桌面图标时显示此渠道的通知
                notificationManager!!.createNotificationChannel(notificationChannel)
                isCreateChannel = true
            }
            builder = Notification.Builder(applicationContext, channelId)
        } else {
            builder = Notification.Builder(applicationContext)
        }

        builder.setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("正在后台运行")
            .setContentIntent(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        this,
                        0,
                        this.packageManager.getLaunchIntentForPackage(this.packageName),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                } else {
                    PendingIntent.getActivity(
                        this,
                        0,
                        this.packageManager.getLaunchIntentForPackage(this.packageName),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            )
            .setWhen(System.currentTimeMillis())
        notification = builder.build()
        return notification
    }

    fun toastT(srt: String) {
        if (BuildConfig.DEBUG)
            toast(srt)
    }
}