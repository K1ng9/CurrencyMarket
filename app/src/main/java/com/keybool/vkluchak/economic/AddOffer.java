package com.keybool.vkluchak.economic;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * Created by vkluc_000 on 14.02.2015.
 */
public class AddOffer extends Activity implements OnEditorActionListener {
    final String LOG_TAG = "myLogs";

    Switch swOffer;
    ImageButton btnAddOffer, btnAddLoc;
    EditText etCourse,etAmount, etPhone, etLocation;
    Spinner spinner2;
    String nameCarrent;

    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addoffer);

        btnAddOffer = (ImageButton) findViewById(R.id.btnAddOffer);
        btnAddLoc = (ImageButton) findViewById(R.id.btnAddLoc);

        swOffer = (Switch) findViewById(R.id.swOffer);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        etCourse = (EditText) findViewById(R.id.etCourse);
        etAmount = (EditText) findViewById(R.id.etAmount);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etPhone.setOnEditorActionListener(this);
        //для кординат
        String cordinats = "";

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
        courseF = Float.parseFloat(etCourse.getText().toString());
        String amount = etAmount.getText().toString();
        String phone = etPhone.getText().toString();
        String location = etLocation.getText().toString();


        switch (v.getId()){
            case R.id.btnAddOffer:
                String address1 = location.split( " -" )[0]; // адрес
                String address2   = location.split( "-" )[1]; // кординати
                if(swOffer.isChecked()) {
                    Log.d(LOG_TAG, "----Insert currency: ----");
                    db.addRec(nameCarrent, courseF, amount, phone, address1, 0, address2);
                    Log.d(LOG_TAG, "----Done----");
                }else
                    db.addRec(nameCarrent, courseF, amount, phone, address1, 1, address2);
                break;
            case R.id.btnAddLoc:
                        Intent myIntent = new Intent(this,
                               MapsActivity.class);
                        startActivityForResult(myIntent, 1);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        String address = data.getStringExtra("address");
        etLocation.setText(address);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch(v.getId()) {
            case R.id.etAmount:
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (etAmount.getText().toString().trim().equalsIgnoreCase("")){
                        //if (etAmount.getText().toString().trim().matches("(?i).*[a-zа-я].*")) { // проверь регулярку
                            etAmount.setError("Please enter digits");
                            return false;
                        } else
                            Toast.makeText(getApplicationContext(), "Notnull", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            case R.id.etCourse:
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (etCourse.getText().toString().trim().equalsIgnoreCase("")){
                       // if (etCourse.getText().toString().trim().matches("(?i).*[a-zа-я].*")) {
                            etCourse.setError("Please enter some thing!!!");
                            return false;
                        } else return false;
                }
                return true;
            case R.id.etPhone:
              //if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (!etPhone.getText().toString().trim().equalsIgnoreCase("")) {
                    if (etPhone.getText().toString().trim().length() != 10) {
                        etPhone.setError("Enter in correct form - 0901112233");
                        return false;
                    }
                //} else return false;
              }
              return true;
            case R.id.etLocation:
                break;
        }return true;
        //return false;
    }
}
