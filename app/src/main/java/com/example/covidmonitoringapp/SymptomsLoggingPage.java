package com.example.covidmonitoringapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

public class SymptomsLoggingPage extends AppCompatActivity {

    private Button symptomsSubmitButton;
    private CheckBox headacheCheckbox;
    private CheckBox feverCheckbox;
    private CheckBox coughCheckbox;
    private CheckBox shortBreathCheckbox;
    private CheckBox feelingTiredCheckbox;
    private CheckBox tasteSmellCheckbox;
    private CheckBox soreThroatCheckbox;
    private CheckBox nauseaCheckbox;
    private CheckBox diarrheaCheckBox;
    private CheckBox muscleAcheCheckBox;
    public String symptomsvalue;
    public RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_logging_page);

        symptomsSubmitButton = (Button) findViewById(R.id.symptomsSubmitButton);
        headacheCheckbox =(CheckBox) findViewById(R.id.headacheCheckbox);
        feverCheckbox =(CheckBox) findViewById(R.id.feverCheckbox);
        coughCheckbox =(CheckBox) findViewById(R.id.coughCheckbox);
        shortBreathCheckbox =(CheckBox) findViewById(R.id.shortBreathCheckbox);
        feelingTiredCheckbox =(CheckBox) findViewById(R.id.feelingTiredCheckbox);
        tasteSmellCheckbox =(CheckBox) findViewById(R.id.tasteSmellCheckbox);
        soreThroatCheckbox =(CheckBox) findViewById(R.id.soreThroatCheckbox);
        nauseaCheckbox =(CheckBox) findViewById(R.id.nauseaCheckbox);
        diarrheaCheckBox =(CheckBox) findViewById(R.id.diarrheaCheckBox);
        muscleAcheCheckBox = (CheckBox) findViewById(R.id.muscleAcheCheckBox);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        symptomsvalue = "";
        symptomsSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitSymptomsButton();
            }
        });
    }

    public void submitSymptomsButton()
    {
        boolean obtainedFever = false,
                obtainedNausea = false,
                obtainedHeadache = false,
                obtainedDiarrhea = false,
                obtainedSoarThroat = false,
                obtainedMuscleAche = false,
                obtainedlsat = false,
                obtaineCough = false,
                obtainedSob = false,
                obtainedFeelingTired = false;
//        String checkedCheckBoxList = "";
        float rating = ratingBar.getRating();
        double obtainedFeverValue = rating;
        if(headacheCheckbox.isChecked()) {
            Log.d("headache","headache");
            obtainedHeadache = true;
        } if(feverCheckbox.isChecked()) {
            obtainedFever = true;
        } if(coughCheckbox.isChecked()) {
            obtaineCough = true;
        } if(shortBreathCheckbox.isChecked()) {
            obtainedSob = true;
        } if(feelingTiredCheckbox.isChecked()) {
            obtainedFeelingTired = true;
        } if(tasteSmellCheckbox.isChecked()) {
            obtainedlsat = true;
        } if(soreThroatCheckbox.isChecked()) {
            obtainedSoarThroat = true;
        } if(nauseaCheckbox.isChecked()) {
            obtainedNausea = true;
        } if(diarrheaCheckBox.isChecked()) {
            obtainedDiarrhea = true;
        } if(muscleAcheCheckBox.isChecked()) {
            obtainedMuscleAche = true;
        }
//        symptomsvalue = checkedCheckBoxList;
        Log.d("symptomsvalue","symptomsvalue"+symptomsvalue);
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "JsonFolder/valuesJSON.json";
        Log.d("tag1",fileName);
        File file = new File(baseDir+"/"+fileName);
        if(file.exists()) {
            //get previous json values and store them.
            JSONObject jsonContents = getJsonFileContents(file);
            String toBeEnteredSymptoms="";
            try {
                double obtainedHeartRateValue = jsonContents.getDouble("heart_rate");
                double obtainedRespiratoryRateValue = jsonContents.getDouble("respiratory_rate");
//
//                if(symptomsvalue != "") {//recoding and calculation done
//                    toBeEnteredSymptoms = symptomsvalue;
//                }
                // enter json contents
                enterContentsToJSONFile(file,obtainedHeartRateValue,obtainedRespiratoryRateValue,obtainedFeverValue,obtainedFever,obtainedNausea,
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