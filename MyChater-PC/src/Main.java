import java.awt.geom.FlatteningPathIterator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.omg.PortableInterceptor.DISCARDING;

public class Main {
	
	public static DataInputStream dis;
	
	public static DataOutputStream dos;
	
	private static String ip = "";
	
	public static Socket socket;

	public static void main(String[] args) {
		
		try {
			//获取本机IP
			InetAddress inetAddress = InetAddress.getLocalHost();
			System.out.println("IP Address:" + inetAddress.getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		//输入目标IP
		System.out.println("Input address(it will wait for others to connect you if this ip is invalid or null):");
		Scanner scanner = new Scanner(System.in);
		ip = scanner.nextLine();
		
		//Main类无法直接new内部类
		Main m = new Main();
		m.new ConnectThread().start();
		
	}
	
	class ConnectThread extends Thread {
		
		//是否连接成功标志位
		private int connectFlag = 1;
		
		@Override
		public void run() {
			InetSocketAddress address = new InetSocketAddress(ip, 6666);
			socket = new Socket();
			try {
				//连接主机，非阻塞
				socket.connect(address, 1000);
			} catch (Exception e) {
				//没有主机在线，开启ServerSocket等待
				System.out.println("waiting for connect....");
				new ReceiveThread().start();
				connectFlag = 0;
			}
			if (connectFlag == 1) {
				//连接成功
				System.out.println("connect success!");
				try {
					dis = new DataInputStream(socket.getInputStream());
					dos = new DataOutputStream(socket.getOutputStream());
					//开启发送和接受信息的线程
					new InputThread(dis).start();
					new OutputThread(dos).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class ReceiveThread extends Thread {
		@Override
		public void run() {
			try {
				ServerSocket serverSocket = new ServerSocket(6666);
				//等待连接
				Socket socket = serverSocket.accept();
				//连接诶成功
				System.out.println("connect success!");
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				//开启发送和接受信息的线程
				new InputThread(dis).start();
				new OutputThread(dos).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
