package com.keybool.vkluchak.economic;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;


public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 1;
    Button btnAdd;
    SimpleCursorAdapter scAdapter;
    ListView lvList;
    Spinner spinner;
    DB db;
    Cursor cursor;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        spinner = (Spinner) findViewById(R.id.spinner);
        lvList = (ListView) findViewById(R.id.lvList);

        // откриваем подлючение к ДБ
        db = new DB(this);
        db.open();
        cursor = null;

        adapterListView();
        adapterSpinner();
    }

    public void adapterSpinner(){

        // Адаптер для спинера
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.current, android.R.layout.simple_spinner_item);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // позиция нажатого елемента
                Object item = parent.getItemAtPosition(position);
                if(item.toString().trim().length() > 0 ) {
                    cursor = db.selectCurrent(item.toString());
                    scAdapter.swapCursor(cursor);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    // проверяеm курсор
    void checkCursor (Cursor cursor){


        if (cursor.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = cursor.getColumnIndex("_id");
            int nameColIndex = cursor.getColumnIndex(DB.COLUMN_NAME);
            int courseColIndex = cursor.getColumnIndex(DB.COLUMN_COURSE);
            int amountColIndex = cursor.getColumnIndex(DB.COLUMN_AMOUNT);
            int phoneColIndex = cursor.getColumnIndex(DB.COLUMN_PHONE);
            int locColIndex = cursor.getColumnIndex(DB.COLUMN_LOCATION);

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG,
                        "ID = " + cursor.getInt(idColIndex) + ", name = "
                                + cursor.getString(nameColIndex) + ", course = "
                                + cursor.getString(courseColIndex) + ", amount = "
                                + cursor.getString(amountColIndex) + ", phone = "
                                + cursor.getString(phoneColIndex) + ", location = "
                                + cursor.getString(locColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false -
                // выходим из цикла
            } while (cursor.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
    }

    public void adapterListView(){

        //----------------------------------------------------------------------------
        // формируем столбцы сопоставления
        String[] from = new String[]{DB.COLUMN_NAME, DB.COLUMN_COURSE, DB.COLUMN_AMOUNT, DB.COLUMN_PHONE, DB.COLUMN_LOCATION};
        int[] to = new int[]{R.id.tvText1, R.id.tvText2, R.id.tvText3, R.id.tvText4, R.id.tvText5};

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.item_of_list, cursor, from, to, 0);
        lvList.setAdapter(scAdapter);
        // добавляем контекстное меню к списку
        registerForContextMenu(lvList);

        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);
        //------------------------------------------------------------------------------
    }


    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                // включяем активити addTicket виводим его на екран
                Intent intent = new Intent(this, AddOffer.class);
                startActivity(intent);
                break;
        }

    }

    //----------------------Контекстное меню ------------------------------
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            db.delRec(acmi.id);
            // получаем новый курсор с данными
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        }
        return super.onContextItemSelected(item);

    }
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }


    // -------------------------LOADER  -----------------------
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        scAdapter.swapCursor(null);
    }

    static class MyCursorLoader extends CursorLoader {
        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getAllData();

            return cursor;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }
}


