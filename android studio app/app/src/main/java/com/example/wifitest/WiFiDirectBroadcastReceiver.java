package com.example.wifitest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel; //A channel that connects the application to the Wifi p2p framework.
    // Most p2p operations require a Channel as an argument. An instance of Channel is obtained by doing a call on WifiP2pManager.initialize(Context, Looper, WifiP2pManager.ChannelListener)
    private MainActivity mActivity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, MainActivity mActivity)
    {
        this.mManager = mManager; //mMangaer from this activity = mManager from main activity
        this.mChannel = mChannel;
        this.mActivity = mActivity;
    }
    @Override
    //
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //Toast.makeText(context, "in here", Toast.LENGTH_SHORT).show();
        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) //if action equal to action capture
        {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
                Toast.makeText(context, "Wifi is on", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "Wifi is off", Toast.LENGTH_SHORT).show();
            }
        }
        else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {
            if(mManager != null)
            {
                //Toast.makeText(context, "in here", Toast.LENGTH_SHORT).show();
                mManager.requestPeers(mChannel, mActivity.peerListListener);
                //discoverPeers发起查找以后，一旦查找到附近设备，WIFI_P2P_PEERS_CHANGED_ACTION广播就会触发，我们可以在该广播中调用requestPeers方法来请求过去设备列表。
            }
        }
        else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {
            //do something
        }
        else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
        {
            //do something
        }


    }
}
