package tn.esprit.spring.kaddem.entities;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Contrat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idContrat;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date dateDebutContrat;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date dateFinContrat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Specialite specialite;

    private Boolean archive;

    private Integer montantContrat;

    public Contrat(Date dateDebutContrat, Date dateFinContrat, Specialite specialite, Boolean archive, Integer montantContrat) {
        this.dateDebutContrat = dateDebutContrat;
        this.dateFinContrat = dateFinContrat;
        this.specialite = specialite;
        this.archive = archive;
        this.montantContrat = montantContrat;
    }

    public void setEtudiant(Etudiant e) {
    }
}