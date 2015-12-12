package br.com.casadecodigo.boaviagem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 16/10/2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String BANCO_DADOS = "BoaViagem";
    private static int VERSAO = 4;

    public DataBaseHelper(Context context) {
        super(context, BANCO_DADOS, null, VERSAO);
    }


    public static final class Viagem {
        public static final String TABELA = "viagem";
        public static final String _ID = "_id";
        public static final String DESTINO = "destino";
        public static final String DATA_CHEGADA = "data_chegada";
        public static final String DATA_SAIDA = "data_saida";
        public static final String ORCAMENTO = "orcamento";
        public static final String QUANTIDADE_PESSOAS = "qtd_pessoas";
        public static final String TIPO_VIAGEM = "tipo_viagem";

        public static final String[] COLUNAS = new String[]{
                _ID, DESTINO, DATA_CHEGADA, DATA_SAIDA,
                TIPO_VIAGEM, ORCAMENTO, QUANTIDADE_PESSOAS};
    }

    public static final class Gasto {
        public static final String TABELA = "gasto";
        public static final String _ID = "_id";
        public static final String VIAGEM_ID = "viagem_id";
        public static final String CATEGORIA = "categoria";
        public static final String DATA = "data";
        public static final String DESCRICAO = "descricao";
        public static final String VALOR = "valor";
        public static final String LOCAL = "local";
        ;

        public static final String[] COLUNAS = new String[]{
                _ID, VIAGEM_ID, CATEGORIA, DATA, DESCRICAO,
                VALOR, LOCAL
        };
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE viagem(_id INTERGER PRIMARY KEY, " +
                " destino TEXT, tipo_viagem INTERGER, data_chegada DATE, " +
                " data_saida DATE, orcamento DOUBLE, qtd_pessoas INTERGER);");

        db.execSQL("CREATE TABLE gasto(_id INTERGER PRIMARY KEY, " +
                " categoria TEXT, valor DOUBLE, data DATE, " +
                " descricao TEXT, local TEXT, viagem_id INTERGER, " +
                " FOREIGN KEY(viagem_id) REFERENCES viagem(_id));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table viagem");
        db.execSQL("drop table gasto");
        onCreate(db);
    }
}
