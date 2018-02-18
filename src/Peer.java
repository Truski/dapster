import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * The Peer object represents a peer. In a peer program, it makes requests to the Indexing Server and other peers
 * to download files. It can be sent over the network as a PeerID. A peer also services requests from other peers via
 * its skeleton.
 */

public class Peer implements Serializable {
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
      is = new FileInputStream(filename); // Open file and grab stream
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
   * @return Returns true if successful, false is no peer with file exists or error downloading.
   */
  public boolean get(String filename){
    // Get list of peers from the server
    ArrayList<Peer> peers = server.search(filename);

    // If no peers or error, return false
    if(peers == null){
      return false;
    }

    // Prune an instance of this peer (disallow downloading from self)
    for(Peer p : peers){
      if(p.getFullAddress().equals(this.getFullAddress())){
        peers.remove(p);
        break;
      }
    }

    // If no peers, return false
    if(peers.size() == 0){
      return false;
    }

    // Create stub to Peer to download file from
    PeerStub peer = new PeerStub(peers.get(0));

    // Download file from Peer
    if(peer.obtain(filename)){
      // Print Success message
      System.out.println("Successfully downloaded " + filename + " from " + peers.get(0).getFullAddress());

      // Print file contents if small file
      try {
        long length = new File(filename).length();
        if(length > 1024){
          System.out.println("File is large - will not display.");
        } else {
          FileInputStream fileInputStream = new FileInputStream(filename);
          byte[] buffer = new byte[1024];
          fileInputStream.read(buffer, 0, 1024);
          System.out.println("<==|== Start of File Contents ==||==>");
          System.out.println(new String(buffer));
          System.out.println("<==|==  End of File Contents  ==||==>");
        }
      } catch (Exception e){
        e.printStackTrace();
      }

      return true; // Successfully downloaded file
    }

    return false; // Error downloading file
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