import java.util.*;

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
    Scanner in = new Scanner(System.in);
    Server server = new Server();
    ServerSkeleton skeleton = new ServerSkeleton(server);
    new Thread(() -> skeleton.listen()).start();
    while(true){
      System.out.println("Please enter a command: list | list {filename} | exit");
      System.out.print("dapster-server >>> ");
      String[] command = in.nextLine().split(" ");
      if(command[0].equals("exit")){
        System.exit(0);
      } else if(command[0].equals("list")){
        if(command.length == 1){
          System.out.println("Indexing Server Contents: ");
          HashMap<String, ArrayList<Peer>> registry = server.registry;
          System.out.println(" +-- " + registry.size() + " files --+ ");
          for(String filename : registry.keySet()){
            System.out.println(String.format(Locale.US, "%20s | %d peers", filename, registry.get(filename).size()));
            for(Peer p : registry.get(filename)){
              System.out.println(" - " + p.getFullAddress());
            }
          }
          continue;
        } else {
          String filename = command[1];
          ArrayList<Peer> peers = server.registry.get(filename);
          if(peers == null){
            System.out.println(String.format(Locale.US, "%20s | %d peers", filename, 0));
            continue;
          }
          System.out.println(String.format(Locale.US, "%20s | %d peers", filename, peers.size()));
          for(Peer p : peers){
            System.out.println(" - " + p.getFullAddress());
          }
          continue;
        }
      }

      System.out.println("Invalid command!");
    }
  }

  public static void runPeer(int port){
    Scanner in = new Scanner(System.in);
    Peer peer = new Peer(port);
    ServerStub serverStub = new ServerStub();
    peer.setServerStub(serverStub);
    PeerSkeleton peerSkeleton = new PeerSkeleton(peer);

    new Thread(() -> peerSkeleton.listen()).start();

    while(true){
      System.out.println("Please enter a command: get {filename} | register {filename} | exit");
      System.out.print("dapster-peer >>> ");
      String[] command = in.nextLine().split(" ");
      String function = command[0];
      if(function.equals("exit")){
        System.exit(0);
      } else if(function.equals("get")){

        if (command.length == 2){
          String filename = command[1];
          boolean success = peer.get(filename);
          if(!success){
            System.out.println("Failed to download file: " + filename);
          }
          continue;
        }

      } else if(function.equals("register")){

        if(command.length == 2){

          String filename = command[1];
          peer.register(filename);
          continue;

        }

      } else if(function.equals("test")){
        if(command.length == 2){
          int times = Integer.parseInt(command[1]);
          long total = 0;
          for(int i = 0; i < times; i++){
            long startTime = System.nanoTime();
            serverStub.search(i + ".txt");
            long endTime = System.nanoTime();
            total += endTime - startTime;
          }
          System.out.println("Total: " + total /1000000f + "ms ; Average: " + total / 1000000f / times + "ms .");
          continue;
        }

      }

      System.out.println("Invalid command!");
    }
  }

}