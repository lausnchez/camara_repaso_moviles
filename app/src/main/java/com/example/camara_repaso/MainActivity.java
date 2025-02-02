package com.example.camara_repaso;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    /*
    * PASOS PARA AGREGAR LA CÁMARA AL MÓVIL
    *
    *       PERMISOS
    *       ----------------------------------------------------------------------------------------
    *
    *   >   Agregar los permisos pertinentes al Manifest.xml (Pedirá que agregues una línea para
    *       que funcione correctamente).
    *
    *   >   Primero deberemos verificar los permisos que tiene la aplicación. Se hace una función
    *       que nos devuelva un array de strings que nos dirá que permisos queremos pedirle al
    *       usuario, y después crearemos una función que nos compruebe esos permisos uno por uno.
    *       En caso de que los permisos sean concedidos nos devolverá true.
    *
    *           - PERMISOS_REQUIRIDOS
    *           - todosPermisosConcedidos
    *
    *   >   Tenemos que crear la función onRequestPermissionsResult, que básicamente hace una cosa u
    *       otra dependiendo de los permisos que nos hayan sido aceptados.
    *
    *           - onRequestPermissionsResult
    *
    *   >   Creamos una función que nos solicite los permisos y use las funciones creadas
    *       anteriormente.
    *
    *
    *       CÁMARA
    *       ----------------------------------------------------------------------------------------
    *
    *   >   Para poder almacenar fotos en el dispositivo tendremos que configurar primero el provider
    *       en el manifesto y crear un archivo xml en la carpeta res/xml llamado "file_paths.xml".
    *       Esto nos sirve para decirle a la app dónde podremos guardar nuestros archivos.
    *       android:exported="false" hace que solo tu app pueda acceder a esos archivos. Por otro
    *       lado en el xml external-files-path permite acceder a getExternalFilesDir(Environment.DIRECTORY_PICTURES),
    *       es decir, devuelve el directorio específico para imágenes dentro del almacenamiento
    *       externo de la aplicación.
    *
    * */

    private static final int CODIGO_SOLICITUD_PERMISOS = 10;
    private String rutaFotoActual;
    private ImageView iv_foto = findViewById(R.id.img_foto);

    /**
     * Genera un array de Strings con los permisos que queremos pedirle al usuario
     */
    private static final String[] PERMISOS_REQUERIDOS = new String[]{
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    /**
     * Verifica si todos los permisos necesarios han sido concedidos.
     * @return true si todos los permisos han sido concedidos, false en caso contrario.
     */
    private boolean todosPermisosConcedidos() {
        for (String permiso : PERMISOS_REQUERIDOS) {
            if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Maneja la respuesta del usuario a la solicitud de permisos.
     *
     * @param requestCode Código de solicitud de permisos <- estado del permiso.
     * @param permissions Lista de permisos solicitados.
     * @param grantResults Resultados de la solicitud de permisos.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_SOLICITUD_PERMISOS) {
            if (todosPermisosConcedidos()) {
                Toast.makeText(this, "Se han aceptado los permisos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Se han denegado los permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Pide permisos desde la actividad, con los permisos delimitados por el array de Strings llamado
     * anteriomente y que cambian en caso de aceptar al codigo de solicitud de permisos.
     */
    private void solicitarPermisos() {
        ActivityCompat.requestPermissions(
                this,
                PERMISOS_REQUERIDOS,
                CODIGO_SOLICITUD_PERMISOS
        );
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Crea un archivo de imagen para almacenar la foto capturada. Recoge el directorio del provider
     * y del file_paths.xml.
     *
     * @return El archivo de imagen creado.
     * @throws IOException Si ocurre un error al crear el archivo.
     */
    private File crearArchivoImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new
                Date());
        File directorioAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(
                "JPEG_" + timeStamp + "_",
                ".jpg",
                directorioAlmacenamiento
        );
        rutaFotoActual = imagen.getAbsolutePath();
        return imagen;
    }

    /**
     * Lanza el intent para capturar una foto.
     * Crea un archivo de imagen usando la función anterior, y en caso de que exista guarda su uri
     * y lanza el intent para hacer la foto
     */
    private void lanzarIntentCapturaFoto() {
        File archivoFoto = null;
        try {
            archivoFoto = crearArchivoImagen(); // Crea el archivo
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (archivoFoto != null) {
            Uri uriFoto = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider", archivoFoto);
            rutaFotoActual = uriFoto.toString();    // guarda la ruta de la foto si existe
            lanzadorCapturaFoto.launch(uriFoto);    // abre el intent de hacer foto
        }
    }

    /**
     * Lanzador para capturar una foto usando la cámara.
     */
    private final ActivityResultLauncher<Uri> lanzadorCapturaFoto =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicture(),
                    exito -> {
                        if (exito) {
                            try {
                                Bitmap bitmap =
                                        MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(rutaFotoActual));
                                iv_foto.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        solicitarPermisos();
    }
}