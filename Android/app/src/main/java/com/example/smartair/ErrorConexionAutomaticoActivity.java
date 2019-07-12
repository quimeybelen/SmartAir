package com.example.smartair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ErrorConexionAutomaticoActivity extends AppCompatActivity {

    /***Constantes***/
    private static final int ERROR_CONNECTION_REQUEST = 0;

    /***Variables***/
    ImageView errorAutomaticoDialog, cruzAutomatico;
    TextView mensajeErrorAutomatico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_conexion_automatico);

	/***Enlace de componentes***/
        cruzAutomatico = (ImageView) findViewById(R.id.cruzAutomatico);
        errorAutomaticoDialog = (ImageView) findViewById(R.id.errorAutomaticoDialog);
        mensajeErrorAutomatico = (TextView) findViewById(R.id.mensajeErrorAutomatico);

        /***Accion 'cruz'***/
        cruzAutomatico.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ModoAutomaticoActivity.class);
                startActivityForResult(intent, ERROR_CONNECTION_REQUEST);
            }
        });
    }

    /***onBackPressed action (Botón 'atrás' del celular)***/
    @Override
    public void onBackPressed (){
    }

}
