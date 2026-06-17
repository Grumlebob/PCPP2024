// Http connection to server at address given to getUrlBytes
// jst@itu.dk * 2024-03-19
package exercises11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
// Fetch data from HTTP
// jst@itu.dk * 2024-05-06

import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

/* Most of this class is copied from
// Android Programming: The Big Nerd Ranch Guide
//by Bill Phillips, Chris Stewart and Kristin Marsicano Chapter 25 */

public class NetworkFetcher {

  public byte[] getUrlBytes(String urlSpec) throws IOException {
    URL url= new URL(urlSpec);
    HttpURLConnection connection= (HttpURLConnection)url.openConnection();
    //System.out.println("AA");
    try {
      ByteArrayOutputStream out= new ByteArrayOutputStream();
      //System.out.println("AB");
      InputStream in= connection.getInputStream();
      //System.out.println("AC");
      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        throw new IOException(connection.getResponseMessage() +
            ": with " +  urlSpec);
      }
      int bytesRead= 0;
      //System.out.println("AD");
      byte[] buffer= new byte[1024];
      while ((bytesRead = in.read(buffer)) > 0) {
        out.write(buffer, 0, bytesRead);
      }
      //System.out.println("AE ");
      out.close();
      return out.toByteArray();
    } finally {
      connection.disconnect();
    }
  }

  public String getUrlString(String url) throws IOException {

    //return new String(getUrlBytes(url), StandardCharsets.UTF_8); // Somehting wrong with chararterset
    return new String(getUrlBytes(url));
  }
  public String fetchItems(String url) {
    //warning may return null
    try {
      String jsonString = getUrlString(url);
      System.out.println("Received JSON");
      JSONObject jsonBody = new JSONObject(jsonString);
      return parseItems(jsonBody);
    } catch (JSONException je) {
      System.out.println("Failed to parse JSON" + je);
      return null;
    } catch (IOException ioe) {
      System.out.println("Failed to fetch items" + ioe);
      return null;
    }
  }

  private String parseItems(JSONObject jsonBody) throws IOException, JSONException {
    JSONObject depBoard= jsonBody.getJSONObject("DepartureBoard");
    JSONArray depArray= depBoard.getJSONArray("Departure");
    System.out.println("JSON l="+ depArray.length());
    if (depArray.length()>0) {
      String res= ""; int found= 0;
      for (int i=0; ((i<depArray.length() && (found<4))); i++) {  // Here the number of departures (4) can be changed
        String bName= depArray.getJSONObject(i).getString("name");
        System.out.println(depArray.getJSONObject(i).getString("finalStop"));
          res = res + "\n" +bName +": "+ depArray.getJSONObject(i).getString("time") +
              " mod " + depArray.getJSONObject(i).getString("finalStop");
          found= found+1;
      }
      return res;
    } else return null;
  }
}

/*  https://stackoverflow.com/questions/88838/how-to-convert-strings-to-and-from-utf8-byte-arrays-in-java

/* Convert a list of UTF-8 numbers to a normal String
 * Usefull for decoding a jms message that is delivered as a sequence of bytes instead of plain text
 
public String convertUtf8NumbersToString(String[] numbers){
    int length = numbers.length;
    byte[] data = new byte[length];

    for(int i = 0; i< length; i++){
        data[i] = Byte.parseByte(numbers[i]);
    }
    return new String(data, Charset.forName("UTF-8"));
}

*/