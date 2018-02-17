import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerSkeleton {
	private Peer peer;

	public PeerSkeleton(Peer p){
    this.peer = p;
  }

  public void listen(){
    try {
      ServerSocket socket = new ServerSocket(peer.getPort());
      while(true){
        Socket s = socket.accept();
        new Thread(() -> obtain(s)).start();
      }
    } catch (Exception e){
        e.printStackTrace();
    }
  }

  public void obtain(Socket s){
    try {
      ObjectInputStream is = new ObjectInputStream(s.getInputStream());
      String filename = (String) is.readObject();
      FileInputStream fis = peer.obtain(filename);

      OutputStream os = s.getOutputStream();
      int count;
      byte[] buffer = new byte[4096];
      while ((count = fis.read(buffer)) > 0){
          os.write(buffer, 0, count);
      }
      s.close();
      fis.close();

    } catch (Exception e){
	    e.printStackTrace();
    }
  }
}