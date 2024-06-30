package com.example.firebaseweatherapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    Map<String, Object> results = new HashMap<>();
    //private List<CityTemperature> listItems;
    private TextView display;
    private String cityName;
    private float currTemp;
    private float prevTemp;
    private float averageTemp;
    private List<String> data_list;
    private ArrayAdapter<String> data_adapter;
    private ListView data_view;

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

        //Data
        String[] data = {"Amogh","Ramnath","Shashank","Sujay"};                        //Dummy Data, Assuming json
        data_list = new ArrayList<>(Arrays.asList(data));                              //Initializing City List with Dummy data
        data_adapter = new ArrayAdapter<>(this,R.layout.list_cities, R.id.city_name,data_list); //Adapter Initialization
        data_view = findViewById(android.R.id.list);                                   //Finding List View
        data_view.setAdapter(data_adapter);                                            //Setting Adapter

        //Displaying Cities
        show_list();

    }

    // Function to add city to the list of cities
    private void addCityFirebase() {
        //Toast.makeText(this,"Adding City", Toast.LENGTH_SHORT).show();
        EditText newCity = findViewById(R.id.addCity);  //Getting City Name
        cityName = newCity.getText().toString();
        results.put("city", cityName);                  //For Displaying
        // Accepting user input
        if(!cityName.isEmpty()) //Ensure Data is present
        {
            data_list.add(cityName);
            data_adapter.notifyDataSetChanged();
            newCity.setText("");                       // Clear the EditText
        } else {
            Toast.makeText(getApplicationContext(), "Enter a valid name", Toast.LENGTH_SHORT).show();
        }
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
        //updateDisplay(cityName);                     //Calling Display Function
        newTemp.setText("");                           // Clear the EditText
    }

    // Function to update Display
    private void updateDisplay(String cityName) {
        StringBuilder displayText = new StringBuilder();
        results.put("city", cityName);
        displayText.append("Selected City: ").append(results.get("city")).append("\n");
        displayText.append("Temperature in °C: ").append(results.get("temp")).append("°C\n");
        displayText.append("Average Temperature in °C: ").append(results.get("avgTemp")).append("°C\n");
        display.setText(displayText.toString());
    }
    private View show_list() {
        data_view.setOnItemClickListener(new AdapterView.OnItemClickListener() { //Listner for item click
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = data_list.get(position); //Get Position of selected item
                //Toast.makeText(getApplicationContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                updateDisplay(selectedItem);                                 //Display Selected City
            }
        });
        return data_view;
    }

    //TODO: Average Temperature
}//Activity