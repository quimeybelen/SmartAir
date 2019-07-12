package com.example.smartair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import static com.example.smartair.ConexionBluetoothService.enviarAArduino;

public class ModoVentilacionActivity extends AppCompatActivity {

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
    private static final int VENTILACION_REQUEST = 0;
    private static final int BT_CONNECTION = 1;
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
    private static final int LIGHT = 1;
    private static final int DO_ACTION_LIGHT = 0;
    private static final int DONT_DO_ACTION_LIGHT = 1;
    private static final int START_POS_STRING = 0;
    private static final int MIN_STRING_LENGTH = 7;
    private static final String OFF_CMD = "1";
    private static final String AUTOMATICO_CMD = "5";
    private static final String HIGH_FAN_SPEED_CMD = "8";
    private static final String MIN_FAN_SPEED_CMD = "9";
    private static final String TRAMA_REQUEST_CMD = "12";
    private static final String SMART_IS_ON = "2";
    private static final String SMART_IS_OFF = "1";
    private static final String SMART_CALOR = "1";
    private static final String SMART_INICIO = "4";
    private static final String SMART_AUTOMATICO = "2";
    private static final String SMART_SEGURO = "3";
    private static final String SMART_FRIO = "0";
    private static final String MAX_FAN_SPEED = "5";
    private static final String MIN_FAN_SPEED = "0";
    private static final String SEPARADOR = "|";
    private static final String STRING_CELSIUS = " °C";
    private static final String ACTUAL_MODE = "Ventilacion";
    private static final String GO_TO_OFF_MODE = "Apagado";
    private static final String GO_TO_ON_MODE = "Prendido";


    /***Variables***/
    Handler myHandlerVentilacion = new Handler();
    private final int tiempo = 3000;
    Handler myHandlerSensoresVentilacion = new Handler();
    private final int tiempoSensores = 1000;

