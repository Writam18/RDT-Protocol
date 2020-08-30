public class MsgCode{

    public int HEX_PER_LINE = 10;
    public byte[] CRLF = new byte[] { 0x0a, 0x0d };
    public byte[] REQUEST = new byte[] {0x52, 0x45, 0x51, 0x55, 0x45, 0x53, 0x54};
    public byte[] RDT = new byte[] { 0x52, 0x44, 0x54 };
    public byte[] ACK = new byte[] {0x41, 0x43, 0x4b};


    public String byteToHex(byte b) {
        int i = b & 0xFF;
        return Integer.toHexString(i);
    }
    
    public void printBytesAsHex(byte[] bytes) {        
        int i=0;
        int j=0;
        while (i<bytes.length) {
            while (i<bytes.length && j<HEX_PER_LINE) {
                System.out.print("0x" + byteToHex(bytes[i++]) + " ");
                j++;
            }
            System.out.println(" ");
            j = 0;
        }
        
    }

    public byte[] encode(byte[] msg, byte seq, int msgType){
        byte[] hexMsg;
        switch(msgType){
            case 0: hexMsg=concatenateByteArrays(REQUEST, msg, CRLF, new byte[]{});
            break;
            case 1: hexMsg=concatenateByteArrays(RDT, new byte[]{seq}, msg, CRLF);
            break;
            case 2: hexMsg=concatenateByteArrays(ACK, new byte[]{seq}, CRLF, new byte[]{});
            break;            
            default: hexMsg=new String("Invalid Message").getBytes();
        }

        return hexMsg;
    }

    public byte[] decode(byte[] hexMsg){
        String msg=new String(hexMsg);
        byte[] d= new byte[1];
        if(msg.startsWith("REQUEST")){
            d = substringByteArray(hexMsg, 7, hexMsg.length-3);
        }
        if(msg.startsWith("RDT")){
            d = substringByteArray(hexMsg, 3, hexMsg.length-3);
        }
        if(msg.startsWith("ACK")){
            d = substringByteArray(hexMsg, 3, hexMsg.length-3);
        }
        return d;
    }


    public byte[] concatenateByteArrays(byte[] a, byte[] b, byte[] c, byte[] d) {
        byte[] result = new byte[a.length + b.length + c.length + d.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        System.arraycopy(c, 0, result, a.length+b.length, c.length);
        System.arraycopy(d, 0, result, a.length+b.length+c.length, d.length);
        return result;
    }

    public byte[] substringByteArray(byte[] a, int start, int end){
        byte[] b = new byte[end-start+1];
        int i, j=start;
        for(i=0;i<end-start+1;i++){
            b[i] = a[j++];
        }
        return b;
    }

}
