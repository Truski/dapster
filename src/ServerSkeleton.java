import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class acts as a network interface for the server part of the peer. It listens for incoming peer requests.
 */

public class ServerSkeleton {
  private Server server;

  /**
   * Creates a Server Skeleton that will listen to incoming connections for the given Server.
   *
   * @param s Server that will service requests received by this skeleton
   */
  public ServerSkeleton(Server s){
    this.server = s;

    // Initializes the server's logging functionality
    Logger.StartLogging();
  }

  /**
   * Listens for incoming connections and dispatches them to the appropriate Server functions.
   */
  public void listen(){
    try {
      // Create server socket
      ServerSocket socket = new ServerSocket(Server.PORT);

      // Forever listen for incoming requests
      while(true){
        Socket s = socket.accept(); // Block until new request

        // Start new thread to service request
        new Thread(() -> {
          try {
            // Receive RPC name
            ObjectInputStream is = new ObjectInputStream(s.getInputStream());
            String rpc = (String) is.readObject();

            // Dispatch request based on RPC name
            if(rpc.equals("register")){
              register(is);
            } else if(rpc.equals("search")){
              ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream()); // Get OutputStream to send data back
              search(is, os);
            }

            // Release resources
            s.close();

          } catch (Exception e){
            e.printStackTrace(); // An error occurred when servicing request
          }
        }).start();

      }
    } catch (Exception e){
      e.printStackTrace(); // An error occurred when making connection
    }
  }

  /**
   * Deserializes Server::register parameters and dispatches the register request to Server object.
   *
   * @param is Stream containing parameters to deserialize.
   * @throws Exception if an error occurs when reading or writing over the network
   */
  private void register(ObjectInputStream is) throws Exception {
    // Receive RPC parameters
    Peer p = (Peer) is.readObject(); // Receive PeerID
    String filename = (String) is.readObject(); // Receive filename

    // Dispatch call to Server
    server.register(p, filename);

    // Log action
    Logger.Log("Registering " + p.getFullAddress() + " with file " + filename +".");
  }

  /**
   * Deserializes Server::search parameters and dispatches the search request to the Server. Sends results (the list of
   * peers) over the the network.
   *
   * @param is Stream containing parameters to deserialize
   * @param os Stream to send result to
   * @throws Exception if an error occurs when reading or writing over the network
   */
  private void search(ObjectInputStream is, ObjectOutputStream os) throws Exception {
    // Receive RPC parameters
    String filename = (String) is.readObject(); // Receive filename

    // Dispatch call to Server
    ArrayList<Peer> peers = server.search(filename);

    // Return result of RPC over the network
    os.writeObject(peers);

    // Log action
    Logger.Log("Searching for " + filename + ": " + ((peers == null) ? 0 : peers.size()) + " peers found.");
  }
}