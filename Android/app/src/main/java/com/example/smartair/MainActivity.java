package com.example.smartair;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.smartair.ConexionBluetoothService.enviarAArduino;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    /***Constantes***/
    private static final int MAIN_REQUEST = 0;
    private static final int ON_MODE = 0;
    private static final int OFF_MODE = 1;
    private static final int MSG_SENT = 1;
    private static final int BT_CONNECTED = 1;
    private static final int BT_NO_CONNECTED = 0;
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
    private static final String ON_CMD = "0";
    private static final String OFF_CMD = "1";
    private static final String FRIO_CMD = "2";
    private static final String CALOR_CMD = "3";
    private static final String AUTOMATICO_CMD = "5";
    private static final String VENTILACION_CMD = "4";
    private static final String TRAMA_REQUEST_CMD = "12";
    private static final String SMART_IS_ON = "2";
    private static final String SMART_IS_OFF = "1";
    private static final String SMART_FRIO = "0";
    private static final String SMART_CALOR = "1";
    private static final String SMART_AUTOMATICO = "2";
    private static final String SMART_SEGURO = "3";
    private static final String SMART_VENTILACION = "5";
    private static final String SEPARADOR = "|";
    private static final String ACTUAL_MODE = "Main";
    private static final String NO_BLUETOOTH = "El dispositivo no soporta bluetooth";
    

    /***Variables***/
    Handler myHandler = new Handler();
    private final int tiempo = 3000;
    Handler myHandlerSensores = new Handler();
    private final int tiempoSensores = 1000;
    public static int estado = 1;
    public static String modoPrevio = "Main";
    public static int conectadoBT = 0;

    /***Componentes de la vista***/
    Button frioButton, calorButton, ventilacionButton, automaticoButton;
    ImageView apagarButtonMain;
    TextView smartAir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /***Bluetooth adapter***/
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), NO_BLUETOOTH, Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {

            }
        }

        /***Enlaces a componentes***/
        frioButton = (Button) findViewById(R.id.frioButton);
        calorButton = (Button) findViewById(R.id.calorButton);
        ventilacionButton = (Button) findViewById(R.id.ventilacionButton);
        automaticoButton = (Button) findViewById(R.id.automaticoButton);
        apagarButtonMain = (ImageView) findViewById(R.id.apagarButtonMain);
        smartAir = (TextView) findViewById(R.id.smartAir);

        /***Actualizacion de datos de pantalla main (cada 5 seg)***/
        actualizarDatosMain();

        /***Chequeo de sensores cada 1 seg***/
        chequeoSensoresMain();

        /***Verificacion de estado***/
        if(estado==OFF_MODE)
        {
            frioButton.setEnabled(false);
            calorButton.setEnabled(false);
            automaticoButton.setEnabled(false);
            ventilacionButton.setEnabled(false);
        }
        else
        {
            frioButton.setEnabled(true);
            calorButton.setEnabled(true);
            automaticoButton.setEnabled(true);
            ventilacionButton.setEnabled(true);
        }

        /***Boton para ir a modo 'Frio'***/
        frioButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if( enviarAArduino(FRIO_CMD) == MSG_SENT)
                {
                    Intent intent = new Intent (v.getContext(), ModoFrioActivity.class);
                    startActivityForResult(intent, MAIN_REQUEST);
                }
                else
                {
                    //Error de conexion a embebido en Main
                    Intent intent = new Intent(v.getContext(), ErrorConexionMainActivity.class);
                    startActivityForResult(intent, MAIN_REQUEST);
                }
            }
        });

        /***Boton para ir a modo 'Calor'***/
        calorButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if( enviarAArduino(CALOR_CMD) == MSG_SENT)
                {
                    Intent intent = new Intent(v.getContext(), ModoCalorActivity.class);
                    startActivityForResult(intent, MAIN_REQUEST);
                }
                else
                {
                    //Error de conexion a embebido en Main
                    Intent intent = new Intent(v.getContext(), ErrorConexionMainActivity.class);
                    startActivityForResult(intent, MAIN_REQUEST);
                }
            }
        });

        /***Boton para ir a modo 'Ventilacion'***/
        ventilacionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if( enviarAArduino(VENTILACION_CMD) == MSG_SENT)
                {
                    Intent intent = new Intent(v.getContext(), ModoVentilacionActivity.class);
                    startActivityForResult(intent, MAIN_REQUEST);
                }
                else
                {
                    //Error de conexion a embebido en Main
                    Intent intent = new Intent(v.getContext(), ErrorConexionMainActivity.class);
                    startActivityForResult(intent, MAIN_REQUEST);
                }
            }
        });

        /***Boton para ir a modo 'Automatico'***/
        automaticoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if( enviarAArduino(AUTOMATICO_CMD) == MSG_SENT)
                {
                    Intent intent = new Intent(v.getContext(), ModoAutomaticoActivity.class);
                    startActivityForResult(intent, MAIN_REQUEST);
                }
                else
                {
                    //Error de conexion a embebido en Main
                    Intent intent = new Intent(v.getContext(), ErrorConexionMainActivity.class);
                    startActivityForResult(intent, MAIN_REQUEST);
                }
            }
        });

        /***Boton 'Apagar/Prender'***/
        apagarButtonMain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if( estado==OFF_MODE && enviarAArduino(ON_CMD) == MSG_SENT)
                {
                    frioButton.setEnabled(true);
                    calorButton.setEnabled(true);
                    automaticoButton.setEnabled(true);
                    ventilacionButton.setEnabled(true);
                    estado=ON_MODE;
                }
                else
                {
                    if( estado==ON_MODE && enviarAArduino(OFF_CMD) == MSG_SENT)
                    {
                        frioButton.setEnabled(false);
                        calorButton.setEnabled(false);
                        automaticoButton.setEnabled(false);
                        ventilacionButton.setEnabled(false);
                        estado=OFF_MODE;
                    }
                    else
                    {
                        //Error de conexion a embebido en Main
                        Intent intent = new Intent(v.getContext(), ErrorConexionMainActivity.class);
                        startActivityForResult(intent, MAIN_REQUEST);
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed (){
    }

    @Override
    public void onResume(){
        super.onResume();
        /***Inicio del servicio Bluetooth***/
        if(conectadoBT == BT_NO_CONNECTED)
        {
            Intent service = new Intent(getBaseContext(),ConexionBluetoothService.class);
            startService(service);
            conectadoBT = BT_CONNECTED;
        }

        /***Inicio del servicio de sensores***/
        Intent servicioSensores = new Intent(getBaseContext(),SensoresService.class);
        startService(servicioSensores);
    }


    /***Chequeo de sensores***/
    public void chequeoSensoresMain()
    {

        myHandlerSensores.postDelayed(new Runnable() {
            public void run() {
                if(conectadoBT == BT_CONNECTED)
                {
                    //Sensor shake: Para apagar y prender SmartAir
                    if(SensoresService.shakeCheck == SHAKED)
                    {
                        if( estado == OFF_MODE && enviarAArduino(ON_CMD) == MSG_SENT)
                        {
                            frioButton.setEnabled(true);
                            calorButton.setEnabled(true);
                            automaticoButton.setEnabled(true);
                            ventilacionButton.setEnabled(true);
                            estado = ON_MODE;
                            SensoresService.shakeCheck = NO_SHAKED;
                        }
                        else
                        {
                            if( estado == ON_MODE && enviarAArduino(OFF_CMD) == MSG_SENT)
                            {
                                frioButton.setEnabled(false);
                                calorButton.setEnabled(false);
                                automaticoButton.setEnabled(false);
                                ventilacionButton.setEnabled(false);
                                estado = OFF_MODE;
                                SensoresService.shakeCheck = NO_SHAKED;
                            }
                            else
                            {
                                //Error de conexion a embebido en Main
                                SensoresService.shakeCheck = NO_SHAKED;
                                Intent intent = new Intent(getApplicationContext(), ErrorConexionMainActivity.class);
                                startActivityForResult(intent, MAIN_REQUEST);

                            }
                        }
                    }

                    //Sensor de proximidad: Para bloqueo de la pantalla
                    if( SensoresService.proxCheck == PROXIMITY && SensoresService.accionProx == DO_ACTION_PROXIMITY )
                    {
                        frioButton.setEnabled(false);
                        calorButton.setEnabled(false);
                        automaticoButton.setEnabled(false);
                        ventilacionButton.setEnabled(false);
                        apagarButtonMain.setEnabled(false);
                        SensoresService.accionProx = DONT_DO_ACTION_PROXIMITY;
                    }
                    else
                    {
                        if( SensoresService.proxCheck == NO_PROXIMITY && SensoresService.accionProx == DONT_DO_ACTION_PROXIMITY )
                        {
                            frioButton.setEnabled(true);
                            calorButton.setEnabled(true);
                            automaticoButton.setEnabled(true);
                            ventilacionButton.setEnabled(true);
                            apagarButtonMain.setEnabled(true);
                            SensoresService.accionProx = DO_ACTION_PROXIMITY;
                        }
                        else
                        {
                            //Sensor de luz: Para poner en modo automatico a la noche
                            if( SensoresService.luzCheck == LIGHT && SensoresService.accionLuz == DO_ACTION_LIGHT && SensoresService.proxCheck == NO_PROXIMITY )
                            {
                                if( enviarAArduino(AUTOMATICO_CMD) == MSG_SENT)
                                {
                                    Intent intent = new Intent (getApplicationContext(), ModoAutomaticoActivity.class);
                                    startActivityForResult(intent, MAIN_REQUEST);
                                }
                                else
                                {
                                    SensoresService.accionLuz = DONT_DO_ACTION_LIGHT;
                                    //Error de conexion a embebido en Main
                                    Intent intent = new Intent(getApplicationContext(), ErrorConexionMainActivity.class);
                                    startActivityForResult(intent, MAIN_REQUEST);
                                }

                            }
                        }
                    }
                }

                myHandlerSensores.postDelayed(this, tiempoSensores);
            }

        }, tiempoSensores);

    }

    public void refrescarVistaMain()
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
            frioButton.setEnabled(true);
            calorButton.setEnabled(true);
            automaticoButton.setEnabled(true);
            ventilacionButton.setEnabled(true);
            estado = ON_MODE;
        }
        else
        {
            if(aux.equals(SMART_IS_OFF))
            {
                frioButton.setEnabled(false);
                calorButton.setEnabled(false);
                automaticoButton.setEnabled(false);
                ventilacionButton.setEnabled(false);
                estado = OFF_MODE;
            }
        }

        trama = trama.substring(trama.indexOf(SEPARADOR)+1);
        aux = trama.substring(START_POS_STRING, trama.indexOf(SEPARADOR));

        switch(aux)
        {
            case SMART_FRIO:
            {
                Intent intent = new Intent(getApplicationContext(), ModoFrioActivity.class);
                startActivityForResult(intent, MAIN_REQUEST);
                break;
            }
            case SMART_CALOR:
            {
                Intent intent = new Intent(getApplicationContext(), ModoCalorActivity.class);
                startActivityForResult(intent, MAIN_REQUEST);
                break;
            }
            case SMART_AUTOMATICO:
            {
                Intent intent = new Intent(getApplicationContext(), ModoAutomaticoActivity.class);
                startActivityForResult(intent, MAIN_REQUEST);
                break;
            }
            case SMART_SEGURO:
            {
                modoPrevio = ACTUAL_MODE;
                Intent intent = new Intent(getApplicationContext(), ModoSeguroActivity.class);
                startActivityForResult(intent, MAIN_REQUEST);
                break;
            }

            case SMART_VENTILACION:
            {
                Intent intent = new Intent(getApplicationContext(), ModoVentilacionActivity.class);
                startActivityForResult(intent, MAIN_REQUEST);
                break;
            }
        }
    }

    public void actualizarDatosMain()
    {
        myHandler.postDelayed(new Runnable() {
            public void run() {
                if(conectadoBT == BT_CONNECTED && enviarAArduino(TRAMA_REQUEST_CMD) == MSG_SENT)
                {
                    if((ConexionBluetoothService.tramaLine).length() > MIN_STRING_LENGTH)
                    {
                        /***Actualizo la vista (en caso de errores)***/
                        refrescarVistaMain();
                    }

                }

                myHandler.postDelayed(this, tiempo);
            }

        }, tiempo);

    }



}
