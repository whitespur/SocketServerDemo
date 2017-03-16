
package com.example.socketserverdemo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView toggleTextView;
    private TextView iptTextView;
    private static TextView statusTextView;

    private Button toggleButton;
    private Button getIPButton;
    private Button StatusButton;

    private String ipStr = "0.0.0.0";
    private int ip = 0;
    private WifiAdmin wifi; 

    private int status = 0;//0 off,1 on 
    private Context mContext;
    private static String TAG = "Server_MainActivity";
    public static final int CONN_SUCCESS = 1;
    public static final int CONN_ERROR = 0;
    public static MainActivity mActivity;  
    public static SystemManager systemManager = null;      

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggleTextView = (TextView) findViewById(R.id.toggleTV);
        iptTextView = (TextView) findViewById(R.id.ipTV);
        statusTextView = (TextView) findViewById(R.id.statusTV);
        toggleButton = (Button) findViewById(R.id.server_toggle);
        getIPButton = (Button) findViewById(R.id.getIP);
        StatusButton= (Button) findViewById(R.id.display_status);
        mContext = getApplicationContext();
        mActivity = this;
        systemManager = new SystemManager();  
        
        wifi = new WifiAdmin(mContext);
        ip = wifi.GetIPAddress();
        toggleButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent mIntent = new Intent(mContext, SocketService.class);
                if(status == 0){
                    Log.i(TAG,"start Service");
                    startService(mIntent);
                    status = 1;
                    toggleTextView.setText("ON");
                }else if (status == 1){
                    Log.i(TAG,"stop Service");
                    stopService(mIntent);
                    status = 0;
                    toggleTextView.setText("OFF");
                }
                
            }
        });
        getIPButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
                ip = wifi.GetIPAddress();
                Log.i(TAG, "ip is "+ip);
                ipStr = wifi.longToIP(ip);
                Log.i(TAG, "ipstr is "+ipStr);
                ipStr=wifi.getLocalIPAddress();
                Log.i(TAG, "use getLocalIPAddress ipstr is "+ipStr);
                iptTextView.setText(ipStr);
                
            }
        });
        StatusButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //do_exec("input keyevent 3 \n");
                SystemManager.execute("input keyevent 3\n");
                
            }
        });
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    public static Handler mHandler = new Handler(){
       
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String statusStr = "";
            switch (msg.what) {
                case CONN_SUCCESS:
                    
                    int num = 1;
                    for(Socket socket:SocketService.socketList){
                        statusStr+="-----client"+num+":"+socket.toString();
                    }
                    Log.i(TAG, "statusStr is "+statusStr);
                    statusTextView.setText(statusStr);
                    break;
                case CONN_ERROR:
                    statusStr = "connect error";
                    statusTextView.setText(statusStr);
                    break;

                default:
                    break;
            }
        };
    };
    String do_exec(String cmd) {  
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
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        
        return cmd;       
    }  
}
