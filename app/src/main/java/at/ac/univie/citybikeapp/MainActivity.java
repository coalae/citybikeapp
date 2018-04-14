package at.ac.univie.citybikeapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import at.ac.univie.citybikeapp.logic.JSONReader;
import at.ac.univie.citybikeapp.model.CityData;

// Die Klasse MainActivity ist die Startseite der CityBikeApp.
public class MainActivity extends AppCompatActivity {

    /* Instanzvariablen:
     * ArrayList<CityData> citydataList - Liste mit Stadtname und href
     * Button stadtWaehlenButton - Button, mit dem eine Stadt aus der Liste der vorhandenen Staedte ausgewaehlt werden kann
     */
    private ArrayList<CityData> cityDataList;
    private Button stadtWaehlenButton;

    /* Die Methode onCreate ruft die Startseite der App auf.
     * Es wird anfangs ueberprueft, ob eine Internetverbindung besteht. Wenn nicht, dann wird dem User in Form eines
     * ProgressDialog angezeigt, dass er keine Internetverbindung hat und die App nichts machen kann.
     * Der stadtWaehlenButton wird dann zugreifbar gemacht.
     * Der BackgroundWorker wird aufgerufen mit execute(), um die Daten zu laden.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isConnected())
        {
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Achtung!!!");
            progressDialog.setMessage("Sie haben keine Internetverbindung!!");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        stadtWaehlenButton = (Button) findViewById(R.id.stadtWaehlenButton); // Button Resource wird zugreifbar gemacht

        new BackgroundWorker().execute();
    }

    /* Die Methode onClickDatenAbfragen nimmt einen View als Parameter.
     * In der CityDataList wird dann der Name der Stadt, die ueber den stadtWaehlenButton ausgewaehlt wurde,
     * gesetzt und ein Intent-Objekt angelegt, damit die Informationen zur OutputActivity (wo die Daten ausgegeben
     * werden) weitergeleitet werden koennen. Es wird ein Bundle-Objekt angelegt, in dem der Stadtname und der
     * zugehoerige href gespeichert werden. Die Informationen des Bundle-Objekts werden dann im Intent-Objekt gesetzt und
     * es wird von MainActivity auf OutputActivity weitergeschaltet in der App, damit die gewuenschten Daten in der
     * Output Seite angezeigt werden koennen.
     */
    public void onClickDatenAbfragen(View v) {
        CityData cityData = null;
        if (stadtWaehlenButton.getText() != null) {
            for (int i = 0; i < cityDataList.size(); i++) {
                if (cityDataList.get(i).getName().equals(stadtWaehlenButton.getText())) {
                    cityData = cityDataList.get(i);
                }
            }
        }
        Intent intent = new Intent(MainActivity.this, OutputActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", cityData.getName());
        bundle.putString("href", cityData.getHref());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /* Die Methode onClickStadtWaehlen nimmt einen View als Parameter.
     * Es wird ein CharSequence-Objekt angelegt, um alle Stadtnamen fuer die Anzeige in
     * einer Liste zu speichern.
     * Mittels AlertDialog.Builder-Objekt wird das Dialogfenester mit der Liste fuer die Stadtauswahl praesentiert,
     * in der der User dann die gewuenschte Stadt fuer die Datenabfrage auswaehlen kann.
     */
    public void onClickStadtWaehlen(View v){
        final CharSequence cityNameList [] = new CharSequence[cityDataList.size()];
        for (int i= 0; i < cityDataList.size(); i++){
            cityNameList[i] = cityDataList.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Stadtauswahl");
        builder.setItems(cityNameList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stadtWaehlenButton.setText(cityNameList[which]);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /* Die Methode isConnected ueberprueft, ob es eine Internetverbinung gibt.
     * Getestet wird dies hier ueber den Befehl ping, der versucht, die google-Website zu erreichen.
     */
    private boolean isConnected() {
        String befehl = "ping -c 1 google.at";
        try {
            return (Runtime.getRuntime().exec(befehl).waitFor() == 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* Die Klasse BackgroundWorker ist von der Klasse AsyncTask abgeleitet und ermoeglicht eine
     * asynchrone Verarbeitung. Der BackgroundWorker wird in der Methode onCreate ausgefuehrt,
     * damit er die Daten ladet und sortiert und eine Anzeige in Form eines ProgressDialog gemacht wird.
     */
    private class BackgroundWorker extends AsyncTask<Void,Void,Void>{
        private JSONReader jsonReader;
        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        // Die Methode onPreExecute zeigt an, dass die Daten geladen werden.
        @Override
        protected void onPreExecute(){
            jsonReader = new JSONReader();
            progressDialog.setTitle("");
            progressDialog.setMessage("Daten werden geladen!!");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /* Die Methode doInBackground nimmt die Liste der Stadtnamen und zugehoerigen Hrefs aus dem JSON.
         * Dann wird die Liste alphabetisch sortiert.
         */
        @Override
        protected Void doInBackground(Void... params) {
            cityDataList = jsonReader.getHrefAndCity();
            Collections.sort(cityDataList, new Comparator<CityData>() {
                @Override
                public int compare(CityData o1, CityData o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            return null;
        }

        // Die Methode onPostExecute schliesst den ProgressDialog.
        @Override
        protected void onPostExecute(Void v){
            //Toast.makeText(MainActivity.this, "JsonReader geladen!!!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

    }
}
