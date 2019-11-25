package com.cj.lyfwlocation.L_M.job;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import androidx.annotation.Nullable;
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
 * 5.0 、 6.0 后台工作线程
 */
public class L_M_WorkService extends JobService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        sendMessage(WorkServiceConfig.MSG_ON_START, jobParameters);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                LocationCenter.getInstance().startLocate(new ILocateResultCallback() {
                    @Override
                    public void onLocation(LocationInfoEntity info) {
                        String address = "";
                        if (info != null) {
                            address = info.getAddress();
                        }
                        Log.e(WorkServiceConfig.TAG, "address = " + address);
                        LocationCenter.getInstance().stopLocate();
                        //todo 上传坐标 start

                        /**上传坐标的逻辑一定要写在这里**/
                        postDataHttp(String.valueOf(info.getLatitude()),String.valueOf(info.getLongitude()));

                        //todo 上传坐标 stop
                        jobFinished(jobParameters, false);
                    }
                });

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


}
