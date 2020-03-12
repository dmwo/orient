package com.example.wifitest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button btnOnOff, btnDiscover, btnSend;
    ListView listView;
    TextView read_msg_box, connectionStatus;
    EditText writeMsg;

    WifiManager wifiManager; //define wifi manager

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>(); //identify a peers in class type wifip2pDevice
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray; //A class representing a Wi-Fi p2p device

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initalWork(); //function for initilize
        exqListener(); //function for button

    }

    private void exqListener() {
        //set a new listen activity for button onOff
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wifiManager.isWifiEnabled())
                {
                    wifiManager.setWifiEnabled(false);
                    btnOnOff.setText("ON");
                }
                else
                {
                    wifiManager.setWifiEnabled(true);
                    btnOnOff.setText("OFF");
                }
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initiate peer discovery. A discovery process involves scanning for available Wi-Fi peers
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionStatus.setText("Discovery Started");
                    }

                    @Override
                    public void onFailure(int reason) {
                        connectionStatus.setText("Discovery failed");
                    }
                });
            }
        });
    }
//INITILIZE
    private void initalWork() {
        btnOnOff = (Button) findViewById(R.id.onOff);
        btnDiscover = (Button) findViewById(R.id.discover);
        btnSend = (Button) findViewById(R.id.sendButton);
        listView = (ListView) findViewById(R.id.peerListView);
        read_msg_box = (TextView) findViewById(R.id.readMeg);
        connectionStatus = (TextView) findViewById(R.id.connectionStatus);
        writeMsg = (EditText) findViewById(R.id.writeMg);

        //initilize wifi manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE); //get manager
        mChannel = mManager.initialize(this, getMainLooper(),null);

        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this); //导到WIFIDirectBroadcastReceiver
        mIntentFilter = new IntentFilter();

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
    }

    //Interface for callback invocation when peer list is available
    //capture nearby device code
    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            Log.i(TAG, "in here");
            if(!peerList.getDeviceList().equals(peers))
            {
                Log.i(TAG, "in function");
                Log.i(TAG, "peers size " + peers.size());
                peers.clear();
                peers.addAll(peerList.getDeviceList()); //getDeviceList = Get the list of devices

                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index=0;

                for(WifiP2pDevice device: peerList.getDeviceList())
                {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device; // save wifi p2p
                    index++;
                    //Log.d(TAG, "index count: "+ index);
                }
//Returns a view for each object in a collection of data objects you provide
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,deviceNameArray);
                listView.setAdapter(adapter); //Sets the data behind this ListView.
            }

            if(peers.size()==0)
            {
                Toast.makeText(getApplicationContext(), "No Device Found", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,mIntentFilter); //begin receiver, broadcast
        Log.i(TAG, "on resume");
        //connectionStatus.setText("on resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver); //stop receiver, stop broadcast
        Log.i(TAG, "on pause");
        //Toast.makeText(MainActivity.this, "in here", Toast.LENGTH_SHORT).show();
        //connectionStatus.setText("in here");
    }
}
