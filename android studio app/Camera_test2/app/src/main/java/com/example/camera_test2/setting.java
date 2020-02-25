package com.example.camera_test2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class setting extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button btnOnOff, btnDiscover, btnSend;
    ListView listView;
    TextView read_msg_box, connectionStatus;
    //EditText writeMsg;

    WifiManager wifiManager; //define wifi manager

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    WifiP2pDevice[] deviceArray; //A class representing a Wi-Fi p2p device
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>(); //identify a peers in class type wifip2pDevice  Constructs an empty list with an initial capacity of ten.
    String[] deviceNameArray;

    ServerClass serverClass; //define server class
    ClientClass clientClass; //define client class

    static final int MESSAGE_READ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initalWork(); //function for initilize
        exqListener(); //function for button
    }

    private void initalWork()
    {
        btnOnOff = (Button) findViewById(R.id.onOff);
        btnDiscover = (Button) findViewById(R.id.discover);
        btnSend = (Button) findViewById(R.id.sendButton);
        listView = (ListView) findViewById(R.id.peerListView);
        read_msg_box = (TextView) findViewById(R.id.readMeg);
        connectionStatus = (TextView) findViewById(R.id.connectionStatus);
        //writeMsg = (EditText) findViewById(R.id.writeMg);

        //initilize wifi manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE); //get manager
        mChannel = mManager.initialize(this, getMainLooper(),null);

        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this); //导到WIFIDirectBroadcastReceiver
        mIntentFilter = new IntentFilter();

//Add a new Intent action to match against.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
    }

    private void exqListener()
    {
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
//click button, if successful, WIFI_P2P_PEERS_CHANGED_ACTION changes
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
                        //mManager.connect();
                    }
                });
            }
        });

//after click the name of the device, function start a p2p connection, if connect, WIFI_P2P_THIS_DEVICE_CHANGED_ACTION changes status, resume broadcast
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final WifiP2pDevice device = deviceArray[position]; //A class representing a Wi-Fi p2p device
                WifiP2pConfig config = new WifiP2pConfig();  //A class representing a Wi-Fi P2p configuration for setting up a connection
                config.deviceAddress = device.deviceAddress; //pairing device MAC address uniquely identifies a Wi-Fi p2p device

                //Start a p2p connection to a device with the specified configuration.
                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener()
                {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Connect to "+ device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        //A class representing a Wi-Fi P2p device list.
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            Log.i(TAG, "in here");
            if(!peerList.getDeviceList().equals(peers))
            {
                Log.i(TAG, "in function");
                Log.i(TAG, "peers size " + peers.size());
                peers.clear();
                peers.addAll(peerList.getDeviceList()); //getDeviceList = Get the list of devices add all available device to peer

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
                listView.setAdapter(adapter); //Sets the data behind this ListView
            }

            if(peers.size()==0)
            {
                Toast.makeText(getApplicationContext(), "No Device Found", Toast.LENGTH_SHORT).show();
                return;
            }
            connectionStatus.setText("Discovery Restart");
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        //This class represents an Internet Protocol (IP) address.
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress groupOwenerAddress = info.groupOwnerAddress;

            if(info.groupFormed && info.isGroupOwner)
            {
                connectionStatus.setText("Device Connected: Host");
                serverClass = new ServerClass();
                serverClass.start();
            }
            else if(info.groupFormed)
            {
                connectionStatus.setText("Device Connected: Client");
                clientClass = new ClientClass(groupOwenerAddress);
                clientClass.start();
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

//so first, when server function begins, it create a serversocket, and it will wait until it get the first available socket. when it find the available socket which made in the Client function, it accpet
    public class ServerClass extends Thread //Server thread
    {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run()
        {
            try {
                serverSocket = new ServerSocket(8888); //Creates a server socket, bound to the specified port in here 8888
                socket = serverSocket.accept();//choose the one on the socket serve client list  .Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made.

                //sendReceive = new SendReceive(socket);
                //sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public class ClientClass extends Thread //Client thread
    {
        Socket socket;
        String hostAdd;

        public ClientClass (InetAddress hostAddress)
        {
            hostAdd = hostAddress.getHostAddress(); //get Ip address
            socket = new Socket(); //Creates an unconnected socket, with the system-default type of SocketImpl.
        }

        @Override
        public void run()
        {
            try{
                socket.connect(new InetSocketAddress(hostAdd, 8888), 500); //Creates a socket address from a hostname and a port number. - InetSocketAddress
                //sendReceive = new SendReceive(socket);
                //sendReceive.start();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}


