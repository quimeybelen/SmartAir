#include <SoftwareSerial.h>
#define DIFERECIA_TEMPERATURA 1
#define TEMPERATURA_MAXIMA 26
#define TEMPERATURA_MINIMA 18
#define FAN_MAXIMO 4
#define  FAN_MINIMO 1
#define SUBIR 1
#define BAJAR 0
#define VEL_NULA 0
#define VEL_BAJA 64
#define VEL_MEDIA 128
#define VEL_MEDIA_ALTA 192
#define VEL_ALTA 255
#define MIN 0
#define MAX 12
#define NO_ESTADO -1
#define ERROR_FUNCTION -1

char SEPARADOR='|';
String INICIOINFO="info";
String FIN="fin";
bool _init = false;

/****PINES DIGITALES****/
int sensor_incl = 2; //digital
/*int led_blanco=7; //digital*/
int led_verde = 4; //digital
int buzzer = 3; //pwm
int led_azul = 12; //digital
int led_rojo = 13; //digital
int peltier_calor = 6;
int peltier_frio =7;
int fan = 5;
SoftwareSerial BT(10, 11);

/****PINES ANALOGICOS****/
int sensor_temp_interna= 0; //TMP-36
int sensor_temp_externa=1; //LM35
int temp_externa;
int temp_interna;
int temperatura_SmartAir=24;
int velocidad_fan=2;

int umbral_temp=18;
int dejar_sonar=0;
int buzzer_tone=0;
int lectura_incl;
int flag=0;
int inicio_tone;
int inicio_pausa=0;
bool pausa_buzzer=0;
int buzzer_noTone=0;
int inicio_soft;
int sonido_inicio;
bool inclinacion;

long tiempo_incl = 0;


enum estadoSmartAir
{
  INICIO=0,
  APAGADO,
  PRENDIDO,
};

enum estadoApagado
{
  INICIO_APAGADO=0,
  ESPERANDO_PRENDIDO,
};

enum estadoPrendido
{
  FRIO=0,
  CALOR,
  AUTOMATICO,
  MODOSEGURO,
  ESPERANDO_ORDEN,
  VENTILACION,
};

enum acciones
{
  PRENDER=0,
  APAGAR,
  CAMBIAR_ESTADO_FRIO,
  CAMBIAR_ESTADO_CALOR,
  CAMBIAR_ESTADO_VENTILACION,
  CAMBIAR_ESTADO_AUTOMATICO,
  FORZAR_MODO_SEGURO,
  FORZAR_SALIDA_MODO_SEGURO,
  SUBIR_FAN,
  BAJAR_FAN,
  SUBIR_TEMP,
  BAJAR_TEMP,
  ENVIAR_INFO
};

estadoSmartAir estadoSA;
estadoApagado estadoOFF=NO_ESTADO;
estadoPrendido estadoON=NO_ESTADO;
estadoPrendido estado_anterior_ON=NO_ESTADO;

void encenderPeltierFrio(){
  digitalWrite(peltier_calor,LOW);
  digitalWrite(peltier_frio,HIGH); 
}

void encenderPeltierCalor(){
  digitalWrite(peltier_calor,HIGH);
  digitalWrite(peltier_frio,LOW); 
}
void apagarPeltier(){
  digitalWrite(peltier_frio,LOW);
  digitalWrite(peltier_calor,LOW);

}

void encenderFan(){
  digitalWrite(fan, VEL_MEDIA_ALTA);
}

void apagarFan(){
  digitalWrite(fan,VEL_NULA);
}


/****FUNCION PARA SONAR BUZZER AL INICIO****/
void sonarBuzzerAlInicio(int tiempo_sonido)
{
  int aux=millis();
  if(buzzer_tone==0)
  {
    inicio_tone=millis();
    tone(buzzer,5000);
  }
  else
  {
    if(tiempo_sonido<aux-inicio_tone)
    {
      buzzer_tone=0;
      noTone(buzzer);
      sonido_inicio=0;
    }
  }
}

void enviarInfo(){
  String info="";
  info+=INICIOINFO;
  info+=SEPARADOR;
  info+=estadoSA;
  info+=SEPARADOR;
  info+=estadoON;
  info+=SEPARADOR;
  info+=temperatura_SmartAir;
  info+=SEPARADOR;
  info+=temp_externa;
  info+=SEPARADOR;
  info+=temp_interna;
  info+=SEPARADOR;
  info+=velocidad_fan;
  info+=SEPARADOR;
  info+=inclinacion;
  info+=SEPARADOR;
  info+=FIN;

  BT.println(info);
  
}

