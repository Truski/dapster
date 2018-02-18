import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class acts as a network interface for the server part of a peer. It listens for incoming requests from other
 * peers, and starts new threads to send files.
 */

public class PeerSkeleton {
	private Peer peer;

  /**
   * Creates a Peer Skeleton that will listen to incoming connections for the given Peer.
   *
   * @param p The Peer that the skeleton is to provide interface for
   */
	public PeerSkeleton(Peer p){
    this.peer = p;
  }

  /**
   * Listens for incoming connections. Should be called on a separate thread so that a peer could also make its own
   * requests.
   */
  public void listen(){
    try {
      // Create server socket
      ServerSocket socket = new ServerSocket(peer.getPort());

      // Forever listen for incoming requests
      while(true){
        Socket s = socket.accept(); // Block until new request
        new Thread(() -> obtain(s)).start(); // Start new thread to service request
      }
    } catch (Exception e){
        e.printStackTrace();
    }
  }

  /**
   * Services request by the Peer on the other side of the connection of the socket. Sends the requested file over
   * the network.
   * @param s Connection to listen for file name ane send file over
   */
  private void obtain(Socket s){
    try {
      // Receive filename
      ObjectInputStream is = new ObjectInputStream(s.getInputStream());
      String filename = (String) is.readObject();

      // Send file
      FileInputStream fis = peer.obtain(filename); // File reading stream
      OutputStream os = s.getOutputStream(); // File uploading stream

      int count; // Number of bytes read from file
      byte[] buffer = new byte[4096]; // Buffer to store file in
      while ((count = fis.read(buffer)) > 0){ // Read from file into buffer
          os.write(buffer, 0, count); // Write to Peer from Buffer
      }

      // Release resources
      s.close();
      fis.close();

    } catch (Exception e){
	    e.printStackTrace(); // An error occurred
    }
  }
}