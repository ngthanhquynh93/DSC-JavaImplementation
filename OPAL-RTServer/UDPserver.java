import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

//this program works as a server to transfer data from OPAL to Rasp Pis
//Before Execute it, Let verify the number of data in data_in and data_out firstly.
public class UDPserver {

	private static Data data_out = new Data(18);
	private static Data data_in= new Data(21);
	public static boolean Runnable = true;
	private static DatagramSocket clientSocket = null;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		InetSocketAddress opal = new InetSocketAddress("195.220.18.5",50000);
		clientSocket = new DatagramSocket();
		ThreadUDPserver T1 = new ThreadUDPserver(opal);
		T1.start();
		ThreadUDPclient T2 = new ThreadUDPclient();
		T2.start(); 		
		Scanner s = new Scanner(System.in);
		
		while(true){
			byte stop = s.nextByte();
			if(stop=='y'){
				Runnable = false;
			}
		}
	}
	
	public static DatagramSocket getclientSocket(){
		return clientSocket;
	}
	
	public static void setData_in(int pos, double value){
		data_in.setData(pos, value);
	}
	
	public static Data getDataOut() {
		return data_out;
	}
	
	public static Data getDataIn() {
		return data_in;
	}
}
