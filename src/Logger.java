import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Logger class is a simple utility class used for the server-aspect of the Peer as well as the Indexing Server.
 * It only contains 3 functions: StartLogging, which creates the log file, Log, which logs a message to the file, and
 * EndLogging, which closes the file, first flushing its contents.
 *
 * The logger is to be used to write down the history of requests from users. That way, the terminal of peers and the
 * server is clean and it will only show user-requested input/output.
 */

public class Logger {

  private static OutputStreamWriter out;
  private static final String LOG_DIR = "log/";
  private static DateFormat df = new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss");

  /**
   * Creates the log file in the appropriate log directory. The file's name is the current datetime. Should only be
   * called once in any program, and should be called in the Skeleton part of a program (where the server or peer
   * listens for incoming connections).
   */
  public static void StartLogging(){
    // Create filename
    String filename = df.format(new Date()) + ".txt";

    try {

      // Create log directory if doesn't exist
      File logDir = new File(LOG_DIR);
      if(!logDir.exists()){
        logDir.mkdir();
      }

      // Create new log file
      FileOutputStream fos = new FileOutputStream(LOG_DIR + filename);
      out = new OutputStreamWriter(fos, "UTF-8");
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Writes to the log file the provided message. The message is prepended with the current datetime.
   *
   * @param message Message to be logged.
   */
  public static void Log(String message){
    // Get current datetime
    String date = df.format(new Date());

    try {
      // Write message to log
      out.write(date + ": " + message + "\n");
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Closes the log file, flushing its contents to disk
   */
  public static void EndLogging(){
    try {
      out.close();
    } catch (Exception e){
      e.printStackTrace();
    }
  }
}