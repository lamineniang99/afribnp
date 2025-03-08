package sn.afribnpl.ocrservice.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AwsCredentialsLoader {
    public static Properties loadCredentials() {
        Properties properties = new Properties();
        try (InputStream input = AwsCredentialsLoader.class.getClassLoader().getResourceAsStream("aws-credentials.properties")) {
            if (input == null) {
                throw new RuntimeException("Fichier aws-credentials.properties introuvable dans le classpath.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier aws-credentials.properties", e);
        }
        return properties;
    }
}
