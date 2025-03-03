package sn.afribnpl.clientservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @ToString @Builder
public class ClientRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom ;
    @NotBlank(message = "Le prenom est obligatoire")
    private String prenom ;
    @NotNull(message = "Le email est obligatoire")
    private String email ;
    private String password ;
    @NotNull(message = "La date de naissance est obligatoire")
    private LocalDate birthday ;
    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse ;
    @NotNull(message = "Le fichier CNI est obligatoire")
    private MultipartFile cni ;

}
