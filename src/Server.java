import java.util.ArrayList;
import java.util.HashMap;

public class Server {

  public static final int PORT = 1888;
  public HashMap<String, ArrayList<Peer>> registry;

  public Server(){
    registry = new HashMap<String, ArrayList<Peer>>();
  }

  public void register(Peer peer, String filename){
    if(registry.containsKey(filename)){
      registry.get(filename).add(peer);
    } else {
      ArrayList<Peer> peers = new ArrayList<Peer>();
      peers.add(peer);
      registry.put(filename, peers);
    }
  }

  public ArrayList<Peer> search(String filename) {
    return registry.get(filename);
  }
}