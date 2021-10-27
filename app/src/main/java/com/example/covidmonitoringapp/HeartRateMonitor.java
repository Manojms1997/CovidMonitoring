package com.example.covidmonitoringapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class HeartRateMonitor extends AppCompatActivity {

    private Button checkHeartRateButton;
    private Button heartRateSubmitButton;
    private VideoView heartRateVideoView;
    private Button calculateHeartRate;
    public Double heartRateValue;
    public TextView heartRateDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_monitor);
        askPermissions();
        checkHeartRateButton = (Button) findViewById(R.id.recordHeartRateButton);
        heartRateSubmitButton = (Button) findViewById(R.id.heartRateSubmitButton);
        heartRateVideoView = (VideoView) findViewById(R.id.heartRateVideoView);
        calculateHeartRate = (Button) findViewById(R.id.calculateHeartRate);
        heartRateDisplay = (TextView) findViewById(R.id.heartRateDisplay);

        heartRateSubmitButton.setEnabled(false);
        heartRateValue = 53.1;

        checkHeartRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordHeartRate();
            }
        });

        heartRateSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitHeartRate();
            }
        });

        calculateHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateHeartRate();
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("heartRateUpdater"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Double message = intent.getDoubleExtra("HeartRate",0.00);
            Toast.makeText(getApplicationContext(), String.valueOf(message)+"heartRateReceived", Toast.LENGTH_SHORT).show();
            heartRateValue = message;
            heartRateDisplay.setText("Obtained heart rate: "+String.valueOf(heartRateValue));
            heartRateSubmitButton.setEnabled(true);
            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "JsonFolder/valuesJSON.json";
            Log.d("tag1",fileName);
            File file = new File(baseDir+"/"+fileName);
            if(file.exists()) {
                //get previous json values and store them.
                JSONObject jsonContents = getJsonFileContents(file);
                double toBeEnteredHeartRateValue=0.00;

                try {
                    double obtainedHeartRateValue = jsonContents.getDouble("heart_rate");
                    double obtainedRespiratoryRateValue = jsonContents.getDouble("respiratory_rate");
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

                    if(heartRateValue != 0.00) {//recoding and calculation done
                        toBeEnteredHeartRateValue = heartRateValue;
                    }
                    // enter json contents
                    enterContentsToJSONFile(file,toBeEnteredHeartRateValue,obtainedRespiratoryRateValue, obtainedFeverValue,obtainedFever,obtainedNausea,
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

    private void recordHeartRate()
    {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 45);
        Uri fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }


    public void submitHeartRate()
    {
        // logic to submit valid heart rate

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private void calculateHeartRate() {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "CovidMonitoringVideos/test_video.mp4";
        String videoFile = (baseDir+"/"+fileName);
        Log.d("tag",videoFile);
        heartRateDisplay.setText("Calculating...");
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(videoFile);
//        String frameRate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE);
//        Log.d("framerate=",frameRate);
        PixelCalculatorTread pixelCalculatorTread = new PixelCalculatorTread(videoFile,getApplicationContext());
        pixelCalculatorTread.start();
        heartRateVideoView.setVideoPath(videoFile);
        heartRateVideoView.start();
        // call calculation logic

    }

    public Uri getOutputMediaFileUri()
    {
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "CovidMonitoringVideos");
        imagesFolder.mkdir();
        File image = new File(imagesFolder, "test_video.mp4");
        Uri uriSavedVideo = FileProvider.getUriForFile(HeartRateMonitor.this,getApplicationContext().getPackageName()+ ".provider", image);
        return uriSavedVideo;
    }

    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
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

class PixelCalculatorTread extends Thread { // for now I will remove csv feature
    String filePath;
    Context context;

    PixelCalculatorTread(String filePath, Context context)
    {
        this.filePath = filePath;
        this.context = context;
    }

    @Override
    public void run() {
//        File file = new File(Environment.getExternalStorageDirectory()+"/Csvs/test1" +".csv");
//        if (!file.exists()) {
//            try{
//                generateCvsFolderPath();
//            } catch (Exception e) {
//
//            }
//        }
//        String outputValues = "";
        ArrayList<Integer> redPixelValues = new ArrayList<Integer>();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        String frameCount = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
        Log.d("frameCount:",frameCount);
        for(int i=0;i<Integer.parseInt(frameCount);i+=5)
        {
            Log.d("loop",String.valueOf(i));
            Bitmap bitmap = retriever.getFrameAtIndex(i);
            int pixel1 = bitmap.getPixel(100,100);
            int red1 = Color.red(pixel1);
            int pixel2 = bitmap.getPixel(100,250);
            int red2 = Color.red(pixel2);
//            int pixel3 = bitmap.getPixel(100,500);
//            int red3 = Color.red(pixel3);
//            int pixel4 = bitmap.getPixel(100,750);
//            int red4 = Color.red(pixel4);
            int pixel5 = bitmap.getPixel(200,100);
            int red5 = Color.red(pixel5);
            int pixel6 = bitmap.getPixel(200,250);
            int red6 = Color.red(pixel6);
//            int pixel7 = bitmap.getPixel(200,500);
//            int red7 = Color.red(pixel7);
//            int pixel8 = bitmap.getPixel(200,750);
//            int red8 = Color.red(pixel8);
            int pixel9 = bitmap.getPixel(400,100);
            int red9 = Color.red(pixel9);
            int pixel10 = bitmap.getPixel(400,250);
            int red10 = Color.red(pixel10);
//            int pixel11 = bitmap.getPixel(400,500);
//            int red11 = Color.red(pixel11);
//            int pixel12 = bitmap.getPixel(400,750);
//            int red12 = Color.red(pixel12);
            int pixel13 = bitmap.getPixel(500,100);
            int red13 = Color.red(pixel13);
            int pixel14 = bitmap.getPixel(500,250);
            int red14 = Color.red(pixel14);
//            int pixel15 = bitmap.getPixel(500,500);
//            int red15 = Color.red(pixel15);
//            int pixel16 = bitmap.getPixel(500,750);
//            int red16 = Color.red(pixel16);
            int averageRedValue = (red1+red2+red5+red6+red9+red10+red13+red14)/16;
            redPixelValues.add(averageRedValue);
//            Toast.makeText(MainActivity.getApplicationContext(), String.valueOf(red), Toast.LENGTH_SHORT).show();
//            outputValues+=String.valueOf(averageRedValue)+";";
        }
        Log.d("redPixelValues", redPixelValues.toString());

        // Write the peak detection logic here:(input data for peak detection: redPixelValues)
        int totalPeaks = calculatePeakValues(redPixelValues);
        double heartRate = (4 * totalPeaks)/3;
        sendMessageToActivity(heartRate);

//        FileWriter fw = null;
//        try {
//            fw = new FileWriter(file.getAbsoluteFile());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        BufferedWriter bw = new BufferedWriter(fw);
//        try {
//            bw.write(outputValues);
//            bw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    public void generateCvsFolderPath()
    {
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Csvs");
        imagesFolder.mkdir(); // <----
        File image = new File(imagesFolder, "test1.csv");
    }

    public int calculatePeakValues(ArrayList<Integer> timeSeriesData)
    {
        int peaks = 0, totalPeaks = 0;
        int timeSeriesDataSize = timeSeriesData.size();
        for(int i=1; i<timeSeriesDataSize-1; i++) // iterating from second to last-1 element
        {
            if(timeSeriesData.get(i-1) < timeSeriesData.get(i))
            {
                if(timeSeriesData.get(i + 1) < timeSeriesData.get(i))
                {
                    peaks+=1;
                }
            } else {
                if(timeSeriesData.get(i+1) > timeSeriesData.get(i))
                {
                    peaks+=1;
                }
            }
        }
        totalPeaks = peaks / 2;
//        Toast.makeText(MainActivity.getApplicationContext(), String.valueOf(totalPeaks), Toast.LENGTH_SHORT).show();
        Log.d("totalPeaks",String.valueOf(totalPeaks));
        return totalPeaks;
    }

    void sendMessageToActivity(double heartRate) {
        Intent intent = new Intent("heartRateUpdater");
        Log.d("heartRate value",String.valueOf(heartRate));
        intent.putExtra("HeartRate", heartRate);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}