package com.example.carflix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.datatype.Duration;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InputActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String oldSelectedCarBrand;
    private String selectedCarBrand;
    private String selectedCarModel;
    private String selectedWeekDay;


    private TextView usernameTextView;
    private TextView passwordTextView;
    private TextView weekdayTextView;
    private TextView repeatTextView;
    private Spinner brandSpinner;
    private Spinner modelSpinner;
    private Spinner weekdaySpinner;
    private Spinner time_spinner;

//    private String time_string = time_spinner.getSelectedItem().toString();

    private CheckBox repeatCheckBox;

    private boolean repeatBox = true;



    Map<String, String[]> carMap = new HashMap<String, String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        usernameTextView = findViewById(R.id.username);
        passwordTextView = findViewById(R.id.password);

        weekdayTextView = findViewById(R.id.text_view_weekday);
        repeatTextView = findViewById(R.id.text_view_repeat);

        configureBrandSpinner();
        configureModelSpinner();
        configureWeekdaySpinner();

        carMap.put("tesla", getResources().getStringArray(R.array.tesla));
        carMap.put("volkswagen", getResources().getStringArray(R.array.volkswagen));
        carMap.put("honda", getResources().getStringArray(R.array.honda));
        carMap.put("nissan", getResources().getStringArray(R.array.nissan));
        carMap.put("mercedes", getResources().getStringArray(R.array.mercedes));
        carMap.put("lada", getResources().getStringArray(R.array.lada));
        carMap.put("bmw", getResources().getStringArray(R.array.bmw));
        carMap.put("volvo", getResources().getStringArray(R.array.volvo));
    }

    protected void configureBrandSpinner() {
        brandSpinner = (Spinner) findViewById(R.id.spinner);
        brandSpinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cars, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        brandSpinner.setAdapter(adapter);
    }

    protected void configureModelSpinner() {
        modelSpinner = (Spinner) findViewById(R.id.model_spinner);
        modelSpinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tesla, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        modelSpinner.setAdapter(adapter);
    }

    protected void configureWeekdaySpinner(){
        weekdaySpinner = (Spinner) findViewById(R.id.weekday_spinner);
        weekdaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                repeatTextView.setText("Soll dies für jeden " + weekdaySpinner.getSelectedItem().toString() + " gelten:");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weekdays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekdaySpinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedCarBrand = brandSpinner.getSelectedItem().toString();
        Log.d("Test", selectedCarBrand);

        if(selectedCarBrand != oldSelectedCarBrand){
            ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,carMap.get(selectedCarBrand.toLowerCase()));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            modelSpinner.setAdapter(adapter);
        }

        oldSelectedCarBrand = selectedCarBrand;

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void submit(@NonNull View view) {

        Toast toast = Toast.makeText(view.getContext(), "Das Auto wurde bestellt!", Toast.LENGTH_LONG);
        toast.show();


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:5241/api/Car");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("CarBrand", selectedCarBrand);
                    jsonParam.put("CarModel", selectedCarModel);
                    jsonParam.put("WeekDay", selectedWeekDay);
                 //   jsonParam.put("passwort", time_string);
                    jsonParam.put("repeat", repeatBox);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }
}