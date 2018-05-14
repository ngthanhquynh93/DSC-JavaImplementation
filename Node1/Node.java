import java.util.Calendar;
import java.util.TimeZone;
import java.util.ArrayList;

//define the structure of
public class Node {
	
	private Address addr;
	public double freq;
	public double volt;
	public double p;
	public double q;
	public double delta_f;
	public double delta_v;
	public double droop_p;
	public double droop_q;
	public float fo ;
	public float vo;
	public float c;
	public long time;
	public int ident;
	public double nqgene;
	
	public ArrayList<Neighbor> neighbor = new ArrayList<Neighbor>();
	// this part can be modified, depends on the specified parameters of each Node
	public Node() {
		addr = new Address("192.168.1.101",30000);
		delta_f=0;
		delta_v = 0;
		droop_p = 1e-5;
		droop_q = 1e-3;
		c = 2;
		ident = 1;
		q = 0;
		fo = 50;
		vo = 311;
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		time = cal.getTimeInMillis();
		this.neighbor.add(new Neighbor("192.168.1.102", 30000)); 
	}
	
	public Address getAddress() {
		return addr;
	}
}
