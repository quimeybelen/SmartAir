package com.example.smartair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import static com.example.smartair.ConexionBluetoothService.enviarAArduino;

public class ModoAutomaticoActivity extends AppCompatActivity {

    /***Comandos de arduino a embebido***/
    // 0 - Prendido
    // 1 - Apagado
    // 2 - Frio
    // 3 - Calor
    // 4 - Ventilacion
    // 5 - Automatico
    // 6 -
    // 7 -
    // 8 - Subir fan
    // 9 - Bajar fan
    // 10 - Bajar temperatura
    // 11 - Subir temperatura

    /***Constantes***/
    private static final int AUTOMATICO_REQUEST = 0;
    private static final int ON_MODE = 0;
    private static final int OFF_MODE = 1;
    private static final int MSG_SENT = 1;
    private static final int BT_CONNECTED = 1;
    private static final int SHAKED = 1;
    private static final int NO_SHAKED = 1;
    private static final int NO_PROXIMITY = 1;
    private static final int PROXIMITY = 1;
    private static final int DO_ACTION_PROXIMITY = 0;
    private static final int DONT_DO_ACTION_PROXIMITY = 1;
    private static final int START_POS_STRING = 0;
    private static final int MIN_STRING_LENGTH = 7;
    private static final String OFF_CMD = "1";
    private static final String TRAMA_REQUEST_CMD = "12";
    private static final String SMART_IS_ON = "2";
    private static final String SMART_FRIO = "0";
    private static final String SMART_CALOR = "1";
    private static final String SMART_INICIO = "4";
    private static final String SMART_SEGURO = "3";
    private static final String SMART_VENTILACION = "5";
    private static final String SEPARADOR = "|";
    private static final String ACTUAL_MODE = "Automatico";
    private static final String GO_TO_OFF_MODE = "Apagado";
    private static final String GO_TO_ON_MODE = "Prendido";
    private static final String STRING_CELSIUS = " °C";

    /***Variables***/
    Handler myHandlerAutomatico = new Handler();
    private final int tiempo = 3000;
    Handler myHandlerSensoresAutomatico = new Handler();
    private final int tiempoSensores = 1000;

