

public class PeerStub{
  private String address;
  private port port;

  public PeerStub(Peer p){
    this.address = p.getHostName();
    this.port = p.getPort();
  }

  public byte[] obtain(String filename){
    Socket socket = new Socket(address, port);
    ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
    os.writeObject(filename);
    ByteArrayInputStream is = new ByteArrayInputStream(socket.getInputStream());
    byte[] file = is.read();
    socket.close();
    return file;
  }
}