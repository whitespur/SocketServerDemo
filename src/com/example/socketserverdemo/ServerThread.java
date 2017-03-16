package com.example.socketserverdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

/**
 * 负责处理每个线程通信的线程类
 *
 */
public class ServerThread implements Runnable 
{
    //定义当前线程所处理的Socket
    Socket s = null;
    //该线程所处理的Socket所对应的输入流
    BufferedReader br = null;
    private static String TAG ="Server_Thread";

     /**
     * 连接请求
     */
    public static final String OPERATION_CONNECT = "CN";
    
    /**
     * 方向
     */
    public static final String OPERATION_DIRECTION_TOP = "DT";
    public static final String OPERATION_DIRECTION_BOTTOM = "DB";
    public static final String OPERATION_DIRECTION_LEFT = "DL";
    public static final String OPERATION_DIRECTION_RIGHT = "DR";
    
    /**
     * 音量
     */
    public static final String OPERATION_VOLUME = "VO";
    
    /**
     * 亮度
     */
    public static final String OPERATION_BRIGHTNESS = "BR";
    
    /**
     * 单击&确认
     */
    public static final String OPERATION_CLICK = "CL";
    
    /**
     * 菜单
     */
    public static final String OPERATION_MENU = "ME";
    
    /**
     * 返回
     */
    public static final String OPERATION_BACK = "BA";
    
    /**
     * 电源
     */
    public static final String OPERATION_POWER = "PO";
    
    /**
     * Home
     */
    public static final String OPERATION_HOME = "HO";
    
    
    public ServerThread(Socket s)
        throws IOException
    {
        this.s = s;
        //初始化该Socket对应的输入流
        br = new BufferedReader(new InputStreamReader(s.getInputStream() , "utf-8"));   //②
        
    }

    /**
     * 向所有接入客户端发信息 sendMessageToClient
     */
    public synchronized static void sendMessageToClient(String msg){
        Log.i(TAG, "sendMessageToClient --> : " + msg);
        
        //遍历socketList中的每个Socket，
        //将读到的内容向每个Socket发送一次
        for (Socket s : SocketService.socketList)
        {
            try {
                OutputStream os = s.getOutputStream();
                os.write((msg + "\n").getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, " --> sendMessageToClient error <-- 删除该Socket : " + s);
                //删除该Socket。
                SocketService.socketList.remove(s);    //①
            }
        }
    }
    
    /**
     * 此线程不断接收客户端发过来的请求信息
     */
    public void run()
    {
        try
        {
            String content = null;
            //采用循环不断从Socket中读取客户端发送过来的数据
            while ((content = readFromClient()) != null)
            {

                if(content.length() < 2) continue;  // 避免发送空指令 或错误指令导致异常
                
                String requestStr = content.substring(0, 2);
                Log.i(TAG, "SocketServer ---> content : " + content + "   , requestStr : " + requestStr );
                
                if(requestStr.endsWith(OPERATION_VOLUME)){
                    MainActivity.systemManager.CtrlVolume(content);
                }else if(requestStr.endsWith(OPERATION_BRIGHTNESS)){
                    MainActivity.systemManager.CtrlBrightness(content);
                }else if(requestStr.equals(OPERATION_CLICK)){  //模拟按键analog_control<--> input
                    String cmd = "input keyevent 23\n";  
                    SystemManager.execute(cmd);  
                }else if(requestStr.equals(OPERATION_MENU)){  
                    String cmd = "input keyevent 82\n";  
                    SystemManager.execute(cmd);  
                }else if(requestStr.equals(OPERATION_BACK)){  
                    String cmd = "input keyevent 4\n";  
                    SystemManager.execute(cmd);  
                }else if(requestStr.equals(OPERATION_POWER)){  
                    String cmd = "input keyevent 26\n";  
                    SystemManager.execute(cmd);  
                }else if(requestStr.equals(OPERATION_HOME)){  
                    String cmd = "input keyevent 3\n";  
                    SystemManager.execute(cmd);  
                }else if(requestStr.equals(OPERATION_DIRECTION_TOP)){  
                    String cmd = "input keyevent 19\n";  
                    SystemManager.execute(cmd);  
                }else if(requestStr.equals(OPERATION_DIRECTION_BOTTOM)){  
                    String cmd = "input keyevent 20\n";  
                    SystemManager.execute(cmd);  
                }else if(requestStr.equals(OPERATION_DIRECTION_LEFT)){  
                    String cmd = "input keyevent 21\n";  
                    SystemManager.execute(cmd);  
                }else if(requestStr.equals(OPERATION_DIRECTION_RIGHT)){  
                    String cmd = "input keyevent 22\n";  
                    SystemManager.execute(cmd);  
                }  
                
                
                

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    /**
     * 定义读取客户端数据的方法
     * @return 接收到的请求信息
     */
    private String readFromClient()
    {
        try
        {
            return br.readLine();   
        }
        //如果捕捉到异常，表明该Socket对应的客户端已经关闭
        catch (IOException e)
        {
            //删除该Socket。
            SocketService.socketList.remove(s);    
            MainActivity.mHandler.sendEmptyMessage(MainActivity.CONN_ERROR);
        }
        return null;
    }
}