import java.io.File;
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

  public byte[] obtain(String filename) throws Exception {
    File file = new File(filename);
    byte[] bytes = Files.readAllBytes(file.toPath());
    return bytes;
  }

  public void get(String filename){
    // Connect to indexing server to find peers that have this file
    ArrayList<Peer> peers = server.search(filename);
    if(peers == null){
      System.out.println("Obtained 0 peers for file " + filename);
      return;
    }
    System.out.println("Obtained " + peers.size() + " peers for file " + filename);
    for(Peer p : peers){
      System.out.println(p.getAddress());
    }

    if(peers.size() == 0){
      return;
    }

    PeerStub peer = new PeerStub(peers.get(0));

    byte[] file = peer.obtain(filename);

    System.out.println("Successfully read file " + filename + " from Peer " + peers.get(0).getAddress() + ". File has " + file.length + " byte.");
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