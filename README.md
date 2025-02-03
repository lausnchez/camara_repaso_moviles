PASOS PARA AGREGAR LA CÁMARA AL MÓVIL

       PERMISOS
       ----------------------------------------------------------------------------------------

       Agregar los permisos pertinentes al Manifest.xml (Pedirá que agregues una línea para
       que funcione correctamente).

       Primero deberemos verificar los permisos que tiene la aplicación. Se hace una función
       que nos devuelva un array de strings que nos dirá que permisos queremos pedirle al
       usuario, y después crearemos una función que nos compruebe esos permisos uno por uno.
       En caso de que los permisos sean concedidos nos devolverá true.

           - PERMISOS_REQUIRIDOS
           - todosPermisosConcedidos

       Tenemos que crear la función onRequestPermissionsResult, que básicamente hace una cosa u
       otra dependiendo de los permisos que nos hayan sido aceptados.

           - onRequestPermissionsResult

       Creamos una función que nos solicite los permisos y use las funciones creadas
       anteriormente.


       CÁMARA
      ----------------------------------------------------------------------------------------

       Para poder almacenar fotos en el dispositivo tendremos que configurar primero el provider
       en el manifesto y crear un archivo xml en la carpeta res/xml llamado "file_paths.xml".
       Esto nos sirve para decirle a la app dónde podremos guardar nuestros archivos.
       android:exported="false" hace que solo tu app pueda acceder a esos archivos. Por otro
       lado en el xml external-files-path permite acceder a getExternalFilesDir(Environment.DIRECTORY_PICTURES),
       es decir, devuelve el directorio específico para imágenes dentro del almacenamiento
       externo de la aplicación.
