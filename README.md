
## Fonctionnalités

- **Inscription d'un client** : Le client soumet ses informations personnelles, telles que nom, prénom, date de naissance, adresse, etc.
- **Upload du document de la carte d'identité** : Le fichier CNI du client est téléchargé sur Amazon S3, en utilisant un nom de fichier basé sur l'UUID du client.
- **Stockage dans S3** : Le document est stocké dans un bucket S3 sécurisé.
- **Communication avec un autre microservice** : Ce microservice peut envoyer des informations à un autre microservice pour vérifier les informations du client en fonction du document CNI.

## Prérequis

Avant de commencer, assurez-vous d'avoir les outils suivants installés :

- [Java 17+](https://adoptopenjdk.net/)
- [Maven](https://maven.apache.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [AWS SDK](https://aws.amazon.com/sdk-for-java/)
- [Postman](https://www.postman.com/) (pour tester les API)

### Variables d'environnement

Ce microservice utilise Amazon S3 pour stocker les fichiers d'identité des clients. Vous devez configurer un fichier de propriétés pour les informations d'identification AWS.

#### Création du fichier `aws-credentials.properties`

1. Créez un fichier nommé `aws-credentials.properties` dans le répertoire `src/main/resources` de votre projet.

2. Ajoutez les informations suivantes dans ce fichier (remplacez les valeurs par vos propres informations AWS) :

```properties
aws.s3.region=us-east-1
aws.s3.bucket-name=my-bucket-name
aws.s3.access-key=AKIAEXAMPLEACCESSKEY
aws.s3.secret-key=EXAMPLESECRETKEY
