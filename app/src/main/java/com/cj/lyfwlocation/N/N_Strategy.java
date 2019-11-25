package com.cj.lyfwlocation.N;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.cj.lyfwlocation.N.job.N_WorkService;
import com.cj.lyfwlocation.config.WorkServiceConfig;

/**
 * Package:com.cj.lyfwlocation.N
 */
public class N_Strategy implements I_N_Strategy {

    private Context mContext;
    private JobScheduler mScheduler;
    private JobInfo jobInfo;

    //是否需要循环执行
    private boolean needReschedule = false;

    private N_Strategy() {

    }

    private static class Holder {
        private static final N_Strategy instance = new N_Strategy();
    }

    public static N_Strategy getInstance() {
        return Holder.instance;
    }

    public boolean isNeedReschedule() {
        return needReschedule;
    }

    @Override
    public void work_on_Nougat(Context context) {
        mContext = context;
        needReschedule = true;
        initScheduler();
    }

    @Override
    public void stopWork() {
        needReschedule = false;
        mScheduler.cancelAll();
    }

    public void initScheduler() {

        if(mScheduler==null){
            mScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }

        //mScheduler.cancel(WorkServiceConfig.jobID);

        JobInfo.Builder jBuilder = new JobInfo.Builder(WorkServiceConfig.jobID, new ComponentName(mContext.getPackageName(), N_WorkService.class.getName()));

        //当你的设备重启之后你的任务是否还要继续执行
        jBuilder.setPersisted(true);
        //它表明需要任意一种网络才使得任务可以执行
        jBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        //不需要接通电源
        jBuilder.setRequiresCharging(false);
        //任务只有当用户没有在使用该设备且有一段时间没有使用时才会启动该任务
        jBuilder.setRequiresDeviceIdle(false);
        //通过最小延迟时间绕过最小15分钟间隔限制
        jBuilder.setMinimumLatency(WorkServiceConfig.period);
        //设置最大延迟执行时间
        jBuilder.setOverrideDeadline(WorkServiceConfig.period);
        //设置退避/重试策略
        jBuilder.setBackoffCriteria(3000, JobInfo.BACKOFF_POLICY_LINEAR);

        jobInfo = jBuilder.build();
        mScheduler.schedule(jobInfo);
    }



}
