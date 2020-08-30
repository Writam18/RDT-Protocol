import java.net.*; 
import java.io.*; 
import java.util.*;

public class FClient1 {

public static void main(String[] args) {

DatagramSocket cs = null;
FileOutputStream fos = null; 
MsgCode mm = new MsgCode();

try {
	cs = new DatagramSocket(); 
	byte[] rd, sd, decMsg;
	String reply, filename=null;
	if(args.length>2)
    	filename=args[2];
	DatagramPacket sp,rp;
	byte count=0;
	int consignment=-1, simACKLoss=0; 
	boolean end = false,check=false;

	String rfilename=filename.substring(0,filename.lastIndexOf('.'))+"1"+filename.substring(filename.lastIndexOf('.'));
// write received data into newfilename
		 fos = new FileOutputStream(rfilename);

	sd = mm.encode(filename.getBytes(), (byte)0, 0); 
	sp = new DatagramPacket(sd, sd.length,InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
	cs.send(sp);
	
		while(!end)
		{
// send ACK
 
			sd=mm.encode(new byte[]{}, count, 2); 
			sp=new DatagramPacket(sd,sd.length,InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
			if(simACKLoss!=6||simACKLoss==6 && check==true){
				if(count!=0){
					System.out.println("Sent ACK #"+count); 
					
				}
				cs.send(sp);
					count++;
				}
				if(simACKLoss!=6||simACKLoss==6 && check==false){
					System.out.println("Forgot ACK #"+count);
				}
				
					rd=new byte[626];
					rp=new DatagramPacket(rd,rd.length); 
					cs.receive(rp);

// concat consignment
				decMsg = mm.decode(rp.getData());

				if(decMsg[0]==consignment){
						System.out.println("Received Consignment #"+decMsg[0]+" duplicate - discarding");
 
				} else {
				
					consignment=decMsg[0];
					System.out.println("Received Consignment #"+decMsg[0]); 
					decMsg = mm.substringByteArray(decMsg, 1, 512);
					reply = new String(decMsg);

				
				if (reply.trim().equals("END")){
						 end = true;
						System.out.println();
				}
				else 
					fos.write(decMsg);
 

				}
					simACKLoss++;

				}
} catch (IOException ex) { 
		System.out.println(ex.getMessage());
 

} finally {

try {
if (fos != null)
	fos.close(); 
if (cs != null)
	cs.close();
} catch (IOException ex) { 
		System.out.println(ex.getMessage());
}
}
}
}
