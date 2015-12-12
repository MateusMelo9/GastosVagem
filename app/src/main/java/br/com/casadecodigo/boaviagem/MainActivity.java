package br.com.casadecodigo.boaviagem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String MANTER_CONECTADO = "manter_conectado";
    private EditText etUsuario;
    private EditText etSenha;
    private CheckBox manterConectado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        etUsuario = (EditText) findViewById(R.id.usuario);
        etSenha = (EditText) findViewById(R.id.senha);
        manterConectado = (CheckBox) findViewById(R.id.cbManterConectado);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean conectedo = preferences.getBoolean(MANTER_CONECTADO, false);

        if(conectedo){
            startActivity(new Intent(this,DashboardActivity.class));
        }

    }

    public void entrarOnClick(View v){
        String entraUsuario = etUsuario.getText().toString();
        String entraSenha = etSenha.getText().toString();

            if("mateus".equals(entraUsuario)&&"123".equals(entraSenha)){

                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(MANTER_CONECTADO,manterConectado.isChecked());
                editor.commit();

                startActivity(new Intent(this,DashboardActivity.class));
        }else{
            String msgErro = getString(R.string.erro_autenticacao);
            Toast toast = Toast.makeText(this,msgErro,Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

}
