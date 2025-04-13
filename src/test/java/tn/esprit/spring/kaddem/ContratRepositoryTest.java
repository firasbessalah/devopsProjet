package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Specialite;
import tn.esprit.spring.kaddem.repositories.ContratRepository;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ContratRepositoryTest {

    @Autowired
    private ContratRepository contratRepository;

    @Test
    void testSaveAndFindContrat() {
        Contrat contrat = new Contrat();
        contrat.setDateDebutContrat(new Date(2024 - 1900, 0, 1));
        contrat.setDateFinContrat(new Date(2024 - 1900, 11, 31));
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        contrat.setMontantContrat(300);

        Contrat saved = contratRepository.save(contrat);

        Contrat found = contratRepository.findById(saved.getIdContrat()).orElse(null);
        assertNotNull(found);
        assertEquals(Specialite.IA, found.getSpecialite());
        assertEquals(300, found.getMontantContrat());
        assertFalse(found.getArchive());
    }

    @Test
    void testFindAllContrats() {
        Contrat contrat1 = new Contrat(new Date(2024 - 1900, 0, 1), new Date(2024 - 1900, 11, 31), Specialite.IA, false, 300);
        Contrat contrat2 = new Contrat(new Date(2024 - 1900, 0, 1), new Date(2024 - 1900, 11, 31), Specialite.CLOUD, true, 400);

        contratRepository.save(contrat1);
        contratRepository.save(contrat2);

        List<Contrat> contrats = contratRepository.findAll();
        assertEquals(2, contrats.size());
        assertTrue(contrats.stream().anyMatch(c -> c.getSpecialite() == Specialite.IA));
        assertTrue(contrats.stream().anyMatch(c -> c.getSpecialite() == Specialite.CLOUD));
    }

    @Test
    void testUpdateContrat() {
        Contrat contrat = new Contrat();
        contrat.setDateDebutContrat(new Date(2024 - 1900, 0, 1));
        contrat.setDateFinContrat(new Date(2024 - 1900, 11, 31));
        contrat.setSpecialite(Specialite.SECURITE);
        contrat.setArchive(false);
        contrat.setMontantContrat(450);

        Contrat saved = contratRepository.save(contrat);

        saved.setMontantContrat(500);
        saved.setArchive(true);
        contratRepository.save(saved);

        Contrat updated = contratRepository.findById(saved.getIdContrat()).orElse(null);
        assertNotNull(updated);
        assertEquals(500, updated.getMontantContrat());
        assertTrue(updated.getArchive());
    }

    @Test
    void testDeleteContrat() {
        Contrat contrat = new Contrat();
        contrat.setDateDebutContrat(new Date(2024 - 1900, 0, 1));
        contrat.setDateFinContrat(new Date(2024 - 1900, 11, 31));
        contrat.setSpecialite(Specialite.RESEAUX);
        contrat.setArchive(false);
        contrat.setMontantContrat(350);

        Contrat saved = contratRepository.save(contrat);
        Integer id = saved.getIdContrat();

        contratRepository.deleteById(id);

        assertFalse(contratRepository.existsById(id));
    }

    @Test
    void testNbContratsValides() {
        Contrat contrat = new Contrat();
        contrat.setDateDebutContrat(new Date(2024 - 1900, 0, 1));
        contrat.setDateFinContrat(new Date(2024 - 1900, 11, 31));
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(true);
        contrat.setMontantContrat(300);

        contratRepository.save(contrat);

        Integer count = contratRepository.getnbContratsValides(
                new Date(2023 - 1900, 0, 1), new Date(2025 - 1900, 11, 31));

        assertEquals(1, count);
    }

    @Test
    void testNbContratsValides_NoMatches() {
        Contrat contrat = new Contrat();
        contrat.setDateDebutContrat(new Date(2024 - 1900, 0, 1));
        contrat.setDateFinContrat(new Date(2024 - 1900, 11, 31));
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false); // Not archived
        contrat.setMontantContrat(300);

        contratRepository.save(contrat);

        Integer count = contratRepository.getnbContratsValides(
                new Date(2023 - 1900, 0, 1), new Date(2025 - 1900, 11, 31));

        assertEquals(0, count);
    }
}