package com.example.firebaseweatherapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    Map<String, Object> results = new HashMap<>();
    Map<String, Object> cityData = new HashMap<>();
    //private List<CityTemperature> listItems;
    private TextView display;
    private String cityName;
    private float currTemp;
    private float prevTemp;
    private float averageTemp;
    private List<String> data_list;
    private ArrayAdapter<String> data_adapter;
    private ListView data_view;
    private String currentDate;
    private DatabaseReference mDatabase;

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
        //String[] data = {"Amogh","Ramnath","Shashank","Sujay"};                                      //Dummy Data
        data_list = new ArrayList<>(Arrays.asList()); //new ArrayList<>(Arrays.asList(data));          //Initializing City List with Dummy data
        data_adapter = new ArrayAdapter<>(this,R.layout.list_cities, R.id.city_name,data_list); //Adapter Initialization
        data_view = findViewById(android.R.id.list);                                                   //Finding List View
        data_view.setAdapter(data_adapter);                                                            //Setting Adapter

        //Displaying Cities
        show_list();

        //Data for db - Date
        currentDate = getCurrentDate();

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference team5Ref = mDatabase.child("teams").child("5").child("cities");

        // Read from the database
        team5Ref.addListenerForSingleValueEvent(new ValueEventListener() {//team5Ref.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Iterate over each city
                for (DataSnapshot citySnapshot : dataSnapshot.getChildren()) {
                    String cityName = citySnapshot.getKey();
                    data_list.add(cityName);
                    Log.d("FirebaseData", "City: " + cityName);

                    // Iterate over each date for the city
                    for (DataSnapshot dateSnapshot : citySnapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        Log.d("FirebaseData", "Date: " + date);

                        // Iterate over each time for the date
                        for (DataSnapshot timeSnapshot : dateSnapshot.getChildren()) {
                            String time = timeSnapshot.getKey();
                            Double temperature = timeSnapshot.getValue(Double.class);

                            Log.d("FirebaseData", "Time: " + time + ", Temperature: " + temperature);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //Fetch and display city names
        fetchCityNames();
    }

    // Function to fetch and display existing city names
    private void fetchCityNames() {
        mDatabase.child("teams").child("5").child("cities").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                data_list.clear();  // Clear the existing list
                for (DataSnapshot citySnapshot : dataSnapshot.getChildren()) {
                    String cityName = citySnapshot.getKey();
                    data_list.add(cityName);
                }
                data_adapter.notifyDataSetChanged();  // Notify adapter to update ListView
            } else {
                Toast.makeText(MainActivity.this, "Failed to load city names.", Toast.LENGTH_SHORT).show();
            }
        });
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
            // Update Firebase with the new city
            mDatabase.child("teams").child("5").child("cities").child(cityName).child(currentDate).setValue(true);
            Toast.makeText(this,"City Added with current date", Toast.LENGTH_SHORT).show();
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
        results.put("temp", currTemp);                                   //For Displaying
        String currentTime = String.valueOf(System.currentTimeMillis()); //TimeStamp
        results.put("timeStamp", currentTime);                           //For Displaying
        if (!cityName.equals("None")) {                                  //Update DB
            mDatabase.child("teams").child("5").child("cities").child(cityName).child(currentDate).child(currentTime).setValue(currTemp);
            Toast.makeText(this, "Temperature of: "  + cityName + "updated to: " + currTemp , Toast.LENGTH_SHORT).show();
            newTemp.setText("");
        } else {
            Toast.makeText(this, "Please select a city first", Toast.LENGTH_SHORT).show();
        }
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
        displayText.append("System Time: ").append(results.get("timeStamp")).append("ms\n");
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

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}//Activity