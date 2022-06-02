package com.example.carflix;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    TextView username;
    TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username =(TextView) findViewById(R.id.username);
        password =(TextView) findViewById(R.id.password);

        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("abcdefg", "aaaaaaaaaaaaaaaaaaaa" + username.getText() + password.getText());

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Connection auf die in Visual Studio erstellte Route
                            URL url = new URL("http://10.0.2.2:5241/api/Users");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            // Request Method auf POST setzen
                            conn.setRequestMethod("POST");

                            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                            conn.setRequestProperty("Accept","application/json");
                            conn.setDoOutput(true);
                            conn.setDoInput(true);

                            // Variablen werden in JSON umformatiert
                            JSONObject jsonParam = new JSONObject();
                            jsonParam.put("username", username.getText());
                            jsonParam.put("passwort", password.getText());

                            Log.i("JSON", jsonParam.toString());

                            // Daten werden geschrieben
                            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
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


                Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(loginIntent);

            }

        });
    }

}