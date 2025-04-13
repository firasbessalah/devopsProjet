package tn.esprit.spring.kaddem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.kaddem.entities.Contrat;

import java.util.Date;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Integer> {
    @Query("SELECT count(c) FROM Contrat c WHERE c.archive = true AND " +
            "(c.dateDebutContrat BETWEEN :startDate AND :endDate OR c.dateFinContrat BETWEEN :startDate AND :endDate)")
    Integer getnbContratsValides(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}