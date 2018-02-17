import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerSkeleton {
	private Peer peer;

	public PeerSkeleton(Peer p){
    this.peer = p;
  }

  public void listen(){
    try {
      ServerSocket socket = new ServerSocket(p.getPort());
      while(true){
        Socket s = socket.accept();
        new Thread(){
          public void run(){
            PeerSkeleton.obtain(s);
          }
        }.run();
      }
    }
  }

  public void obtain(Socket s){
	  try {
      ObjectInputStream is = new ObjectInputStream(s.getInputStream());
      String filename = (String) is.readObject();
      byte[] file = peer.obtain(filename);
      ByteArrayOutputStream os = new ByteArrayOutputStream(s.getOutputStream());
      os.write(file);
      s.close();
    } catch (Exception e){
	    e.printStackTrace();
    }
  }
}