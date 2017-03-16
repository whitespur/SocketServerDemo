package com.example.socketserverdemo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.net.http.SslCertificate;
import android.os.IBinder;
import android.util.Log;

public class SocketService extends Service{
    private ServerSocket mServerSocket;
    private int port=58888;
    private String TAG="Server_Service";
    public static ArrayList<Socket> socketList = new ArrayList<Socket>();  
    private boolean quit = false; 
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i(TAG, "SocketService onCreate");
        if(mThread!=null){
            Log.i(TAG,"mThread.start");
            mThread.start();
        }
           
        
    }
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            quit = true;
            socketList.clear();
            mServerSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    Thread mThread = new Thread(){
        public void run(){
               try {
                  
                mServerSocket = new ServerSocket(port);
                Log.i(TAG,"mThread run quit is  "+quit);
                while (!quit) {
                    Socket s1 = mServerSocket.accept();
                    Log.i(TAG,"socket s1 is "+s1.toString());
                    socketList.add(s1);
                    //TODO new ServerThread to read message from server
                    new Thread(new ServerThread(s1)).start();  
                    MainActivity.mHandler.sendEmptyMessage(MainActivity.CONN_SUCCESS);  
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
               
        }
    };
    
}