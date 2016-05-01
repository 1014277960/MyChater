package com.example.wulinpeng.tcptest;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int CONNECT_CUT = 0;

    private final int INPUT_MSG = 1;

    private final int OUTPUT_MSG = 2;

    private EditText contentEditText;

    private Button send;

    private ListView chat;

    private List<ChatBean> data;

    private MyAdapter myAdapter;

    public static  DataInputStream dis = null;

    public static DataOutputStream dos = null;

    private String talk = "";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT_CUT:
                    //连接断开
                    //talk += "\n" + "Connect disconnect!";
                    ChatBean beanMsg = new ChatBean();
                    beanMsg.setType(2);
                    beanMsg.setText("Connect disconnect!");
                    data.add(beanMsg);
                    break;

                case INPUT_MSG:
                    //接受信息
                    //talk += "\n" + "Other side:" + (String)msg.obj;
                    ChatBean beanIn = new ChatBean();
                    beanIn.setType(0);
                    beanIn.setText((String) msg.obj);
                    beanIn.setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                    data.add(beanIn);
                    break;

                case OUTPUT_MSG:
                    //发送信息
                    //talk += "\n" + "I:" + (String)msg.obj;
                    ChatBean beanOut = new ChatBean();
                    beanOut.setType(1);
                    beanOut.setText((String) msg.obj);
                    beanOut.setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                    data.add(beanOut);
                    //清除输入框内容
                    contentEditText.setText("");
                    break;

                default:
                    break;
            }
            myAdapter.setData(data);
            chat.setSelection(data.size());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentEditText = (EditText)findViewById(R.id.content);
        chat = (ListView)findViewById(R.id.chat);
        send = (Button)findViewById(R.id.send);
        send.setOnClickListener(this);

        data = new ArrayList<ChatBean>();

        ChatBean beanMsg = new ChatBean();
        beanMsg.setType(2);
        beanMsg.setText("connect success");
        data.add(beanMsg);

        myAdapter = new MyAdapter(this, data);
        chat.setAdapter(myAdapter);

        //开启接受信息线程
        new InputThread().start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                try {
                    String data = contentEditText.getText().toString();
                    dos.writeUTF(data);
                    sendMessage(OUTPUT_MSG, data);

                } catch (IOException e) {
                    sendMessage(CONNECT_CUT, null);
                }
                break;

            default:
                break;
        }
    }
    class InputThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    String data = dis.readUTF();
                    sendMessage(INPUT_MSG, data);
                }
            } catch (IOException e) {
                sendMessage(CONNECT_CUT, null);
                try {
                    //同时关闭输出流
                    dos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    //发送消息
    private void sendMessage(int what, Object obj) {
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        handler.sendMessage(message);
    }

}
