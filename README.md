# Android-App-BrujulaVoz-CompassVoice

BrujulaVoz is an android application created to learn about the orientation sensor and speech recognition. 

It has two activities, the first one has a speech recognition Activity that saves data given by the user, e.g. Norte 10 (the app is in 
Spanish), the first word (Cardinal point), is the direction the user is looking for and the second is a margin of error, using the same
example, Norte 10 means the user will be facing the right direction when the sensor's values are in  the interval [-10,10] (north is the degree = 0). 

This information is sent to the second Activity which will have a compass image rotating thanks to the orientation sensor. 
The user will know when is in the right direction by hearing a the sound of a bell.

Sorry for my english, If I have any mistake please dont hesitate to contact me in order to solve it.

/-------------------------------------------------------------------------------------------------------------------------------------/
BrujulaVoz es una aplicación hecha para aprender a utilizar el sensor de orientación de Android. En primer lugar utilizamos una
actividad de google que nos permite utilizar reconocimiento de voz para captar datos para la aplicación. El usuario deberá decir una
de las siguientes opciones:
  - Norte / Sur / Este / Oeste + Error 
  - El error creará un intervalo en el que el usuario estará dentro de la dirección, por ejemplo Norte 10 creará el intervalo 
    [-10,10] debido a que el grado asociado al norte es 0. Cuando en la segunda Actividad el usuario se coloque en este intervalo,
    la aplicación lo dará por correcto.

Una vez guardado el reconocimiento de voz se envía a la segunda actividad donde se abrirá una brújula que permitirá al usuario 
orientarse y colocarse en dirección al objetivo que dijo por voz. Cuando esté dentro del rango de valores sonará una campanilla
para dar feedback al usuario de que está colocado correctamente.

/-------------------------------------------------------------------------------------------------------------------------------------/


