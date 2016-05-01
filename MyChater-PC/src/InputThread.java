import java.io.DataInputStream;
import java.io.IOException;
import java.util.MissingFormatArgumentException;

public class InputThread extends Thread {
	
	private boolean isConnect = true; 

	private DataInputStream dis;
	
	public InputThread(DataInputStream dis) {
		this.dis = dis;
	}

	@Override
	public void run() {
		while (isConnect) {
			String data;
			try {
				data = dis.readUTF();
				System.out.println("Other side:" + data);
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
