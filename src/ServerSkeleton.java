import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ServerSkeleton {
  private Server server;

  public ServerSkeleton(Server s){
    this.server = s;
  }

  public void listen(){
    try {
      ServerSocket socket = new ServerSocket(Server.PORT);
      while(true){
        System.out.println("Waiting for client to connect...");
        Socket s = socket.accept();

        System.out.println("Just connected to " + s.getRemoteSocketAddress());

        ObjectInputStream is = new ObjectInputStream(s.getInputStream());

        String rpc = (String) is.readObject();

        if(rpc.equals("register")){
          register(is);
        } else if(rpc.equals("search")){
          ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
          search(is, os);
        }

        s.close();

      }
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  private void register(ObjectInputStream is) throws Exception {
    Peer p = (Peer) is.readObject();
    String filename = (String) is.readObject();
    System.out.println("Registering " + p.getAddress() + " with file " + filename);
    server.register(p, filename);
  }

  private void search(ObjectInputStream is, ObjectOutputStream os) throws Exception {
    String filename = (String) is.readObject();
    ArrayList<Peer> peers = server.search(filename);
    os.writeObject(peers);
    if(peers == null){
      System.out.println("Searching for file " + filename + " | Found 0 peers");
    } else {
      System.out.println("Searching for file " + filename + " | Found " + peers.size() + " peers");
    }
  }
}