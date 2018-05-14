

public class Neighbor {

	private Address addr;
	public double freq ;
	public double volt;
	public double mpgene;
	
	public Neighbor(String add, int port) {
		addr = new Address(add, port);
	}
	
	public void setAddress(String add, int port) {
		addr.setAddress(add, port);
	}
	
	public Address getAddress() {
		return addr;
	}
	
	public void setValue(double f, double v, double mp) {
		freq = f;
		volt = v; 
		mpgene = mp;
	}
}
