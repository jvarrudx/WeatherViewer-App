package com.deitel.weatherviewer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import android.os.AsyncTask;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText locationEditText;

    private ArrayList<Weather> weatherList = new ArrayList<>();
    private WeatherArrayAdapter weatherArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        locationEditText = findViewById(R.id.locationEditText);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(v -> {
            String location = locationEditText.getText().toString().trim();
            dismissKeyboard(locationEditText);

            URL url = createURL(location);

            if (url != null) {
                new GetWeatherTask().execute(url);
            } else {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.invalid_url, Snackbar.LENGTH_LONG).show();
            }
        });

        // Inicializa o Adapter e o ListView
        weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);
        ListView listView = findViewById(R.id.weatherListView);
        listView.setAdapter(weatherArrayAdapter);
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

            return new URL(urlString);

        } catch (Exception e) {
            return null;
        }
    }

    private void convertJSONtoArrayList(JSONObject forecast) {
        weatherList.clear();

        try {
            JSONArray daysArray = forecast.getJSONArray("days");

            for (int i = 0; i < daysArray.length(); i++) {
                JSONObject day = daysArray.getJSONObject(i);

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
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
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
                return null;

            } finally {
                if (connection != null) connection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json == null) {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.connect_error, Snackbar.LENGTH_LONG).show();
                return;
            }

            convertJSONtoArrayList(json);
            weatherArrayAdapter.notifyDataSetChanged();

            Snackbar.make(findViewById(R.id.coordinatorLayout),
                    "Previsão atualizada", Snackbar.LENGTH_SHORT).show();
        }
    }
}
