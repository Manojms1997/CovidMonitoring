package com.example.covidmonitoringapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

public class MainActivity extends AppCompatActivity {
    private Button heartRateButton;
    private Button respiratoryRateButton;
    private Button symptomsButton;
    private Button finalSubmitButton;
    private TextView heartRateValue;
    private TextView respiratoryRateValue;
    private TextView symptomsValue;
    private EditText patientsName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermissions();
        heartRateButton = (Button) findViewById(R.id.heartRateButton);
        respiratoryRateButton = (Button) findViewById(R.id.respiratoryRateButton);
        symptomsButton = (Button) findViewById(R.id.symptomsButton);
        finalSubmitButton = (Button) findViewById(R.id.finalSubmitButton);
        heartRateValue = (TextView) findViewById(R.id.heartRateValue);
        respiratoryRateValue = (TextView) findViewById(R.id.respiratoryRateValue);
        symptomsValue = (TextView) findViewById(R.id.symptomsValue);
        patientsName = (EditText)  findViewById(R.id.patientsName);
        heartRateValue.setText("");
        respiratoryRateValue.setText("");
        symptomsValue.setText("");



        // initial json logic:
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "JsonFolder/valuesJSON.json";
        Log.d("tag1",fileName);
        File file = new File(baseDir+"/"+fileName);
        if(!file.exists()) // if file does not exist create it
        {
            // create new file
            File jsonFile = createJSONFile();
            Log.d("tag2","FileDoesNotExist");
            // add json contents for the first time
            enterContentsToJSONFile(jsonFile,0.00,0.00,0,false,false,false,false,false,false,false,false,false,false);
        }

        // now that the file has been created, get the heart, respiratory and symptoms values
        JSONObject jsonContents = getJsonFileContents(file);
        try {
            double obtainedHeartRateValue = jsonContents.getDouble("heart_rate");
            double obtainedRespiratoryRateValue = jsonContents.getDouble("respiratory_rate");
//            String obtainedSymptoms = jsonContents.getString("symptoms");
            Double obtainedFeverValue = jsonContents.getDouble("fever_value");
            Boolean obtainedFever = jsonContents.getBoolean("fever");
            Boolean obtainedNausea = jsonContents.getBoolean("nausea");
            Boolean obtainedHeadache = jsonContents.getBoolean("headache");
            Boolean obtainedDiarrhea = jsonContents.getBoolean("diarrhea");
            Boolean obtainedSoarThroat = jsonContents.getBoolean("soar_throat");
            Boolean obtainedMuscleAche = jsonContents.getBoolean("muscle_ache");
            Boolean obtainedlsat = jsonContents.getBoolean("lsat");
            Boolean obtaineCough = jsonContents.getBoolean("cough");
            Boolean obtainedSob = jsonContents.getBoolean("sob");
            Boolean obtainedFeelingTired = jsonContents.getBoolean("feeling_tired");

            if(obtainedHeartRateValue != 0.00)
            {
                heartRateValue.setText("Obtained Heart rate: "+String.valueOf(obtainedHeartRateValue));
            } if(obtainedRespiratoryRateValue != 0.00) {
                respiratoryRateValue.setText("Obtained Respiratory rate"+String.valueOf(obtainedRespiratoryRateValue));
            }
            String obtainedSymptoms = "";
            if(obtainedFeverValue != 0.00){
                obtainedSymptoms += "fever_value:"+String.valueOf(obtainedFeverValue)+"\n";
            } if(obtainedFever) {
                obtainedSymptoms += "fever \n";
            } if(obtainedNausea) {
                obtainedSymptoms += "nausea\n";
            } if (obtainedHeadache) {
                obtainedSymptoms += "headache\n";
            } if(obtainedDiarrhea) {
                obtainedSymptoms += "diarrhea\n";
            } if(obtainedSoarThroat) {
                obtainedSymptoms += "sore_throat\n";
            } if(obtainedMuscleAche) {
                obtainedSymptoms += "muscle_ache\n";
            } if(obtainedlsat) {
                obtainedSymptoms += "Loss of Smell and Taste\n";
            } if(obtaineCough) {
                obtainedSymptoms += "cough\n";
            } if(obtainedSob) {
                obtainedSymptoms += "Shortness of Breath\n";
            } if(obtainedFeelingTired) {
                obtainedSymptoms += "feeling_tired\n";
            }
            if(obtainedSymptoms != "") {
                symptomsValue.setText("Obtained Symptoms:\n"+obtainedSymptoms);
            }
        } catch(Exception e)
        { }

        heartRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHeartRateMonitorScreen();
            }
        });

        respiratoryRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRespiratoryRateMonitorScreen();
            }
        });

        symptomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSymptomsScreen();
            }
        });

        finalSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalSubmitButtonClicked();
            }
        });
    }
    public void openHeartRateMonitorScreen()
    {
        Intent intent = new Intent(this,HeartRateMonitor.class);
        startActivity(intent);
    }

    public void openRespiratoryRateMonitorScreen()
    {
        Intent intent = new Intent(this,RespiratoryMonitor.class);
        startActivity(intent);
    }

    public void openSymptomsScreen()
    {
        Intent intent = new Intent(this,SymptomsLoggingPage.class);
        startActivity(intent);
    }

    public void finalSubmitButtonClicked()
    {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "JsonFolder/valuesJSON.json";
        File file = new File(baseDir+"/"+fileName);
        JSONObject jsonContents = getJsonFileContents(file);
        String patientName = patientsName.getText().toString();
        try {
            double obtainedHeartRateValue = jsonContents.getDouble("heart_rate");
            double obtainedRespiratoryRateValue = jsonContents.getDouble("respiratory_rate");


            if(obtainedHeartRateValue != 0.00 && obtainedRespiratoryRateValue!=0.00 && patientName!="")
            {
                Intent intent = new Intent(this,ResultActivity.class);
                intent.putExtra("name", patientName);
                startActivity(intent);
            }
        }catch(Exception e)
        { }
    }

    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    public File createJSONFile()
    {
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "JsonFolder");
        imagesFolder.mkdir();
        File valuesJSONFile = new File(imagesFolder, "valuesJSON.json");
        return valuesJSONFile;
    }

    public void enterContentsToJSONFile(File file,  double heartRateValue, double respiratoryRateValue, float fever_value,
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