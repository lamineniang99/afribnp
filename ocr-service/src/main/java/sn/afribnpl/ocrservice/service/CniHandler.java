package sn.afribnpl.ocrservice.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import sn.afribnpl.ocrservice.config.AwsCredentialsLoader;
import sn.afribnpl.ocrservice.dto.Client;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

@Component
public class CniHandler {

    private static final Logger log = LoggerFactory.getLogger(CniHandler.class);
    @Autowired
    private StreamBridge streamBridge ;

    @Bean
    public Consumer<Client> cniConsumer() {
        return (cni) -> {
            log.info("*******************les infos recues sur le topic cni depuis le client service");
            log.info("Nom : {} Prenom : {} Birthday : {}", cni.getNom(), cni.getPrenom(), cni.getBirthday());
            log.info("*******************recuperation du fichier sur le bean ");

            byte[] bytes = downloadCniFile(cni.getUrlCni());
            if (bytes != null) {
                log.info("***********fichier telecharger depuis S3 avec succes ! ");
            }
            try {
                List<BufferedImage> bufferedImages = extractImagesFromPdf(bytes);
                String text = extractTextFromImages(bufferedImages) ;
                log.info("//////////////Texte extrait dans la liste des images : {} ", text);
                log.info("------------------extraction du prenom dans le NAS ...");
                String prenom = extractPreNomFromText(text) ;
                String nom = extractNomFromText(text) ;

                if (nom.equals(cni.getNom()) && prenom.equals(cni.getPrenom())) {
                    log.info("les infos concernant nom et prenom sont conforme");
                    log.info("preparation de publication sur le broker");
                    cni.setCniVerified(true);
                    streamBridge.send("cni-resp", cni) ;
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }




    private String extractPreNomFromText(String text) {
        // Supposons que le nom est précédé d'un libellé comme "Nom :" ou "Nom complet :"
        String[] lines = text.split("\n"); // Divise le texte en lignes
        log.info("le nombre de ligne recuperer : {}", lines.length);

        for (String line : lines) {

            if (line.contains("Prénom") || line.contains("First Name")) {
                // Extrait le nom après le libellé
                String[] parts = line.split(":");
                log.info("------- la ligne concerné {} ", line);
                if (parts.length > 0) {
                    log.info("Le prenom recupére dans le NAS {}", parts[1].trim());
                    return parts[1].trim(); // Retourne le nom en supprimant les espaces inutiles
                }
            }
        }

        // Si le nom n'est pas trouvé, retourne null
        return null;

    }

    private String extractNomFromText(String text) {
        String[] lines = text.split("\n"); // Divise le texte en lignes

        for (String line : lines) {

            if (line.contains("Nom(s) de Famille") || line.contains("Family Name(s)")) {
                // Extrait le nom après le libellé
                String[] parts = line.split(":");
                log.info("------- la ligne concerné {} ", line);
                if (parts.length > 0) {
                    log.info("Le nom recupére dans le NAS {}", parts[1].trim());
                    return parts[1].trim(); // Retourne le nom en supprimant les espaces inutiles
                }
            }
        }

        // Si le nom n'est pas trouvé, retourne null
        return null;

    }


    public byte[] downloadCniFile(String urlCni) {
        // Chargez les credentials depuis le fichier
        Properties awsCredentials = AwsCredentialsLoader.loadCredentials();
        String accessKeyId = awsCredentials.getProperty("aws.s3.access-key");
        String secretKey = awsCredentials.getProperty("aws.s3.secret-key");
        String region = awsCredentials.getProperty("aws.s3.region");

        // Créez un client S3 avec les credentials
        S3Client s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretKey)))
                .region(Region.of(region))
                .build();

        try {
            // Extrayez le nom du bucket et la clé de l'objet à partir de l'URL
            String bucketName = "afribnpl"; // Remplacez par le nom de votre bucket
            String key = extractKeyFromUrl(urlCni); // Méthode pour extraire la clé de l'URL

            // Créez une requête pour obtenir l'objet
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            // Téléchargez l'objet sous forme de tableau de bytes
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes());

            return objectBytes.asByteArray();
        } catch (S3Exception e) {
            // Gérez les erreurs spécifiques à S3
            System.err.println("Erreur lors du téléchargement du fichier depuis S3 : " + e.awsErrorDetails().errorMessage());
            throw e;
        } catch (Exception e) {
            // Gérez les erreurs d'entrée/sortie
            System.err.println("Erreur d'entrée/sortie lors du téléchargement du fichier : " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            // Fermez le client S3
            s3Client.close();
        }
    }

    /**
     * Extrait la clé de l'objet à partir de l'URL S3.
     * Exemple d'URL : https://afribnpl.s3.amazonaws.com/081b1a3a-ed5d-4f4c-91f7-b130d952d87e.pdf
     */
    private String extractKeyFromUrl(String urlCni) {
        // Supprimez le préfixe de l'URL pour obtenir la clé
        String prefix = "https://afribnpl.s3.amazonaws.com/";
        if (urlCni.startsWith(prefix)) {
            return urlCni.substring(prefix.length());
        }
        throw new IllegalArgumentException("URL S3 invalide : " + urlCni);
    }

    public List<BufferedImage> extractImagesFromPdf(byte[] pdfBytes) throws IOException {
        List<BufferedImage> images = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfBytes)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // Parcourir chaque page du PDF
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                // Rendre la page en image
                BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300, ImageType.RGB);
                images.add(image);
            }
        }

        return images;
    }

    public String extractTextFromImages(List<BufferedImage> images) {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata"); // Chemin vers le dossier tessdata
        tesseract.setLanguage("fra"); // Langue française

        StringBuilder extractedText = new StringBuilder();

        for (BufferedImage image : images) {
            try {
                String text = tesseract.doOCR(image);
                extractedText.append(text).append("\n");
            } catch (TesseractException e) {
                System.err.println("Erreur lors de l'extraction du texte de l'image : " + e.getMessage());
            }
        }

        return extractedText.toString();
    }
}
