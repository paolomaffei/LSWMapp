import java.awt.*;
import java.io.*;
import java.security.*;
import java.nio.*;


class checkwatermark extends Frame implements ImgIntfc02 {
  int imgCols;
  int imgRows;
  
  TextField inputField; //file di firma
  
  
  checkwatermark() {
    //GUI per scegliere keypair
    setLayout(new FlowLayout());

    Label instructions = new Label("Signature file");
    add(instructions);

    inputField = new TextField("keypair",5);
    add(inputField);

    setTitle("LSWM - Check watermark");
    setBounds(450,0,200,100);
    setVisible(true);
  }
  
  
  public int[][][] processImg(int[][][] threeDPix, int imgRows, int imgCols) {
    int[][][] signMsg = new int[imgRows][imgCols][4];
    byte[] signature = new byte[64]; //512 bit
    
    this.imgRows = imgRows; //non cambia
    this.imgCols = imgCols; //non cambia
    
    signMsg = getSignatureMessage(threeDPix);
    
    checkSignature(signMsg, threeDPix);
    
    return threeDPix;
  }
  
  
  private int[][][] getSignatureMessage(int[][][] temp3D) {
    int[][][] signMsg = new int[imgRows][imgCols][4];
    
    for(int row = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        signMsg[row][col][0] = (temp3D[row][col][0] & 0xFC); //C = 11111100
        signMsg[row][col][1] = (temp3D[row][col][1] & 0xFC);
        signMsg[row][col][2] = (temp3D[row][col][2] & 0xFC);
        signMsg[row][col][3] = (temp3D[row][col][3] & 0xFC);
      }
    }
  
    return signMsg;
  }
  
  
  public byte[] convertToBytes(int[][][] data) {
    int[] oneDPix = new int[imgCols * imgRows * 4];
    
    //Compatta i 4 valori di ogni pixel (1byte l'uno) in interi da 4byte l'uno
    for(int row = 0,cnt = 0;row < imgRows;row++) {
      for(int col = 0;col < imgCols;col++){
        oneDPix[cnt] = ((data[row][col][0] << 24) & 0xFF000000) | ((data[row][col][1] << 16) & 0x00FF0000) | ((data[row][col][2] << 8) & 0x0000FF00) | ((data[row][col][3]) & 0x000000FF);
        cnt++;
      }
    }
    
    //Trasforma l'array di pixel in byte.
    ByteBuffer b = ByteBuffer.allocate(oneDPix.length * 4);
    IntBuffer i = b.asIntBuffer();
    i.put(oneDPix);
    byte[] msg = new byte[oneDPix.length * 4];
    b.get(msg);
    
    return msg;
  }
  
  
  private void checkSignature(int[][][] signMsg, int[][][] temp3D) {
    
    //estrazione messaggio
    int insertionPoint = 5000;
    byte[] extractedTwoBitBytes = new byte[(imgRows * imgCols - insertionPoint) * 4];
    int twoBitByteCnt = 0;
    
    //leggo i 2 least significant bit di ogni valore di ogni pixel
    for(int row = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        if((row * col > insertionPoint)){
          extractedTwoBitBytes[twoBitByteCnt++] = (byte)(temp3D[row][col][0] & 0x03);
          extractedTwoBitBytes[twoBitByteCnt++] = (byte)(temp3D[row][col][1] & 0x03);
          extractedTwoBitBytes[twoBitByteCnt++] = (byte)(temp3D[row][col][2] & 0x03);
          extractedTwoBitBytes[twoBitByteCnt++] = (byte)(temp3D[row][col][3] & 0x03);
        }
      }
    }
    
    //trasformo gruppi di 4 byte da 2 bit in 1 byte da 8 bit
    byte[] eightBitBytes = new byte[extractedTwoBitBytes.length/4];
    twoBitByteCnt = 0;
    for (int i=0;i<eightBitBytes.length;i++) {
      eightBitBytes[i] = (byte)(extractedTwoBitBytes[twoBitByteCnt++]);
      eightBitBytes[i] = (byte)(eightBitBytes[i] | (extractedTwoBitBytes[twoBitByteCnt++] << 2));
      eightBitBytes[i] = (byte)(eightBitBytes[i] | (extractedTwoBitBytes[twoBitByteCnt++] << 4));
      eightBitBytes[i] = (byte)(eightBitBytes[i] | (extractedTwoBitBytes[twoBitByteCnt++] << 6));
    }
    
    byte[] newSignature = new byte[64];
    for (int i=0;i<64;i++) {
      newSignature[i] = eightBitBytes[i];
    }
 
    System.out.print("Extracted signature: ");
    for (int i=0;i<64;i++)
      System.out.print(Integer.toString(newSignature[i] & 0xff, 16).toUpperCase());
    System.out.println();
    
    String fileName = inputField.getText();
    
    FileInputStream fis = null;
    ObjectInputStream in = null;
    KeyPair kp = null;
    PublicKey pubKey = null;
    PrivateKey prvKey = null;
    Signature sig = null;
    
    try {
      fis = new FileInputStream(fileName);
      in = new ObjectInputStream(fis);
      kp = (KeyPair)in.readObject();
      in.close();
      
      pubKey = kp.getPublic();
      
      sig = Signature.getInstance("MD5withRSA");
      sig.initVerify(pubKey);
      byte[] msg = convertToBytes(signMsg);
      sig.update(msg);
      
      System.out.println("Signature " + ((sig.verify(newSignature))? "verified" : "not verified"));
    }
    catch (Exception e) {
      System.out.println("Invalid signature file.");
    }
    
  }
  
  
  public boolean write() {
      return false;
  }
  
  public boolean firstEvent() {
    return false;
  }
  
}
