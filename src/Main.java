import java.util.Scanner;

public class Main {

  public static void main(String[] args){

    if(args.length != 2){
      System.out.println("Failure! Correct usage:");
      System.out.println("Please enter 'server' for indexing server or 'peer' for peer");
      System.out.println("Please enter port number to listen to");
      return;
    }

    String type = args[0];
    int port = Integer.parseInt(args[1]);
    if(type.equals("server")){
      runServer();
    } else if(type.equals("peer")){
      runPeer(port);
    } else {
      System.out.println("Invalid type!");
    }
  }

  public static void runServer(){
    Server server = new Server();
    ServerSkeleton skeleton = new ServerSkeleton(server);
    skeleton.listen();
  }

  public static void runPeer(int port){
    Scanner in = new Scanner(System.in);
    Peer peer = new Peer(port);
    ServerStub serverStub = new ServerStub();
    peer.setServerStub(serverStub);
    PeerSkeleton peerSkeleton = new PeerSkeleton(peer);

    new Thread(() -> peerSkeleton.listen()).start();

    while(true){
      try {
        System.out.println("Running the peer boys");
        String command = in.next();
        if(command.equals("exit")){
          break;
        }
        String filename = in.next();
        if(command.equals("get")){
          peer.get(filename);
        } else if (command.equals("register")){
          peer.register(filename);
        }
        in.nextLine();
      } catch (Exception e){
        System.out.println("Invalid command");
        in.nextLine();
      }
    }
  }

}