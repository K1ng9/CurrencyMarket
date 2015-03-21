package com.keybool.vkluchak.economic;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

/**
 * Created by vkluc_000 on 14.02.2015.
 */
public class AddOffer extends Activity{
    final String LOG_TAG = "myLogs";

    Switch swOffer;
    Button btnAddOffer, btnUpd;
    EditText etName, etCourse, etId, etAmount, etPhone, etLocation;
    Spinner spinner2;
    //Cursor cursor;
    String nameCarrent;

    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addticket);

        btnAddOffer = (Button) findViewById(R.id.btnAddOffer);
        btnUpd = (Button) findViewById(R.id.btnUpd);


        etId = (EditText) findViewById(R.id.etID);
        //etName = (EditText) findViewById(R.id.etName);
        swOffer = (Switch) findViewById(R.id.swOffer);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        etCourse = (EditText) findViewById(R.id.etCourse);
        etAmount = (EditText) findViewById(R.id.etAmount);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etLocation = (EditText) findViewById(R.id.etLocation);

        // откриваем подлючение к ДБ
        db = new DB(this);
        db.open();
        adapterSpinner();
    }
    public void adapterSpinner(){
        // Адаптер для спинера
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.current, android.R.layout.simple_spinner_item);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner2.setAdapter(adapter);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // позиция нажатого елемента
                Object item = parent.getItemAtPosition(position);
                nameCarrent=item.toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void onclick(View v){
        float courseF;
        //обект для даннх
        ContentValues cv = new ContentValues();
        //данние из полей в переменние
        //String name = etName.getText().toString();
        String course = etCourse.getText().toString();
        String amount = etAmount.getText().toString();
        String phone = etPhone.getText().toString();
        String location = etLocation.getText().toString();
        //boolean sw = swOffer.getShowText();

        //Log.d(LOG_TAG, "Switch = " +sw  );
        if (!TextUtils.isEmpty(course)) {
             courseF = Float.parseFloat(course);
            Log.d(LOG_TAG, "----Curse : "+ courseF);
        }else courseF = (float) 0.01;

        switch (v.getId()){
            case R.id.btnAddOffer:
                if(swOffer.isChecked()) {
                    Log.d(LOG_TAG, "----Insert currency: ----");
                    db.addRec(nameCarrent, courseF, amount, phone, location, 0, 0);
                    Log.d(LOG_TAG, "----Done----");
                }else
                    db.addRec(nameCarrent, courseF, amount, phone, location, 1, 0);

                break;
        }

    }
}