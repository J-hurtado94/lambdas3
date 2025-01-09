package org.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import software.amazon.awssdk.regions.Region;  // Asegúrate de importar de aws-sdk v2
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LambdaFunctionHandler implements RequestHandler<Object, String> {

    // Región de AWS
    private static final Region REGION = Region.US_EAST_1; // Cambia la región según sea necesario
    private static final String BUCKET_NAME = "automationbuckets3"; // Nombre de tu bucket S3
    private static final String FILE_NAME = "test.txt"; // Nombre del archivo en S3
    private static final String FILE_CONTENT = "{"
            + "\"name\": \"John\","
            + "\"age\": 30,"
            + "\"city\": \"New York\""
            + "}";// Contenido del archivo

    @Override
    public String handleRequest(Object o, Context context) {
        // Configuración del cliente S3
        S3Client s3Client = S3Client.builder()
                .region(REGION)
                .build();

        // Llamar a la función que sube el archivo a S3
        uploadFileToS3(s3Client);

        // Cerrar el cliente S3
        s3Client.close();

        // Devolver mensaje de éxito
        return "Archivo subido exitosamente a S3.";
    }

    private void uploadFileToS3(S3Client s3Client) {
        try {
            // Ruta temporal en Lambda (/tmp) para almacenar el archivo
            Path filePath = Paths.get("/tmp", FILE_NAME);

            // Escribir el contenido del archivo en el directorio temporal
            Files.write(filePath, FILE_CONTENT.getBytes());

            // Crear solicitud para subir el archivo a S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(FILE_NAME)
                    .build();

            // Subir el archivo a S3
            PutObjectResponse response = s3Client.putObject(putObjectRequest, filePath);

            // Confirmación de que el archivo fue subido correctamente
            System.out.println("Archivo subido exitosamente con ETag: " + response.eTag());

        } catch (Exception e) {
            System.err.println("Error subiendo el archivo a S3: " + e.getMessage());
        }
    }
}
