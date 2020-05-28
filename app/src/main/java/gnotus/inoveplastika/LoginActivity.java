package gnotus.inoveplastika;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    public static final String MYFILE = "configs";
    public static final String CAMINHO = "caminho";
    public static final String LASTUSER = "lastuser";
    private static final String USERID = "userid";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button button_login, button_sair;

        sharedpreferences = getSharedPreferences(MYFILE, Context.MODE_PRIVATE);

        button_login = (Button) findViewById(R.id.button_login_entrar);
        button_sair = (Button) findViewById(R.id.button_login_sair);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USERID, "");
        editor.apply();

        if (sharedpreferences.contains(CAMINHO)) {

            Globals.getInstance().setCaminho(sharedpreferences.getString(CAMINHO, ""));
        }

        if (sharedpreferences.contains(USERID)) {

            Globals.getInstance().setUserid("");
        }


        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        button_sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


    }
}
