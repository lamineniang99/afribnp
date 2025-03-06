package sn.afribnpl.clientservice.enitity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "clients")
@Getter @Setter @ToString @Builder
public class Client {

    @Id
    private String id ;
    @Column( nullable = false)
    private String nom ;
    @Column( nullable = false)
    private String prenom ;
    @Column( nullable = false, unique = true)
    private String email ;
    @Column( nullable = false)
    private String password ;
    @Column( nullable = false)
    private LocalDate birthday ;
    private String adresse ;
    private String urlCni ;
}
