package com.evport.businessapp.upgrade;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import com.evport.businessapp.upgrade.provider.UpgradeFileProvider;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

/**
 * Util
 *
 * @author itsnows, xue.com.fei@gmail.com
 * @since 2018/2/10 19:44
 */
public class UpgradeUtil {
    private static final String TAG = UpgradeUtil.class.getSimpleName();

    /**
     * 判断申请外部存储所需权限
     *
     * @param context
     * @param isRequest
     * @return
     */
    public static boolean mayRequestExternalStorage(Context context, boolean isRequest) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (isRequest) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x1024);
        }
        return false;
    }

    /**
     * 获取应用程序路径
     *
     * @return
     */
    public static String getAppPath(Context context) {
        String path = context.getPackageName();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                path = context.getExternalFilesDir(null).getPath();
            } else {
                path = Environment.getExternalStorageDirectory() + File.separator + path;
            }
        } else {
            path = Environment.getRootDirectory() + File.separator + path;
        }
        return path;
    }

    /**
     * 获取应用程序图标
     *
     * @param context
     * @return
     */
    public static Bitmap getAppIcon(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            Drawable drawable = applicationInfo.loadIcon(packageManager);
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
            Bitmap bitmap = Bitmap.createBitmap(width, height, config);
            bitmap.setHasAlpha(true);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取应用程序名称
     *
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return context.getResources().getString(packageInfo.applicationInfo.labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    /**
     * 获取序列号
     *
     * @return
     */
    public static String getSerial() {
        try {
            Field field = Build.class.getField("SERIAL");
            return (String) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 格式化字节单位
     *
     * @param size
     * @return
     */
    public static String formatByte(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    /**
     * 获取运行中的进程
     *
     * @param context
     * @return
     */
    public static List<ActivityManager.RunningAppProcessInfo> getRunningProcess(Context context) {
        if (context != null) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processs = manager.getRunningAppProcesses();
            return processs;
        }
        return null;
    }

    /**
     * 服务是否运行
     *
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : list) {
            if (info.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前活动是否为栈顶
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isActivityTop(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            if (cn != null) {
                return packageName.equals(cn.getPackageName());
            }
        }
        return false;
    }

    /**
     * 重启应用程序
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean rebootApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return false;
        }
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 杀死指定进程
     *
     * @param pid 进程id
     */
    public static void killProcess(int pid) {
        android.os.Process.killProcess(pid);
    }

    /**
     * 杀死当前进程
     */
    public static void killCurrentProcess() {
        killProcess(android.os.Process.myPid());
    }

    /**
     * 杀死所有进程
     *
     * @param context
     */
    public static void kiiAllProcess(Context context) {
        for (ActivityManager.RunningAppProcessInfo process : getRunningProcess(context)) {
            killProcess(process.pid);
        }
    }

    /**
     * 安装程序
     *
     * @param context
     * @param path
     */
    public static void installApk(Context context, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = FileProvider.getUriForFile(context,
                    String.format(UpgradeFileProvider.AUTHORITY, context.getPackageName()), file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 安装程序
     *
     * @param path
     */
    public static boolean installApk(String path) {
        if (path == null || !path.endsWith(".apk")) {
            return false;
        }

        File apk = new File(path);
        if (!apk.exists()) {
            return false;
        }
        int result = cmd("chmod 777 " + apk.getPath() + " \n" +
                "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r " + apk.getPath() + " \n");
        if (result == 0) {
            UpgradeLogger.d(TAG, "Install apk successfully");
            return true;
        } else if (result == 1) {
            UpgradeLogger.d(TAG, "Install apk failed");
            return false;
        } else {
            UpgradeLogger.d(TAG, "Install apk unknown");
            return false;
        }
    }

    /**
     * 启动程序
     */
    public static void launch(Context context, String className) {
        cmd("am start -S  " + context.getPackageName() + "/" + className + " \n");
    }

    /**
     * 是否有Root权限
     *
     * @return
     */
    public static boolean isRooted() {
        boolean result = false;
        try {
            result = new File("/system/bin/su").exists()
                    || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 执行命令
     *
     * @param cmd
     * @return
     */
    public static int cmd(String cmd) {
        if (cmd == null || cmd.length() == 0) {
            return -1;
        }
        Process process = null;
        DataOutputStream outputStream = null;
        BufferedReader inputReader = null;
        BufferedReader errorReader = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec("su");
            outputStream = new DataOutputStream(process.getOutputStream());
            inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            outputStream.writeBytes(cmd);
            outputStream.writeBytes("exit \n");
            outputStream.flush();
            int result = process.waitFor();
            StringBuilder logcat = new StringBuilder();
            String line = null;
            while ((line = inputReader.readLine()) != null) {
                logcat.append(line).append("\n");
            }
            while ((line = errorReader.readLine()) != null) {
                logcat.append(line).append("\n");
            }
            UpgradeLogger.d(TAG, "Shell logcat " + logcat);
            return result;
        } catch (Exception e) {
            UpgradeLogger.d(TAG, "Shell error " + e);
        } finally {

            if (errorReader != null) {
                try {
                    errorReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (process != null) {
                try {
                    process.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return -1;
    }

}
