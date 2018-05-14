import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Calendar;
import java.util.TimeZone;
import org.json.JSONObject;
import org.json.JSONException;

// This program is in charge of server, handle all connection from client UDP, receive only
public class UDPHandle{
	//declare
	private static Node node = new Node();
	private static InetSocketAddress ad= new InetSocketAddress(node.getAddress().getAddress(),node.getAddress().getPort());
	public static JSONObject data_out = new JSONObject();
	public static JSONObject data_in = new JSONObject();
	public static JSONObject data_send = new JSONObject();
	private static DatagramSocket serverSocket;
	public UDPHandle() throws IOException, ClassNotFoundException{
		serverSocket= new DatagramSocket(ad);
	}
	
	public static void main(String[] args) throws IOException, SocketException, InterruptedException , JSONException , Exception
	{
		serverSocket= new DatagramSocket(ad);
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		System.out.println("starting!");		
		while(true)
		{
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);			
			try{
				serverSocket.receive(receivePacket);
			}catch(Exception e){
				e.printStackTrace();
			}
			String Data = new String(receivePacket.getData());
			//update data_out
			if(!Data.isEmpty()) 
			{
				//cast to JSONObject
				data_out = new JSONObject(Data);
				String table = (String) data_out.get("table");
				System.out.println(table);
				if (table.equals("Busdata")) {	
					// save to node parameter
					double qo = node.q;
					node.freq = data_out.getDouble("frequency");
					node.volt = data_out.getDouble("voltage");
					node.p = data_out.getDouble("pgene");
					node.q = data_out.getDouble("qgene");
					node.nqgene = node.droop_q*(node.q - qo);
					//calculate T1
					if(node.freq!=0){
					double mpgene = (node.p*node.droop_p);
					//send to its neighbor
					data_send.put("table", "neighbor");
					data_send.put("frequency", node.freq);
					data_send.put("voltage", node.volt);
					data_send.put("mpgene", mpgene);
					try {
						sendData = data_send.toString().getBytes();
						for (int i=0; i<node.neighbor.size(); i++) {
							InetSocketAddress addr= new InetSocketAddress(node.neighbor.get(i).getAddress().getAddress(),node.neighbor.get(i).getAddress().getPort());
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr.getAddress(),addr.getPort());
							serverSocket.send(sendPacket);
						}
					}catch(Exception e1) {
						e1.printStackTrace();
					}
					//calculate output					
					double df = 0;
					double dv = 0;
					for (int i=0; i<node.neighbor.size(); i++) {
						if(node.neighbor.get(i).freq!=0){
							df = df + node.neighbor.get(i).mpgene - mpgene + node.neighbor.get(i).freq-node.freq;
							dv = dv + node.neighbor.get(i).volt - node.volt;
						}
					}
					//get instant time				
					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
					long to = cal.getTimeInMillis();
					System.out.println(to-node.time);
					//the output
					node.delta_f = Math.min(0.5, Math.max((node.delta_f+ (to-node.time)*node.c*0.001*(df+node.fo - node.freq)), -0.5));
					node.delta_v = Math.min(30, Math.max((node.delta_v + (to-node.time)*0.001*(node.c*(dv+node.vo - node.volt)+node.nqgene)), -30));
					node.time  = to;
					}
					//pack data to Opal
					data_in.put("table", "Output");
					data_in.put("ident", node.ident);
					data_in.put("efreq", node.delta_f);
					data_in.put("evolt", node.delta_v);					
					//send data set-point to OPAL
					sendData = data_in.toString().getBytes();
					System.out.println(data_in.toString());
					if(sendData.length !=0){
						DatagramPacket sendPacket2 = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
						try {
							serverSocket.send(sendPacket2);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if (table.equals("neighbor")) {
						for(int i=0; i<node.neighbor.size(); i++) {
							if(receivePacket.getAddress().getHostAddress().equals( node.neighbor.get(i).getAddress().getAddress())) {
								node.neighbor.get(i).setValue(data_out.getDouble("frequency"), data_out.getDouble("voltage"), data_out.getDouble("mpgene"));
								//re-compute the output
								double df = 0;
								double dv = 0;
								for (int j=0; j<node.neighbor.size(); j++) {
									df =  (df + node.neighbor.get(i).mpgene -node.droop_p*node.p + node.neighbor.get(i).freq-node.freq);
									dv = dv + node.neighbor.get(i).volt - node.volt;
								}
								//get instant time
								Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
								long to = cal.getTimeInMillis();
								//the output
								node.delta_f = Math.min(0.5, Math.max((node.delta_f+ (to-node.time)*node.c*0.001*(df+node.fo - node.freq)), -0.5));
								node.delta_v = Math.min(30, Math.max((node.delta_v + (to - node.time)*0.001*(node.c*(dv+node.vo - node.volt)+node.nqgene)), -30));
								node.time  = to;
								break;
							}
						}
					}
				}
			}
		}
	}

