package com.helloword.lgy.mobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;

public class GPSService extends Service {
    private LocationManager lm;
    private MyLocationListenter listenter;
    public GPSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        listenter = new MyLocationListenter();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Criteria criteria=new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //因为有时候场景不一样，精确度不一样，选择的位置提供者不一样，所以要选择做好的位置提供者
        String provider=lm.getBestProvider(criteria,true);//获取最好的位置提供者  第一个参数是条件，可设置精确度等。第二个参数是位置提供者是否可用

        lm.requestLocationUpdates(provider, 0, 0, listenter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.removeUpdates(listenter);
        listenter=null;
    }
    class MyLocationListenter implements LocationListener {

        //位置改变时调用
        @Override
        public void onLocationChanged(Location location) {
            String longitdue= "j:"+location.getLongitude()+"\n";
            String latitudue="w:"+ location.getLatitude()+"\n";
            String accuray="a:"+ location.getAccuracy()+"\n";
            //把标准位置转换为火星坐标
            InputStream is;
            try {
                is=getAssets().open("axisoffset.dat");
                ModifyOffset offset=ModifyOffset.getInstance(is);
                offset.s2c(new PointDouble(location.getLongitude(),
                        location.getLatitude()));
                longitdue="j:"+offset.X+"\n";
                latitudue="w:"+offset.Y+"\n";
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


            //把最后一次位置保存到sp
            SharedPreferences sp=getSharedPreferences("config",MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("lastlocation",longitdue+latitudue+accuray);
            editor.commit();



        }

        //开启关闭手机位置服务状态时调用
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        //位置服务提供者可用时调用
        @Override
        public void onProviderEnabled(String provider) {

        }
        //位置服务提供者不可用时调用
        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
