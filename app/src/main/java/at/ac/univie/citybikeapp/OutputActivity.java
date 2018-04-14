package at.ac.univie.citybikeapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.Collections;
import java.util.Comparator;

import at.ac.univie.citybikeapp.logic.JSONReader;
import at.ac.univie.citybikeapp.model.CityData;

/**
 * Die Klasse OutputActivity ist die Anzeige, die nach dem druecken des Button 'Daten abfragen' in der MainActivity bzw.
 * Startseite gemacht wird.
 */
public class OutputActivity extends AppCompatActivity {

    // Instanzvariablen der Outputs, die basierend auf der Datenabfrage gemacht werden.
    private TextView stadtOutput;
    private TextView hrefOutput;
    private TextView freiePlaetzeOutput;
    private TextView freieRaederOutput;
    private TextView ausgelieheneRaederOutput;

    /* Die Methode onCreate macht setzt den ContentView auf die Outputseite und macht die
     * Textfelder fuer die Outputs (Stadtname, Hyperlink, Freie Plaetze, Freie Raeder, Ausgeliehene Raeder) zugaenglich.
     * Es wird dann ein Bundle-Objekt erstellt, das die Informationen aus dem Intent-Objekt herausholt und die TextViews
     * Stadtname und Href setzt.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);
        stadtOutput = (TextView) findViewById(R.id.stadtOutput);
        hrefOutput = (TextView) findViewById(R.id.hrefOutput);
        freiePlaetzeOutput = (TextView) findViewById(R.id.freiePlaetzeOutput);
        freieRaederOutput = (TextView) findViewById(R.id.freieRaederOutput);
        ausgelieheneRaederOutput = (TextView) findViewById(R.id.ausgelieheneRaederOutput);

        Bundle bundle = getIntent().getExtras();
        Toast.makeText(OutputActivity.this, bundle.getString("name"), Toast.LENGTH_LONG);
        Toast.makeText(OutputActivity.this, bundle.getString("href"), Toast.LENGTH_LONG);
        stadtOutput.setText("Stadt: " + bundle.getString("name"));
        hrefOutput.setText("Href: " + bundle.getString("href"));

        new BackgroundWorker().execute();
    }


    /* Die Klasse BackgroundWorker wird abgeleitet von AsyncTask.
     * Sie fuehrt die Taetigkeiten des Ladens der Daten ueber empty_slots, free_bikes und die
     * Berechnung der ausgeliehenen Raeder in % aus und liest diese Daten vom JSON-File.
     */
    private class BackgroundWorker extends AsyncTask<Void,Void,Void> {
        // Instanzvariablen
        private JSONReader jsonReader;
        private CityData cityData;
        private ProgressDialog progressDialog = new ProgressDialog(OutputActivity.this);

        // Die Methode onPreExecute zeigt einen ProgressDialog ueber das Laden der Daten an und legt einen neuen JSONReader an.
        @Override
        protected void onPreExecute(){
            jsonReader = new JSONReader();
            progressDialog.setTitle("");
            progressDialog.setMessage("Daten werden geladen!!");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        // Die Methode doInBackground liest die Daten ueber free_bikes und empty_slots vom jeweiligen href.
        @Override
        protected Void doInBackground(Void... params) {
            Bundle bundle = getIntent().getExtras();
            try {
                cityData = jsonReader.getFreeBikesAndEmptySlots(bundle.getString("href"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /* Die Methode onPostExecute setzt die TextViews Freie Plaetze und Freie Raeder auf die jeweiligen Werte,
         * ueber den href fuer die Stadt gelesen wurden. Wenn diese Daten vorhanden sind, wird die Berechnung
         * fuer die ausgeliehenen Raeder in % gemacht und in der Anzeige im TextView gezeigt. Der ProgressDialog
         * wird dann geschlossen.
         */
        @Override
        protected void onPostExecute(Void v){
            freiePlaetzeOutput.setText("Freie Plaetze: " + cityData.getEmpty_slots());
            freieRaederOutput.setText("Freie Raeder: " + cityData.getFree_bikes());
            if (cityData.getFree_bikes() != 0){

                double summe = cityData.getEmpty_slots() + cityData.getFree_bikes();
                double ausgelieheneRaeder = cityData.getEmpty_slots();  // Annahme: wenn Slot leer ist, wurde 1 Rad ausgeliehen
                int ergebnis = (int) (ausgelieheneRaeder / summe * 100);
                ausgelieheneRaederOutput.setText("Ausgeliehene Raeder (in %): " + ergebnis);
            }

            progressDialog.dismiss();
        }

    }

}
