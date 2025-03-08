package sn.afribnpl.ocrservice.dto;

import java.time.LocalDate;


public class Client {
    private String id ;
    private String nom ;
    private String prenom ;
    private String email ;
    private String password ;
    private LocalDate birthday ;
    private String adresse ;
    private String urlCni ;
    private boolean cniVerified = false ;
    private boolean emailVerified = false;

    public Client(String id, String nom, String prenom, String email, String password, LocalDate birthday, String adresse, String urlCni, boolean cniVerified, boolean emailVerified) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.adresse = adresse;
        this.urlCni = urlCni;
        this.cniVerified = cniVerified;
        this.emailVerified = emailVerified;
    }

    public Client() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getUrlCni() {
        return urlCni;
    }

    public void setUrlCni(String urlCni) {
        this.urlCni = urlCni;
    }

    public boolean isCniVerified() {
        return cniVerified;
    }

    public void setCniVerified(boolean cniVerified) {
        this.cniVerified = cniVerified;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
