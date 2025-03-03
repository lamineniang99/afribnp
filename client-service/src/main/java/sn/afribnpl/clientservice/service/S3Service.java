package sn.afribnpl.clientservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;

@Service
public class S3Service {
    private String bucketName;

    private S3Client s3Client;

    public S3Service(@Value("${aws.s3.bucket-name}")String bucketName, S3Client s3Client) {
        this.bucketName = bucketName;
        this.s3Client = s3Client;
    }

    /**
     * uploader un fichier vers S3 et retourne url du fichier
     * @param userId
     * @param file
     * @return
     */

    public String uploadFile(String userId, MultipartFile file) {
        String fileName = userId + ".pdf" ;

        try {
            // Création de la requête S3
            PutObjectRequest putObjectRequest = PutObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build() ;
            // uploader le fichier vers s3
            PutObjectResponse response  = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // Vérification du succès
            if (response.sdkHttpResponse().isSuccessful()) {
                return getFileUrl(fileName);
            } else {
                throw new RuntimeException("Échec de l'upload du fichier");
            }
        }catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier", e);
        }
    }

    /**
     * Generer l'url du fichier sur S3
     * @param fileName
     * @return
     */
    private String getFileUrl(String fileName) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
    }

}
