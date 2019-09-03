package com.xinlan.phoneinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mAndroidIdText;
    private Button mCopyAndroidBtn;

    private TextView mImeiText;
    private Button mCopyImeiBtn;

    private TextView mMacText;
    private Button mCopyMacBtn;

    private String mAndroidId;
    private String mIMEI;
    private String mMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewGroup s;
        mAndroidIdText = findViewById(R.id.androdid);
        mCopyAndroidBtn = findViewById(R.id.copy_androidid);
        mCopyAndroidBtn.setOnClickListener(this);


        mImeiText = findViewById(R.id.imei_text);
        mCopyImeiBtn = findViewById(R.id.copy_imei);
        mCopyImeiBtn.setOnClickListener(this);

        mMacText = findViewById(R.id.mac_text);
        mCopyMacBtn = findViewById(R.id.copy_mac);
        mCopyMacBtn.setOnClickListener(this);

        getPhoneInfo();
        Toast.makeText(this, "是否开启了开发者模式" + Utils.isDevMode(this), Toast.LENGTH_SHORT).show();

        System.out.println("WIFI ; " + Utils.getWIFISSID(this));
        System.out.println("macAddress ; " + Utils.macAddr(this));
    }


    private void getPhoneInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
            return;
        }

        doGetPhoneInfo();
    }

    @SuppressLint("MissingPermission")
    private void doGetPhoneInfo() {
        mAndroidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        mAndroidIdText.setText(mAndroidId);

        mIMEI = getMachineImei(this);
        mImeiText.setText(mIMEI);

        mMac = getMacAddr();
        mMacText.setText(mMac);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100)
            doGetPhoneInfo();
    }

    @SuppressLint("MissingPermission")
    public static String getMachineImei(Context context) {
        String imei = null;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei = telephonyManager.getImei();
        } else {
            imei = telephonyManager.getDeviceId();
        }

        if (TextUtils.isEmpty(imei)) {//imei没有获得
            imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return imei;
    }

    private static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copy_androidid:
                Utils.copy(mAndroidId, this);
                Toast.makeText(this, "androidid 拷贝到剪切板成功!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.copy_imei:
                Utils.copy(mIMEI, this);
                Toast.makeText(this, "IMEI 拷贝到剪切板成功!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.copy_mac:
                Utils.copy(mMac, this);
                Toast.makeText(this, "mac 拷贝到剪切板成功!", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
