import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

// this program is in charge of server to store data from OPAL to data_out
public class ThreadUDPserver extends Thread {
	
	private Thread t;
	private InetSocketAddress add;
	private DatagramSocket serverSocket = null;
	
	public ThreadUDPserver(InetSocketAddress addr) {
		this.add = addr;		
		try {
			serverSocket = new DatagramSocket(add);
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}	
		System.out.println("created a thread successful!");
	}
	
	public void run() {
		while(UDPserver.Runnable) {
			byte[] receiveDataOp = new byte[1024];
			//receive packet from OPAL
			DatagramPacket receivePacketOp = new DatagramPacket(receiveDataOp, receiveDataOp.length);
			try {
				serverSocket.receive(receivePacketOp);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
			InetAddress ipOpal = receivePacketOp.getAddress();
			int pOpal = receivePacketOp.getPort();
			try {
				UDPserver.getDataOut().toData(receivePacketOp.getData());
				//System.out.println(UDPserver.data_out.toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DatagramPacket sendPacketOP = null;			
			try {
				//UDPserver.data_in.msg_id++;
				sendPacketOP = new DatagramPacket(UDPserver.getDataIn().toBytes(), UDPserver.getDataIn().toBytes().length, ipOpal, pOpal);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				serverSocket.send(sendPacketOP);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void start() {
		System.out.println("starting"+add.getAddress());
		if(t==null) {
			t = new Thread(this);
			t.setDaemon(true);
			t.start();
		}	
	}
}
