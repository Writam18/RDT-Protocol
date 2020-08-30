//RDTServerS
import java.io.*;
import java.net.*;

public class RDTServerS extends Thread{
    public DatagramSocket ss = null;
    public DatagramPacket rp=null, sp=null;
    int count;
    String path;
    public RDTServerS(DatagramPacket rp, DatagramSocket client, int count,String path){
        this.rp = rp;
        this.ss = client;
        this.count = count;   //constructor declaration
        this.path=path;
    }
    public void run()
    {
        System.out.println("Starting Server" + count);
        
        FileInputStream fis = null;
        byte[] rd, sd, sde;
        rd = new byte[128];
        sd = new byte[512];

        
        MsgCode mm = new MsgCode();
        byte decMsg[];
	
        String filename;

        InetAddress ip = null;
        int port=0;


        int count=0, consignment=0, simFrameLoss=0;
        String strConsignment;
        int result = 0; // number of bytes read

        try{
            System.out.println("Server"+this.count+" is up...."); //thread started

            decMsg = mm.decode(rp.getData());
            filename = new String(decMsg).trim();
            // read file into buffer
            System.out.println(path+filename);
            fis = new FileInputStream(path+filename);

            while(result!=-1){
                 try{
                    ss.setSoTimeout(3000); // Set Timeout to 3000ms
                    rd = new byte[100];
                    rp = new DatagramPacket(rd,rd.length);
                    ss.receive(rp);
                    
                    // get client's consignment request from DatagramPacket
                    ip = rp.getAddress();
                    port =rp.getPort();

                    decMsg = mm.decode(rp.getData());
                    consignment = decMsg[0];
                    System.out.println("Server"+this.count+": Client ACK = " + consignment);

                    // prepare data
                    sd = new byte[512];
                    result = fis.read(sd);
                    if (result == -1) {
                        sd = new String("END").getBytes();
                    }

                    sde = mm.encode(sd, (byte)consignment, 1);
                    rp=new DatagramPacket(sde, sde.length, ip, port);
                    
                     if(simFrameLoss!=4){
                        ss.send(rp);
                        System.out.println("Server"+this.count+": Sent Consignment #" + consignment);

                        count++;
                     }
                    
                    rp = null;
                    sp = null;                    
                    
                    simFrameLoss++;
                 } catch(SocketTimeoutException e){
                    System.out.println("Server"+this.count+":Timeout");
                    if((count-1) != consignment){
                        System.out.println("Server"+this.count+": Forgot Consignment #"+consignment);
                        System.out.println("Server"+this.count+": Resent Consignment #" + consignment);
                    } else {
                        System.out.println("Server"+this.count+": Sent Consignment #" + consignment);
                    }
                    sde = mm.encode(sd, (byte)consignment, 1);
                    rp=new DatagramPacket(sde, sde.length, ip, port);
                    ss.send(rp);

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
            System.out.println("Ending Server " + this.count);
        }

    }
}
