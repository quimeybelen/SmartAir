package com.example.smartair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import static com.example.smartair.ConexionBluetoothService.enviarAArduino;

public class ModoCalorActivity extends AppCompatActivity {

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
    private static final int CALOR_REQUEST = 0;
    private static final int ON_MODE = 0;
    private static final int OFF_MODE = 1;
    private static final int MSG_SENT = 1;
    private static final int BT_CONNECTED = 1;
    private static final int MIN_EXTERNAL_TEMP = 26;
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
    private static final int END_POS_STRING_TEMP = 2;
    private static final int MIN_STRING_LENGTH = 7;
    private static final int MIN_TEMP_VAL = 17;
    private static final int MAX_TEMP_VAL = 27;
    private static final String OFF_CMD = "1";
    private static final String AUTOMATICO_CMD = "5";
    private static final String HIGH_FAN_SPEED_CMD = "8";
    private static final String LOW_FAN_SPEED_CMD = "9";
    private static final String HIGH_TEMP_CMD = "11";
    private static final String LOW_TEMP_CMD = "10";
    private static final String TRAMA_REQUEST_CMD = "12";
    private static final String SMART_IS_ON = "2";
    private static final String SMART_FRIO = "0";
    private static final String SMART_INICIO = "4";
    private static final String SMART_AUTOMATICO = "2";
    private static final String SMART_SEGURO = "3";
    private static final String SMART_VENTILACION = "5";
    private static final String MAX_FAN_SPEED = "5";
    private static final String MIN_FAN_SPEED = "0";
    private static final String SEPARADOR = "|";
    private static final String STRING_CELSIUS = " °C";
    private static final String MAX_TEMP_STRING = "26 °C";
    private static final String ACTUAL_MODE = "Calor";
    private static final String GO_TO_OFF_MODE = "Apagado";
    private static final String GO_TO_ON_MODE = "Prendido";

    /***Variables***/
    Handler myHandlerCalor = new Handler();
    private final int tiempo = 3000;
    Handler myHandlerSensoresCalor = new Handler();
    private final int tiempoSensores = 1000;

