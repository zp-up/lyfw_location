package com.cj.lyfwlocation.config;

/**
 * 任务配置类
 */
public class WorkServiceConfig {

    public static String TAG = "Scheduler - ";

    public static final int MSG_ON_START = 10;

    public static final int MSG_ON_STOP = 20;

    //后台任务执行周期 默认 10s
    public static long period = 10* 1000;

    public static int jobID = 9001;


}
