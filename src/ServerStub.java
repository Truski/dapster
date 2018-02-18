import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class is the client-side interface of the central indexing server. In order to make requests to a Server, a peer
 * must instantiate a ServerStub.
 */

public class ServerStub {
  private String address;
  private int port;

  /**
   * Creates a ServerStub with the default address (localhost:defaultport)
   */
  public ServerStub() {
    this.address = "localhost";
    this.port = Server.PORT;
  }

  /**
   * Creates a ServerStub with the given address and default port.
   *
   * @param address Address to connect to
   */
  public ServerStub(String address){
    this.address = address;
    this.port = Server.PORT;
  }

  /**
   * Creates a ServerStub with the given address, port combination.
   *
   * @param address Address to connect to
   * @param port Endpoint on the server
   */
  public ServerStub(String address, int port){
    this.address = address;
    this.port = port;
  }

  /**
   * Registers the given peer-filename combination on the server. Contains the peer-side implementation of sending the
   * peer-filename combination over a socket.
   *
   * @param p PeerID (address, port combination)
   * @param filename The name of the file that the peer has to share
   */
  public void register(Peer p, String filename){
    try {
      // Connect to the server
      Socket socket = new Socket(address, port);

      // Send request over the network
      ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
      String rpc = "register";
      os.writeObject(rpc); // Send RPC name
      os.writeObject(p); // Send PeerID
      os.writeObject(filename); // Send filename

      // Release resources
      socket.close();

    } catch (Exception e){
      e.printStackTrace(); // An error occurred
    }
  }

  /**
   * Sends a search request for the given filename to the Server and returns the list of Peers that have that file.
   *
   * @param filename Name of the file to search for
   * @return List of peers that have the file. Returns null on error or no peers.
   */
  public ArrayList<Peer> search(String filename){
    ArrayList<Peer> peers = null;
    try {
      // Connect to the Server
      Socket socket = new Socket(address, port);

      // Send request over the network
      ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
      String rpc = "search";
      os.writeObject(rpc); // Send RPC name
      os.writeObject(filename); // Send filename

      // Receive list of Peers
      ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
      peers = (ArrayList<Peer>) is.readObject();

      // Release resources
      socket.close();

    } catch (Exception e){
      e.printStackTrace(); // An error occurred
    }

    return peers;
  }
}