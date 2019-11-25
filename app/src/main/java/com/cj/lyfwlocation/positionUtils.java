package com.cj.lyfwlocation;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.cj.lyfwlocation.core.WorkCenter;
import com.tencent.mmkv.MMKV;

public class positionUtils {

    public Context context;


    public positionUtils(Context context){
        this.context=context;
    }

    @JavascriptInterface
    public  void  addPosition(final String userId){
        MMKV kv = MMKV.defaultMMKV();
        kv.encode("userId",userId);
        WorkCenter.getInstance().startWork(context);
    }

    @JavascriptInterface
    public  void  endPostition(){
        WorkCenter.getInstance().stopWork();
    }

}
