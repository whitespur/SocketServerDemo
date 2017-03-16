package com.example.socketserverdemo;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.text.format.Formatter;

public class WifiAdmin  
{  
    //定义WifiManager对象  
    private WifiManager mWifiManager;  
    //定义WifiInfo对象  
    private WifiInfo mWifiInfo;  
    //扫描出的网络连接列表  
    private List<ScanResult> mWifiList;  
    //网络连接列表  
    private List<WifiConfiguration> mWifiConfiguration;  
    //定义一个WifiLock  
    WifiLock mWifiLock;  
    //构造器  
    public  WifiAdmin(Context context)  
    {  
        //取得WifiManager对象  
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
        //取得WifiInfo对象  
        mWifiInfo = mWifiManager.getConnectionInfo();  
    }  
    //打开WIFI  
    public void OpenWifi()  
    {  
        if (!mWifiManager.isWifiEnabled())  
        {  
            mWifiManager.setWifiEnabled(true);  

        }  
    }  
    //关闭WIFI  
    public void CloseWifi()  
    {  
        if (!mWifiManager.isWifiEnabled())  
        {  
            mWifiManager.setWifiEnabled(false);   
        }  
    }  
    //锁定WifiLock  
    public void AcquireWifiLock()  
    {  
        mWifiLock.acquire();  
    }  
    //解锁WifiLock  
    public void ReleaseWifiLock()  
    {  
        //判断时候锁定  
        if (mWifiLock.isHeld())  
        {  
            mWifiLock.acquire();  
        }  
    }  
    //创建一个WifiLock  
    public void CreatWifiLock()  
    {  
        mWifiLock = mWifiManager.createWifiLock("Test");  
    }  
    //得到配置好的网络  
    public List<WifiConfiguration> GetConfiguration()  
    {  
        return mWifiConfiguration;  
    }  
    //指定配置好的网络进行连接  
    public void ConnectConfiguration(int index)  
    {  
        //索引大于配置好的网络索引返回  
        if(index > mWifiConfiguration.size())  
        {  
            return;  
        }  
        //连接配置好的指定ID的网络  
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);  
    }  
    public void StartScan()  
    {  
        mWifiManager.startScan();  
        //得到扫描结果  
        mWifiList = mWifiManager.getScanResults();  
        //得到配置好的网络连接  
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();  
    }  
    //得到网络列表  
    public List<ScanResult> GetWifiList()  
    {  
        return mWifiList;  
    }  
    //查看扫描结果  
    public StringBuilder LookUpScan()  
    {  
        StringBuilder stringBuilder = new StringBuilder();  
        for (int i = 0; i < mWifiList.size(); i++)  
        {  
            stringBuilder.append("Index_"+new Integer(i + 1).toString() + ":");  
            //将ScanResult信息转换成一个字符串包  
            //其中把包括：BSSID、SSID、capabilities、frequency、level  
            stringBuilder.append((mWifiList.get(i)).toString());  
            stringBuilder.append("/n");  
        }  
        return stringBuilder;  
    }  
    //得到MAC地址  
    public String GetMacAddress()  
    {  
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();  
    }  
    //得到接入点的BSSID  
    public String GetBSSID()  
    {  
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();  
    }  
    //得到IP地址  
    public int GetIPAddress()  
    {  
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();  
    }  
    public String getLocalIPAddress () {
        String ipAddress="";
       if(mWifiInfo != null){
            ipAddress = FormatIP(mWifiInfo.getIpAddress());
       }
        return ipAddress;
    }
    public String FormatIP (int ip) {
        return Formatter.formatIpAddress(ip);
    }
    //得到连接的ID  
    public int GetNetworkId()  
    {  
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();  
    }  
    //得到WifiInfo的所有信息包  
    public String GetWifiInfo()  
    {  
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();  

    }  
    //添加一个网络并连接  
    public void AddNetwork(WifiConfiguration wcg)  
    {  
        int wcgID = mWifiManager.addNetwork(wcg);   
        mWifiManager.enableNetwork(wcgID, true);   
    }  
    //断开指定ID的网络  
    public void DisconnectWifi(int netId)  
    {  
        mWifiManager.disableNetwork(netId);  
        mWifiManager.disconnect();  
    }

    //将127.0.0.1形式的IP地址转换成十进制整数，这里没有进行任何错误处理
    public static long ipToLong(String strIp){
        long[] ip = new long[4];
        //先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        //将每个.之间的字符串转换成整型
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1+1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2+1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3+1));
        return (ip[3] << 24) + (ip[2] << 16) + (ip[1] <<  8 ) + ip[0];
    }

    //将十进制整数形式转换成127.0.0.1形式的ip地址
    public static String longToIP(long longIp){
        StringBuffer sb = new StringBuffer("");
        //将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        sb.append(".");
        //将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>>  8 ));
        sb.append(".");
        //将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        //直接右移24位
        sb.append(String.valueOf((longIp >>> 24)));
        return sb.toString();
    }
}  