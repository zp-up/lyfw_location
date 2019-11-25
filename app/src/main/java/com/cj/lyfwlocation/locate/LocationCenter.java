package com.cj.lyfwlocation.locate;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cj.lyfwlocation.config.WorkServiceConfig;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 高德定位工具类
 */
public class LocationCenter implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static Context context;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private ILocateResultCallback locationCallback;

    //google
    private GoogleApiClient googleApiClient;

    private LocationCenter() {
    }

    private static class Holder {
        private static final LocationCenter instance = new LocationCenter();
    }

    public static LocationCenter getInstance() {
        return Holder.instance;
    }

    /**
     * this method must invoke firstly
     *
     * @param con
     */
    public void register(Context con) {
        if (context == null) {
            context = con;
        }

        initLocation();
    }

    //初始化定位参数
    private void initLocation() {

        if (context == null) {
            throw new RuntimeException(LocationCenter.class.getName() + "尚未初始化");
        }

        mLocationClient = new AMapLocationClient(context);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setNeedAddress(true);
        //设置定位监听
        mLocationClient.setLocationListener(aMapLocationListener);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置只定位一次
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setWifiScan(true);
        mLocationOption.setGpsFirst(false);
        //设置定位场景倾向于签到场景，单次定位即可
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(5000);
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

            if (aMapLocation == null) {
                dispatchLocationInfo(null);
                return;
            }

            if (aMapLocation.getErrorCode() != 0) {
                dispatchLocationInfo(null);
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e(WorkServiceConfig.TAG + "定位错误码：", aMapLocation.getErrorCode() + "");
                Log.e(WorkServiceConfig.TAG + "定位错误信息：", aMapLocation.getErrorInfo());
                return;
            }

            /**定位成功**/
            LocationInfoEntity entity = new LocationInfoEntity();
            entity.setLocationType(aMapLocation.getLocationType());
            entity.setLatitude(aMapLocation.getLatitude());
            entity.setLongitude(aMapLocation.getLongitude());
            entity.setAccuracy(aMapLocation.getAccuracy());
            entity.setAddress(aMapLocation.getAddress());
            entity.setCountry(aMapLocation.getCountry());
            entity.setProvince(aMapLocation.getProvince());
            entity.setCity(aMapLocation.getCity());
            entity.setDistrict(aMapLocation.getDistrict());
            entity.setStreet(aMapLocation.getStreet());
            entity.setStreetNum(aMapLocation.getStreetNum());//街道门牌号信息
            entity.setCityCode(aMapLocation.getCityCode());//城市编码
            entity.setAdCode(aMapLocation.getAdCode());//地区编码
            entity.setAoiName(aMapLocation.getAoiName());//获取当前定位点的AOI信息
            entity.setBuildingId(aMapLocation.getBuildingId());//获取当前室内定位的建筑物Id
            entity.setFloor(aMapLocation.getFloor());//获取当前室内定位的楼层
            entity.setGpsAccuracyStatus(aMapLocation.getGpsAccuracyStatus());//获取GPS的当前状态
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//获取定位时间
            Date date = new Date(aMapLocation.getTime());
            entity.setDate(df.format(date));

            dispatchLocationInfo(entity);

        }
    };

    private void dispatchLocationInfo(LocationInfoEntity location) {
        if (locationCallback != null) {
            locationCallback.onLocation(location);
        }
    }


    //开始定位
    public synchronized void startLocate(ILocateResultCallback locateResultCallback) {
        this.locationCallback = locateResultCallback;
        mLocationClient.startLocation();
        //Google();
    }

    //停止定位
    public void stopLocate() {
        if (mLocationClient.isStarted()) {
            mLocationClient.stopLocation();
        }
    }


    private void Google() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this) //添加结果监听，如果连接成功会执行监听器中的onConnected方法。
                .addOnConnectionFailedListener(this) //监听失败结果
                .addApi(LocationServices.API) //表明我们要获取地址
                .build();

        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        switch (errorCode) {
            case ConnectionResult.SUCCESS:
                googleApiClient.connect();
                break;
            default:
                GooglePlayServicesUtil.getErrorDialog(errorCode, (Activity) context, 999).show();
        }


    }


    //连接google服务成功
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (bundle == null || location == null) {
            //google play不可用

            return;
        }

        double la = location.getLatitude();
        double lg = location.getLongitude();
        Toast.makeText(context, "la = " + la + "  lg=" + lg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {
        int iii = i;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult != null) {

        }
    }

}
