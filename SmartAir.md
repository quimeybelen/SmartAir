# SmartAir

SmartAir es un aire inverter pequeño y transportable controlado desde una aplicación Android desde tu celular.  
Se tendrá la posibilidad de controlar la temperatura interna, la temperatura externa, entre otras funciones más detalladas a continuación.
  
###### Integrantes:  
  - Bedetti, Nicolas (37.844.342) --> nbedetti@gmail.com  
  - Hoz, Aylen (40.129.336)--> ailu.hoz28@gmail.com  
  - Torres, Quimey (38.891.324) --> quimey.torres@gmail.com  
  
  
### Materiales a disposición:
	- 5 leds verdes.
	- 5 leds azules.
	- 5 leds blancos.
	- 5 leds rojos.
	- 1 buzzer.
	- 1 sensor de inclinación.
	- 1 protoboard de 400 puntos.
	- Arduino Uno SMD CH340.
	- 1 cable USB.
	- 50 cables M-M.
	- 5 resistencias de 200k.
	- 5 resistencias de 10k.
	- 5 resistencias de 1k.
	- 1 tira de 40 pines.
	- 2 celdas peltier TEC1-12706.
	- 3 sensores de temperatura TMP36.
	- Modulo bluetooth HC-06 slave.
	- Sensor optico infrarrojo reflectivo.
	- Modulo WIFI Esp8266.
	- Fuente de PC de 12V.
	- 2 coolers (con disipador).
	- Disipadores.
	- Pasta térmica.
	
### Entradas:
- Sensor de temperatura TMP36 (Temperatura interna): Este sensor será utilizado para conocer la temperatura interna del aire inverter.
- Sensor de temperatura TMP36 (Temperatura externa): Este sensor será utilizado para conocer la temperatura del aire que sale por el cooler frontal del aire inverter.
- Sensor de inclinación: Este sensor será utilizado para detectar cuando el aire inverter sea movido de su lugar de manera manual. En este caso se procederá a apagar el aire para proteger sus componentes en caso de movimientos bruscos.
  
### Salidas:
- Cooler frontal: Este cooler será utilizado para impulsar el aire con la temperatura deseada y/o indicada por el usuario.
- Cooler trasero: Este cooler será utilizado para controlar la temperatura interna del aire inverter para evitar un sobrecalentamiento de los componentes.
- Led verde: Este led indicará cuando SmartAir este encendido.
- Led azul: Este led indicará cuando SmartAir este en modo "aire frio".
- Led rojo: Este led indicará cuando SmartAir este en modo "aire cálido".
- Celdas peltier: Estas se encenderán y apagarán para controlar la temperatura, tambien se cambiará la polaridad para entregar frío o calor según el modo en el que funcione el aire.
- Buzzer: Se utiliza para indicar de forma sonora el encendido, apagado, cambio de modo, al subir o bajar la temperatura y velocidad de los coolers, ademas para indicar que el dispositivo se encuentra en modo seguridad.
  
##### Microentregas:
- Microentrega 1:
- [x] Integrantes del grupo y idea del proyecto.
  
- Microentrega 2:
- [x] Materiales.
- [x] Entradas y salidas del proyecto.
- [x] Hardware que cumple las funciones del proyecto.
- [x] Bloques que componen el proyecto.
  
- Microentrega 3:
- [x] Diagrama funcional.
- [x] Diagrama físico.
- [x] Esquema del producto final.
  
- Microentrega 4:
- [x] Diagrama lógico.
  
- Microentrega 5:
- [x] Diagrama de software.
- [ ] Circuito de conexión.
  
- Microentrega 6:
- [x] Secuencia de diseño incremental.
  
- Microentrega 7:
- [x] Explicación del proyecto.
- [x] Objetivos (Para qué sirve, que problema rsuelve y como lo resuelve).
- [x] Utilización (Como se opera, que necesita para funcionar, en qué secuencia cumple el objetivo).
- [ ] Como funciona internamente.
- [ ] Procedimiento (Como logra el objetivo).
