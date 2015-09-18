package com.example.esberksafak.homelight;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private Switch acKapa;
    private TextView isOpen;

    URL url;
    URLConnection urlConnection;
    InputStream in ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        isOpen= (TextView) findViewById(R.id.textView);
        acKapa = (Switch) findViewById(R.id.switch1);
        acKapa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String sUrl = "http://www.safakesberk.com/klasor/kontrol.php?test=";
                if (b){
                    sUrl += "true";
                    isOpen.setText("IŞIK AÇIK");
                    isOpen.setTextColor(Color.parseColor("#8BC34A"));
                }
                else {
                    sUrl += "false";
                    isOpen.setText("IŞIK KAPALI");
                    isOpen.setTextColor(Color.parseColor("#F44336"));
                }
                try {
                   url= new URL(sUrl);
                   urlConnection = url.openConnection();
                   in= new BufferedInputStream(urlConnection.getInputStream());
                   in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        getSupportActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String stringResult = result.get(0);
                    String sUrl = "http://www.safakesberk.com/klasor/kontrol.php?test=";
                    if ("ışığı aç".equals(stringResult)){
                        Toast.makeText(MainActivity.this, "Işığı Açıyorsun", Toast.LENGTH_SHORT).show();
                        sUrl += "true";
                        isOpen.setText("IŞIK AÇIK");
                        isOpen.setTextColor(Color.parseColor("#8BC34A"));
                        acKapa.setChecked(true);
                    }
                    else if("ışığı kapat".equals(stringResult)){
                        sUrl += "false";
                        Toast.makeText(MainActivity.this, "Işığı Kapıyorsun", Toast.LENGTH_SHORT).show();
                        isOpen.setText("IŞIK KAPALI");
                        isOpen.setTextColor(Color.parseColor("#F44336"));
                        acKapa.setChecked(false);
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Alakasız bir şey söyledin", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    try {
                        url= new URL(sUrl);
                        urlConnection = url.openConnection();
                        in= new BufferedInputStream(urlConnection.getInputStream());
                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

        }
    }
}
