package com.example.smartair;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import static com.example.smartair.ConexionBluetoothService.enviarAArduino;

public class ModoFrioActivity extends AppCompatActivity {

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
    private static final int FRIO_REQUEST = 0;
    private static final int ON_MODE = 0;
    private static final int OFF_MODE = 1;
    private static final int MSG_SENT = 1;
    private static final int BT_CONNECTED = 1;
    private static final int MIN_EXTERNAL_TEMP = 18;
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
    private static final String SMART_IS_OFF = "1";
    private static final String SMART_CALOR = "1";
    private static final String SMART_INICIO = "4";
    private static final String SMART_AUTOMATICO = "2";
    private static final String SMART_SEGURO = "3";
    private static final String SMART_VENTILACION = "5";
    private static final String MAX_FAN_SPEED = "5";
    private static final String MIN_FAN_SPEED = "0";
    private static final String SEPARADOR = "|";
    private static final String STRING_CELSIUS = " °C";
    private static final String MIN_TEMP_STRING = "18 °C";
    private static final String ACTUAL_MODE = "Frio";
    private static final String GO_TO_OFF_MODE = "Apagado";
    private static final String GO_TO_ON_MODE = "Prendido";

    /***Variables***/
    Handler myHandlerFrio = new Handler();
    private final int tiempo = 3000;
    Handler myHandlerSensoresFrio = new Handler();
    private final int tiempoSensores = 1000;

