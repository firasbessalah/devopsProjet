package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Specialite;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.services.ContratServiceImpl;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContratServiceImplTest {

    @Mock
    private ContratRepository contratRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private ContratServiceImpl contratService;

    private Contrat contrat;
    private Etudiant etudiant;
    private Date startDate;
    private Date endDate;

    @BeforeEach
    void setUp() {
        startDate = new Date(2024 - 1900, 0, 1); // 2024-01-01
        endDate = new Date(2024 - 1900, 11, 31); // 2024-12-31
        contrat = new Contrat();
        contrat.setIdContrat(1);
        contrat.setDateDebutContrat(startDate);
        contrat.setDateFinContrat(endDate);
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        contrat.setMontantContrat(300);

        etudiant = new Etudiant();
        etudiant.setIdEtudiant(1);
        etudiant.setNomE("John");
        etudiant.setPrenomE("Doe");
        etudiant.setContrats(new HashSet<>());
    }



    @Test
    void testAddContrat() {
        when(contratRepository.save(contrat)).thenReturn(contrat);

        Contrat result = contratService.addContrat(contrat);

        assertNotNull(result);
        assertEquals(300, result.getMontantContrat());
        verify(contratRepository).save(contrat);
    }

    @Test
    void testUpdateContrat() {
        when(contratRepository.save(contrat)).thenReturn(contrat);

        Contrat result = contratService.updateContrat(contrat);

        assertNotNull(result);
        assertEquals(Specialite.IA, result.getSpecialite());
        verify(contratRepository).save(contrat);
    }

    @Test
    void testRetrieveContrat() {
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));

        Contrat result = contratService.retrieveContrat(1);

        assertNotNull(result);
        assertEquals(1, result.getIdContrat());
        verify(contratRepository).findById(1);
    }

    @Test
    void testRetrieveContratNotFound() {
        when(contratRepository.findById(1)).thenReturn(Optional.empty());

        Contrat result = contratService.retrieveContrat(1);

        assertNull(result);
        verify(contratRepository).findById(1);
    }

    @Test
    void testRemoveContrat() {
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
        doNothing().when(contratRepository).delete(contrat);

        contratService.removeContrat(1);

        verify(contratRepository).findById(1);
        verify(contratRepository).delete(contrat);
    }






    @Test
    void testNbContratsValides() {
        when(contratRepository.getnbContratsValides(startDate, endDate)).thenReturn(5);

        Integer result = contratService.nbContratsValides(startDate, endDate);

        assertEquals(5, result);
        verify(contratRepository).getnbContratsValides(startDate, endDate);
    }

    @Test
    void testGetChiffreAffaireEntreDeuxDates() {
        when(contratRepository.findAll()).thenReturn(Collections.singletonList(contrat));

        float result = contratService.getChiffreAffaireEntreDeuxDates(startDate, endDate);

        float expected = (365.0f / 30) * 300; // ~12.17 months * 300
        assertEquals(expected, result, 0.1f); // Increased delta for stability
        verify(contratRepository).findAll();
    }


}