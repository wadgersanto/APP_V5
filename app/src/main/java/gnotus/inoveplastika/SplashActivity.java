package gnotus.inoveplastika;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;

public class SplashActivity extends AppCompatActivity implements AsyncRequest.OnAsyncRequestComplete {

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        File f = new File(
                "/data/data/gnotus.inoveplastika/shared_prefs/configs.xml");

        // se o ficheiro de configurações não existir vamos abrir a atividade de configurações

        if (! f.exists()) {
            openConfigsActitivy();
            return;
        }

        sharedpreferences = getSharedPreferences("configs", Context.MODE_PRIVATE);

        if (sharedpreferences.contains("caminho")) {
            Globals.getInstance().setCaminho(sharedpreferences.getString("caminho", ""));
        }
        else {
            openConfigsActitivy();
            return;
        }

        atribuiFabricaBarcelos();

        // deleteCache(this);

        // colocar esta opção para automaticamente atribuir a fábrica de barcelos caso o parametro não exista ou esteja vazio

        if (!sharedpreferences.contains("fabrica") || sharedpreferences.getString("fabrica","").isEmpty()) {
            openConfigsActitivy();
            return;
        }
        else
        {
            Globals.getInstance().setSelectedFactory(sharedpreferences.getString("fabrica",""));
        }

        // abrir a mainActivity_lista
        Intent intent;
        intent = new Intent(SplashActivity.this, MainActivity_Lista.class);
        startActivity(intent);
        finish();

    }



    private void atribuiFabricaBarcelos() {

        // se o parametro fábrica não existe ou se existe mas está vazio então vamos atribuir por defeito Barcelos

        if (! sharedpreferences.contains("fabrica") || sharedpreferences.getString("fabrica","").isEmpty()) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Globals.SHAREDPREFS_PARAM_NAME_FABRICA,Globals.FACTORY_BARCELOS);
            editor.apply();
            Globals.getInstance().setSelectedFactory(sharedpreferences.getString("fabrica",""));
        }

    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void asyncResponse(String response, int op) {

    }

    private void openConfigsActitivy() {

        Intent intent;
        intent = new Intent(SplashActivity.this, ConfigsActivity.class);
        startActivity(intent);
        finish();

    }

}