void procesarFan(bool accion){

  if(accion == SUBIR)
  {   
      if(velocidad_fan<FAN_MAXIMO){
        Serial.println("Subo FAN.");
          velocidad_fan++;
      }
  }else 
  {
      if(velocidad_fan>FAN_MINIMO){
        Serial.println("Bajo FAN.");
          velocidad_fan--;
      }
  }
  
  switch(velocidad_fan)
  {
    case 1:
    {
      digitalWrite(fan,VEL_BAJA);
      break;
    }
    case 2:
    {
      digitalWrite(fan, VEL_MEDIA);
      break;
    }
     case 3:
    {
      digitalWrite(fan, VEL_MEDIA_ALTA);
      break;
    }
    case 4:
    {
      digitalWrite(fan, VEL_ALTA);
      break;
    }
  }
}

void procesarTemperatura(bool accion){

   if(accion == SUBIR)
  {   
      if(temperatura_SmartAir<TEMPERATURA_MAXIMA){
          temperatura_SmartAir++;
          Serial.println("Subo TEMPERATURA.");
      }
  }else 
  {
      if(temperatura_SmartAir>TEMPERATURA_MINIMA){
          temperatura_SmartAir--;
          Serial.println("Bajo TEMPERATURA.");
      }
  }
  
}

/****FUNCION PARA PREGUNTAR SI HAY ORDEN DE BT****/
int leerOrdenBT()
{
  String aux;
  //Ver esta seccion donde debo asignarle a bluetooth_mando lo que envia el BT
   if (BT.available()){
        aux=BT.readString();
        BT.flush();
        if(aux){
               
          Serial.print(atoi(aux.c_str()));
          return atoi(aux.c_str());
        }
    
   }
      return ERROR_FUNCTION;
            
}

void procesarBT(int comando)
{ 
  if(comando<MIN || comando>MAX)
    return;

    switch(comando){
          
          case PRENDER:
          {
            if(estadoSA==PRENDIDO)
              return;
            estadoSA=PRENDIDO;
            estadoON = ESPERANDO_ORDEN;
           break;
          }
          
          case APAGAR:
          {
            if(estadoSA==APAGADO)
              return;
            estadoSA=APAGADO;
            estadoOFF = INICIO_APAGADO;
            estadoON = NO_ESTADO;
            estado_anterior_ON = NO_ESTADO;
            break;
          }
          case CAMBIAR_ESTADO_FRIO:
          { 
            
            if(estadoSA==APAGADO)
              return;
            estadoSA=PRENDIDO; 
            estadoON=FRIO;
            break;
          }
          
          case CAMBIAR_ESTADO_CALOR:
          {
            
            if(estadoSA==APAGADO)
              return;
            estadoSA=PRENDIDO; 
            estadoON=CALOR;
            break;
          }
          
          case CAMBIAR_ESTADO_VENTILACION:
          {
            
            if(estadoSA==APAGADO)
              return;
            estadoSA=PRENDIDO; 
            estadoON=VENTILACION;
            break;
          }
          
          case CAMBIAR_ESTADO_AUTOMATICO:
          {
            
            if(estadoSA==APAGADO)
              return;
            estadoSA=PRENDIDO; 
            estadoON=AUTOMATICO;
            break;
          }
          
          case FORZAR_MODO_SEGURO:
          {
            
            if(estadoSA==APAGADO)
              return;
            estadoSA=PRENDIDO; 
            estadoON=MODOSEGURO;
            break;
          }
          
          case FORZAR_SALIDA_MODO_SEGURO:
          {          
            
            if(estadoSA==APAGADO)
              return;  
            estadoSA=PRENDIDO; 
            estadoON=estado_anterior_ON;
            break;
          }
          
          case SUBIR_FAN:
          {
            
            if(estadoSA==APAGADO)
              return;
            procesarFan(SUBIR);
            break;
          }
          
          case BAJAR_FAN:
          {
            
            if(estadoSA==APAGADO)
              return;
            procesarFan(BAJAR);
            break;
          }
          
          case SUBIR_TEMP:
          {
            
            if(estadoSA==APAGADO)
              return;
            procesarTemperatura(SUBIR);
            break;
          }
          
          case BAJAR_TEMP:
          {
            
            if(estadoSA==APAGADO)
              return;
            procesarTemperatura(BAJAR);
            break;
          }

          case ENVIAR_INFO:
          //cada tres segundos
              enviarInfo();
            break;
    }

  
   
  
}

