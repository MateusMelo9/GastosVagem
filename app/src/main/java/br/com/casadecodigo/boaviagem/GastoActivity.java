package br.com.casadecodigo.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Calendar;

public class GastoActivity extends Activity {
    private Spinner categoria;
    private int dia,mes,ano;
    private Button btnData;
    private Button btnGastei;
    private DataBaseHelper helper;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gasto);

        Calendar calendario = Calendar.getInstance();
        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        ano = calendario.get(Calendar.YEAR);

        btnData = (Button) findViewById(R.id.btnData);
        btnData.setText(dia+"/"+(mes+1)+"/"+ano);
        btnGastei = (Button) findViewById(R.id.btnGastei);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.categoria_gasto, android.R.layout.simple_spinner_item);
        categoria = (Spinner) findViewById(R.id.spCategoria);
        categoria.setAdapter(adapter);

        helper = new DataBaseHelper(this);
        id = getIntent().getStringExtra(Constantes.VIAGEM_ID);


        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v.getId());
            }
        });

        btnGastei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues values = new ContentValues();

            }
        });


    }

    private void preparacao(){
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT viagem_id, categoria, data, descricao, valor, local FROM gasto WHERE _id=?", new String[]{id});
        cursor.moveToFirst();

    }
    @Override
    protected Dialog onCreateDialog(int id) {
        if (R.id.btnData == id) {
            return new DatePickerDialog(this, listener, ano, mes, dia);
        }
        return null;
    }


    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ano=year;
            mes=monthOfYear;
            dia = dayOfMonth;
            btnData.setText(dia+"/"+(mes+1)+"/"+ano);
        }
    };
}
