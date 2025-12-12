package com.deitel.weatherviewer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView; // Importação adicionada

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WeatherViewer"; // Tag para Logcat
    private EditText locationEditText;
    private ListView weatherListView; // Variável para o ListView
    private TextView placeholderTextView; // Novo TextView para placeholder
    private ArrayList<Weather> weatherList;
    private WeatherArrayAdapter weatherArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Acessa o layout incluído para encontrar os views dentro dele
        View contentMain = findViewById(R.id.content_main_include);

        // **CORREÇÃO 1: Acessando Views corretamente dentro do content_main**
        locationEditText = contentMain.findViewById(R.id.locationEditText);
        weatherListView = contentMain.findViewById(R.id.weatherListView);

        // **CORREÇÃO 2: Buscando o TextView simples para o placeholder**
        placeholderTextView = contentMain.findViewById(R.id.placeholderTextView);

        FloatingActionButton fab = findViewById(R.id.fab);

        weatherList = new ArrayList<>();
        weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);

        weatherListView.setAdapter(weatherArrayAdapter);

        // Mensagem inicial (Remoção do placeholder Weather e uso do TextView simples)
        if (weatherList.isEmpty() && placeholderTextView != null) {
            placeholderTextView.setVisibility(View.VISIBLE);
            weatherListView.setVisibility(View.GONE); // Garante que a lista não apareça se vazia
        }

        fab.setOnClickListener(v -> {
            String location = locationEditText.getText().toString().trim();
            dismissKeyboard(locationEditText);

            if (location.isEmpty()) {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        "Digite uma cidade", Snackbar.LENGTH_SHORT).show();
                return;
            }

            // Oculta a mensagem de placeholder ao iniciar a busca
            if (placeholderTextView != null) {
                placeholderTextView.setVisibility(View.GONE);
                weatherListView.setVisibility(View.VISIBLE); // Mostra a lista (que será atualizada)
            }

            URL url = createURL(location);

            if (url != null) {
                new GetWeatherTask().execute(url);
            } else {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.invalid_url, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void dismissKeyboard(View view) {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private URL createURL(String city) {
        try {
            String baseUrl = getString(R.string.web_service_url);
            String apiKey = getString(R.string.api_key);
            int days = 7;

            String urlString = baseUrl
                    + "?city=" + URLEncoder.encode(city, "UTF-8")
                    + "&days=" + days
                    + "&APPID=" + apiKey;

            Log.d(TAG, "URL de Requisição: " + urlString);
            return new URL(urlString);

        } catch (Exception e) {
            Log.e(TAG, "Erro ao criar URL", e);
            return null;
        }
    }

    private void convertJSONtoArrayList(JSONObject forecast) {
        weatherList.clear();

        try {
            // Acessando o array 'days' que contém a previsão (conforme PDF)
            JSONArray daysArray = forecast.getJSONArray("days");
            Log.d(TAG, "Parsing JSON: Encontrados " + daysArray.length() + " dias.");

            for (int i = 0; i < daysArray.length(); i++) {
                JSONObject day = daysArray.getJSONObject(i);

                // Mapeamento das chaves (date, minTempC, maxTempC, humidity, description, icon)
                // Os nomes das chaves são verificados no Logcat se ocorrer um JSONException
                String date = day.getString("date");
                double min = day.getDouble("minTempC");
                double max = day.getDouble("maxTempC");
                double humidity = day.getDouble("humidity");
                String description = day.getString("description");
                String icon = day.getString("icon");

                weatherList.add(
                        new Weather(date, min, max, humidity, description, icon)
                );
            }

        } catch (Exception e) {
            // **CORREÇÃO 3: Logging detalhado para JSONException**
            Log.e(TAG, "Erro ao converter JSON ou JSON malformado", e);
            Snackbar.make(findViewById(R.id.coordinatorLayout),
                    R.string.read_error, Snackbar.LENGTH_LONG).show();
        }
    }

    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) params[0].openConnection();
                connection.setConnectTimeout(5000); // Aumentei o timeout
                connection.setReadTimeout(5000);    // Aumentei o timeout

                int responseCode = connection.getResponseCode();

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "HTTP Error: Response Code " + responseCode);
                    return null;
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );

                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                return new JSONObject(builder.toString());

            } catch (Exception e) {
                Log.e(TAG, "Erro de conexão/IO no doInBackground", e); // Logging de erro de rede
                return null;

            } finally {
                if (connection != null) connection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json == null) {
                // Exibe erro de conexão
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.connect_error, Snackbar.LENGTH_LONG).show();

                // Se falhar, reexibe a mensagem inicial e oculta a lista
                weatherListView.setVisibility(View.GONE);
                if (placeholderTextView != null) {
                    placeholderTextView.setVisibility(View.VISIBLE);
                }
                return;
            }

            convertJSONtoArrayList(json);
            weatherArrayAdapter.notifyDataSetChanged();

            if (weatherList.isEmpty()) {
                // Caso a API retorne JSON válido, mas sem dias de previsão
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        "Nenhuma previsão encontrada para esta cidade.", Snackbar.LENGTH_LONG).show();
                weatherListView.setVisibility(View.GONE);
                if (placeholderTextView != null) placeholderTextView.setVisibility(View.VISIBLE);
            } else {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        "Previsão atualizada", Snackbar.LENGTH_SHORT).show();
                weatherListView.setVisibility(View.VISIBLE);
                if (placeholderTextView != null) placeholderTextView.setVisibility(View.GONE);
            }
        }
    }
}