int chequearTemperaturaSA(){

  return temp_externa - temperatura_SmartAir ;
  
  
}

/****FUNCION PARA SONAR BUZZER EN MODO SEGURO****/
void sonarBuzzerModoSeguro(int tiempo_sonido, int tiempo_pausa)
{
  int aux;
  if(buzzer_tone==0 && buzzer_noTone==0)
  {
    //Serial.println("Empece a sonar");
    tone(buzzer,5000);
    inicio_tone=millis();
    buzzer_tone=1;
  }
  else
  {
    aux=millis();
    if(buzzer_tone==1 && tiempo_sonido<aux-inicio_tone && buzzer_noTone==0)
    {
      //Serial.println("Deje de sonar, empieza mi pausa");
      //int sonido=aux-inicio_tone;
      //Serial.println(sonido);
      noTone(buzzer);
      buzzer_noTone=1;
      inicio_pausa=millis();
      buzzer_tone=0;
    }
    else
    {
      if(buzzer_noTone==1 && buzzer_tone==0 && tiempo_pausa<aux-inicio_pausa)
      {
        
        //Serial.println("Termino mi pausa");
        //int pausita=aux-inicio_pausa;
        //Serial.println(pausita);
        buzzer_noTone=0;
      }
    }
  }
  
}

/****FUNCION DE DETECTAR INCLINACION****/
bool detectarInclinacion()
{
  return digitalRead(sensor_incl);
  //return 0;
}

/****FUNCION DE VERIFICAR IR A MODO SEGURO****/
void verificarIrAModoSeguro()
{
  if (detectarInclinacion())
    {
      if(estadoSA == PRENDIDO && estadoON != MODOSEGURO)
      {
         Serial.println("SmartAir inclinado.");
         inclinacion = 1;
         estado_anterior_ON = estadoON;
         estadoON = MODOSEGURO;
         digitalWrite(led_rojo,LOW);
         digitalWrite(led_azul,LOW);
         apagarPeltier();
         apagarFan();
      }
    }
}

/****FUNCION PARA LEER TEMPERATURA****/
float leerTemperatura(int sensor_pin){        
    int temp = analogRead(sensor_pin);
    float temperatura_actual;
    if(sensor_pin==0)
    {
      temperatura_actual = (5.0 /1024 * temp) * 100 -50 ;  
    }
    else
    {
      temperatura_actual = (5.0 /1024 * temp) * 100 ;
    }
    
    //Serial.println(temperatura) ; 
    //if (temperatura >= umbral_temp) 
    //Serial.println("Se alcanzo la temperatura") ;
    return temperatura_actual;
}

/****SETUP DEL SOFTWARE****/
void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  Serial.println("Init Setup");
  

  /*Pines*/
  //LEDs
  pinMode(led_rojo,OUTPUT);
  pinMode(led_azul,OUTPUT);
  pinMode(led_verde,OUTPUT);
  pinMode(peltier_calor,OUTPUT);
  pinMode(peltier_frio,OUTPUT);
  pinMode(fan,OUTPUT);
  //Buzzer
  pinMode(buzzer,OUTPUT);
  //Sensor de inclinacion
  pinMode(sensor_incl, INPUT_PULLUP);
  //Bluetooth
  //pinMode(8, OUTPUT);        // Al poner en HIGH forzaremos el modo AT
  pinMode(9, OUTPUT);        // cuando se alimente de aqui
  digitalWrite(9, HIGH);
  //digitalWrite (8, HIGH);    //Enciende el modulo
  BT.begin(9600); 

  

  sonido_inicio=0;
  estadoSA = INICIO; 
  inclinacion = 0;
 
}

