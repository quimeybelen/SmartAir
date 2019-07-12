package com.example.smartair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ErrorConexionMainActivity extends AppCompatActivity {

    /***Constantes***/
    private static final int ERROR_CONNECTION_REQUEST = 0;

    /***Variables***/
    ImageView errorMainDialog, cruzMain;
    TextView mensajeErrorMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_conexion_main);

	/***Enlaces de componentes***/
        cruzMain = (ImageView) findViewById(R.id.cruzMain);
        errorMainDialog = (ImageView) findViewById(R.id.errorFrioDialog);
        mensajeErrorMain = (TextView) findViewById(R.id.mensajeErrormain);

	/***Acción botón 'cruz'***/
        cruzMain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(intent, ERROR_CONNECTION_REQUEST);
            }
        });


    }

    /***onBackPressed action (Botón 'atrás' del celular)***/
    @Override
    public void onBackPressed (){
    }

}
