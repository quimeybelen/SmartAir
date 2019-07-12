package com.example.smartair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ErrorConexionVentilacionActivity extends AppCompatActivity {

    /***Constantes***/
    private static final int ERROR_CONNECTION_REQUEST = 0;

    /***Variables***/
    ImageView errorVentilacionDialog, cruzVentilacion;
    TextView mensajeErrorVentilacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_conexion_ventilacion);

	/***Enlaces de componentes***/
        cruzVentilacion = (ImageView) findViewById(R.id.cruzVentilacion);
        errorVentilacionDialog = (ImageView) findViewById(R.id.errorVentilacionDialog);
        mensajeErrorVentilacion = (TextView) findViewById(R.id.mensajeErrorVentilacion);

	/***Acción botón 'cruz'***/
        cruzVentilacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ModoVentilacionActivity.class);
                startActivityForResult(intent, ERROR_CONNECTION_REQUEST);
            }
        });
    }

    /***onBackPressed action (Botón 'atrás' del celular)***/
    @Override
    public void onBackPressed (){
    }

}
