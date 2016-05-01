package com.example.wulinpeng.tcptest;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by wulinpeng on 16/4/30.
 */
public class ConnectActivity extends AppCompatActivity {

    private final int ENABLE_BUTTON = 0;

    private final int DISABLE_BUTTON = 1;

    private final int STATE_CONNECTING = 2;

    private final int STATE_FAIL = 3;

    private EditText other;

    private Button connect;

    private TextView ip, state;

    private Socket socket;

    private ServerSocket serverSocket;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENABLE_BUTTON:
                    connect.setEnabled(true);
                    other.setEnabled(true);
                    break;

                case DISABLE_BUTTON:
                    connect.setEnabled(false);
                    other.setEnabled(false);
                    break;

                case STATE_CONNECTING:
                    state.setText("State:connecting...");
                    break;

                case STATE_FAIL:
                    state.setText("State:connect failed");
                    connect.setEnabled(true);
                    other.setEnabled(true);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMan.getConnectionInfo();

        final int ipAddress = info.getIpAddress();
        String ipString = "";// 本机在WIFI状态下路由分配给的IP地址

        // 获得IP地址
        if (ipAddress != 0) {
            ipString = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                    + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
        }

        ip = (TextView)findViewById(R.id.ip);
        ip.setText("IP Address:" + ipString);

        state = (TextView)findViewById(R.id.state);
        state.setText("State:null");

        other = (EditText)findViewById(R.id.otherip);

        connect = (Button)findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConnectTread().start();
            }
        });
    }

    class ConnectTread extends Thread {
        @Override
        public void run() {
            try {
                InetSocketAddress address = new InetSocketAddress(other.getText().toString(), 6666);
                socket = new Socket();
                socket.connect(address, 1000);

                MainActivity.dis = new DataInputStream(socket.getInputStream());
                MainActivity.dos = new DataOutputStream(socket.getOutputStream());
                Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                startActivity(intent);

            } catch (IOException e) {
                //按钮失效
                sendMessage(DISABLE_BUTTON);
                //开启ServerSocket等待连接
                new ReceiveThread().start();
            }
        }
    }

    class ReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(6666);
                sendMessage(STATE_CONNECTING);
                socket = serverSocket.accept();
                MainActivity.dis = new DataInputStream(socket.getInputStream());
                MainActivity.dos = new DataOutputStream(socket.getOutputStream());
                Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                startActivity(intent);
            } catch (IOException e1) {
                Log.d("Debug", "error");
                sendMessage(STATE_FAIL);
            }
        }
    }

    //发送消息
    private void sendMessage(int what) {
        Message message = new Message();
        message.what = what;
        handler.sendMessage(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //最好都关闭防止出现奇怪的现象
            if (MainActivity.dis != null) {
                MainActivity.dis.close();
            }
            if (MainActivity.dos != null) {
                MainActivity.dos.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        state.setText("State:null");
        sendMessage(ENABLE_BUTTON);
    }
}
