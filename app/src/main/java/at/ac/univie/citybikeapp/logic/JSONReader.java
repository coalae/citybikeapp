package at.ac.univie.citybikeapp.logic;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import at.ac.univie.citybikeapp.MainActivity;
import at.ac.univie.citybikeapp.model.CityData;

/**
 * Die Klasse JSONReader liest die Daten vom XML-File (https://api.citybik.es/v2/networks/ und von den jeweiligen CityBike-Unternehmen).
 */
public class JSONReader {

    // Instanzvariable ArrayList<CityData> cityDataList, in der die abgefragten Daten gespeichert werden
    private ArrayList<CityData> cityDataList = new ArrayList<CityData>();

    /* Die Methode readDate nimmt einen String jsonAddress als Parameter und gibt alle abgefragten
    JSON-Daten als ein String zurück*/
    public String readData (String jsonAddress) {
        URL url = null;
        StringBuilder sb = new StringBuilder();
        BufferedInputStream bis = null;
        try {
            url = new URL(jsonAddress);
            bis = new BufferedInputStream(url.openStream());
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while((bytesRead = bis.read(buffer)) > 0) {
                String text = new String(buffer, 0, bytesRead);   // 0 ... dass zeile ab anfang gelesen wird
                sb.append(text);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(bis!=null) bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // System.out.println(sb.toString());
        return sb.toString();  // gesamter JSON-String wird returned
    }

    /* Die Methode readJSONForHrefandCity nimmt den Parameter jsonString als String und gibt eine
    ArrayList<CityData> mit den abgefragten Daten (name und href) zurueck.*/
    private void readJSONForHrefAndCity(String jsonString) throws JSONException {
        //ArrayList<String> hrefList = new ArrayList<String>();
        JSONObject root = new JSONObject(jsonString);
        JSONArray networks = root.getJSONArray("networks");
        for(int i=0; i < networks.length(); i++){
            JSONObject networkelements = networks.getJSONObject(i);
            JSONObject location = networkelements.getJSONObject("location");
            //System.out.println(location.getString("city"));
            CityData cityData = new CityData();
            cityData.setName(location.getString("city"));
            cityData.setHref(networkelements.getString("href"));
            cityDataList.add(cityData);
        }

    }

    /* Die Methode readJSONForRemainingData nimmt den Parameter href als String und gibt in einem
    CityData Objekt die Daten fuer den jeweiligen href zurueck (die empty_slots und free_bikes
    Variablen werden gesetzt). */
    private CityData readJSONForRemainingData(String href) throws JSONException {

             CityData cityData = new CityData();

            String jsonString = readData("http://api.citybik.es" + href);
            //System.out.println("http://api.citybik.es" + hrefList.get(i));
            JSONObject root = new JSONObject(jsonString);
            JSONObject network = root.getJSONObject("network");
            if(!network.isNull("location")){
                JSONObject location = network.getJSONObject("location");
                //System.out.println(location.getString("city"));
                JSONArray stations = network.getJSONArray("stations");
                int emptySlotSum = 0;
                int freeBikesSum = 0;
                for(int f=0;f<stations.length();f++){
                    JSONObject stationelements = stations.getJSONObject(f);
                    //System.out.println(stationelements.getString("name"));
                    //System.out.println(stationelements.get("empty_slots")); // get machen, weil getInt exception wirft
                    //System.out.println(stationelements.getInt("free_bikes"));
                    if(!stationelements.isNull("empty_slots")) emptySlotSum+=stationelements.getInt("empty_slots");
                    if(!stationelements.isNull("free_bikes")) freeBikesSum+=stationelements.getInt("free_bikes");
                }
                cityData.setEmpty_slots(emptySlotSum);
                cityData.setFree_bikes(freeBikesSum);
            }else{
                System.err.println("http://api.citybik.es" + href);
            }
            return cityData;

    }

    // Die Methode getHrefAndCity gibt eine ArrayList<CityData> mit href und name Informationen zurueck.
    public ArrayList<CityData> getHrefAndCity(){
        String jsonStr = readData("http://api.citybik.es/v2/networks");
        try {
            readJSONForHrefAndCity(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(MainActivity.class.getSimpleName(), "Size: " + cityDataList.size());  // check fuer Groesse der ArrayList<CityData>
        return cityDataList;
    }

    /* Die Methode getFreeBikesAndEmpty nimmt den Parameter href und gibt für diesen href die Informationen
    ueber free_bikes und empty_slots in Form eines CityData Objekts zurueck.*/
    public CityData getFreeBikesAndEmptySlots(String href) throws JSONException {
        return readJSONForRemainingData(href);
    }

    /*
    public ArrayList<CityData> getCityDataList(){
       // JSONReader jsonReader = new JSONReader();
        String jsonStr = readData("http://api.citybik.es/v2/networks");

        try {
            readJSONForHrefAndCity(jsonStr);
            //readJSONForRemainingData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cityDataList;
    }
    */
}
