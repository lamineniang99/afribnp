package sn.afribnpl.clientservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

@Service
public class S3Service {
    private static final Logger log = LoggerFactory.getLogger(S3Service.class);

    private final String bucketName;
    private final S3Client s3Client;

    public S3Service(@Value("${aws.s3.bucket-name}") String bucketName, S3Client s3Client) {
        this.bucketName = bucketName;
        this.s3Client = s3Client;
    }

    /**
     * Upload un fichier vers S3 et retourne l'URL du fichier.
     *
     * @param userId ID de l'utilisateur (utilisé pour générer le nom du fichier).
     * @param file   Fichier à uploader.
     * @return URL du fichier sur S3.
     * @throws RuntimeException Si le fichier est vide, l'upload échoue, ou une erreur se produit.
     */
    public String uploadFile(String userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide ou null");
        }

        String fileName = userId + ".pdf";
        log.info("Tentative d'upload du fichier : {} vers le bucket : {}", fileName, bucketName);

        try {
            // Création de la requête S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            // Upload du fichier vers S3
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // Vérification du succès
            if (response.sdkHttpResponse().isSuccessful()) {
                log.info("Fichier uploadé avec succès : {}", fileName);
                return getFileUrl(fileName);
            } else {
                log.error("Échec de l'upload du fichier : {}", fileName);
                throw new RuntimeException("Échec de l'upload du fichier. Réponse S3 : " + response.sdkHttpResponse().statusText());
            }
        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier : {}", fileName, e);
            throw new RuntimeException("Erreur lors de la lecture du fichier", e);
        } catch (S3Exception e) {
            log.error("Erreur S3 lors de l'upload du fichier : {}", fileName, e);
            throw new RuntimeException("Erreur S3 lors de l'upload du fichier", e);
        }
    }

    /**
     * Génère l'URL du fichier sur S3.
     *
     * @param fileName Nom du fichier sur S3.
     * @return URL du fichier.
     */
    private String getFileUrl(String fileName) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
    }
}