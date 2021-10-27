package com.example.covidmonitoringapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

public class RespiratoryMonitor extends AppCompatActivity {

    private Button measureRespiratoryRateButton;
    private Button respiratoryRateSubmitButton;
    public Double respiratoryRateValue;
    public TextView respiratoryRateDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiratory_monitor);

        measureRespiratoryRateButton = (Button) findViewById(R.id.measureRespiratoryRateButton);
        respiratoryRateSubmitButton = (Button) findViewById(R.id.respiratoryRateSubmitButton);
        respiratoryRateDisplay = (TextView) findViewById(R.id.respiratoryRateDisplay);
        respiratoryRateSubmitButton.setEnabled(false);
        respiratoryRateValue = 31.3;

        measureRespiratoryRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateRespiratoryRate();
            }
        });

        respiratoryRateSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRespiratoryRate();
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("respiratoryRateUpdater"));
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Double message = intent.getDoubleExtra("RespiratoryRate",0.00);
            Toast.makeText(getApplicationContext(), String.valueOf(message)+"heartRateReceived", Toast.LENGTH_SHORT).show();
            respiratoryRateValue = message;
            respiratoryRateSubmitButton.setEnabled(true);
            respiratoryRateDisplay.setText("Obtained respiratory rate: "+String.valueOf(message));
            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "JsonFolder/valuesJSON.json";
            Log.d("tag1",fileName);
            File file = new File(baseDir+"/"+fileName);
            if(file.exists()) {
                //get previous json values and store them.
                JSONObject jsonContents = getJsonFileContents(file);
                double toBeEnteredRespiratoryRateValue=0.00;
                try {
                    double obtainedHeartRateValue = jsonContents.getDouble("heart_rate");
                    double obtainedRespiratoryRateValue = jsonContents.getDouble("respiratory_rate");
                    double obtainedFeverValue = jsonContents.getDouble("fever_value");
                    boolean obtainedFever = jsonContents.getBoolean("fever");
                    boolean obtainedNausea = jsonContents.getBoolean("nausea");
                    boolean obtainedHeadache = jsonContents.getBoolean("headache");
                    boolean obtainedDiarrhea = jsonContents.getBoolean("diarrhea");
                    boolean obtainedSoarThroat = jsonContents.getBoolean("soar_throat");
                    boolean obtainedMuscleAche = jsonContents.getBoolean("muscle_ache");
                    boolean obtainedlsat = jsonContents.getBoolean("lsat");
                    boolean obtaineCough = jsonContents.getBoolean("cough");
                    boolean obtainedSob = jsonContents.getBoolean("sob");
                    boolean obtainedFeelingTired = jsonContents.getBoolean("feeling_tired");
                    if(respiratoryRateValue != 0.00) {//recoding and calculation done
                        toBeEnteredRespiratoryRateValue = respiratoryRateValue;
                    }
                    // enter json contents
//                    enterContentsToJSONFile(file,obtainedHeartRateValue,toBeEnteredRespiratoryRateValue,obtainedSymptoms);
                    enterContentsToJSONFile(file,obtainedHeartRateValue,toBeEnteredRespiratoryRateValue, obtainedFeverValue,obtainedFever,obtainedNausea,
                            obtainedHeadache,
                            obtainedDiarrhea,
                            obtainedSoarThroat,
                            obtainedMuscleAche,
                            obtainedlsat,
                            obtaineCough,
                            obtainedSob,
                            obtainedFeelingTired);
                } catch(Exception e)
                { }
            }
        }
    };

    private int calculateRespiratoryRate()
    {
        Intent intent = new Intent(getApplicationContext(),GetAccelValue.class);
        startService(intent);
        respiratoryRateDisplay.setText("Obtaining sensor data...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopService(intent);
            }
        }, 45000);
        //calculate respiratory rate

        return 0;
    }

    public void submitRespiratoryRate()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void enterContentsToJSONFile(File file,  double heartRateValue, double respiratoryRateValue, double fever_value,
                                        boolean fever,
                                        boolean nausea,
                                        boolean headache,
                                        boolean diarrhea,
                                        boolean soar_throat,
                                        boolean muscle_ache,
                                        boolean lsat,
                                        boolean cough,
                                        boolean sob,
                                        boolean feeling_tired){
        JSONObject obj = new JSONObject() ;
//        String[] symptoms = {};
        try {
            obj.put("heart_rate", heartRateValue);
            obj.put("respiratory_rate", respiratoryRateValue);
            obj.put("fever_value", fever_value);
            obj.put("fever",fever);
            obj.put("nausea",nausea);
            obj.put("headache",headache);
            obj.put("diarrhea",diarrhea);
            obj.put("soar_throat",soar_throat);
            obj.put("muscle_ache",muscle_ache);
            obj.put("lsat",lsat);
            obj.put("cough",cough);
            obj.put("sob",sob);
            obj.put("feeling_tired",feeling_tired);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //write json object to file:
        try {
            Writer output = null;
            Log.d("tag3","inside try");
            output = new BufferedWriter(new FileWriter(file));
            Log.d("tag3","inside try1");

            output.write(obj.toString());
            output.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public JSONObject getJsonFileContents(File jsonFile)
    {
        JSONObject jsonObject = null;
        try {
            FileReader fileReader = new FileReader(jsonFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            String response = stringBuilder.toString();
            jsonObject  = new JSONObject(response);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }
}