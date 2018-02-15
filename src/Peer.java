import java.util.ArrayList;
import java.io.Serializable;

public class Peer implements Serializable {
  private String address = "localhost";
  private int port;
  private transient ServerStub server;

  public Peer(int port){
    this.port = port;
  }

  public void obtain(String filename){
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
    // With the peer's address, open a connection of them
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