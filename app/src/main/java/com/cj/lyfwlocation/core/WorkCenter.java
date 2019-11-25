package com.cj.lyfwlocation.core;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.cj.lyfwlocation.L_M.L_M_WorkStrategy;
import com.cj.lyfwlocation.N.N_Strategy;
import com.cj.lyfwlocation.locate.LocationForegroundService;

/**
 * Package:com.cj.lyfwlocation
 */
public class WorkCenter {

    private int v = Build.VERSION.SDK_INT;

    private WorkCenter() {
    }

    private static class Holder {
        private static final WorkCenter instance = new WorkCenter();
    }

    public static WorkCenter getInstance() {
        return Holder.instance;
    }

    public void startWork(Context context) {

        Intent intent = new Intent(context, LocationForegroundService.class);
        //启动前台服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

        //5.x 21、22
        if (v == Build.VERSION_CODES.LOLLIPOP || v == Build.VERSION_CODES.LOLLIPOP_MR1) {
            L_M_WorkStrategy.getInstance().work_on_Lollipop(context);
            return;
        }

        //6.0 23
        if (v == Build.VERSION_CODES.M) {
            L_M_WorkStrategy.getInstance().work_on_Marshmallow(context);
            return;
        }

        //7.x 24、25
        if (v == Build.VERSION_CODES.N || v == Build.VERSION_CODES.N_MR1) {
            N_Strategy.getInstance().work_on_Nougat(context);
            return;
        }

        //8.x 26、27
        if (v == Build.VERSION_CODES.O || v == Build.VERSION_CODES.O_MR1) {
            N_Strategy.getInstance().work_on_Nougat(context);
            return;
        }

        //9.0 28
        if (v == Build.VERSION_CODES.P) {
            N_Strategy.getInstance().work_on_Nougat(context);
            return;
        }

        //10.0 29
        if (v == Build.VERSION_CODES.Q) {
            N_Strategy.getInstance().work_on_Nougat(context);
            return;
        }


    }

    public void stopWork() {
        //5.x 21、22
        if (v == Build.VERSION_CODES.LOLLIPOP || v == Build.VERSION_CODES.LOLLIPOP_MR1) {
            L_M_WorkStrategy.getInstance().stopWork();
            return;
        }

        //6.0 23
        if (v == Build.VERSION_CODES.M) {
            L_M_WorkStrategy.getInstance().stopWork();
            return;
        }

        //7.x 24、25
        if (v == Build.VERSION_CODES.N || v == Build.VERSION_CODES.N_MR1) {
            N_Strategy.getInstance().stopWork();
            return;
        }

        //8.x 26、27
        if (v == Build.VERSION_CODES.O || v == Build.VERSION_CODES.O_MR1) {
            N_Strategy.getInstance().stopWork();
            return;
        }

        //9.0 28
        if (v == Build.VERSION_CODES.P) {
            N_Strategy.getInstance().stopWork();
            return;
        }

        //10.0 29
        if (v == Build.VERSION_CODES.Q) {
            N_Strategy.getInstance().stopWork();
            return;
        }


    }


}