    /***Componentes de la vista***/
    ImageView backModoVentilacion, backArrowVentilacion, subirFanVentilacionButton, bajarFanVentilacionButton, fanVentilacion, apagarButtonVentilacion, logoSmartAirVentilacion;
    TextView headerModoVentilacion, tempActualVentilacion, modoVentilacion, estadoVentilacionValor, estadoVentilacion, velocidadFanVentilacionValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_ventilacion);

        /***Enlaces a componentes***/
        backModoVentilacion = (ImageView) findViewById(R.id.backModoVentilacion);
        subirFanVentilacionButton = (ImageView) findViewById(R.id.subirFanVentilacionButton);
        bajarFanVentilacionButton = (ImageView) findViewById(R.id.bajarFanVentilacionButton);
        fanVentilacion = (ImageView) findViewById(R.id.fanVentilacion);
        apagarButtonVentilacion = (ImageView) findViewById(R.id.apagarButtonVentilacion);
        backArrowVentilacion = (ImageView) findViewById(R.id.backArrowVentilacion);
        logoSmartAirVentilacion = (ImageView) findViewById(R.id.logoSmartAirVentilacion);
        estadoVentilacion = (TextView) findViewById(R.id.estadoVentilacion);
        estadoVentilacionValor = (TextView) findViewById(R.id.estadoVentilacionValor);
        headerModoVentilacion = (TextView) findViewById(R.id.headerModoVentilacion);
        modoVentilacion = (TextView) findViewById(R.id.modoVentilacion);
        velocidadFanVentilacionValor = (TextView) findViewById(R.id.velocidadFanVentilacionValor);
        tempActualVentilacion = (TextView) findViewById(R.id.tempActualVentilacion);

        /***Actualizacion de datos de pantalla 'Ventilacion' (cada 3 seg)***/
        actualizarDatosVentilacion();

        /***Chequeo de sensores cada 1 seg***/
        chequeoSensoresVentilacion();

        /***Boton 'apagar/prender'***/
        apagarButtonVentilacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.estado == ON_MODE && enviarAArduino(OFF_CMD) == MSG_SENT) {
                    estadoVentilacionValor.setText(GO_TO_OFF_MODE);
                    MainActivity.estado = OFF_MODE;
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    startActivityForResult(intent, VENTILACION_REQUEST);
                } else {
                    //Mensaje de error ventilacion
                    Intent intent = new Intent(v.getContext(), ErrorConexionVentilacionActivity.class);
                    startActivityForResult(intent, VENTILACION_REQUEST);
                }

            }
        });

        /***Boton 'subir fan'***/
        subirFanVentilacionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String val = Integer.toString(Integer.parseInt((velocidadFanVentilacionValor.getText()).toString()) + 1);
                if (!val.equals(MAX_FAN_SPEED)) //La maxima velocidad es 4
                {
                    if(enviarAArduino(HIGH_FAN_SPEED_CMD) == MSG_SENT)
                        velocidadFanVentilacionValor.setText(val);
                    else
                    {
                        //Mensaje de error ventilacion
                        Intent intent = new Intent(v.getContext(), ErrorConexionVentilacionActivity.class);
                        startActivityForResult(intent, VENTILACION_REQUEST);
                    }

                }

            }
        });

        /***Boton 'bajar fan'***/
        bajarFanVentilacionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String val = Integer.toString(Integer.parseInt((velocidadFanVentilacionValor.getText()).toString()) - 1);
                if (!val.equals(MIN_FAN_SPEED)) //La minima velocidad es 1
                {
                    if(enviarAArduino(MIN_FAN_SPEED_CMD) == MSG_SENT)
                        velocidadFanVentilacionValor.setText(val);
                    else
                    {
                        //Mensaje de error ventilacion
                        Intent intent = new Intent(v.getContext(), ErrorConexionVentilacionActivity.class);
                        startActivityForResult(intent, VENTILACION_REQUEST);
                    }

                }

            }
        });



        /***Boton 'atras'***/
        backArrowVentilacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Ir a menu
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(intent, VENTILACION_REQUEST);
            }
        });

    }

    @Override
    public void onBackPressed (){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivityForResult(intent, VENTILACION_REQUEST);
    }

    /***Chequeo de sensores***/
    public void chequeoSensoresVentilacion()
    {

        myHandlerSensoresVentilacion.postDelayed(new Runnable() {
            public void run() {

                if(MainActivity.conectadoBT == BT_CONNECTION)
                {
                    //Sensor shake: Para apagar y prender SmartAir
                    if(SensoresService.shakeCheck == SHAKED)
                    {
                        if( MainActivity.estado == ON_MODE && enviarAArduino(OFF_CMD) == MSG_SENT)
                        {
                            estadoVentilacionValor.setText(GO_TO_OFF_MODE);
                            MainActivity.estado = OFF_MODE;
                            SensoresService.shakeCheck = NO_SHAKED;
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivityForResult(intent, VENTILACION_REQUEST);

                        }
                        else
                        {
                            SensoresService.shakeCheck = NO_SHAKED;
                            Intent intent = new Intent(getApplicationContext(), ErrorConexionVentilacionActivity.class);
                            startActivityForResult(intent, VENTILACION_REQUEST);
                        }

                    }

                    //Sensor de proximidad: Para bloqueo de la pantalla
                    if( SensoresService.proxCheck == PROXIMITY && SensoresService.accionProx == DO_ACTION_PROXIMITY )
                    {
                        subirFanVentilacionButton.setEnabled(false);
                        bajarFanVentilacionButton.setEnabled(false);
                        backArrowVentilacion.setEnabled(false);
                        apagarButtonVentilacion.setEnabled(false);
                        SensoresService.accionProx = DONT_DO_ACTION_PROXIMITY;
                    }
                    else
                    {
                        if( SensoresService.proxCheck == NO_PROXIMITY && SensoresService.accionProx == DONT_DO_ACTION_PROXIMITY )
                        {
                            subirFanVentilacionButton.setEnabled(true);
                            bajarFanVentilacionButton.setEnabled(true);
                            backArrowVentilacion.setEnabled(true);
                            apagarButtonVentilacion.setEnabled(true);
                            SensoresService.accionProx = DO_ACTION_PROXIMITY;
                        }
                        else
                        {
                            //Sensor de luz: Para poner automaticamente a modo automatico de noche
                            if( SensoresService.luzCheck == LIGHT && SensoresService.accionLuz == DO_ACTION_LIGHT && SensoresService.proxCheck == NO_PROXIMITY )
                            {
                                if( enviarAArduino(AUTOMATICO_CMD) == MSG_SENT)
                                {
                                    SensoresService.accionLuz = DONT_DO_ACTION_LIGHT;
                                    Intent intent = new Intent (getApplicationContext(), ModoAutomaticoActivity.class);
                                    startActivityForResult(intent, VENTILACION_REQUEST);
                                }
                                else
                                {
                                    Intent intent = new Intent(getApplicationContext(), ErrorConexionVentilacionActivity.class);
                                    startActivityForResult(intent, VENTILACION_REQUEST);
                                }

                            }
                        }
                    }

                }

                myHandlerSensoresVentilacion.postDelayed(this, tiempoSensores);
            }

        }, tiempoSensores);


    }

    public void refrescarVistaVentilacion()
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
            estadoVentilacionValor.setText(GO_TO_ON_MODE);
            MainActivity.estado = ON_MODE;
        }
        else
        {
	    if(aux.equals(SMART_IS_OFF))
	    {
               estadoVentilacionValor.setText(GO_TO_OFF_MODE);
	       MainActivity.estado = OFF_MODE;
               Intent intent = new Intent(getApplicationContext(), MainActivity.class);
               startActivityForResult(intent, VENTILACION_REQUEST);
	    }
        }

        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        switch(aux)
        {
            case SMART_CALOR:
            {
                Intent intent = new Intent(getApplicationContext(), ModoCalorActivity.class);
                startActivityForResult(intent, VENTILACION_REQUEST);
                break;
            }
            case SMART_AUTOMATICO:
            {
                Intent intent = new Intent(getApplicationContext(), ModoAutomaticoActivity.class);
                startActivityForResult(intent, VENTILACION_REQUEST);
                break;
            }
            case SMART_SEGURO:
            {
                MainActivity.modoPrevio = ACTUAL_MODE;
                Intent intent = new Intent(getApplicationContext(), ModoSeguroActivity.class);
                startActivityForResult(intent, VENTILACION_REQUEST);
                break;
            }
            case SMART_INICIO:
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, VENTILACION_REQUEST);
                break;
            }
            case SMART_FRIO:
            {
                Intent intent = new Intent(getApplicationContext(), ModoFrioActivity.class);
                startActivityForResult(intent, VENTILACION_REQUEST);
                break;
            }
        }

        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        tempActualVentilacion.setText(aux + STRING_CELSIUS);

        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        velocidadFanVentilacionValor.setText(aux);


    }

    public void actualizarDatosVentilacion()
    {
        myHandlerVentilacion.postDelayed(new Runnable() {
            public void run() {

                if(MainActivity.conectadoBT == BT_CONNECTED && enviarAArduino(TRAMA_REQUEST_CMD) == MSG_SENT)
                {
                    if((ConexionBluetoothService.tramaLine).length() > MIN_STRING_LENGTH)
                    {
                        /***Actualizo la vista (en caso de errores)***/
                        refrescarVistaVentilacion();
                    }

                }

                myHandlerVentilacion.postDelayed(this, tiempo);
            }

        }, tiempo);

    }


}
