import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class contains the implementation for a Server. It maintains a HashMap that maps file names to a list of peers
 * that have that file.
 */

public class Server {

  public static final int PORT = 1888;
  public HashMap<String, ArrayList<Peer>> registry;

  /**
   * Creates a Server object with an empty registry.
   */
  public Server() {
    this.registry = new HashMap<String, ArrayList<Peer>>();
  }

  /**
   * Adds the Peer-Filename combination to the Indexing registry.
   *
   * @param peerid PeerID (Address, Port) of the file to add
   * @param filename Name of the file that that Peer has
   */
  public synchronized void register(Peer peerid, String filename){
    // Check if the filename is in the HashMap
    if(registry.containsKey(filename)){
      // If the filename is in the index, just add the new Peer to the list
      registry.get(filename).add(peerid);
    } else {
      // If the filename is not in the index, make a new list and add the new Peer to it
      ArrayList<Peer> peers = new ArrayList<Peer>();
      peers.add(peerid);

      // Add the new list to the index
      registry.put(filename, peers);
    }
  }

  /**
   * Returns a list of Peers that have the the file with the given name.
   * @param filename Name of the file to search for
   * @return Returns a list of peers that have that file. Returns null if no peer has the file.
   */
  public synchronized ArrayList<Peer> search(String filename) {
    return registry.get(filename);
  }
}