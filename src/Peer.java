import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.io.Serializable;

public class Peer implements Serializable {
  private String address = "localhost";
  private int port;
  private transient ServerStub server;

  public Peer(int port){
    this.port = port;
  }

  public FileInputStream obtain(String filename) {
    FileInputStream is = null;
    try {
      is = new FileInputStream(filename);
    } catch (Exception e){
      e.printStackTrace();
    }
    return is;
  }

  public void get(String filename){
    // Connect to indexing server to find peers that have this file
    ArrayList<Peer> peers = server.search(filename);
    if(peers == null){
      System.out.println("Obtained 0 peers for file " + filename);
      return;
    } else {
      for(Peer p : peers){
        if(p.getAddress().equals(this.getAddress())){
          peers.remove(p);
          break;
        }
      }
    }
    System.out.println("Obtained " + peers.size() + " peers for file " + filename);
    for(Peer p : peers){
      System.out.println(p.getAddress());
    }

    if(peers.size() == 0){
      return;
    }

    PeerStub peer = new PeerStub(peers.get(0));

    if(peer.obtain(filename)){
      System.out.println("Successfully downloaded " + filename + "from " + peers.get(0).getAddress());
      try {
        long length = new File(filename).length();
        if(length > 1024){
          System.out.println("File is large - will not display.");
        } else {
          FileInputStream fileInputStream = new FileInputStream(filename);
          byte[] buffer = new byte[1024];
          fileInputStream.read(buffer, 0, 1024);
          System.out.println("<==|== Start of File Contents ==||==>");
          System.out.println(new String(buffer));
          System.out.println("<==|==  End of File Contents  ==||==>");
        }
      } catch (Exception e){
        e.printStackTrace();
      }
    }

    // System.out.println("Successfully read file " + filename + " from Peer " + peers.get(0).getAddress() + ". File has " + file.length + " byte.");
  }

  public String getHostName(){
    return address;
  }

  public int getPort(){
    return port;
  }

  public String getAddress(){
    return address + ":" + port;
  }

  public void setServerStub(ServerStub s){
    this.server = s;
  }

  public void register(String filename){
    server.register(this, filename);
  }

}