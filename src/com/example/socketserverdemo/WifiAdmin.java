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
    //����WifiManager����  
    private WifiManager mWifiManager;  
    //����WifiInfo����  
    private WifiInfo mWifiInfo;  
    //ɨ��������������б�  
    private List<ScanResult> mWifiList;  
    //���������б�  
    private List<WifiConfiguration> mWifiConfiguration;  
    //����һ��WifiLock  
    WifiLock mWifiLock;  
    //������  
    public  WifiAdmin(Context context)  
    {  
        //ȡ��WifiManager����  
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
        //ȡ��WifiInfo����  
        mWifiInfo = mWifiManager.getConnectionInfo();  
    }  
    //��WIFI  
    public void OpenWifi()  
    {  
        if (!mWifiManager.isWifiEnabled())  
        {  
            mWifiManager.setWifiEnabled(true);  

        }  
    }  
    //�ر�WIFI  
    public void CloseWifi()  
    {  
        if (!mWifiManager.isWifiEnabled())  
        {  
            mWifiManager.setWifiEnabled(false);   
        }  
    }  
    //����WifiLock  
    public void AcquireWifiLock()  
    {  
        mWifiLock.acquire();  
    }  
    //����WifiLock  
    public void ReleaseWifiLock()  
    {  
        //�ж�ʱ������  
        if (mWifiLock.isHeld())  
        {  
            mWifiLock.acquire();  
        }  
    }  
    //����һ��WifiLock  
    public void CreatWifiLock()  
    {  
        mWifiLock = mWifiManager.createWifiLock("Test");  
    }  
    //�õ����úõ�����  
    public List<WifiConfiguration> GetConfiguration()  
    {  
        return mWifiConfiguration;  
    }  
    //ָ�����úõ������������  
    public void ConnectConfiguration(int index)  
    {  
        //�����������úõ�������������  
        if(index > mWifiConfiguration.size())  
        {  
            return;  
        }  
        //�������úõ�ָ��ID������  
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);  
    }  
    public void StartScan()  
    {  
        mWifiManager.startScan();  
        //�õ�ɨ����  
        mWifiList = mWifiManager.getScanResults();  
        //�õ����úõ���������  
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();  
    }  
    //�õ������б�  
    public List<ScanResult> GetWifiList()  
    {  
        return mWifiList;  
    }  
    //�鿴ɨ����  
    public StringBuilder LookUpScan()  
    {  
        StringBuilder stringBuilder = new StringBuilder();  
        for (int i = 0; i < mWifiList.size(); i++)  
        {  
            stringBuilder.append("Index_"+new Integer(i + 1).toString() + ":");  
            //��ScanResult��Ϣת����һ���ַ�����  
            //���аѰ�����BSSID��SSID��capabilities��frequency��level  
            stringBuilder.append((mWifiList.get(i)).toString());  
            stringBuilder.append("/n");  
        }  
        return stringBuilder;  
    }  
    //�õ�MAC��ַ  
    public String GetMacAddress()  
    {  
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();  
    }  
    //�õ�������BSSID  
    public String GetBSSID()  
    {  
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();  
    }  
    //�õ�IP��ַ  
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
    //�õ����ӵ�ID  
    public int GetNetworkId()  
    {  
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();  
    }  
    //�õ�WifiInfo��������Ϣ��  
    public String GetWifiInfo()  
    {  
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();  

    }  
    //���һ�����粢����  
    public void AddNetwork(WifiConfiguration wcg)  
    {  
        int wcgID = mWifiManager.addNetwork(wcg);   
        mWifiManager.enableNetwork(wcgID, true);   
    }  
    //�Ͽ�ָ��ID������  
    public void DisconnectWifi(int netId)  
    {  
        mWifiManager.disableNetwork(netId);  
        mWifiManager.disconnect();  
    }

    //��127.0.0.1��ʽ��IP��ַת����ʮ��������������û�н����κδ�����
    public static long ipToLong(String strIp){
        long[] ip = new long[4];
        //���ҵ�IP��ַ�ַ�����.��λ��
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        //��ÿ��.֮����ַ���ת��������
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1+1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2+1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3+1));
        return (ip[3] << 24) + (ip[2] << 16) + (ip[1] <<  8 ) + ip[0];
    }

    //��ʮ����������ʽת����127.0.0.1��ʽ��ip��ַ
    public static String longToIP(long longIp){
        StringBuffer sb = new StringBuffer("");
        //����24λ��0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        sb.append(".");
        //����16λ��0��Ȼ������8λ
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>>  8 ));
        sb.append(".");
        //����8λ��0��Ȼ������16λ
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        //ֱ������24λ
        sb.append(String.valueOf((longIp >>> 24)));
        return sb.toString();
    }
}  