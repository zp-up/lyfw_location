package com.cj.lyfwlocation.N.job;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.cj.lyfwlocation.MainActivity;
import com.cj.lyfwlocation.N.N_Strategy;
import com.cj.lyfwlocation.R;
import com.cj.lyfwlocation.config.WorkServiceConfig;
import com.cj.lyfwlocation.locate.ILocateResultCallback;
import com.cj.lyfwlocation.locate.LocationCenter;
import com.cj.lyfwlocation.locate.LocationInfoEntity;
import com.tencent.mmkv.MMKV;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Package:com.cj.lyfwlocation.N.job
 */
public class N_WorkService extends JobService {


    @Override
    public void onCreate() {
        super.onCreate();
        //showNotify();

    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        sendMessage(WorkServiceConfig.MSG_ON_START, jobParameters);

        LocationCenter.getInstance().startLocate(new ILocateResultCallback() {
            @Override
            public void onLocation(LocationInfoEntity info) {

                if (info != null) {
                    String address = info.getAddress();
                    String country = info.getCountry();
                    double latitude = info.getLatitude();
                    double longitude = info.getLongitude();

                    Log.e(WorkServiceConfig.TAG,"country ="+country+"  address ="+address+"  latitude = "+latitude+"  longitude ="+longitude);
                }
                LocationCenter.getInstance().stopLocate();

                //todo 上传坐标 start

                /**上传坐标的逻辑一定要写在这里**/
                postDataHttp(String.valueOf(info.getLatitude()),String.valueOf(info.getLongitude()));
                //todo 上传坐标 stop

                //重启service
                if(N_Strategy.getInstance().isNeedReschedule()){
                    N_Strategy.getInstance().initScheduler();
                }

                jobFinished(jobParameters,false);
            }
        });


        return true;
    }
    private static  void  postDataHttp(String lat,String lng){
        try {
            MMKV kv = MMKV.defaultMMKV();
            String userId=kv.decodeString("userId");

            OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
            FormBody.Builder body=new FormBody.Builder();
            body.add("userId",userId);
            body.add("lat",lat);
            body.add("lng",lng);
            Request request = new Request.Builder()
                    .url("http://t.lyfw.tjsjnet.com/reserve/orders/position.htm").post(body.build()) .build();//创建一个Request对象
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        Log.e("lyfw","调用成功返回值"+response.body().string());
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        sendMessage(WorkServiceConfig.MSG_ON_STOP, jobParameters);
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler mJobHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(final Message msg) {
            //JobParameters p = (JobParameters) msg.obj;
            int w = msg.what;

            switch (w) {
                case WorkServiceConfig.MSG_ON_START:
                    Log.e(WorkServiceConfig.TAG, "service on start");
                    break;
                case WorkServiceConfig.MSG_ON_STOP:
                    Log.e(WorkServiceConfig.TAG, "service on stop");
                    break;
            }

            return true;
        }

    });


    private void sendMessage(int what, @Nullable Object params) {
        Message m = Message.obtain();
        m.what = what;
        m.obj = params;
        mJobHandler.sendMessage(m);
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
                .setWhen(System.currentTimeMillis());

        //调用这个方法把服务设置成前台服务
        //8.0之上必须设置channel
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("back_ground_locate_channel","channel_name", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId("back_ground_locate_channel");
        }

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        //8.0后禁止在后台启动service
//        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
//            startForegroundService(nfIntent);
//        }else {
//            startForeground(110, notification);
//        }

        startForeground(110, notification);

    }




}
