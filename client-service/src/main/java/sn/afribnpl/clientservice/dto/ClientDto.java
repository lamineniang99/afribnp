package sn.afribnpl.clientservice.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString @Builder
public class ClientDto {

    private String id ;
    private String nom ;
    private String prenom ;
    private String email ;
    private LocalDate birthday ;
    private String adresse ;
    private String urlCni ;
}
