package com.cj.lyfwlocation.locate;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.cj.lyfwlocation.MainActivity;
import com.cj.lyfwlocation.R;

import static com.cj.lyfwlocation.config.WorkServiceConfig.TAG;

/**
 * 前台定位service
 */

public class LocationForegroundService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //生成通知栏
        showNotify();

       return START_STICKY;

    }


    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    //显示通知栏
    public void showNotify() {
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Intent nfIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("正在后台定位")
                .setContentText("定位进行中")
                .setWhen(System.currentTimeMillis()).
                setAutoCancel(false);

        //调用这个方法把服务设置成前台服务
        //8.0之上必须设置channel
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("back_ground_locate_channel","channel_name", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);//不显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId("back_ground_locate_channel");
        }

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE|Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
        //8.0后禁止在后台启动service
//        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
//            startForegroundService(nfIntent);
//        }else {
//            startForeground(110, notification);
//        }
        startForeground(110, notification);

    }


    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        LocationForegroundService getService() {
            return LocationForegroundService.this;
        }
    }





}
