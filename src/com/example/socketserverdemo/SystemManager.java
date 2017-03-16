
package com.example.socketserverdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

public class SystemManager {
    private AudioManager audioManager;

    private Activity mActivity;

    private static int brightnessNum = 2;

    private static String TAG = "Server_SystemManager";

    public SystemManager() {
        mActivity = MainActivity.mActivity;
        audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);

        getScreenBrightness(mActivity);
        brightnessNum = (int) ((getScreenBrightness(mActivity) * 1.0 / 255) * 5);
        Log.i(TAG, "---> getScreenBrightness(mActivity) : " + getScreenBrightness(mActivity));
        Log.i(TAG, "--->  brightnessNum : " + brightnessNum);
    }

    /**
     * ý����������
     */
    public void CtrlVolume(String ctrlCode) {
        Log.i(TAG, "ctrlCode is " + ctrlCode);
        if (ctrlCode.contains("-")) {
            // ��һ����������������
            // �ڶ������������������ķ���
            // ��������������ѡ�ı�־λ
            Log.i(TAG, "Volume ---");
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                    AudioManager.FLAG_SHOW_UI);
        } else {
            Log.i(TAG, "Volume ++++");
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_SHOW_UI); // ��������
        }
    }

    /**
     * ϵͳ��������
     */
    public void CtrlBrightness(String ctrlCode) {
        if (brightnessNum < 0 || brightnessNum > 5)
            brightnessNum = 2;

        if (ctrlCode.contains("-")) {
            if (brightnessNum > 0)
                brightnessNum--;
        } else {
            if (brightnessNum < 5)
                brightnessNum++;
        }
        // TODO jinglong uncomment this below
        /*
         * Message msg = new Message(); msg.what = SocketService.SET_BRIGHTNESS;
         * msg.obj = (brightnessNum)*1.0f/5;
         * SocketService.mHandler.sendMessage(msg);
         */
    }

    /**
     * ��ȡ��Ļ������
     * 
     * @param activity
     * @return
     */
    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * ����ϵͳ����
     * 
     * @param brightness : 0~1
     */
    public static void setBrightness(Activity activity, float brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = brightness; // �������÷�Χ 0~1.0
        activity.getWindow().setAttributes(lp);

        Settings.System.putInt(activity.getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS, (int) (brightness * 255));
    }

    /**
     * ������������״̬
     * 
     * @param resolver
     * @param brightness : 0~1
     */
    public static void saveBrightness(ContentResolver resolver, float brightness) {
        Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
        android.provider.Settings.System.putInt(resolver, "screen_brightness",
                (int) (brightness * 255));

        resolver.notifyChange(uri, null);
    }

    /**
     * ִ������
     * 
     * @param cmd
     * @return
     */
    public static boolean execute(String cmd) {
        boolean isSuccess = false;
        String s="\n";
        try {
             Process proc = Runtime.getRuntime().exec("su"); // �豸��Ҫӵ��suȨ��
             //Process proc = Runtime.getRuntime().exec(cmd); // �豸��Ҫӵ��suȨ��
             proc.getOutputStream().write(cmd.getBytes());
             proc.getOutputStream().write(cmd.getBytes());
             proc.getOutputStream().flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                s += line + "\n";
            }
            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
            isSuccess = false;
        }
        Log.i(TAG, "---> cmd : " + cmd + ", isSuccess : " + isSuccess+"\n return is "+s);
        return isSuccess;
        /*boolean isSuccess = false;
        String s = "/n";  
        try {  
            Log.i(TAG,"cmd is "+cmd);
            Process p = Runtime.getRuntime().exec(cmd);  
            BufferedReader in = new BufferedReader(  
                                new InputStreamReader(p.getInputStream()));  
            String line = null;  
            while ((line = in.readLine()) != null) {  
                s += line + " ";                 
            }  
            Log.i(TAG,"do_exec s is "+s);
            isSuccess=true;
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            isSuccess=false;
        }  
        return isSuccess;*/
    }

}