    /***Componentes de la vista***/
    ImageView backModoFrio, backArrowFrio, subirTempFrioButton, bajarTempFrioButton, subirFanFrioButton, bajarFanFrioButton, thermoFrio, fanFrio, apagarButtonFrio, logoSmartAirFrio;
    TextView headerModoFrio, tempActualFrio, modoFrio, estadoFrioValor, estadoFrio, velocidadFanFrioValor, tempFrioValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_frio);

        /***Enlaces a componentes***/
        backModoFrio = (ImageView) findViewById(R.id.backModoFrio);
        subirTempFrioButton = (ImageView) findViewById(R.id.subirTempFrioButton);
        bajarTempFrioButton = (ImageView) findViewById(R.id.bajarTempFrioButton);
        subirFanFrioButton = (ImageView) findViewById(R.id.subirFanFrioButton);
        bajarFanFrioButton = (ImageView) findViewById(R.id.bajarFanFrioButton);
        thermoFrio = (ImageView) findViewById(R.id.thermoFrio);
        fanFrio = (ImageView) findViewById(R.id.fanFrio);
        apagarButtonFrio = (ImageView) findViewById(R.id.apagarButtonFrio);
        backArrowFrio = (ImageView) findViewById(R.id.backArrowFrio);
        logoSmartAirFrio = (ImageView) findViewById(R.id.logoSmartAirFrio);
        estadoFrio = (TextView) findViewById(R.id.estadoFrio);
        estadoFrioValor = (TextView) findViewById(R.id.estadoFrioValor);
        headerModoFrio = (TextView) findViewById(R.id.headerModoFrio);
        modoFrio = (TextView) findViewById(R.id.modoFrio);
        velocidadFanFrioValor = (TextView) findViewById(R.id.velocidadFanFrioValor);
        tempFrioValor = (TextView) findViewById(R.id.tempFrioValor);
        tempActualFrio = (TextView) findViewById(R.id.tempActualFrio);

        /***Actualizacion de datos de pantalla 'Frio' (cada 3 seg)***/
        actualizarDatosFrio();

        /***Chequeo de sensores cada 1 seg***/
        chequeoSensoresFrio();

        /***Boton 'apagar/prender'***/
        apagarButtonFrio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.estado == ON_MODE && enviarAArduino(OFF_CMD) == MSG_SENT) {
                    estadoFrioValor.setText(GO_TO_OFF_MODE);
                    MainActivity.estado = OFF_MODE;
                    //myHandlerFrio.removeCallbacks(null);
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    startActivityForResult(intent, FRIO_REQUEST);
                } else {
                    //Mensaje de error frio
                    Intent intent = new Intent(v.getContext(), ErrorConexionFrioActivity.class);
                    startActivityForResult(intent, FRIO_REQUEST);
                }

            }
        });

        /***Boton 'subir fan'***/
        subirFanFrioButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String val = Integer.toString(Integer.parseInt((velocidadFanFrioValor.getText()).toString()) + 1);
                if (!val.equals(MAX_FAN_SPEED) ) //La maxima velocidad es 4
                {
                    if(enviarAArduino(HIGH_FAN_SPEED_CMD) == MSG_SENT)
                    {
                        velocidadFanFrioValor.setText(val);
                    }
                    else
                    {
                        //Mensaje de error frio
                        Intent intent = new Intent(v.getContext(), ErrorConexionFrioActivity.class);
                        startActivityForResult(intent, FRIO_REQUEST);
                    }

                }
            }
        });

        /***Boton 'bajar fan'***/
        bajarFanFrioButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String val = Integer.toString(Integer.parseInt((velocidadFanFrioValor.getText()).toString()) - 1);
                if (!val.equals(MIN_FAN_SPEED)) //La minima velocidad es 1
                {
                    if(enviarAArduino(LOW_FAN_SPEED_CMD) == MSG_SENT)
                    {
                        velocidadFanFrioValor.setText(val);
                    }
                    else
                    {
                        //Mensaje de error frio
                        Intent intent = new Intent(v.getContext(), ErrorConexionFrioActivity.class);
                        startActivityForResult(intent, FRIO_REQUEST);
                    }

                }

            }
        });


        /***Boton 'bajar temperatura'***/
        bajarTempFrioButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int val = Integer.parseInt(((tempFrioValor.getText()).toString()).substring(START_POS_STRING, END_POS_STRING_TEMP)) - 1;
                int ext = Integer.parseInt(((tempActualFrio.getText()).toString()).substring(START_POS_STRING,((tempActualFrio.getText()).toString()).indexOf(STRING_CELSIUS)));
                if ( val > MIN_TEMP_VAL && val < ext)
                {
                    if(enviarAArduino(LOW_TEMP_CMD) == MSG_SENT)
                    {
                        tempFrioValor.setText(val + STRING_CELSIUS);
                    }
                    else
                    {
                        //Mensaje de error frio
                        Intent intent = new Intent(v.getContext(), ErrorConexionFrioActivity.class);
                        startActivityForResult(intent, FRIO_REQUEST);
                    }

                }

            }
        });

        /***Boton 'subir temperatura'***/
        subirTempFrioButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int val = Integer.parseInt(((tempFrioValor.getText()).toString()).substring(START_POS_STRING, END_POS_STRING_TEMP)) + 1;
                int ext = Integer.parseInt(((tempActualFrio.getText()).toString()).substring(START_POS_STRING,((tempActualFrio.getText()).toString()).indexOf(STRING_CELSIUS)));
                if ( val < MAX_TEMP_VAL && val < ext)
                {
                    if(enviarAArduino(HIGH_TEMP_CMD) == MSG_SENT)
                    {
                        tempFrioValor.setText(val + STRING_CELSIUS);
                    }
                    else
                    {
                        //Mensaje de error frio
                        Intent intent = new Intent(v.getContext(), ErrorConexionFrioActivity.class);
                        startActivityForResult(intent, FRIO_REQUEST);
                    }

                }

            }
        });

        /***Boton 'atras'***/
        backArrowFrio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Ir a menu
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(intent, FRIO_REQUEST);
            }
        });

    }

    @Override
    public void onBackPressed (){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivityForResult(intent, FRIO_REQUEST);
    }

    /***Chequeo de sensores***/
    public void chequeoSensoresFrio()
    {

        myHandlerSensoresFrio.postDelayed(new Runnable() {
            public void run() {

                if(MainActivity.conectadoBT == BT_CONNECTED)
                {
                    //Sensor shake: Para apagar y prender SmartAir
                    if(SensoresService.shakeCheck == SHAKED)
                    {
                        if( MainActivity.estado == ON_MODE && enviarAArduino(OFF_CMD) == MSG_SENT)
                        {
                            estadoFrioValor.setText(GO_TO_OFF_MODE);
                            MainActivity.estado = OFF_MODE;
                            SensoresService.shakeCheck = NO_SHAKED;
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivityForResult(intent, FRIO_REQUEST);

                        }
                        else
                        {
                            SensoresService.shakeCheck = NO_SHAKED;
                            Intent intent = new Intent(getApplicationContext(), ErrorConexionFrioActivity.class);
                            startActivityForResult(intent, FRIO_REQUEST);
                        }

                    }

                    //Sensor de proximidad: Para bloqueo de la pantalla
                    if( SensoresService.proxCheck == PROXIMITY && SensoresService.accionProx == DO_ACTION_PROXIMITY )
                    {
                        subirFanFrioButton.setEnabled(false);
                        bajarFanFrioButton.setEnabled(false);
                        subirTempFrioButton.setEnabled(false);
                        bajarTempFrioButton.setEnabled(false);
                        apagarButtonFrio.setEnabled(false);
                        backArrowFrio.setEnabled(false);
                        SensoresService.accionProx = DONT_DO_ACTION_PROXIMITY;
                    }
                    else
                    {
                        if( SensoresService.proxCheck == NO_PROXIMITY && SensoresService.accionProx == DONT_DO_ACTION_PROXIMITY )
                        {
                            subirFanFrioButton.setEnabled(true);
                            bajarFanFrioButton.setEnabled(true);
                            subirTempFrioButton.setEnabled(true);
                            bajarTempFrioButton.setEnabled(true);
                            backArrowFrio.setEnabled(true);
                            apagarButtonFrio.setEnabled(true);
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
                                    startActivityForResult(intent, FRIO_REQUEST);
                                }
                                else
                                {
                                    Intent intent = new Intent(getApplicationContext(), ErrorConexionFrioActivity.class);
                                    startActivityForResult(intent, FRIO_REQUEST);
                                }

                            }
                        }
                    }

                }


                myHandlerSensoresFrio.postDelayed(this, tiempoSensores);
            }

        }, tiempoSensores);

    }



    public void refrescarVistaFrio()
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
            estadoFrioValor.setText(GO_TO_ON_MODE);
            MainActivity.estado = ON_MODE;
        }
        else{
            if(aux.equals(SMART_IS_OFF))
             {
                 estadoFrioValor.setText(GO_TO_OFF_MODE);
                 MainActivity.estado = OFF_MODE;
                 Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                 startActivityForResult(intent, FRIO_REQUEST);
             }
         }

        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        switch(aux)
        {
            case SMART_CALOR:
            {
                Intent intent = new Intent(getApplicationContext(), ModoCalorActivity.class);
                startActivityForResult(intent, FRIO_REQUEST);
                break;
            }
            case SMART_AUTOMATICO:
            {
                Intent intent = new Intent(getApplicationContext(), ModoAutomaticoActivity.class);
                startActivityForResult(intent, FRIO_REQUEST);
                break;
            }
            case SMART_SEGURO:
            {
                MainActivity.modoPrevio = ACTUAL_MODE;
                Intent intent = new Intent(getApplicationContext(), ModoSeguroActivity.class);
                startActivityForResult(intent, FRIO_REQUEST);
                break;
            }
            case SMART_INICIO:
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, FRIO_REQUEST);
                break;
            }
            case SMART_VENTILACION:
            {
                Intent intent = new Intent(getApplicationContext(), ModoVentilacionActivity.class);
                startActivityForResult(intent, FRIO_REQUEST);
                break;
            }
        }

        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        tempFrioValor.setText(aux + STRING_CELSIUS);

        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

       tempActualFrio.setText(aux + STRING_CELSIUS);

        int actual, elegida;
        actual = Integer.parseInt(aux);
        elegida = Integer.parseInt(((tempFrioValor.getText()).toString()).substring(0,((tempFrioValor.getText()).toString()).indexOf(" °C")));

        if(elegida > actual && actual > MIN_TEMP_VAL && actual < MAX_TEMP_VAL)
        {
            if(actual < MIN_EXTERNAL_TEMP)
            {
                tempFrioValor.setText(MIN_TEMP_STRING);
            }
            else
            {
                tempFrioValor.setText(actual + STRING_CELSIUS);
            }
        }

        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        velocidadFanFrioValor.setText(aux);


    }

    public void actualizarDatosFrio()
    {
        myHandlerFrio.postDelayed(new Runnable() {
            public void run() {

                if(MainActivity.conectadoBT == BT_CONNECTED && enviarAArduino(TRAMA_REQUEST_CMD) == MSG_SENT)
                {
                    if((ConexionBluetoothService.tramaLine).length() > MIN_STRING_LENGTH)
                    {
                        /***Actualizo la vista (en caso de errores)***/
                        refrescarVistaFrio();
                    }

                }


                myHandlerFrio.postDelayed(this, tiempo);
            }

        }, tiempo);

    }



}
