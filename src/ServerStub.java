import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerStub {
  private String address;

  public ServerStub(){
    this.address = "localhost";
  }

  public void register(Peer p, String filename){
    try {
      Socket socket = new Socket(address, Server.PORT);
      System.out.println("Sending " + filename + " to " + socket.getRemoteSocketAddress());
      ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
      String rpc = "register";
      os.writeObject(rpc);
      os.writeObject(p);
      os.writeObject(filename);

      socket.close();
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  public ArrayList<Peer> search(String filename){
    ArrayList<Peer> peers = null;
    try {
      Socket socket = new Socket(address, Server.PORT);
      System.out.println("Searching for  " + filename + " at " + socket.getRemoteSocketAddress());
      ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
      String rpc = "search";
      os.writeObject(rpc);
      os.writeObject(filename);
      ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
      peers = (ArrayList<Peer>) is.readObject();
      socket.close();
    } catch (Exception e){
      e.printStackTrace();
    }
    return peers;
  }
}