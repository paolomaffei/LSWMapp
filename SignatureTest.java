// File: src\jsbook\ch3\ex1\SignatureTest.java
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.Signature;
import java.io.*;
//import java.util;

public class SignatureTest {
  private static byte[] sign(String datafile, PrivateKey prvKey, String sigAlg) throws Exception {
    Signature sig = Signature.getInstance(sigAlg);
    sig.initSign(prvKey);
    FileInputStream fis = new FileInputStream(datafile);
    byte[] dataBytes = new byte[1024];
    int nread = fis.read(dataBytes);
    while (nread > 0) {
      sig.update(dataBytes, 0, nread);
      nread = fis.read(dataBytes);
    };
    return sig.sign();
  }
  
  private static boolean verify(String datafile, PublicKey pubKey, String sigAlg, byte[] sigbytes) throws Exception {
    Signature sig = Signature.getInstance(sigAlg);
    sig.initVerify(pubKey);
    FileInputStream fis = new FileInputStream(datafile);
    byte[] dataBytes = new byte[1024];
    int nread = fis.read(dataBytes);
    while (nread > 0) {
      sig.update(dataBytes, 0, nread);
      nread = fis.read(dataBytes);
    };
    return sig.verify(sigbytes);
  }
  
  public static void main(String[] unused) throws Exception {
    // Generate a key-pair
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(512); // 512 is the keysize.
    KeyPair kp = kpg.generateKeyPair();
    PublicKey pubk = kp.getPublic();
    PrivateKey prvk = kp.getPrivate();

    String datafile = "SignatureTest.java";
    byte[] sigbytes = sign(datafile, prvk, "MD5withRSA");
    //System.out.println("Signature(in hex):: " + Util.byteArray2Hex(sigbytes));

    boolean result = verify(datafile, pubk, "MD5withRSA", sigbytes);
    System.out.println("Signature Verification Result = " + result);
    
    //write generated keypair to file
    String filename = "keypair";
     
    FileOutputStream fos = null;
    ObjectOutputStream out = null;
    try {
      fos = new FileOutputStream(filename);
      out = new ObjectOutputStream(fos);
      out.writeObject(kp);
      out.close();
    }
    catch(IOException ex) {
      ex.printStackTrace();
    }
    
    //now try to recover it from the file
    FileInputStream fis = null;
    ObjectInputStream in = null;
    KeyPair newkp = null;
    try
    {
      fis = new FileInputStream(filename);
      in = new ObjectInputStream(fis);
      newkp = (KeyPair)in.readObject();
      in.close();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    
    PrivateKey newprvk = newkp.getPrivate();
    
    System.out.println(prvk.toString());
    System.out.println(newprvk.toString());
    
  }
}
