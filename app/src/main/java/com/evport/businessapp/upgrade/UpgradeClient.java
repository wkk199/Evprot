package com.evport.businessapp.upgrade;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;


import com.evport.businessapp.upgrade.model.bean.UpgradeOptions;
import com.evport.businessapp.upgrade.service.UpgradeService;

import java.lang.ref.WeakReference;

/**
 * UpgradeClient
 *
 * @author itsnows, xue.com.fei@gmail.com
 * @since 2018/3/19 11:09
 */
public class UpgradeClient {
    private static final String TAG = UpgradeClient.class.getSimpleName();
    private final Context context;
    private final UpgradeOptions options;
    private final ServiceConnection connection;
    private Messenger client;
    private Messenger server;
    private OnConnectListener onConnectListener;
    private OnDownloadListener onDownloadListener;
    private OnInstallListener onInstallListener;
    private boolean isConnected;

    private UpgradeClient(Context context, UpgradeOptions options) {
        this.context = context;
        this.options = options;
        this.client = new Messenger(new ClientHandler(this));
        this.connection = new UpgradeServiceConnection();
    }

    /**
     * 附加客户端实例，同时绑定活动
     *
     * @param context
     * @param options
     * @return
     */
    public static UpgradeClient add(Context context, UpgradeOptions options) {
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null");
        }
        return new UpgradeClient(context, options);
    }

    /**
     * 注入连接监听回调接口
     *
     * @param onConnectListener
     */
    public void setOnConnectListener(OnConnectListener onConnectListener) {
        this.onConnectListener = onConnectListener;
    }

    /**
     * 注入下载监听回调接口
     *
     * @param onDownloadListener
     */
    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    /**
     * 注入安装监听回调接口
     *
     * @param onInstallListener
     */
    public void setOnInstallListener(OnInstallListener onInstallListener) {
        this.onInstallListener = onInstallListener;
    }

    /**
     * 销毁客户端实例，同时解除绑定活动
     */
    public void remove() {
        disconnect();
    }

    /**
     * 开始
     */
    public void start() {
        if (!isConnected()) {
            bind();
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (isConnected()) {
            sendMessageToServer(UpgradeConstant.CODE_DOWNLOAD_PAUSE, null);
        }
    }

    /**
     * 继续
     */
    public void resume() {
        if (isConnected()) {
            sendMessageToServer(UpgradeConstant.CODE_DOWNLOAD_RESUME, null);
        }
    }

    /**
     * 取消
     */
    public void cancel() {
        if (isConnected()) {
            sendMessageToServer(UpgradeConstant.CODE_DOWNLOAD_RESUME, null);
        }
    }

    /**
     * 安装
     */
    public void install() {
        if (isConnected()) {
            sendMessageToServer(UpgradeConstant.CODE_INSTALL_START, null);
        }
    }

    /**
     * 重启
     */
    public void reboot() {
        if (isConnected()) {
            sendMessageToServer(UpgradeConstant.CODE_INSTALL_REBOOT, null);
        }
    }

    /**
     * 绑定升级服务
     */
    private void bind() {
        if (context instanceof Activity || context instanceof Service) {
            UpgradeService.start(context, options, connection);
            return;
        }
        UpgradeService.start(context, options);
    }

    /**
     * 解绑升级服务
     */
    private void unbind() {
        if (context != null && connection != null) {
            context.unbindService(connection);
        }
    }

    /**
     * 连接升级服务
     */
    private void connect() {
        if (!isConnected()) {
            sendMessageToServer(UpgradeConstant.CODE_CONNECT, null);
        }
    }

    /**
     * 断开升级服务
     */
    private void disconnect() {
        if (isConnected() && client != null && server != null) {
            sendMessageToServer(UpgradeConstant.CODE_DISCONNECT, null);
        }
    }

    /**
     * 是否连接到升级服务
     *
     * @return
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 发送消息到服务端
     *
     * @param code
     * @param data
     */
    private void sendMessageToServer(int code, Bundle data) {
        try {
            Message message = Message.obtain();
            message.replyTo = client;
            message.what = code;
            message.setData(data != null ? data : new Bundle());
            server.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Handler {
        private final WeakReference<UpgradeClient> reference;

        private ClientHandler(UpgradeClient client) {
            this.reference = new WeakReference<>(client);
        }

        @Override
        public void handleMessage(Message msg) {
            UpgradeClient client = reference.get();
            if (client == null) {
                return;
            }
            Bundle data = msg.getData();
            int code = data.getInt("code");
            String message = data.getString("message");
            switch (msg.what) {
                case UpgradeConstant.CODE_CONNECT:
                    if (code == 0) {
                        client.isConnected = true;
                        if (client.onConnectListener != null) {
                            client.onConnectListener.onConnected();
                        }
                    }
                    UpgradeLogger.d(TAG, message);
                    break;
                case UpgradeConstant.CODE_DISCONNECT:
                    if (code == 0) {
                        if (client.onConnectListener != null) {
                            client.onConnectListener.onDisconnected();
                            client.isConnected = false;
                        }
                    }
                    UpgradeLogger.d(TAG, message);
                    client.unbind();
                    break;
                case UpgradeConstant.CODE_DOWNLOAD_START:
                    UpgradeLogger.d(TAG, "Download：onStart");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onStart();
                    }
                    break;
                case UpgradeConstant.CODE_DOWNLOAD_PROGRESS:
                    UpgradeLogger.d(TAG, "Download：onProgress：");
                    long max = data.getLong("max");
                    long progress = data.getLong("progress");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onProgress(max, progress);
                    }
                    break;
                case UpgradeConstant.CODE_DOWNLOAD_PAUSE:
                    UpgradeLogger.d(TAG, "Download：onPause");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onPause();
                    }
                    break;
                case UpgradeConstant.CODE_DOWNLOAD_CANCEL:
                    UpgradeLogger.d(TAG, "Download：onCancel");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onCancel();
                    }
                    break;
                case UpgradeConstant.CODE_DOWNLOAD_ERROR:
                    UpgradeLogger.d(TAG, "Download：onError");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onError(new UpgradeException());
                    }
                    break;
                case UpgradeConstant.CODE_DOWNLOAD_COMPLETE:
                    UpgradeLogger.d(TAG, "Download：onComplete");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onComplete();
                    }
                    break;
                case UpgradeConstant.CODE_INSTALL_VALIDATE:
                    UpgradeLogger.d(TAG, "Install：onValidate");
                    if (client.onInstallListener != null) {
                        client.onInstallListener.onValidate();
                    }
                    break;
                case UpgradeConstant.CODE_INSTALL_START:
                    UpgradeLogger.d(TAG, "Install：onStart");
                    if (client.onInstallListener != null) {
                        client.onInstallListener.onStart();
                    }
                    break;
                case UpgradeConstant.CODE_INSTALL_CANCEL:
                    UpgradeLogger.d(TAG, "Install：onCancel");
                    if (client.onInstallListener != null) {
                        client.onInstallListener.onCancel();
                    }
                    break;
                case UpgradeConstant.CODE_INSTALL_ERROR:
                    UpgradeLogger.d(TAG, "Install：onError");
                    if (client.onInstallListener != null) {
                        client.onInstallListener.onError(new UpgradeException(code));
                    }
                    break;
                case UpgradeConstant.CODE_INSTALL_COMPLETE:
                    UpgradeLogger.d(TAG, "Install：onComplete");
                    if (client.onInstallListener != null) {
                        client.onInstallListener.onComplete();
                    }
                    break;
                case UpgradeConstant.CODE_INSTALL_REBOOT:
                    UpgradeLogger.d(TAG, "Install：onReboot");
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private class UpgradeServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            server = new Messenger(service);
            connect();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            server = null;
            client = null;
            isConnected = false;
        }
    }

}
