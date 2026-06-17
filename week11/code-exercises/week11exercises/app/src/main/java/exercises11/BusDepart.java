// Fetch data from rejseplanen
// jst@itu.dk * 2024-10-27
package exercises11;

import java.io.*;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class BusDepart {

    final static String RejseplanURL = "https://xmlopen.rejseplanen.dk/bin/rest.exe/departureBoard?offsetTime=0&format=json&id=hoejetaastrup";
    final static String ITU = "000000900";

    NetworkFetcher nf = new NetworkFetcher();

    private String fetchItems(String url) {
        //warning may return null
        try {
            String jsonString = nf.getUrlString(url);
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
        JSONObject depBoard = jsonBody.getJSONObject("DepartureBoard");
        JSONArray depArray = depBoard.getJSONArray("Departure");
        if (depArray.length() > 0) {
            String res = "";
            int found = 0;
            for (int i = 0; ((i < depArray.length() && (found < 4))); i++) {  // Here the number of departures (4) can be changed
                String bName = depArray.getJSONObject(i).getString("name");
                res = res + "\n" + bName + ": " + depArray.getJSONObject(i).getString("time") +
                        " mod " + depArray.getJSONObject(i).getString("finalStop");
                found = found + 1;
            }
            return res;
        } else return null;
    }

    public BusDepart() {
        System.out.println(fetchItems(RejseplanURL + ITU));
    }

    public static void main(String[] args) {
        new BusDepart();
    }
}

