import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

/**
 * This class contains the implementation for a Server. It maintains a HashMap that maps file names to a list of peers
 * that have that file.
 */

public class Server {

  public static void main(String[] args){
    Scanner in = new Scanner(System.in);
    Server server = new Server();
    ServerSkeleton skeleton = new ServerSkeleton(server);
    new Thread(() -> skeleton.listen()).start();
    while(true){
      System.out.println("Please enter a command: list | list {filename} | exit");
      System.out.print("dapster-server >>> ");
      String[] command = in.nextLine().split(" ");
      if(command[0].equals("exit")){
        System.exit(0);
      } else if(command[0].equals("list")){
        if(command.length == 1){
          System.out.println("Indexing Server Contents: ");
          HashMap<String, ArrayList<Peer>> registry = server.registry;
          System.out.println(" +-- " + registry.size() + " files --+ ");
          for(String filename : registry.keySet()){
            System.out.println(String.format(Locale.US, "%20s | %d peers", filename, registry.get(filename).size()));
            for(Peer p : registry.get(filename)){
              System.out.println(" - " + p.getFullAddress());
            }
          }
          continue;
        } else {
          String filename = command[1];
          ArrayList<Peer> peers = server.registry.get(filename);
          if(peers == null){
            System.out.println(String.format(Locale.US, "%20s | %d peers", filename, 0));
            continue;
          }
          System.out.println(String.format(Locale.US, "%20s | %d peers", filename, peers.size()));
          for(Peer p : peers){
            System.out.println(" - " + p.getFullAddress());
          }
          continue;
        }
      }

      System.out.println("Invalid command!");
    }
  }

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
    // Log registration
    System.out.println("Registering " + peerid.getFullAddress() + " with " + filename);

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
    // Log search
    System.out.println("Searching for " + filename + ": found " + (registry.get(filename) == null ? "0" : registry.get(filename).size()) + " peers.");

    return registry.get(filename);
  }
}