package com.example.firebaseweatherapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    Map<String, Object> results = new HashMap<>();
    ArrayList<String> listItems;
    private TextView display;
    private String cityName;
    private float currTemp;
    private float prevTemp;
    private float averageTemp;
    ListView listView;

    @SuppressLint("MissingInflatedId")
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

        //Setting up Buttons and Listeners
        findViewById(R.id.updateTemp).setOnClickListener(v -> updateTempFirebase());    //Calls updateTempFirebase() on Click
        findViewById(R.id.addCitybtn).setOnClickListener(v -> addCityFirebase());       //Calls addCityFirebase() on Click
        //Display
        display = findViewById(R.id.display);

        //Initiate variables
        cityName = "None";
        currTemp = 0.0f;
        averageTemp = 0.0f;

        listItems = new ArrayList<>();
    }

    // Function to add city to the list of cities
    private void addCityFirebase() {
        //Toast.makeText(this,"Adding City", Toast.LENGTH_SHORT).show();
        EditText newCity = findViewById(R.id.addCity);  //Getting City Name
        cityName = newCity.getText().toString();
        results.put("city", cityName);                  //For Displaying
        updateDisplay();                                //Calling Display Function
    }

    // Function to update temperature of the selected city
    private void updateTempFirebase() {
        //Toast.makeText(this,"Updating Temperature", Toast.LENGTH_SHORT).show();
        EditText newTemp = findViewById(R.id.Temp);    //Getting Temperature
        try {                                          //handling blank input
            currTemp = Float.parseFloat(newTemp.getText().toString());
        }catch (NumberFormatException ex){
            Toast.makeText(this,"Temperature must not be empty", Toast.LENGTH_SHORT).show();
        }
        results.put("temp", currTemp);                 //For Displaying
        updateDisplay();                               //Calling Display Function
    }

    // Function to update Display
    private void updateDisplay() {
        StringBuilder displayText = new StringBuilder();
        displayText.append("Selected City: ").append(results.get("city")).append("\n");
        displayText.append("Temperature in °C: ").append(results.get("temp")).append("°C\n");
        displayText.append("Average Temperature in °C: ").append(results.get("avgTemp")).append("°C\n");
        display.setText(displayText.toString());
    }
}