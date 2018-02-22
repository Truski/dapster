import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

/**
 * This class contains the implementation for a Server. It maintains a HashMap that maps file names to a list of peers
 * that have that file.
 */

public class Server {

  /**
   * The entry point for Server processes. A shell prompt is provided to inspect the registry as well as exit
   * gracefully, saving the log file.
   *
   * @param args Command line arguments (not used)
   */
  public static void main(String[] args){

    // Setup the Server
    Server server = new Server(); // Base server object with data structures
    ServerSkeleton skeleton = new ServerSkeleton(server); // Skeleton to listen to incoming connections

    // Start the Server thread that listens for incoming connections
    new Thread(() -> skeleton.listen()).start();

    // Run the command line interface
    runCLI(server);
  }

  /**
   * Function to run the command line interface. Allows server admins to inspect the registry.
   *
   * @param server The server to run the command line for
   */
  private static void runCLI(Server server) {
    // Create scanner for user input on the CLI and run shell loop
    Scanner in = new Scanner(System.in);
    while(true){
      // Print out available commands and prompt
      System.out.println("Please enter a command: list | list {filename} | exit");
      System.out.print("dapster-server >>> ");

      // Read user input
      String[] command = in.nextLine().split(" ");

      // Check command name
      if(command[0].equals("exit")){
        Logger.EndLogging();
        System.exit(0);
      } else if(command[0].equals("list")){
        if(command.length == 1){
          // Print entire registry to the console for debugging / inspection
          System.out.println("Indexing Server Contents: ");
          HashMap<String, ArrayList<Peer>> registry = server.getRegistry();
          System.out.println(" +-- " + registry.size() + " files --+ ");
          for(String filename : registry.keySet()){
            System.out.println(String.format(Locale.US, "%20s | %d peers", filename, registry.get(filename).size()));
            for(Peer p : registry.get(filename)){
              System.out.println(" - " + p.getFullAddress());
            }
          }
          continue;
        } else {
          // Print the list of peers for a specific file
          String filename = command[1];
          ArrayList<Peer> peers = server.getRegistry().get(filename);
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

      // Announce invalid command if none of the available options were selected
      System.out.println("Invalid command!");
    }
  }

  // Start of the Server class object
  public static final int PORT = 1888;
  private HashMap<String, ArrayList<Peer>> registry;

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
   *
   * @param filename Name of the file to search for
   * @return Returns a list of peers that have that file. Returns null if no peer has the file.
   */
  public synchronized ArrayList<Peer> search(String filename) {

    return registry.get(filename);
  }

  /**
   * Get a reference to the Registry/Index for server admin inspection
   *
   * @return This server's registry
   */
  public HashMap<String, ArrayList<Peer>> getRegistry() {
    return registry;
  }
}