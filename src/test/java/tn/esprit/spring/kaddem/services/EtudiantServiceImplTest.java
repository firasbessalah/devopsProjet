package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.*;
import tn.esprit.spring.kaddem.repositories.*;

import javax.transaction.Transactional;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EtudiantServiceImplTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private ContratRepository contratRepository;

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private DepartementRepository departementRepository;

    @InjectMocks
    private EtudiantServiceImpl etudiantService;

    private Etudiant etudiant;
    private Contrat contrat;
    private Equipe equipe;
    private Departement departement;

    @BeforeEach
    void setUp() {
        // Initialisation des objets de test
        etudiant = new Etudiant();
        etudiant.setIdEtudiant(1);
        etudiant.setNomE("Doe");
        etudiant.setPrenomE("John");

        contrat = new Contrat();
        contrat.setIdContrat(1);

        equipe = new Equipe();
        equipe.setIdEquipe(1);
        equipe.setEtudiants(new HashSet<>()); // Important pour addAndAssignEtudiant

        departement = new Departement();
        departement.setIdDepart(1);
    }

    // === Tests pour retrieveAllEtudiants() ===
    @Test
    void testRetrieveAllEtudiants() {
        // Arrange
        List<Etudiant> etudiants = Collections.singletonList(etudiant);
        when(etudiantRepository.findAll()).thenReturn(etudiants);

        // Act
        List<Etudiant> result = etudiantService.retrieveAllEtudiants();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Doe", result.get(0).getNomE());
        verify(etudiantRepository, times(1)).findAll();
    }

    // === Tests pour addEtudiant() ===
    @Test
    void testAddEtudiant() {
        // Arrange
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        // Act
        Etudiant savedEtudiant = etudiantService.addEtudiant(etudiant);

        // Assert
        assertNotNull(savedEtudiant);
        assertEquals(1, savedEtudiant.getIdEtudiant());
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    // === Tests pour retrieveEtudiant() ===
    @Test
    void testRetrieveEtudiantFound() {
        // Arrange
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));

        // Act
        Etudiant foundEtudiant = etudiantService.retrieveEtudiant(1);

        // Assert
        assertEquals("Doe", foundEtudiant.getNomE());
        verify(etudiantRepository, times(1)).findById(1);
    }

    @Test
    void testRetrieveEtudiantNotFound() {
        // Arrange
        when(etudiantRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> etudiantService.retrieveEtudiant(99));
        verify(etudiantRepository, times(1)).findById(99);
    }

    // === Tests pour updateEtudiant() ===
    @Test
    void testUpdateEtudiant() {
        // Arrange
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        // Act
        Etudiant updatedEtudiant = etudiantService.updateEtudiant(etudiant);

        // Assert
        assertEquals(1, updatedEtudiant.getIdEtudiant());
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    // === Tests pour removeEtudiant() ===
    @Test
    void testRemoveEtudiant() {
        // Arrange
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));
        doNothing().when(etudiantRepository).delete(etudiant);

        // Act
        etudiantService.removeEtudiant(1);

        // Assert
        verify(etudiantRepository, times(1)).delete(etudiant);
    }

    // === Tests pour assignEtudiantToDepartement() ===
    @Test
    void testAssignEtudiantToDepartement() {
        // Arrange
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        // Act
        etudiantService.assignEtudiantToDepartement(1, 1);

        // Assert
        assertEquals(departement, etudiant.getDepartement());
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    // === Tests pour addAndAssignEtudiantToEquipeAndContract() ===
    @Test
    @Transactional
    void testAddAndAssignEtudiantToEquipeAndContract() {
        // Arrange
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        // Act
        Etudiant result = etudiantService.addAndAssignEtudiantToEquipeAndContract(etudiant, 1, 1);

        // Assert
        assertEquals(etudiant, contrat.getEtudiant());
        assertTrue(equipe.getEtudiants().contains(etudiant));
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    // === Tests pour getEtudiantsByDepartement() ===
    @Test
    void testGetEtudiantsByDepartement() {
        // Arrange
        List<Etudiant> etudiants = Collections.singletonList(etudiant);
        when(etudiantRepository.findEtudiantsByDepartement_IdDepart(1)).thenReturn(etudiants);

        // Act
        List<Etudiant> result = etudiantService.getEtudiantsByDepartement(1);

        // Assert
        assertEquals(1, result.size());
        verify(etudiantRepository, times(1)).findEtudiantsByDepartement_IdDepart(1);
    }
}