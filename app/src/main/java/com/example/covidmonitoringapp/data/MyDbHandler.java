package com.example.covidmonitoringapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.covidmonitoringapp.data.model.PatientDetails;
import com.example.covidmonitoringapp.params.Params;

import java.util.ArrayList;

public class MyDbHandler extends SQLiteOpenHelper {

    public MyDbHandler(Context context)
    {
        super(context, Params.DB_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create = "CREATE TABLE " + Params.TABLE_NAME + "(" +
                Params.KEY_ID + " INTEGER PRIMARY KEY,"+ Params.KEY_NAME
                + " TEXT, "+ Params.KEY_HEARTRATE + " REAL, "
                + Params.KEY_RESRATE+ " REAL, "
                + Params.KEY_FEVERVALUE + " REAL, "
                + Params.KEY_FEVER + " NUMERIC, "
                + Params.KEY_NAUSEA + " NUMERIC, "
                + Params.KEY_HEADACHE+ " NUMERIC, "
                + Params.KEY_DIARRHEA+ " NUMERIC, "
                + Params.KEY_SOARTHROAT+ " NUMERIC, "
                + Params.KEY_MUSCLEACHE+ " NUMERIC, "
                + Params.KEY_LSAT+ " NUMERIC, "
                + Params.KEY_COUGH+ " NUMERIC, "
                + Params.KEY_SOB+ " NUMERIC, "
                + Params.KEY_TIRED+ " NUMERIC"+")";

        Log.d("db create", "query: "+ create);
        sqLiteDatabase.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addPatientDetails(PatientDetails patientDetails)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Params.KEY_NAME, patientDetails.getName());
        values.put(Params.KEY_HEARTRATE,patientDetails.getHeart_rate());
        values.put(Params.KEY_RESRATE,patientDetails.getRespiratory_rate());
        values.put(Params.KEY_FEVERVALUE,patientDetails.getFever_value());
        values.put(Params.KEY_FEVER,patientDetails.isFever());
        values.put(Params.KEY_NAUSEA, patientDetails.isNausea());
        values.put(Params.KEY_HEADACHE, patientDetails.isHeadache());
        values.put(Params.KEY_DIARRHEA,patientDetails.isDiarrhea());
        values.put(Params.KEY_SOARTHROAT,patientDetails.isSoar_throat());
        values.put(Params.KEY_MUSCLEACHE, patientDetails.isMuscle_ache());
        values.put(Params.KEY_LSAT, patientDetails.isLsat());
        values.put(Params.KEY_COUGH,patientDetails.isCough());
        values.put(Params.KEY_SOB, patientDetails.isSob());
        values.put(Params.KEY_TIRED,patientDetails.isFeeling_tired());

        db.insert(Params.TABLE_NAME, null, values);
        Log.d("db add contact", "Successfully inserted");
        db.close();
    }

    public ArrayList<PatientDetails> getAllPatientsName()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor patients = db.rawQuery("SELECT * FROM " + Params.TABLE_NAME, null);
        ArrayList<PatientDetails> patientsList = new ArrayList<>();
        if (patients.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                patientsList.add(new PatientDetails(patients.getString(1),patients.getDouble(2),
                        patients.getDouble(3),patients.getDouble(4),
                        patients.getInt(5)>0,
                        patients.getInt(6)>0,
                        patients.getInt(7)>0,
                        patients.getInt(8)>0,
                        patients.getInt(9)>0,
                        patients.getInt(10)>0,
                        patients.getInt(11)>0,
                        patients.getInt(12)>0,
                        patients.getInt(13)>0,
                        patients.getInt(14)>0));
            } while (patients.moveToNext());
            // moving our cursor to next.
        }
        return patientsList;
    }
}
