package com.example.covidmonitoringapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.covidmonitoringapp.data.MyDbHandler;
import com.example.covidmonitoringapp.data.model.PatientDetails;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private TextView results;
    private Button goBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        String patientName = getIntent().getExtras().getString("name");
        results = (TextView) findViewById(R.id.results);
        goBackButton = (Button) findViewById(R.id.goBackButton);


        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        MyDbHandler myDbHandler = new MyDbHandler(this);

        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "JsonFolder/valuesJSON.json";
        File file = new File(baseDir+"/"+fileName);
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

            //logic to add new content in json to database

            PatientDetails newPatient = new PatientDetails(
                    patientName,
                    obtainedHeartRateValue,
                    obtainedRespiratoryRateValue,
                    obtainedFeverValue,
                    obtainedFever,
                    obtainedNausea,
                    obtainedHeadache,
                    obtainedDiarrhea,
                    obtainedSoarThroat,
                    obtainedMuscleAche,
                    obtainedlsat,
                    obtaineCough,
                    obtainedSob,
                    obtainedFeelingTired
            );

            myDbHandler.addPatientDetails(newPatient);
        } catch(Exception e) {

        }

        // logic to retrive data from database and display it
        ArrayList<PatientDetails> patients = myDbHandler.getAllPatientsName();
        String output = "";
        for(int i=0;i<patients.size();i++)
        {
            Log.d("patients:",patients.get(i).getName());
            output += "Name: "+patients.get(i).getName()+" |HeartRate: "+patients.get(i).getHeart_rate()+" |" +
                    " RespiratoryRate: "+patients.get(i).getRespiratory_rate()+" \n";
        }
        results.setText(output);

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