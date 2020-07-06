package com.wisdomin.studentcard.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.wisdomin.studentcard.util.LogUtil;
import com.wisdomin.studentcard.util.UIUtil;
import com.wisdomin.studentcard.util.update.FileManager;
import com.wisdomin.studentcard.util.update.IntentUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;



/**
 * 下载APK的后台服务
 */
public class APKDownloadService extends Service {

    private Context mContext;
    private String downloadUrl; // 下载地址
    private String apkName; // 对应APK名称
    // 记录当前正在执行的任务
    private HashMap<Integer, String> taskRecord = new HashMap<Integer, String>();
    private UpdateDataReceiver mUpdateDataReceiver;
    private DownloadAPKTask task;
    public static final String channelId = "channel_1";
    public static final String channelName = "channel_name_1";
    private Notification.Builder builder26;
    private PendingIntent pendingIntent;
    private NotificationManager notificationManager;
    private String STOPSERVICE = "com.wisdomin.studentcard.stopservice";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.showLog("APKDownloadService", "APKDownloadService 服务已启动");
        this.mContext = this;
        mUpdateDataReceiver = new UpdateDataReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(STOPSERVICE);
        UIUtil.getLocalBroadcastManager(this).registerReceiver(mUpdateDataReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.downloadUrl = intent.getStringExtra("downloadUrl");
        this.apkName = intent.getStringExtra("apkName");
        Set<Integer> keySet = taskRecord.keySet();
        for (Integer key : keySet) {
            if (taskRecord.get(key).equals(downloadUrl)) {
                return super.onStartCommand(intent, flags, startId);
            }
        }
        taskRecord.put(startId, downloadUrl);
        // 启动新工作线程下载APK
        task = new DownloadAPKTask(this.downloadUrl, this.apkName, startId);
        task.execute();
        // 服务被中止后，自动重新启动
        return START_REDELIVER_INTENT;
    }

    private class UpdateDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            String flag = intent.getAction();
            if (flag.equals(STOPSERVICE)) {
                onDestroy();
            }
        }
    }


    /**
     * 下载最新APK任务
     *
     * @author shizheng
     */
    private class DownloadAPKTask extends AsyncTask<Void, Integer, Boolean> {

        private static final String TAG = "DownloadAPKTask";
        private String downloadUrl;
        private String apkName; // apk名称
        private int taskId; // 当前下载任务的标示符
        private long apkSize = 0; // APK的大小
        private int downloadSize = 0; // 已经下载了的大小
        private String savePath; // APK存储路径
        private FileOutputStream outputStream;
        private File apkFile;
        // 通知栏跳转Intent
        private Intent intent;

        // 通知栏

        private Notification notification;
        private RemoteViews remoteViews;
        private boolean initialize = false;
        private Uri uri;
        private File mfile;

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
//            publishProgress(0);
        }

        /**
         * @param downloadUrl 下载对应的URL
         * @param apkName     apk名称 （备注：需带后缀名）
         * @param taskId      当前下载任务的标示符
         */
        public DownloadAPKTask(String downloadUrl, String apkName, int taskId) {
            super();
            this.downloadUrl = downloadUrl;
            this.apkName = apkName;
            this.taskId = taskId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                if (apkName != null && !apkName.equals("")) {
                    if (FileManager.hasSDCard()) {
                        // 有SD卡，APK保存在SD卡上
                        String gg = "";

                        if (apkName.contains("-")) {
                            apkName.replace("-", "_");
                        }

                        if (apkName.contains("apk"))
                            gg = apkName;
                        else
                            gg = apkName + ".apk";
                        savePath = FileManager.getFileCachePath(mContext) + gg;
                        LogUtil.e("savePath==="+savePath);
                        apkFile = new File(savePath);
                        // 删除历史APK
                        if (apkFile.exists()) {
                            apkFile.delete();
                        }
                        // 判断文件是否存在
                        if (!apkFile.exists())
                            apkFile.createNewFile();

                        outputStream = new FileOutputStream(apkFile);
                    } else {
                        // 无SD卡，APK保存在手机内存
                        String gg = "";
                        if (apkName.contains("-")) {
                            apkName.replace("-", "_");
                        }

                        if (apkName.contains("apk"))
                            gg = apkName;
                        else
                            gg = apkName + ".apk";
                        apkFile = mContext.getFileStreamPath(gg);
                        savePath = apkFile.getPath();

                        // 删除历史APK
                        if (apkFile.exists()) {
                            apkFile.delete();
                        }
                        // 判断文件是否存在
                        if (!apkFile.exists())
                            apkFile.createNewFile();

                        // 获取对应的输入流，Note:注意添加相应权限
                        outputStream = openFileOutput(gg, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
                    }
                } else {
                    if (FileManager.hasSDCard()) {
                        // 有SD卡，APK保存在SD卡上
                        savePath = FileManager.getFileCachePath(mContext) + downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                        apkFile = new File(savePath);
                        // 删除历史APK
                        if (apkFile.exists()) {
                            apkFile.delete();
                        }
                        outputStream = new FileOutputStream(apkFile);
                        if (outputStream == null)
                            return;
                    } else {
                        // 无SD卡，APK保存在手机内存
                        apkFile = mContext.getFileStreamPath(downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1));
                        savePath = apkFile.getPath();

                        // 删除历史APK
                        if (apkFile.exists()) {
                            apkFile.delete();
                        }
                        // 获取对应的输入流，Note:注意添加相应权限
                        outputStream = openFileOutput(downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1), Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
                    }
                }

                if (outputStream == null) {
//                    LogUtil.showLog(TAG, "outputStream == null");
                    initialize = false;
                }
                prepareNotification(taskId);
                initialize = true;
            } catch (Exception e) {
                initialize = false;
                LogUtil.showLog(TAG, "下载" + apkName + "初始化错误");
                e.printStackTrace();
            }

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (!initialize) {
                return false;
            }
