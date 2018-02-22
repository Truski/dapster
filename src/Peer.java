import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.Serializable;

/**
 * The Peer object represents a peer. In a peer program, it makes requests to the Indexing Server and other peers
 * to download files. It can be sent over the network as a PeerID. A peer also services requests from other peers via
 * its skeleton.
 */

public class Peer implements Serializable {

  public static void main(String[] args){
    if(args.length == 0){
      System.out.println("Please provide a port for the Peer to run on.");
      return;
    }

    int port = Integer.parseInt(args[0]);
    Scanner in = new Scanner(System.in);
    Peer peer = new Peer(port);
    ServerStub serverStub = new ServerStub();
    peer.setServerStub(serverStub);
    PeerSkeleton peerSkeleton = new PeerSkeleton(peer);

    new Thread(() -> peerSkeleton.listen()).start();

    if(args.length == 2){
      if(args[1].equals("auto") || args[1].equals("-a")){
        System.out.println("Auto-registering files in shared directory.");
        File shared = new File("./shared/");
        for(String filename : shared.list()){
          System.out.println("Registering file " + filename);
          peer.register(filename);
        }
      }
    }

    while(true){
      System.out.println("Please enter a command: get {filename} | register {filename} | exit");
      System.out.print("dapster-peer:" + port + " >>> ");
      String[] command = in.nextLine().split(" ");
      String function = command[0];
      if(function.equals("exit")){
        System.exit(0);
      } else if(function.equals("get")){

        if (command.length == 2){
          String filename = command[1];
          peer.get(filename);
          continue;
        }

      } else if(function.equals("register")){

        if(command.length == 2){

          String filename = command[1];
          peer.register(filename);
          continue;

        }

      } else if(function.equals("test")){
        if(command.length == 2){
          int times = Integer.parseInt(command[1]);
          long total = 0;
          for(int i = 0; i < times; i++){
            long startTime = System.nanoTime();
            serverStub.search(i + ".txt");
            long endTime = System.nanoTime();
            total += endTime - startTime;
          }
          System.out.println("Total: " + total /1000000f + "ms ; Average: " + total / 1000000f / times + "ms .");
          continue;
        }

      }

      System.out.println("Invalid command!");
    }
  }

  public static final String SHARED_DIR= "shared/";
  private String address;
  private int port;
  private transient ServerStub server; // Reference to server. Not part of a PeerID so transient

  /**
   * Creates a peer that listens to other peer requests on the given port.
   *
   * @param port Endpoint of the server-aspect of this Peer
   */
  public Peer(int port){
    this.address = "localhost";
    this.port = port;
  }

  /**
   * Obtains a FileStream for the requested file.
   *
   * @param filename Name of the file to obtain
   * @return Returns the stream to the file, null if no file found
   */
  public FileInputStream obtain(String filename) {
    FileInputStream is = null;

    try {
      is = new FileInputStream(SHARED_DIR + filename); // Open file and grab stream
    } catch (Exception e){
      e.printStackTrace(); // An error occurred
    }

    return is;
  }

  /**
   * Downloads the file with the given name from a peer. First checks the indexing server for peers that have the file,
   * then downloads it if there is a peer with the file.
   *
   * @param filename Name of the file to download
   */
  public void get(String filename){
    // Get list of peers from the server
    ArrayList<Peer> peers = server.search(filename);

    // If no peers or error, inform user
    if(peers == null){
      System.out.println("No peers with given file name \"" + filename + "\" or server error!");
      return;
    }

    // Prune an instance of this peer (disallow downloading from self)
    for(Peer p : peers){
      if(p.getFullAddress().equals(this.getFullAddress())){
        peers.remove(p);
        break;
      }
    }

    // If no peers, inform user
    if(peers.size() == 0){
      System.out.println("No peers with given file name \"" + filename + "\".");
      return;
    }

    // Announce list of peers with the given file
    System.out.println("| Found " + peers.size() + " peers for file " + filename + ".");
    for(Peer p : peers){
      System.out.println("|==> " + p.getFullAddress());
    }

    // Select random peer to download from
    int random = (int) (Math.random() * peers.size());
    Peer selectedPeer = peers.get(random);

    // Announce selected peer to download from
    System.out.println("Downloading from " + selectedPeer.getFullAddress() + ".");

    // Create stub to Peer to download file from
    PeerStub peer = new PeerStub(selectedPeer);

    // Download file from Peer
    if(peer.obtain(filename)){
      // Register the file now that the peer has it
      this.register(filename);
      // Print Success message
      System.out.println("Successfully downloaded " + filename + " from " + peers.get(0).getFullAddress());

      // Print file contents if small file
      try {
        long length = new File(SHARED_DIR + filename).length();
        System.out.println("File size: " + length + " bytes.");
        if(length > 1024){
          System.out.println("File is larger than 1K - will not display.");
        } else {
          FileInputStream fileInputStream = new FileInputStream(SHARED_DIR + filename);
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
  }

  /**
   * Gets the host name (without port) of the server part of the Peer
   * @return Host name of the Peer
   */
  public String getAddress(){
    return address;
  }


  /**
   * Gets the port of the server part of the Peer
   * @return Endpoint of the Peer
   */
  public int getPort(){
    return port;
  }

  /**
   * Gets the full name of the server part of the Peer (PeerID)
   * @return Full address of the peer
   */
  public String getFullAddress(){
    return address + ":" + port;
  }

  /**
   * Sets the interface for the Peer to communicate with the Indexing server.
   * @param s The server stub that the peer will interface with
   */
  public void setServerStub(ServerStub s){
    this.server = s;
  }

  /**
   * Registers with the Indexing Server the PeerID and the name of a file it is willing to share.
   * @param filename The name of the file to register with
   */
  public void register(String filename){
    server.register(this, filename);
  }

}