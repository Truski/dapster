import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

  private static OutputStreamWriter out;
  private static final String LOG_DIR = "log/";
  private static DateFormat df = new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss");

  public static void StartLogging(){
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

  public static void Log(String message){
    try {
      String date = df.format(new Date());
      out.write(date + ": " + message + "\n");
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  public static void EndLogging(){
    try {
      out.close();
    } catch (Exception e){
      e.printStackTrace();
    }
  }
}