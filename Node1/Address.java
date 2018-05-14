

public class Address {

	private String addr = "192.168.1.101";
	private int port = 30000;
	public Address(){}
	
	public Address(String add, int p) {
		addr = add;
		port = p;
	}
	
	public Address(int port){
		this.port = port;
	}
	
	public String getAddress(){
		return addr;
	}
	
	public int getPort(){
		return port;
	}
	
	public void setAddress(String add, int port) {
		addr = add;
		this.port = port;
	}
}