//            LogUtil.showLog(TAG, "开始下载  " + apkName + "应用");
            try {
                long downloadSize = downloadAPK();
//                notificationManager.cancel(taskId);// 去除通知栏通知
                if (downloadSize == apkSize) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.showLog(TAG, "下载出现" + apkName + "出现异常 ");
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {

                LogUtil.e("下载完毕");

                if (downloadUrl.substring(downloadUrl.lastIndexOf("/")).contains(".apk")) {
                    try{
                        installApk(mContext, apkFile);
//            //安装apk
//                    OSUtils.installApk(mContext, apkFile.getAbsolutePath());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    intent = IntentUtil.openFile(apkFile.getPath());
                    startActivity(intent);
                }
            } else {
                LogUtil.e( "下载失败，请检查网络连接后重试");
            }

            taskRecord.remove(this.taskId); // 去除已经完成的记录

//            TimerTask deleteTask = new TimerTask() {
//                @Override
//                public void run() {
//                    if (apkFile != null && apkFile.exists()) {
//                        apkFile.delete();
//                        stopSelf(taskId); // 关闭服务
//                    }
//                }
//            };
//            Timer timer = new Timer();
//            timer.schedule(deleteTask, 60 * 60 * 1000); // 60分钟之后删除下载的APK


        }

        /**
         * 更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            LogUtil.e("============当前进度：" + values[0] + "%");
//            LogUtil.showLog(TAG, "onProgressUpdate() 当前进度 = " + values[0] + "%");
//            if (remoteViews == null)
//                return;
//            remoteViews.setProgressBar(R.id.progress_download, (int) apkSize, downloadSize, false);
//            if (values[0] == -1) {
//                remoteViews.setTextViewText(R.id.progress_info, "网络异常," + apkName + " 下载失败");
//                notification.flags = Notification.FLAG_AUTO_CANCEL;
//            } else {
//                remoteViews.setTextViewText(R.id.progress_info, "正在下载 :" + apkName + "，当前进度：" + values[0] + "%");
//            }
//
//            notification.contentView = remoteViews;
//
//            if (values[0] >= 100) {
//                notification.flags = Notification.FLAG_AUTO_CANCEL;
//                notification.defaults = Notification.DEFAULT_SOUND; // 下载完设置声音提示
//                remoteViews.setTextViewText(R.id.progress_info, "  " + apkName + "已下载完成,请点击安装!");
//            }
//            notificationManager.notify(this.taskId, notification);
        }

        private long downloadAPK() throws Exception {
            boolean isHttps = false;
            if (downloadUrl.contains("https://")) {
                isHttps = true;
            } else {
                isHttps = false;
            }
            if (isHttps) {
                trustEveryone();
            }
            HttpURLConnection httpConnection = null;
            InputStream is = null;
            try {
                URL url = new URL(downloadUrl);
                httpConnection = (HttpURLConnection) url.openConnection();
                if (isHttps) {
                    httpConnection = (HttpsURLConnection) url.openConnection();
                } else {
                    httpConnection = (HttpURLConnection) url.openConnection();
                }
                httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
                httpConnection.setRequestProperty("Accept-Encoding", "identity");
                if (downloadSize > 0) {
                    httpConnection.setRequestProperty("RANGE", "bytes=" + downloadSize + "-");
                }
                httpConnection.setConnectTimeout(10000);
                httpConnection.setReadTimeout(20000);

                apkSize = httpConnection.getContentLength();

                if (httpConnection.getResponseCode() == 404) {
                    throw new Exception("fail!");
                }
                is = httpConnection.getInputStream();
                byte buffer[] = new byte[4096]; // 5Kb的缓存
                int readLength = -1;
                int progress = 0;
                int temp = 0;
                double percentage = 0;
                while ((readLength = is.read(buffer)) != -1) {
//                    LogUtil.showLog(TAG, "readLength====:" + readLength);
                    outputStream.write(buffer, 0, readLength);
                    downloadSize += readLength;
//                    LogUtil.showLog(TAG, "downloadSize====:" + downloadSize);
//                    LogUtil.showLog(TAG, "apkSize=======:" + apkSize);
                    //此处因为超出int类型值范围【 -2147483648～2147483647】,故计算出的值为负数,先转double，取余数，然后乘以100
                    percentage = (double) downloadSize / (double) apkSize * 100;
                    progress = (int) (percentage); // 进度百分比
//                    LogUtil.showLog(TAG, "progress====:" + progress);
                    if (progress >= temp || progress == 100) {
                        temp += 3;
//                        LogUtil.showLog(TAG, "百分比====:" + temp);
                        publishProgress(progress); // 每下载超过百分之三更新一下进度，防止更新过于频繁
                    }
                }
                buffer = null; // 释放缓存内存资源
            } catch (Exception e) {
                e.printStackTrace();
                publishProgress(-1); // 提示下载失败
                throw e;
            } finally {
                if (httpConnection != null) {
                    httpConnection.disconnect();
                    httpConnection = null;
                }
                if (is != null) {
                    is.close();
                    is = null;
                }
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                    outputStream = null;
                }
            }
            if (apkFile.exists() && apkFile.length() == apkSize) {
//                LogUtil.showLog(TAG, apkName + "下载完毕");
                return apkSize;
            } else {
                publishProgress(-1); // 提示下载失败
                return -1;
            }
        }

        /**
         * 安装APK
         *
         * @param apkFile 待安装文件
         */
        public void installApk(Context context, final File apkFile) {
            if (apkFile != null && apkFile.exists()) {
                //引力波的设备安装的方式
                Intent intent = new Intent();
                intent.setAction("dw.action.apkauto.install");
                //key  apkFilePath
                // value 下载的文件路径
                intent.putExtra("apkFilePath", apkFile.getPath());
                //key Intent.EXTRA_INSTALLER_PACKAGE_NAME
                //value 包名
                intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME,"com.wisdomin.studentcard");
                sendBroadcast(intent);

//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setDataAndType(Uri.fromFile(apkFile),
//                            "application/vnd.android.package-archive");
//                context.startActivity(intent);
            } else {
                LogUtil.showLog(TAG, "安装文件不存在");
            }
        }

        private void trustEveryone() {
            try {
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null,
                        new X509TrustManager[]{new X509TrustManager() {
                            public void checkClientTrusted(X509Certificate[] chain,
                                                           String authType) throws CertificateException {
                            }

                            public void checkServerTrusted(X509Certificate[] chain,
                                                           String authType) throws CertificateException {
                            }

                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }
                        }
                        }, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(
                        context.getSocketFactory());
            } catch (Exception e) { // should never happen
                e.printStackTrace();
            }
        }

        /**
         * 准备通知
         */
        private void prepareNotification(int notification_id) {
////            LogUtil.showLog(TAG, "prepareNotification()");
//            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                notification = getChannelNotification("和校园", "正在下载 :" + apkName).build();
//            } else {
//                notification = new Notification(R.drawable.download_icon, "正在下载 :" + apkName, System.currentTimeMillis());
////                notification = new NotificationCompat.Builder(mContext,"234").build();
//            }
//            // 实例化一个remoteviews对象给notification以达到自定义notification的效果
//            remoteViews = new RemoteViews(getPackageName(), R.layout.download_apk);
//
//            // 设置进度条，最大值 为100,当前值为0，最后一个参数为true时显示条纹
//            remoteViews.setProgressBar(R.id.progress_download, 100, 0, false);
//            remoteViews.setTextViewText(R.id.progress_info, "正在下载 :" + apkName + "，进度:" + 0 + "%");
//
//            notification.contentView = remoteViews;
//
//            // TODO 下载完成点击去安装
//            intent = IntentUtil.getInstallAPKIntent(uri);
//            pendingIntent = PendingIntent.getActivity(APKDownloadService.this, 0, intent, 0);
//            notification.contentIntent = pendingIntent;
//            notificationManager.notify(notification_id, notification);
//            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            remoteViews = new RemoteViews(getPackageName(), R.layout.download_apk);
//
//            // 设置进度条，最大值 为100,当前值为0，最后一个参数为true时显示条纹
//            remoteViews.setProgressBar(R.id.progress_download, 100, 0, false);
//            remoteViews.setTextViewText(R.id.progress_info, "正在下载 :" + apkName + "，进度:" + 0 + "%");
//            intent = IntentUtil.getInstallAPKIntent(mContext, apkFile);
//            pendingIntent = PendingIntent.getActivity(APKDownloadService.this, 0, intent, 0);
//
//            //--------
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext
//                    , NotifyUtil.getChannelId("down_apk_notice", "下载通知"
//                    , NotificationManager.IMPORTANCE_DEFAULT, notificationManager));
//            builder.setSmallIcon(R.mipmap.ic_launcher)
//                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.download_icon))
//                    .setDefaults(Notification.DEFAULT_ALL)
//                    .setContent(remoteViews)
//                    .setAutoCancel(true)
//                    .setContentIntent(pendingIntent);
//
//            notification = builder.build();
//            notificationManager.notify(notification_id, notification);

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.showLog("APKDownloadService", "APKDownloadService 服务已关闭");
        task.onCancelled();
    }


}
