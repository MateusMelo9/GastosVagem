package br.com.casadecodigo.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ViagemActivity extends Activity {

    private Date dataChegada, dataSaida;
    private DataBaseHelper dbHelper;
    private EditText destino, qtd_pessoas, orcamento;
    private RadioGroup radioGroup;
    private Button btnSalvar,btnDataChegada,btnDataSaida;
    private int dia,mes,ano;
    private String id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nova_viagem);

        Calendar calendario = Calendar.getInstance();
        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        ano = calendario.get(Calendar.YEAR);


        destino = (EditText) findViewById(R.id.etDestino);
        qtd_pessoas = (EditText) findViewById(R.id.etQtdPessoas);
        orcamento = (EditText) findViewById(R.id.etOrcamento);
        radioGroup = (RadioGroup) findViewById(R.id.rgTipoViagem);

        dbHelper = new DataBaseHelper(this);

        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnDataChegada = (Button) findViewById(R.id.data_chegada);
        btnDataSaida = (Button) findViewById(R.id.data_saida);

        id = getIntent().getStringExtra(Constantes.VIAGEM_ID);

        if(id!=null){
            prepararEdicao();
        }
    }

    private void prepararEdicao(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT tipo_viagem, destino, data_chegada, " +
                        "data_saida, quantidade_pessoas, orcamento " +
                        "FROM viagem WHERE _id = ?", new String[]{id}
        );
        cursor.moveToFirst();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        if(cursor.getInt(0) == Constantes.VIAGEM_LAZER){
            radioGroup.check(R.id.rbLazer);
        }else {
            radioGroup.check(R.id.rbNegocios);
        }

        destino.setText(cursor.getString(1));
        dataChegada = new Date(cursor.getLong(2));
        dataSaida = new Date(cursor.getLong(3));
        btnDataChegada.setText(dateFormat.format(dataChegada));
        btnDataSaida.setText(dateFormat.format(dataSaida));
        qtd_pessoas.setText(cursor.getString(4));
        orcamento.setText(cursor.getString(5));
        cursor.moveToNext();
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viagem_menu,menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case R.id.mNovo_gasto:
                startActivity(new Intent(this,GastoActivity.class));
                return true;
            case R.id.mRemover:
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    public void selecionarData(View view){
        showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id){
            case R.id.data_chegada:
                return new DatePickerDialog(this,dataChegadaListener,ano,mes,dia);

            case R.id.data_saida:
                return new DatePickerDialog(this,dataSaidaListener,ano,mes,dia);
        }
        return null;
    }

    private OnDateSetListener dataChegadaListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
           dataChegada = criarData(dayOfMonth,monthOfYear,year);
            btnDataChegada.setText(dia+"/"+(mes+1)+"/"+ano);

        }
    };

    private OnDateSetListener dataSaidaListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dataSaida = criarData(dayOfMonth,monthOfYear,year);
            btnDataSaida.setText(dia+"/"+(mes+1)+"/"+ano);
        }
    };

    private Date criarData(int diaSelecionado, int mesSelecionado,int anoSelecionado){
        Calendar calendar = Calendar.getInstance();
        calendar.set(diaSelecionado,mesSelecionado,anoSelecionado);
        return calendar.getTime();
    }

    public void salvarViagem(View view){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("destino",destino.getText().toString());
        values.put("data_chegada",dataChegada.getTime());
        values.put("data_saida",dataSaida.getTime());
        values.put("qtd_pessoas",qtd_pessoas.getText().toString());
        values.put("orcamento",orcamento.getText().toString());

        int tipo = radioGroup.getCheckedRadioButtonId();

        if(tipo == R.id.rbLazer){
            values.put("tipo_viagem", Constantes.VIAGEM_LAZER);
        }else{
            values.put("tipo_viagem", Constantes.VIAGEM_NEGOCIOS);
        }

        long resultado ;

        if (id == null) {

            resultado = db.insert("viagem",null,values);
        }else{
            resultado = db.update("viagem",values,"_id = ?",new String[]{id});
        }

        if(resultado != -1){
            Toast.makeText(this,getString(R.string.registro_salvo),Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,getString(R.string.erro_salvar),Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
