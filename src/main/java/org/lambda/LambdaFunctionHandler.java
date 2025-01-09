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
    private static final Region REGION = Region.US_EAST_1;
    private static final String BUCKET_NAME = "automationbuckets3";
    private static final String FILE_NAME = "test.txt";
    private static final String FILE_CONTENT = "{"
            + "\"name\": \"John\","
            + "\"age\": 30,"
            + "\"city\": \"New York\""
            + "}";

    @Override
    public String handleRequest(Object o, Context context) {

        S3Client s3Client = S3Client.builder()
                .region(REGION)
                .build();

        uploadFileToS3(s3Client);

        s3Client.close();

        return "Archivo subido exitosamente a S3.";
    }

    private void uploadFileToS3(S3Client s3Client) {
        try {
            Path filePath = Paths.get("/tmp", FILE_NAME);

            Files.write(filePath, FILE_CONTENT.getBytes());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(FILE_NAME)
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, filePath);

            System.out.println("Archivo subido exitosamente con ETag: " + response.eTag());

        } catch (Exception e) {
            System.err.println("Error subiendo el archivo a S3: " + e.getMessage());
        }
    }
}
