import java.net.*;
 import java.io.*; 
import java.util.*;

public class FServer1 {

public static void main(String[] args) {

DatagramSocket ss = null;
 FileInputStream fis = null;
 DatagramPacket rp, sp; 
byte[] rd, sd, sde;
rd = new byte[128]; 
sd = new byte[512];
rp=sp=new DatagramPacket(rd, rd.length); 
MsgCode mm = new MsgCode();
byte decMsg[];
String filename;

InetAddress ip = null;
 int port=0;


int count=0, consignment=0, simFrameLoss=0;
 String strConsignment;
 
int result = 0; // number of bytes read


try {
	ss = new DatagramSocket(Integer.parseInt(args[0]));
	String path = args[1]; 
	System.out.println(path);
	System.out.println("Server is up	");

	rd = new byte[100];

	rp = new DatagramPacket(rd,rd.length);
	 ss.receive(rp);
	decMsg = mm.decode(rp.getData());
	 filename = new String(decMsg).trim();
// read file into buffer
	System.out.println(path+filename);
	fis = new FileInputStream(path+filename);
	//System.out.println(path+filename);



	while(true && result!=-1){ 
	try{
		ss.setSoTimeout(3000); // Set Timeout to 3000ms

			rd = new byte[100];
			rp = new DatagramPacket(rd,rd.length); 
			ss.receive(rp);

// get client's consignment request from DatagramPacket ip = rp.getAddress();
			ip=rp.getAddress();
			port =rp.getPort();

// System.out.println("Client IP Address = " + ip);
// System.out.println("Client port = " + port);

		decMsg = mm.decode(rp.getData()); 
		consignment = decMsg[0];
		if(consignment!=0)
		{
		System.out.println("recieved ACK = " + consignment);
		}

// prepare data
		sd = new byte[512]; 
		result = fis.read(sd); 
		if (result == -1) {
		sd = new String("END").getBytes();
		}
 

		sde = mm.encode(sd, (byte)consignment, 1); 
		sp=new DatagramPacket(sde, sde.length, ip, port);

		if(simFrameLoss!=4){ 
				ss.send(sp);
				System.out.println("Sent Consignment #" + consignment); 
				count++;
			}

		rp = null; 
		sp = null;

		simFrameLoss++;
		if(result==-1)
		{
			System.out.println("END");
		}
} catch(SocketTimeoutException e){ 
	System.out.println("Timeout"); 
		if((count-1) != consignment){
			System.out.println("Forgot Consignment #"+consignment); 
			System.out.println("Resent Consignment #" + consignment);
		} else {
				System.out.println("Sent Consignment #" + consignment);
			}
			sde = mm.encode(sd, (byte)consignment, 1);
 			sp=new DatagramPacket(sde, sde.length, ip, port); 
			ss.send(sp);
			count++;
			 continue;

}
}

} catch (IOException ex) { 
		System.out.println(ex.getMessage());

} finally { 
	try {
		if (fis != null)
		fis.close();
} catch (IOException ex) { 
		System.out.println(ex.getMessage());
}	
}

}
}

