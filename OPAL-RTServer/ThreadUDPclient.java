import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.json.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

// this program package data_out and send to each Raspi
public class ThreadUDPclient extends Thread{

	private Thread t;
	private ArrayList<InetSocketAddress> addr = new ArrayList<InetSocketAddress>();
	private int ident;	
	private JSONObject json = new JSONObject();
	private byte[] sendpack = new byte[1024];
	private static Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	private static long to = cal.getTimeInMillis();
	
	public ThreadUDPclient() {
		// TODO Auto-generated constructor stub
		addr.add(new InetSocketAddress("192.168.1.101",30000));
		addr.add(new InetSocketAddress("192.168.1.102",30000));
		addr.add(new InetSocketAddress("192.168.1.103",30000));
		//this.ident = ident;
		json.put("table", "Busdata");
		json.put("ident", ident);
		json.put("frequency", 0);
		json.put("voltage", 0);
		json.put("pgene", 0);	
		json.put("qgene", 0);		
		System.out.println("created a thread successful!");
	}
	
	public void run(){
		while(UDPserver.Runnable) {
			for(int i=0; i<3; i++) {
				ident = i+1;
				//prepare data in json type to send
				json.put("table", "Busdata");
				json.put("ident", ident);
				json.put("frequency", UDPserver.getDataOut().data[(ident-1)*4]);
				json.put("voltage", UDPserver.getDataOut().data[(ident-1)*4+1]);
				json.put("pgene", UDPserver.getDataOut().data[(ident-1)*4+2]);	
				json.put("qgene", UDPserver.getDataOut().data[(ident-1)*4+3]);
				//System.out.println(json);
				System.out.println(json.toString());
				sendpack = json.toString().getBytes();
				DatagramPacket sendPacketVM = new DatagramPacket(sendpack, sendpack.length, addr.get(i).getAddress(), addr.get(i).getPort());
				try {
					UDPserver.getclientSocket().send(sendPacketVM);
					//System.out.println("sent to Raspi");
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					UDPserver.Runnable = false;
				}
				//receive data backs from  VM
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] receiveDataVM = new byte[2048];
				
				DatagramPacket receivePacket = new DatagramPacket(receiveDataVM, receiveDataVM.length);			
				try {
					UDPserver.getclientSocket().receive(receivePacket);
					//System.out.println("received from raspi");
					String Data = new String(receivePacket.getData());
					
					if(Data.isEmpty()){
						System.out.println("receive nothing!");
					
					}
					//from string to json
					else {
						System.out.println(Data);
						JSONObject js = new JSONObject(Data);
						int ident = js.getInt("ident");
						//update data receiving into data_in
						UDPserver.setData_in((ident-1)*2, js.getDouble("efreq"));
						UDPserver.setData_in(((ident-1)*2+1), js.getDouble("evolt"));
					}
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					UDPserver.Runnable = false;
				}
			}
		}
	}		

	public void start(){
		System.out.println("starting");
		if(t==null){
			t = new Thread(this);
			t.setDaemon(true);
			t.start();
		}
	}
}

