package com.example.socketserverdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

/**
 * ������ÿ���߳�ͨ�ŵ��߳���
 *
 */
public class ServerThread implements Runnable 
{
    //���嵱ǰ�߳��������Socket
    Socket s = null;
    //���߳��������Socket����Ӧ��������
    BufferedReader br = null;
    private static String TAG ="Server_Thread";

     /**
     * ��������
     */
    public static final String OPERATION_CONNECT = "CN";
    
    /**
     * ����
     */
    public static final String OPERATION_DIRECTION_TOP = "DT";
    public static final String OPERATION_DIRECTION_BOTTOM = "DB";
    public static final String OPERATION_DIRECTION_LEFT = "DL";
    public static final String OPERATION_DIRECTION_RIGHT = "DR";
    
    /**
     * ����
     */
    public static final String OPERATION_VOLUME = "VO";
    
    /**
     * ����
     */
    public static final String OPERATION_BRIGHTNESS = "BR";
    
    /**
     * ����&ȷ��
     */
    public static final String OPERATION_CLICK = "CL";
    
    /**
     * �˵�
     */
    public static final String OPERATION_MENU = "ME";
    
    /**
     * ����
     */
    public static final String OPERATION_BACK = "BA";
    
    /**
     * ��Դ
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
        //��ʼ����Socket��Ӧ��������
        br = new BufferedReader(new InputStreamReader(s.getInputStream() , "utf-8"));   //��
        
    }

    /**
     * �����н���ͻ��˷���Ϣ sendMessageToClient
     */
    public synchronized static void sendMessageToClient(String msg){
        Log.i(TAG, "sendMessageToClient --> : " + msg);
        
        //����socketList�е�ÿ��Socket��
        //��������������ÿ��Socket����һ��
        for (Socket s : SocketService.socketList)
        {
            try {
                OutputStream os = s.getOutputStream();
                os.write((msg + "\n").getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, " --> sendMessageToClient error <-- ɾ����Socket : " + s);
                //ɾ����Socket��
                SocketService.socketList.remove(s);    //��
            }
        }
    }
    
    /**
     * ���̲߳��Ͻ��տͻ��˷�������������Ϣ
     */
    public void run()
    {
        try
        {
            String content = null;
            //����ѭ�����ϴ�Socket�ж�ȡ�ͻ��˷��͹���������
            while ((content = readFromClient()) != null)
            {

                if(content.length() < 2) continue;  // ���ⷢ�Ϳ�ָ�� �����ָ����쳣
                
                String requestStr = content.substring(0, 2);
                Log.i(TAG, "SocketServer ---> content : " + content + "   , requestStr : " + requestStr );
                
                if(requestStr.endsWith(OPERATION_VOLUME)){
                    MainActivity.systemManager.CtrlVolume(content);
                }else if(requestStr.endsWith(OPERATION_BRIGHTNESS)){
                    MainActivity.systemManager.CtrlBrightness(content);
                }else if(requestStr.equals(OPERATION_CLICK)){  //ģ�ⰴ��analog_control<--> input
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
     * �����ȡ�ͻ������ݵķ���
     * @return ���յ���������Ϣ
     */
    private String readFromClient()
    {
        try
        {
            return br.readLine();   
        }
        //�����׽���쳣��������Socket��Ӧ�Ŀͻ����Ѿ��ر�
        catch (IOException e)
        {
            //ɾ����Socket��
            SocketService.socketList.remove(s);    
            MainActivity.mHandler.sendEmptyMessage(MainActivity.CONN_ERROR);
        }
        return null;
    }
}