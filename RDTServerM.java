//RDTServerM
import java.io.*;
import java.net.*;

public class RDTServerM
{
     public static void main(String[] args) throws IOException
     {

          DatagramSocket serverSocket = null;
          DatagramPacket rp=null;
          byte[] rd = null;
          int count = 0;
          boolean listening =true;
          String path = args[1];
          try{
            serverSocket = new DatagramSocket(Integer.parseInt(args[0]));
            //String path = args[1];
          } catch(IOException ex){            
            System.out.println(ex.getMessage());
            System.exit(-1);
          }

          while(listening)
          {
            try{
              rd = new byte[100];
              rp = new DatagramPacket(rd,rd.length);
              serverSocket.setSoTimeout(Integer.MAX_VALUE);
              serverSocket.receive(rp);

              RDTServerS worker = new RDTServerS(rp, serverSocket, count++,path);
              worker.start();
              while(worker.isAlive()){}
            }
            catch(Exception e){
              System.out.println(e.getMessage());              
            }
          }
          serverSocket.close();

     }
}
