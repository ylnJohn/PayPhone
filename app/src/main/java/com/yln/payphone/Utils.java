package com.yln.payphone;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.security.MessageDigest;

/**
 * Created by linnan.yao on 2017/9/14.
 */

public class Utils {

    public static void showToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static ProgressDialog showProgressDialog(Context context, String msg) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.setMessage(msg);
        dialog.setProgress(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }

    public static String getIPAddress(Context context) {
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }

    private static String intToIp(int i) {

        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    public static String md5(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
}
