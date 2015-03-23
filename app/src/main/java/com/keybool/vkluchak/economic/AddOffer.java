package com.keybool.vkluchak.economic;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by vkluc_000 on 14.02.2015.
 */
public class AddOffer extends Activity implements TextView.OnEditorActionListener {
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
        setContentView(R.layout.addoffer);

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
        etPhone.setOnEditorActionListener(this);

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
        courseF = Float.parseFloat(etCourse.getText().toString());
        String amount = etAmount.getText().toString();
        String phone = etPhone.getText().toString();
        String location = etLocation.getText().toString();


        //if (!TextUtils.isEmpty(course)) {
        //     courseF = Float.parseFloat(course);
        //    Log.d(LOG_TAG, "----Curse : "+ courseF);
       // }else {
        //    etCourse.setError("Input data");
            //courseF =0;
        //}



        switch (v.getId()){
            case R.id.btnAddOffer:
                if(swOffer.isChecked()) {
                    //if(onEditorAction()) // визивать input проверку
                    Log.d(LOG_TAG, "----Insert currency: ----");
                    db.addRec(nameCarrent, courseF, amount, phone, location, 0, 0);
                    Log.d(LOG_TAG, "----Done----");
                }else
                    db.addRec(nameCarrent, courseF, amount, phone, location, 1, 0);

                break;
        }

    }

    // сделать также для каждого EditText + приошибке останавливать обработку

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch(v.getId()){
            case R.id.etAmount:
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (etAmount.getText().toString().trim().equalsIgnoreCase(""))
                        if(etAmount.getText().toString().trim().matches("(?i).*[a-zа-я].*") ){ // проверь регулярку
                            etAmount.setError("Please enter digits");
                            return false;
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Notnull", Toast.LENGTH_SHORT).show();
                    return false;
                }return true;
            case R.id.etCourse:
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (etCourse.getText().toString().trim().equalsIgnoreCase(""))
                        if(etCourse.getText().toString().trim().matches("(?i).*[a-zа-я].*") ){
                            etCourse.setError("Please enter some thing!!!");
                            return false;
                        }
                        else return false;
                } return true;
            case R.id.etPhone:
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (etPhone.getText().toString().trim().equalsIgnoreCase("")){
                        if(etPhone.getText().toString().trim().length()!= 10 ){
                            etPhone.setError("Enter in correct form - 0901112233");
                            return false;
                        }
                    }else return false;
                }
                return true;
            case R.id.etLocation:
                break;
        }
        return false;
    }
}
