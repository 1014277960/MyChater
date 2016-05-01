import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.MissingFormatArgumentException;
import java.util.Scanner;

public class OutputThread extends Thread {
	
	private boolean isConnect = true;
	
	private DataOutputStream dos;
	
	public OutputThread(DataOutputStream dos) {
		this.dos = dos;
	}
	
	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		while (isConnect) {
			try {
				String data = null;
				data = scanner.nextLine();
				dos.writeUTF(data);
				System.out.println("I:" + data);
			} catch (IOException e) {
				System.out.println("Connect disconnect!");
				isConnect = false;
				if (!Main.socket.isClosed()) {
					try {
						Main.socket.close();
						Main.dis.close();
						Main.dos.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		
	}
	
}
