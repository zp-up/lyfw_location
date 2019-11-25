package com.cj.lyfwlocation.L_M;

import android.content.Context;

import com.cj.lyfwlocation.core.IBaseStrategy;

/**
 * Package:com.cj.lyfwlocation
 */
public interface I_L_M_Strategy extends IBaseStrategy {

    //5.0
    void work_on_Lollipop(Context context);

    //6.0
    void work_on_Marshmallow(Context context);

    void stopWork();

}
