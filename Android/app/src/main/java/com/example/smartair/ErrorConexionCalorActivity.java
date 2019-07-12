package com.example.smartair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ErrorConexionCalorActivity extends AppCompatActivity {

    /***Constantes***/
    private static final int ERROR_CONNECTION_REQUEST = 0;

    /***Variables***/
    ImageView errorCalorDialog, cruzCalor;
    TextView mensajeErrorCalor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_conexion_calor);

	/***Enlace de componentes***/
        cruzCalor = (ImageView) findViewById(R.id.cruzCalor);
        errorCalorDialog = (ImageView) findViewById(R.id.errorCalorDialog);
        mensajeErrorCalor = (TextView) findViewById(R.id.mensajeErrorCalor);

	/***Acción 'cruz'***/
        cruzCalor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ModoCalorActivity.class);
                startActivityForResult(intent, ERROR_CONNECTION_REQUEST);
            }
        });
    }

    /***onBackPressed action (Botón 'atrás' del celular)***/
    @Override
    public void onBackPressed (){
    }

}
