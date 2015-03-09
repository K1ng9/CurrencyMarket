package com.keybool.vkluchak.economic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.concurrent.TimeUnit;


public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 1;
    Button btnBuy, btnSell;
    SimpleCursorAdapter scAdapter;
    ListView lvList;
    DB db;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnBuy = (Button) findViewById(R.id.btnBuy);

        btnSell = (Button) findViewById(R.id.btnSell);
        // откриваем подлючение к ДБ
        db = new DB(this);
        db.open();

        //----------------------------------------------------------------------------
        // формируем столбцы сопоставления
        String[] from = new String[]{DB.COLUMN_NAME, DB.COLUMN_COURSE, DB.COLUMN_AMOUNT, DB.COLUMN_PHONE, DB.COLUMN_LOCATION};
        int[] to = new int[]{R.id.tvText1, R.id.tvText2, R.id.tvText3, R.id.tvText4, R.id.tvText5};

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.item_of_list, null, from, to, 0);
        lvList = (ListView) findViewById(R.id.lvList);
        lvList.setAdapter(scAdapter);

        // добавляем контекстное меню к списку
        registerForContextMenu(lvList);

        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);
        //------------------------------------------------------------------------------

    }


    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.btnBuy:

                //buy();
                break;
            case R.id.btnSell:
                // включяем активити addTicket виводим его на екран
                Intent intent = new Intent(this, AddTicket.class);
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


    // -------------------------LOADER херотень -----------------------
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
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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


