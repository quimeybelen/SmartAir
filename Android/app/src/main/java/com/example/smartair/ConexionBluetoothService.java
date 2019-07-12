package com.example.smartair;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class ConexionBluetoothService extends Service {


    /***Constantes***/
    private static final int MIN_STRING_LENGTH = 0;
    private static final int START_POS_STRING = 0;
    private static final int SOCKET_CONNECTED = 1;
    private static final int SOCKET_NO_CONNECTED = 0;
    private static final int BUFFER_LENGTH = 256;
    private static final String STRING_END = "|fin";
    private static final String HC05_MAC =;
    private static final String SOCKET_CREATION_FAILED_MSG = "La creacción del Socket falló.";
    private static final String CONNECTION_FAILED_MSG = "La Conexión fallo";

    /***Variables bluetooth***/
    public static String myBuffer;
    static Handler bluetoothIn;
    final static int handlerState = 0;
    private BluetoothAdapter btAdapter;
    public static BluetoothSocket btSocket;
    private static StringBuilder DataStringIN = new StringBuilder();
    public static ConnectedThread MyConexionBT;
    public static String tramaLine=null;
    // Identificador unico de servicio (SPP UUID)
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);
                    myBuffer=readMessage;

		    //Trama del arduino
                    int endOfLineIndex = DataStringIN.indexOf(STRING_END);

                    if (endOfLineIndex > MIN_STRING_LENGTH) {
                        tramaLine = DataStringIN.substring(START_POS_STRING, endOfLineIndex);
                        DataStringIN.delete(START_POS_STRING, DataStringIN.length());
                    }
                }
            }
        };

        //Bluetooth adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Seteo la dirección MAC del HC-05
        BluetoothDevice device = btAdapter.getRemoteDevice(HC05_MAC);

        //Creación del socket bluetooth
        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), SOCKET_CREATION_FAILED_MSG, Toast.LENGTH_LONG).show();
        }

        //Establece la conexión con el socket Bluetooth
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {}
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();

        return START_NOT_STICKY;

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //Conexion de salida segura para el dispositivo con servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    //Metodo para envio de tramas al arduino
    public static int enviarAArduino(String cad){

        if(btSocket.isConnected())
        {
            MyConexionBT.write(cad);
            return SOCKET_CONNECTED;
        }
        else
        {
            return SOCKET_NO_CONNECTED;
        }

    }

    //Clase de evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[BUFFER_LENGTH];
            int bytes;

            //Se mantiene en modo escucha
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento por handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Envio de datos al arduino
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //Si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), CONNECTION_FAILED_MSG, Toast.LENGTH_LONG).show();
            }
        }
    }

}
