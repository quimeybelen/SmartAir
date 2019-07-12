package com.example.smartair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ErrorConexionFrioActivity extends AppCompatActivity {

    /***Constantes***/
    private static final int ERROR_CONNECTION_REQUEST = 0;

    /***Variables***/
    ImageView errorFrioDialog, cruzFrio;
    TextView mensajeErrorFrio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_conexion_frio);

	/***Enlaces de componentes***/
        cruzFrio = (ImageView) findViewById(R.id.cruzFrio);
        errorFrioDialog = (ImageView) findViewById(R.id.errorFrioDialog);
        mensajeErrorFrio = (TextView) findViewById(R.id.mensajeErrorFrio);

	/***Acción botón 'cruz'***/
        cruzFrio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ModoFrioActivity.class);
                startActivityForResult(intent, ERROR_CONNECTION_REQUEST);
            }
        });


    }

    /***onBackPressed action (Botón 'atrás' del celular)***/
    @Override
    public void onBackPressed (){
    }

}
