package com.example.myapplication3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements  WifiP2pManager.PeerListListener,WifiP2pManager.ConnectionInfoListener{
    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager.Channel mChannel;
    WifiP2pManager mManager;
    NumberPicker picker;
    Button button;
    TextView textView;
    private final String TAG = "MainActivity";
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG,"WIFI_P2P_STATE_CHANGED_ACTION");
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                Log.d(TAG,"WIFI_P2P_PEERS_CHANGED_ACTION");
                if (mManager != null) {
                    mManager.requestPeers(mChannel, MainActivity.this);
                }
                Log.d(TAG, "P2P peers changed");
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                Log.d(TAG,"WIFI_P2P_CONNECTION_CHANGED_ACTION");
                if (mManager == null) {
                    return;
                }
                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {
                    mManager.requestConnectionInfo(mChannel, MainActivity.this);
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG,"WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        picker = (NumberPicker) findViewById(R.id.pick);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        peersName = new String[1];
        peersName[0]="No Devices";
        picker.setDisplayedValues(peersName);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = picker.getValue();
                connect(num);
            }
        });
        textView = (TextView) findViewById(R.id.text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, intentFilter);
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG,"onSuccess");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG,"onFailure");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
    private List<WifiP2pDevice> peers = new ArrayList<>();
    private String[] peersName;
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        if (peers.size() == 0) {
            Log.d(TAG, "No devices found");
            textView.setVisibility(View.INVISIBLE);
            if (peersName.length>0){
                peersName[0]="No Devices";
            }else {
                peersName = new String[1];
                peersName[0]="No Devices";
            }
            return;
        }else{
            peersName = new String[peers.size()];
            int i=0;
            for(WifiP2pDevice device: peers){
                peersName[i++]=device.deviceName;
            }
            textView.setVisibility(View.VISIBLE);
            textView.setText("(avaliable)");
        }
        picker.setDisplayedValues(peersName);
    }
    public void connect(final int num) {
        // Picking the first device found on the network.
        WifiP2pDevice device = peers.get(num);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"connect sucess");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG,"connect fail");
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        textView.setVisibility(View.VISIBLE);
        textView.setText("(connected)");
        // InetAddress from WifiP2pInfo struct.
        InetAddress groupOwnerAddress = info.groupOwnerAddress;
        Log.d(TAG,"onConnectionInfoAvailable");
        Log.d(TAG,info.toString());
        if (info.groupFormed && info.isGroupOwner) {

        } else if (info.groupFormed) {

        }
    }
}