    /***Componentes de la vista***/
    ImageView backModoAutomatico, backArrowAutomatico, thermoAutomatico, apagarButtonAutomatico, logoSmartAirAutomatico;
    TextView headerModoAutomatico, tempActualAutomatico, modoAutomatico, estadoAutomaticoValor, estadoAutomatico, tempAutomaticoValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_automatico);

        /***Enlaces a componentes***/
        backModoAutomatico = (ImageView) findViewById(R.id.backModoAutomatico);
        thermoAutomatico = (ImageView) findViewById(R.id.thermoAutomatico);
        apagarButtonAutomatico = (ImageView) findViewById(R.id.apagarButtonAutomatico);
        logoSmartAirAutomatico = (ImageView) findViewById(R.id.logoSmartAirAutomatico);
        backArrowAutomatico = (ImageView) findViewById(R.id.backArrowAutomatico);
        estadoAutomatico = (TextView) findViewById(R.id.estadoAutomatico);
        estadoAutomaticoValor = (TextView) findViewById(R.id.estadoAutomaticoValor);
        headerModoAutomatico = (TextView) findViewById(R.id.headerModoAutomatico);
        modoAutomatico = (TextView) findViewById(R.id.modoAutomatico);
        tempAutomaticoValor = (TextView) findViewById(R.id.tempAutomaticoValor);
        tempActualAutomatico = (TextView) findViewById(R.id.tempActualAutomatico);


        /***Actualizacion de datos de pantalla 'Automatico' (cada 3 seg)***/
        actualizarDatosAutomatico();

        /***Chequeo de sensores cada 1 seg***/
        chequeoSensoresAutomatico();

        /***Boton 'apagar/prender'***/
        apagarButtonAutomatico.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.estado == ON_MODE && enviarAArduino(OFF_CMD) == MSG_SENT)
                {
                    estadoAutomaticoValor.setText(GO_TO_OFF_MODE);
                    MainActivity.estado = OFF_MODE;
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    startActivityForResult(intent, AUTOMATICO_REQUEST);
                }
                else
                {
                    //Mensaje de error automatico
                    Intent intent = new Intent(v.getContext(), ErrorConexionAutomaticoActivity.class);
                    startActivityForResult(intent, AUTOMATICO_REQUEST);
                }

            }
        });

        /***Boton 'atras'***/
        backArrowAutomatico.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Ir a menu
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(intent, AUTOMATICO_REQUEST);
            }
        });

    }

    /***Chequeo de sensores***/
    public void chequeoSensoresAutomatico()
    {

        myHandlerSensoresAutomatico.postDelayed(new Runnable() {
            public void run() {

                if(MainActivity.conectadoBT == BT_CONNECTED)
                {
                    //Sensor shake: Para apagar y prender SmartAir
                    if(SensoresService.shakeCheck == SHAKED)
                    {
                        if( MainActivity.estado == ON_MODE && enviarAArduino(OFF_CMD) == MSG_SENT)
                        {
                            MainActivity.estado = OFF_MODE;
                            SensoresService.shakeCheck = NO_SHAKED;
                            estadoAutomaticoValor.setText(GO_TO_OFF_MODE);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivityForResult(intent, AUTOMATICO_REQUEST);
                        }
                        else
                        {
                            SensoresService.shakeCheck = NO_SHAKED;
                            Intent intent = new Intent(getApplicationContext(), ErrorConexionAutomaticoActivity.class);
                            startActivityForResult(intent, AUTOMATICO_REQUEST);
                        }
                    }

                    //Sensor de proximidad: Para bloqueo de la pantalla
                    if( SensoresService.proxCheck == PROXIMITY && SensoresService.accionProx == DO_ACTION_PROXIMITY )
                    {
                        backArrowAutomatico.setEnabled(false);
                        apagarButtonAutomatico.setEnabled(false);
                        SensoresService.accionProx = DONT_DO_ACTION_PROXIMITY;
                    }
                    else
                    {
                        if( SensoresService.proxCheck == NO_PROXIMITY && SensoresService.accionProx == DONT_DO_ACTION_PROXIMITY )
                        {
                            backArrowAutomatico.setEnabled(true);
                            apagarButtonAutomatico.setEnabled(true);
                            SensoresService.accionProx = DO_ACTION_PROXIMITY;
                        }
                    }

                }


                myHandlerSensoresAutomatico.postDelayed(this, tiempoSensores);
            }

        }, tiempoSensores);

    }

    public void refrescarVistaAutomatico()
    {
        /***Formato de trama***/
        // info|modo_encendido|modo|temp_elegida|temp_externa|vel_fan|inclinacion

        /***Agarro la trama y analizo***/
        String trama, aux;
        trama = ConexionBluetoothService.tramaLine;
        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        //El 2 en el arduino quiere decir que SmartAir esta prendido
        if(aux.equals(SMART_IS_ON))
        {
            estadoAutomaticoValor.setText(GO_TO_ON_MODE);
            MainActivity.estado = ON_MODE;
        }
        else
        {
            estadoAutomaticoValor.setText(GO_TO_OFF_MODE);
	        MainActivity.estado = OFF_MODE;
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(intent, AUTOMATICO_REQUEST);
        }

        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        switch(aux)
        {
            case SMART_FRIO:
            {
                Intent intent = new Intent(getApplicationContext(), ModoFrioActivity.class);
                startActivityForResult(intent, AUTOMATICO_REQUEST);
                break;
            }
            case SMART_CALOR:
            {
                Intent intent = new Intent(getApplicationContext(), ModoCalorActivity.class);
                startActivityForResult(intent, AUTOMATICO_REQUEST);
                break;
            }
            case SMART_SEGURO:
            {
                MainActivity.modoPrevio = ACTUAL_MODE;
                Intent intent = new Intent(getApplicationContext(), ModoSeguroActivity.class);
                startActivityForResult(intent, AUTOMATICO_REQUEST);
                break;
            }
            case SMART_INICIO:
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, AUTOMATICO_REQUEST);
                break;
            }
            case SMART_VENTILACION:
            {
                Intent intent = new Intent(getApplicationContext(), ModoVentilacionActivity.class);
                startActivityForResult(intent, AUTOMATICO_REQUEST);
                break;
            }
        }

        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        tempActualAutomatico.setText(aux+STRING_CELSIUS);


    }

    public void actualizarDatosAutomatico()
    {
        myHandlerAutomatico.postDelayed(new Runnable() {
            public void run() {

                if(MainActivity.conectadoBT == BT_CONNECTED && enviarAArduino(TRAMA_REQUEST_CMD) == MSG_SENT)
                {
                    if((ConexionBluetoothService.tramaLine).length() > MIN_STRING_LENGTH)
                    {
                        /***Actualizo la vista (en caso de errores)***/
                        refrescarVistaAutomatico();
                    }

                }


                myHandlerAutomatico.postDelayed(this, tiempo);
            }

        }, tiempo);

    }





}
