import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PeerStub{
  private String address;
  private int port;

  public PeerStub(Peer p){
    this.address = p.getHostName();
    this.port = p.getPort();
  }

  public boolean obtain(String filename) {
    try {
      Socket socket = new Socket(address, port);
      ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
      os.writeObject(filename);
      InputStream is = socket.getInputStream();
      FileOutputStream fos = new FileOutputStream(filename);
      int count;
      byte[] buffer = new byte[4096];
      while((count = is.read(buffer)) > 0 ){
        fos.write(buffer, 0, count);
      }
      socket.close();
      fos.close();
      return true;
    } catch (Exception e){
      e.printStackTrace();
    }
    return false;
  }
}