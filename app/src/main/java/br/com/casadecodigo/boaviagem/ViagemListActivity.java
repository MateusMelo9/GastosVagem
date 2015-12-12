package br.com.casadecodigo.boaviagem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.casadecodigo.boaviagem.DAO.BoaViagemDAO;
import br.com.casadecodigo.boaviagem.domain.Viagem;

public class ViagemListActivity extends ListActivity implements OnItemClickListener, OnClickListener, ViewBinder{

    private List<Map<String,Object>> viagens;
    private int viagemSelecionada;
    private AlertDialog alertDialog;
    private AlertDialog confirmacaoDialog;
    private SimpleDateFormat dataFormat;
    private Double valorLimite;
    private BoaViagemDAO dao;
    private boolean modoSelecionarViagem;
    private DataBaseHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new DataBaseHelper(this);
        dao = new BoaViagemDAO(this);

        dataFormat = new SimpleDateFormat("dd/MM/yyyy");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String valor = preferences.getString("valor_limite", "-1");
        valorLimite = Double.valueOf(valor);

        getListView().setOnItemClickListener(this);
        alertDialog = criaAlertDialog();
        confirmacaoDialog = criaConfirmacaoDialog();

        if (getIntent().hasExtra(Constantes.MODO_SELECIONAR_VIAGEM)) {
            modoSelecionarViagem =
                    getIntent().getExtras()
                            .getBoolean(Constantes.MODO_SELECIONAR_VIAGEM);
        }

        new Task().execute((Void[]) null);
    }

    @Override
    public boolean setViewValue(View view, Object data, String textRepresentation) {
        if (view.getId() == R.id.barraProgresso) {
            Double valores[] = (Double[]) data;
            ProgressBar progressBar = (ProgressBar) view;
            progressBar.setMax(valores[0].intValue());
            progressBar.setSecondaryProgress(valores[1].intValue());
            progressBar.setProgress(valores[2].intValue());
            return true;
        }
        return false;
    }

    private class Task extends AsyncTask<Void,Void, List<Map<String, Object>>>{

        @Override
        protected List<Map<String, Object>> doInBackground(Void... params) {
            return listarViagem();
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> maps) {
            String[] de = {"imagem","destino","data","total","barraProgresso"};
            int[] para = {R.id.tipoViagem,R.id.tvDestino,R.id.tvData,R.id.tvValor,R.id.barraProgresso};

            SimpleAdapter adapter = new SimpleAdapter(ViagemListActivity.this,maps,R.layout.lista_viagem,de,para);

            adapter.setViewBinder(ViagemListActivity.this);

            setListAdapter(adapter);
        }
    }

    private List<Map<String,Object>> listarViagem(){
        viagens = new ArrayList<Map<String,Object>>();

        List<Viagem> listarViagem = dao.listarViagem();

        for(Viagem viagem : listarViagem){
            Map<String, Object> item = new HashMap<String, Object>();

            item.put("id",viagem.getId());

            if(viagem.getTipoViagem()==Constantes.VIAGEM_LAZER){
                item.put("imagem",R.drawable.lazer);
            }else{
                item.put("imagem",R.drawable.negocios);
            }

            item.put("destino", viagem.getDestino());

            String periodo = dataFormat.format(viagem.getDataChegada()) + "a" + dataFormat.format(viagem.getDataSaida());

            item.put("data",periodo);

            double totalGasto = dao.calcularTotalGasto(viagem);

            item.put("total","Gasto total R$ "+totalGasto);

            double alerta = viagem.getOrcamento()*valorLimite/100;
            Double[] valores = {viagem.getOrcamento(),alerta,totalGasto};
            item.put("barraProgresso",valores);

            viagens.add(item);
        }
        return viagens;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(modoSelecionarViagem){
            String destino = (String) viagens.get(position).get("destino");
            String idViagem = (String) viagens.get(position).get("id");

            Intent data = new Intent();
            data.putExtra("viagem_id", idViagem);
            data.putExtra("Viagem_destino", destino);
            setResult(Activity.RESULT_OK, data);
            finish();
        }else{
            viagemSelecionada = position;
            alertDialog.show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int item) {
        Intent intent ;
        String id = (String) viagens.get(viagemSelecionada).get("id");

        switch (item){
            case 0:
                startActivity(new Intent(this,ViagemActivity.class));
                break;
            case 1:
                startActivity(new Intent(this,GastoActivity.class));
                break;
            case 2:
                startActivity(new Intent(this,ViagemListActivity.class));
                break;
            case 3:
                confirmacaoDialog.show();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                viagens.remove(viagemSelecionada);
                dao.removerViagem(Long.valueOf(id));
                getListView().invalidateViews();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                confirmacaoDialog.dismiss();
                break;
        }
    }



    private AlertDialog criaAlertDialog(){
        final CharSequence[] itens = {
                getString(R.string.editar),
                getString(R.string.novo_gasto),
                getString(R.string.gastos_realizados),
                getString(R.string.remover)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.opcoes);
        builder.setItems(itens, this);

        return builder.create();
    }

    private AlertDialog criaConfirmacaoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmacao_exclusao_viagem);
        builder.setPositiveButton(getString(R.string.sim), this);
        builder.setNegativeButton(getString(R.string.nao), this);

        return builder.create();
    }

}