/****LOOP****/
void loop() {
  // put your main code here, to run repeatedly:

    //Leer temperatura externa
    temp_externa = leerTemperatura(sensor_temp_externa);

    //Leer temperatura interna
    temp_interna = leerTemperatura(sensor_temp_interna);
  //Leer Bluetooth
    procesarBT(leerOrdenBT());

  /*Cosas que se harán siempre (a menos que se esté en MODO SEGURO y fin_sonido_inicio)*/
 
    
    if(estadoSA==PRENDIDO)
    {

      if(estadoON!=MODOSEGURO)
      {
         //Verificar si ir a modo seguro
          verificarIrAModoSeguro();
      }
    }
  
  switch(estadoSA)
  {
    case INICIO:
    {
      //Encendemos el led blanco (inicio de SmartAir, no encendido)
//      digitalWrite(led_blanco,HIGH);

      estadoSA = APAGADO;
      estadoOFF = INICIO_APAGADO;
      Serial.println("Inicio.");

      break;
    }

    case APAGADO:
    {   
        switch(estadoOFF)
        {

          case INICIO_APAGADO:
          {
              /*Apagar componentes*/
              Serial.println("Inicio Apagado.");
              digitalWrite(led_rojo,HIGH);
              digitalWrite(led_azul,LOW);
              digitalWrite(led_verde,LOW);
              apagarPeltier();
              apagarFan();
              estadoOFF = ESPERANDO_PRENDIDO;
              break;
          }

          case ESPERANDO_PRENDIDO:
          {
              //Encendido por medio de la app
              /*
              if(bluetooth_mando=PRENDIDO)
              { 
              */
              //Serial.println("Esperando_Prendido.");
                //digitalWrite(led_verde,HIGH);
                //digitalWrite(led_blanco,LOW);
                //sonido_inicio=1;
              /*
              }
              */

              
              break;
          }
        }
       
      
    }

    case PRENDIDO:
    {
       switch(estadoON){

          case ESPERANDO_ORDEN:
          { 
            Serial.println("Esperando_Orden.");
            digitalWrite(led_verde,HIGH);
            digitalWrite(led_rojo,LOW);
            //Hacemos sonar el buzzer de inicio
            if(sonido_inicio==1)
            {
               sonarBuzzerAlInicio(200);
            }
            break;
          }
          
          case MODOSEGURO:
          {   
              Serial.println("Modo Seguro.");
              if(detectarInclinacion())
              {
                noTone(buzzer);
                estadoSA = PRENDIDO;
                estadoON = estado_anterior_ON;
                estado_anterior_ON=MODOSEGURO;
                inclinacion = 0;
              }
              else
              {
                sonarBuzzerModoSeguro(200,3000);
              }
               break;            
          }

          case FRIO:
          {
            if(estadoON!=estado_anterior_ON)
            {
              estado_anterior_ON=estadoON;
              digitalWrite(led_rojo,LOW);
              digitalWrite(led_azul,HIGH);
              Serial.println("FRIO");
              encenderFan();
              
            }
             if(chequearTemperaturaSA()>1)
                  encenderPeltierFrio();
             else if(chequearTemperaturaSA()<ERROR_FUNCTION)
                  apagarPeltier();        

            
            //chequeo estado si ya lleggue a la temperatura indicado para apagar peltier
            //estadoON = CALOR;
            break;
          }

          case CALOR:
          {
            if(estadoON!=estado_anterior_ON)
            {
              estado_anterior_ON=estadoON;
              digitalWrite(led_rojo,HIGH);
              digitalWrite(led_azul,LOW);
              Serial.println("CALOR"); 
              encenderFan();
            }
             if(chequearTemperaturaSA()<ERROR_FUNCTION)
                  encenderPeltierCalor();
             else if(chequearTemperaturaSA()>1)
                  apagarPeltier();
            //chequeo estado si ya lleggue a la temperatura indicado para apagar peltier
            break;
          }

          
          case AUTOMATICO:
          {
            
            if(estadoON!=estado_anterior_ON)
            {
              estado_anterior_ON=estadoON;
              Serial.println("AUTOMATICO");
              digitalWrite(led_rojo,HIGH);
              digitalWrite(led_azul,HIGH);
              temperatura_SmartAir=24;
              encenderFan();
             
            }
             if(chequearTemperaturaSA()>1)
                  encenderPeltierFrio();
                else if(chequearTemperaturaSA()<ERROR_FUNCTION)
                  encenderPeltierCalor();
                  else 
                    apagarPeltier();
            

            break;
          }

           case VENTILACION:
          {
            
            if(estadoON!=estado_anterior_ON)
            {
              estado_anterior_ON=estadoON;
              Serial.println("VENTILACION");
              digitalWrite(led_rojo,LOW);
              digitalWrite(led_azul,LOW);
              apagarPeltier();
              encenderFan();
            }

            break;
          }

          
       }
      
    }
  }



}
