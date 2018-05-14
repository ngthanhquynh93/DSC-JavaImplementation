import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

//msg_len is amount of bytes of data;
public class Data {
	public short dev_id=1;
	public int msg_id;
	public short msg_len;
	public double[] data=new double[100] ;
	
	public Data(){}
	
	public Data(int numberOfData){
		this.msg_len = (short) (numberOfData*8);
		for(int i=0;i<numberOfData;i++){
			data[i]=0;
		}
	}
	
	public Data(short id, int msgid, int numberOfdata){
		this.dev_id = id;
		this.msg_id = msgid;
		this.msg_len = (short)(numberOfdata*8);
		for(int i=0;i<numberOfdata; i++){
			this.data[i]=0;
		}
	}
	
	public void setData(int pos, double value){
		data[pos] = value;
	}
	
	public void dataCopy(Data d, int numberOfdata){
		this.dev_id = d.dev_id;
		this.msg_id = d.msg_id;
		this.msg_len = (short) (numberOfdata*8);
		for(int i=0;i<numberOfdata; i++){
			this.data[i]=0;
		}
	}
	
	public void toData(byte[] bytes) throws IOException, ClassNotFoundException {
		byte[] tmp = Arrays.copyOfRange(bytes, 0,2);
		this.dev_id = ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN).getShort();//get 1st and 2nd element
		this.msg_id = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 2, 6)).order(ByteOrder.LITTLE_ENDIAN).getInt();
		this.msg_len = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 6, 8)).order(ByteOrder.LITTLE_ENDIAN).getShort(); 
		byte[] tmp4 = Arrays.copyOfRange(bytes, 8, this.msg_len+8);
		for(int i=0; i<tmp4.length/8;i++){
			this.data[i] = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 8, this.msg_len+8)).order(ByteOrder.LITTLE_ENDIAN).getDouble(i*8);
		}
	}
	
	public byte[] toBytes() throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(this.msg_len+8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putShort(this.dev_id);
		buffer.putInt(this.msg_id);
		buffer.putShort(this.msg_len);
		int len = this.msg_len;
		for(int i=0; i<len/8; i++){
			buffer.putDouble(this.data[i]);
		}
		buffer.flip();
		return buffer.array();
	}
}