    /***Componentes de la vista***/
    ImageView backModoCalor, backArrowCalor, subirTempCalorButton, bajarTempCalorButton, subirFanCalorButton, bajarFanCalorButton, thermoCalor, fanCalor, apagarButtonCalor, logoSmartAirCalor;
    TextView headerModoCalor, tempActualCalor, modoCalor, estadoCalorValor, estadoCalor, velocidadFanCalorValor, tempCalorValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_calor);

        /***Enlaces a componentes***/
        backModoCalor = (ImageView) findViewById(R.id.backModoCalor);
        subirTempCalorButton = (ImageView) findViewById(R.id.subirTempCalorButton);
        bajarTempCalorButton = (ImageView) findViewById(R.id.bajarTempCalorButton);
        subirFanCalorButton = (ImageView) findViewById(R.id.subirFanCalorButton);
        bajarFanCalorButton = (ImageView) findViewById(R.id.bajarFanCalorButton);
        thermoCalor = (ImageView) findViewById(R.id.thermoCalor);
        fanCalor = (ImageView) findViewById(R.id.fanCalor);
        backArrowCalor = (ImageView) findViewById(R.id.backArrowCalor);
        apagarButtonCalor = (ImageView) findViewById(R.id.apagarButtonCalor);
        logoSmartAirCalor = (ImageView) findViewById(R.id.logoSmartAirCalor);
        estadoCalor = (TextView) findViewById(R.id.estadoCalor);
        estadoCalorValor = (TextView) findViewById(R.id.estadoCalorValor);
        headerModoCalor = (TextView) findViewById(R.id.headerModoCalor);
        modoCalor = (TextView) findViewById(R.id.modoCalor);
        velocidadFanCalorValor = (TextView) findViewById(R.id.velocidadFanCalorValor);
        tempCalorValor = (TextView) findViewById(R.id.tempCalorValor);
        tempActualCalor = (TextView) findViewById(R.id.tempActualCalor);

        /***Actualizacion de datos de pantalla 'Calor' (cada 3 seg)***/
        actualizarDatosCalor();

        /***Chequeo de sensores cada 1 seg***/
        chequeoSensoresCalor();

        /***Boton 'apagar/prender'***/
        apagarButtonCalor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.estado == ON_MODE && enviarAArduino(OFF_CMD) == MSG_SENT) {
                    estadoCalorValor.setText(GO_TO_OFF_MODE);
                    MainActivity.estado = OFF_MODE;
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    startActivityForResult(intent, CALOR_REQUEST);
                } else {
                    //Mensaje de error calor
                    Intent intent = new Intent(v.getContext(), ErrorConexionCalorActivity.class);
                    startActivityForResult(intent, CALOR_REQUEST);
                }

            }
        });

        /***Boton 'subir fan'***/
        subirFanCalorButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String val = Integer.toString(Integer.parseInt((velocidadFanCalorValor.getText()).toString()) + 1);
                if (!val.equals(MAX_FAN_SPEED) ) //La maxima velocidad es 4
                {
                    if(enviarAArduino(HIGH_FAN_SPEED_CMD) == MSG_SENT)
                    {
                        velocidadFanCalorValor.setText(val);
                    }
                    else
                    {
                        //Mensaje de error calor
                        Intent intent = new Intent(v.getContext(), ErrorConexionCalorActivity.class);
                        startActivityForResult(intent, CALOR_REQUEST);
                    }

                }

            }
        });

        /***Boton 'bajar fan'***/
        bajarFanCalorButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String val = Integer.toString(Integer.parseInt((velocidadFanCalorValor.getText()).toString()) - 1);
                if (!val.equals(MIN_FAN_SPEED)) //La minima velocidad es 1
                {
                    if(enviarAArduino(LOW_FAN_SPEED_CMD) == MSG_SENT)
                    {
                        velocidadFanCalorValor.setText(val);
                    }
                    else
                    {
                        //Mensaje de error calor
                        Intent intent = new Intent(v.getContext(), ErrorConexionCalorActivity.class);
                        startActivityForResult(intent, CALOR_REQUEST);
                    }
                }


            }
        });


        /***Boton 'bajar temperatura'***/
        bajarTempCalorButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int val = Integer.parseInt(((tempCalorValor.getText()).toString()).substring(START_POS_STRING, END_POS_STRING_TEMP)) - 1;
                int ext = Integer.parseInt(((tempActualCalor.getText()).toString()).substring(START_POS_STRING,((tempActualCalor.getText()).toString()).indexOf(STRING_CELSIUS)));
                if ( val > MIN_TEMP_VAL && val > ext)
                {
                    if(enviarAArduino(LOW_TEMP_CMD) == MSG_SENT)
                    {
                        tempCalorValor.setText(val + STRING_CELSIUS);
                    }
                    else
                    {
                        //Mensaje de error calor
                        Intent intent = new Intent(v.getContext(), ErrorConexionCalorActivity.class);
                        startActivityForResult(intent, CALOR_REQUEST);
                    }
                }

            }
        });

        /***Boton 'subir temperatura'***/
        subirTempCalorButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int val = Integer.parseInt(((tempCalorValor.getText()).toString()).substring(START_POS_STRING, END_POS_STRING_TEMP)) + 1;
                int ext = Integer.parseInt(((tempActualCalor.getText()).toString()).substring(START_POS_STRING,((tempActualCalor.getText()).toString()).indexOf(STRING_CELSIUS)));
                if ( val < MAX_TEMP_VAL && val > ext)
                {
                    if(enviarAArduino(HIGH_TEMP_CMD) == MSG_SENT)
                    {
                        tempCalorValor.setText(val + STRING_CELSIUS);
                    }
                    else
                    {
                        //Mensaje de error calor
                        Intent intent = new Intent(v.getContext(), ErrorConexionCalorActivity.class);
                        startActivityForResult(intent, CALOR_REQUEST);
                    }
                }

            }
        });

        /***Boton 'atras'***/
        backArrowCalor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Ir a menu
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(intent, CALOR_REQUEST);
            }
        });

    }

    @Override
    public void onBackPressed (){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivityForResult(intent, CALOR_REQUEST);
    }

    /***Chequeo de sensores***/
    public void chequeoSensoresCalor()
    {
        myHandlerSensoresCalor.postDelayed(new Runnable() {
            public void run() {

                if(MainActivity.conectadoBT == BT_CONNECTED)
                {
		    //Sensor shake: Para apagar y prender SmartAir
                    if(SensoresService.shakeCheck == SHAKED)
                    {
                        if( MainActivity.estado == ON_MODE && enviarAArduino(OFF_CMD) == MSG_SENT)
                        {
                            estadoCalorValor.setText(GO_TO_OFF_MODE);
                            MainActivity.estado = OFF_MODE;
                            SensoresService.shakeCheck = NO_SHAKED;
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivityForResult(intent, CALOR_REQUEST);

                        }
                        else
                        {
                            SensoresService.shakeCheck = NO_SHAKED;
                            Intent intent = new Intent(getApplicationContext(), ErrorConexionCalorActivity.class);
                            startActivityForResult(intent, CALOR_REQUEST);
                        }

                    }

                    //Sensor de proximidad: Para bloqueo de la pantalla
                    if( SensoresService.proxCheck == PROXIMITY && SensoresService.accionProx == DO_ACTION_PROXIMITY )
                    {
                        subirFanCalorButton.setEnabled(false);
                        bajarFanCalorButton.setEnabled(false);
                        subirTempCalorButton.setEnabled(false);
                        bajarTempCalorButton.setEnabled(false);
                        apagarButtonCalor.setEnabled(false);
                        backArrowCalor.setEnabled(false);
                        SensoresService.accionProx = DONT_DO_ACTION_PROXIMITY;
                    }
                    else
                    {
                        if( SensoresService.proxCheck == NO_PROXIMITY && SensoresService.accionProx == DONT_DO_ACTION_PROXIMITY )
                        {
                            subirFanCalorButton.setEnabled(true);
                            bajarFanCalorButton.setEnabled(true);
                            subirTempCalorButton.setEnabled(true);
                            bajarTempCalorButton.setEnabled(true);
                            apagarButtonCalor.setEnabled(true);
                            backArrowCalor.setEnabled(true);
                            SensoresService.accionProx = DO_ACTION_PROXIMITY;
                        }
                        else
                        {
                            //Sensor de luz
                            if( SensoresService.luzCheck == LIGHT && SensoresService.accionLuz == DO_ACTION_LIGHT && SensoresService.proxCheck == NO_PROXIMITY )
                            {
                                if( enviarAArduino(AUTOMATICO_CMD) == MSG_SENT)
                                {
                                    SensoresService.accionLuz = DONT_DO_ACTION_LIGHT;
                                    Intent intent = new Intent (getApplicationContext(), ModoAutomaticoActivity.class);
                                    startActivityForResult(intent, CALOR_REQUEST);
                                }
                                else
                                {
                                    Intent intent = new Intent(getApplicationContext(), ErrorConexionCalorActivity.class);
                                    startActivityForResult(intent, CALOR_REQUEST);
                                }

                            }
                        }
                    }

                }


                myHandlerSensoresCalor.postDelayed(this, tiempoSensores);
            }

        }, tiempoSensores);


    }

    public void refrescarVistaCalor()
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
            estadoCalorValor.setText(GO_TO_ON_MODE);
            MainActivity.estado = ON_MODE;
        }
        else
        {
            estadoCalorValor.setText(GO_TO_OFF_MODE);
	    MainActivity.estado = OFF_MODE;
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(intent, CALOR_REQUEST);
        }

        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        switch(aux)
        {
            case SMART_FRIO:
            {
                Intent intent = new Intent(getApplicationContext(), ModoFrioActivity.class);
                startActivityForResult(intent, CALOR_REQUEST);
                break;
            }
            case SMART_AUTOMATICO:
            {
                Intent intent = new Intent(getApplicationContext(), ModoAutomaticoActivity.class);
                startActivityForResult(intent, CALOR_REQUEST);
                break;
            }
            case SMART_SEGURO:
            {
                MainActivity.modoPrevio = ACTUAL_MODE;
                Intent intent = new Intent(getApplicationContext(), ModoSeguroActivity.class);
                startActivityForResult(intent, CALOR_REQUEST);
                break;
            }
            case SMART_INICIO:
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, CALOR_REQUEST);
                break;
            }
            case SMART_VENTILACION:
            {
                Intent intent = new Intent(getApplicationContext(), ModoVentilacionActivity.class);
                startActivityForResult(intent, CALOR_REQUEST);
                break;
            }
        }

        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        tempCalorValor.setText(aux + STRING_CELSIUS);

        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        tempActualCalor.setText(aux + STRING_CELSIUS);

        int actual, elegida;
        actual = Integer.parseInt(aux);
        elegida = Integer.parseInt(((tempCalorValor.getText()).toString()).substring(START_POS_STRING,((tempCalorValor.getText()).toString()).indexOf(STRING_CELSIUS)));

        if(elegida < actual && actual > MIN_TEMP_VAL && actual < MAX_TEMP_VAL)
        {
            if(actual > MIN_EXTERNAL_TEMP)
            {
                tempCalorValor.setText(MAX_TEMP_STRING);
            }
            else
            {
                tempCalorValor.setText(actual + STRING_CELSIUS);
            }
        }


        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        velocidadFanCalorValor.setText(aux);

    }

    public void actualizarDatosCalor()
    {
        myHandlerCalor.postDelayed(new Runnable() {
            public void run() {

                if(MainActivity.conectadoBT == BT_CONNECTED && enviarAArduino(TRAMA_REQUEST_CMD) == MSG_SENT)
                {
                    if((ConexionBluetoothService.tramaLine).length() > MIN_STRING_LENGTH)
                    {
                        /***Actualizo la vista (en caso de errores)***/
                        refrescarVistaCalor();
                    }

                }


                myHandlerCalor.postDelayed(this, tiempo);
            }

        }, tiempo);

    }

}
