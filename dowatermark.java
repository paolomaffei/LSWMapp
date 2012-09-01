import java.awt.*;
import java.io.*;
import java.security.*;
import java.nio.*;


class dowatermark extends Frame implements ImgIntfc02 {
  int[][][] temp3D; //array di pixel
  int imgCols;
  int imgRows;
  
  TextField inputField; //file di firma
  boolean abort = false;
  
  
  dowatermark(){
    //GUI per scegliere keypair
    setLayout(new FlowLayout());

    Label instructions = new Label("Signature file");
    add(instructions);

    inputField = new TextField("keypair",5);
    add(inputField);

    setTitle("LSWM - Do watermark");
    setBounds(450,0,200,100);
    setVisible(true);
  }

  
  public int[][][] processImg(int[][][] threeDPix, int imgRows, int imgCols) {
    temp3D = new int[imgRows][imgCols][4];
    int[][][] signMsg = new int[imgRows][imgCols][4]; //array sul quale effettuare firma (due lsb azzerati)
    byte[] signature = new byte[64]; //512 bit
    
    this.imgRows = imgRows; //non cambia
    this.imgCols = imgCols; //non cambia
    
    //copia di threeDPix in temp3D
    for(int row = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        temp3D[row][col][0] = threeDPix[row][col][0];
        temp3D[row][col][1] = threeDPix[row][col][1];
        temp3D[row][col][2] = threeDPix[row][col][2];
        temp3D[row][col][3] = threeDPix[row][col][3];
      }
    }
    
    /*System.out.println("Stato matrice temp3D");
    for (int i=0;i<35;i++)
      System.out.print(temp3D[1][i][1] + " ");
    System.out.println();*/
    
    signMsg = getSignatureMessage(); //restituisce messaggio da firmare
    
    signature = getSignature(signMsg); //restituisce byte della firma
    
    if (!this.abort) {
      writeSignature(signature); //scrive bit di firma su temp3D
      
      /*System.out.println("Stato matrice temp3D");
      for (int i=0;i<35;i++)
        System.out.print(temp3D[1][i][1] + " ");
      System.out.println();*/
      
      return temp3D;
    }
    else
      return threeDPix;
  }
  
  
  private int[][][] getSignatureMessage() {
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
  
  
  private byte[] getSignature(int[][][] signMsg) {
    String fileName = inputField.getText();
    
    FileInputStream fis = null;
    ObjectInputStream in = null;
    KeyPair kp = null;
    PublicKey pubKey = null;
    PrivateKey prvKey = null;
    Signature sig = null;
    
    try {
      //leggere chiave
      fis = new FileInputStream(fileName);
      in = new ObjectInputStream(fis);
      kp = (KeyPair)in.readObject();
      in.close();
      
      pubKey = kp.getPublic();
      prvKey = kp.getPrivate();
      
      //convertire int[] in byte[]
      byte[] msg = convertToBytes(signMsg);
      
      //generare firma per questi byte
      sig = Signature.getInstance("MD5withRSA");
      sig.initSign(prvKey);
      sig.update(msg);
      
      return sig.sign();
    }
    catch(Exception ex) {
      System.out.println("Invalid signature file.");
      //ex.printStackTrace();
      this.abort = true;
      return new byte[1];
    }
  }
  
  
  //Scrive la firma nei 2 least significant bit di ogni pixel di temp3D
  private void writeSignature(byte[] signature) {
    System.out.println("Signature Length = " + signature.length);
    
    System.out.print("Original signature: ");
    for (int i=0;i<signature.length;i++)
      System.out.print(Integer.toString(signature[i] & 0xff, 16).toUpperCase());
    System.out.println();
    
    /*System.out.println("Stato matrice temp3D");
    for (int i=0;i<35;i++)
      System.out.print(temp3D[1][i][1] + " ");
    System.out.println();*/
    
    byte[] twoBitBytes = new byte[4 * signature.length]; //array di byte da 2 bit
    
    int twoBitByteCnt = 0;
    for(byte element:signature){
      twoBitBytes[twoBitByteCnt++] = (byte)(element & 0x03); //3 = 000000011
      twoBitBytes[twoBitByteCnt++] = (byte)((element >> 2) & 0x03); 
      twoBitBytes[twoBitByteCnt++] = (byte)((element >> 4) & 0x03);
      twoBitBytes[twoBitByteCnt++] = (byte)((element >> 6) & 0x03);
    }
    
    //inserisco i byte da 2 bit nei 2 lsb di ogni valore di ogni pixel
    int insertionPoint = 5000;
    twoBitByteCnt = 0;
    for(int row = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        
        if((row * col > insertionPoint) && (twoBitByteCnt < twoBitBytes.length)) {
          temp3D[row][col][0] = (temp3D[row][col][0] & 0xFC) | twoBitBytes[twoBitByteCnt++];
          temp3D[row][col][1] = (temp3D[row][col][1] & 0xFC) | twoBitBytes[twoBitByteCnt++];
          temp3D[row][col][2] = (temp3D[row][col][2] & 0xFC) | twoBitBytes[twoBitByteCnt++];
          temp3D[row][col][3] = (temp3D[row][col][3] & 0xFC) | twoBitBytes[twoBitByteCnt++];
        }
      }
    }
    
  }
  
  
  public boolean write() {
    if (!this.abort)
      return true;
    else
      return false;
  }
  
  public boolean firstEvent() {
    return false;
  }
  
}
