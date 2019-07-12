package com.example.smartair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import static com.example.smartair.ConexionBluetoothService.enviarAArduino;

public class ModoSeguroActivity extends AppCompatActivity {

    /***Constantes***/
    private static final int BT_CONNECTED = 1;
    private static final int SEGURO_REQUEST = 0;
    private static final int MSG_SENT = 1;
    private static final int MIN_STRING_LENGTH = 7;
    private static final int START_POS_STRING = 0;
    private static final String TRAMA_REQUEST_CMD = "12";
    private static final String SEPARADOR = "|";
    private static final String INICIO_MODE = "Main";
    private static final String FRIO_MODE = "Frio";
    private static final String CALOR_MODE = "Calor";
    private static final String AUTOMATICO_MODE = "Automatico";
    private static final String VENTILACION_MODE = "Ventilacion";
    private static final String SMART_SEGURO = "3";

    /***Variables***/
    Handler myHandlerSeguro = new Handler();
    private final int tiempo = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_seguro);

        //Verifica si sigue en modo seguro cada 2 seg
        verificarSiSeguro();
    }

    @Override
    public void onBackPressed (){
    }


    public void verificarSiSeguro()
    {
        myHandlerSeguro.postDelayed(new Runnable() {
            public void run() {

                if(MainActivity.conectadoBT == BT_CONNECTED && enviarAArduino(TRAMA_REQUEST_CMD) == MSG_SENT)
                {
                    if ((ConexionBluetoothService.tramaLine).length() > MIN_STRING_LENGTH) {
                        /***Formato de trama***/
                        // info|modo_encendido|modo|temp_elegida|temp_externa|vel_fan|inclinacion

                        /***Agarro la trama y analizo***/
                        String trama, aux;
                        trama = ConexionBluetoothService.tramaLine;
                        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
                        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
                        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

                        if(!trama.equals(SMART_SEGURO))
                        {
                            switch(MainActivity.modoPrevio)
                            {
                                case FRIO_MODE:
                                {
                                    Intent intent = new Intent(getApplicationContext(), ModoFrioActivity.class);
                                    startActivityForResult(intent, SEGURO_REQUEST);
                                    break;
                                }
                                case CALOR_MODE:
                                {
                                    Intent intent = new Intent(getApplicationContext(), ModoCalorActivity.class);
                                    startActivityForResult(intent, SEGURO_REQUEST);
                                    break;
                                }
                                case AUTOMATICO_MODE:
                                {
                                    Intent intent = new Intent(getApplicationContext(), ModoAutomaticoActivity.class);
                                    startActivityForResult(intent, SEGURO_REQUEST);
                                    break;
                                }
                                case INICIO_MODE:
                                {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivityForResult(intent, SEGURO_REQUEST);
                                    break;
                                }
                                case VENTILACION_MODE:
                                {
                                    Intent intent = new Intent(getApplicationContext(), ModoVentilacionActivity.class);
                                    startActivityForResult(intent, SEGURO_REQUEST);
                                    break;
                                }
                            }
                        }
                    }
                }



                myHandlerSeguro.postDelayed(this, tiempo);
            }

        }, tiempo);

    }

}
