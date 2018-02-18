import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class is the client-side interface of a Peer server. When connecting to and requesting files from other peers,
 * a peer must instantiate a PeerStub with a Peer it gets from the Server.
 */

public class PeerStub{
  private String address;
  private int port;

  /**
   * Creates a Peer Stub that will connect to the given Peer.
   *
   * @param p The peer to connect to
   */
  public PeerStub(Peer p){
    this.address = p.getHostName();
    this.port = p.getPort();
  }

  /**
   * Downloads the the given File from the Peer to the shared directory.
   *
   * @param filename File to download
   * @return True if the download was successful, False otherwise.
   */
  public boolean obtain(String filename) {
    try {
      // Connect to the Peer
      Socket socket = new Socket(address, port);

      // Send desired filename
      ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
      os.writeObject(filename);

      // Download File
      InputStream is = socket.getInputStream(); // File download stream
      FileOutputStream fos = new FileOutputStream(filename); // File saving stream

      int count; // Number of bytes read
      byte[] buffer = new byte[4096]; // Buffer to store file in
      while((count = is.read(buffer)) > 0 ){ // Read from Peer into buffer
        fos.write(buffer, 0, count); // Write to file from buffer
      }

      // Close resources
      socket.close();
      fos.close();

      return true; // Successfully downloaded file.
    } catch (Exception e){
      e.printStackTrace();
    }
    return false; // Failed to download. Something went wrong.
  }
}