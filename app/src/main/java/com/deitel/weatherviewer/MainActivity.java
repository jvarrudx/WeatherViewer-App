package com.deitel.weatherviewer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {

    private EditText locationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajuste de insets gerado automaticamente
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // 1) Referenciar o EditText

        locationEditText = findViewById(R.id.locationEditText);


        // 2) Referenciar o FloatingActionButton

        FloatingActionButton fab = findViewById(R.id.fab);


        // 3) Clique do FAB

        fab.setOnClickListener(view -> {


            String location = locationEditText.getText().toString().trim();


            dismissKeyboard(locationEditText);



            Snackbar.make(findViewById(R.id.coordinatorLayout),
                    "FAB clicado — createURL será implementado no próximo passo!",
                    Snackbar.LENGTH_SHORT).show();
        });
    }


    // dismissKeyboard

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

}
