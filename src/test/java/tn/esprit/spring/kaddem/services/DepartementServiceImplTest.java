package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class DepartementServiceImplTest {

    @Mock
    private DepartementRepository departementRepository;

    @InjectMocks
    private DepartementServiceImpl departementService;

    private Departement departement;

    @BeforeEach
    void setUp() {
        departement = new Departement();
        departement.setIdDepart(1);
        departement.setNomDepart("Informatique");
    }

    @Test
    void retrieveAllDepartements_shouldReturnListOfDepartements() {
        // Arrange
        Departement dep2 = new Departement(2, "Mathématiques");
        when(departementRepository.findAll()).thenReturn(Arrays.asList(departement, dep2));

        // Act
        List<Departement> result = departementService.retrieveAllDepartements();

        // Assert
        assertEquals(2, result.size());
        verify(departementRepository, times(1)).findAll();
    }

    @Test
    void retrieveAllDepartements_shouldLogTimeAndThrowExceptionOnError() {
        // Arrange
        when(departementRepository.findAll()).thenThrow(new RuntimeException("DB Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> departementService.retrieveAllDepartements());
        verify(departementRepository, times(1)).findAll();
    }

    @Test
    void addDepartement_shouldReturnSavedDepartement() {
        // Arrange
        when(departementRepository.save(any(Departement.class))).thenReturn(departement);

        // Act
        Departement newDep = new Departement();
        newDep.setNomDepart("Informatique");
        Departement result = departementService.addDepartement(newDep);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getIdDepart());
        verify(departementRepository, times(1)).save(any(Departement.class));
    }

    @Test
    void updateDepartement_shouldUpdateWhenDepartementExists() {
        // Arrange
        when(departementRepository.existsById(1)).thenReturn(true);
        when(departementRepository.save(any(Departement.class))).thenReturn(departement);

        // Act
        Departement result = departementService.updateDepartement(departement);

        // Assert
        assertEquals("Informatique", result.getNomDepart());
        verify(departementRepository, times(1)).save(any(Departement.class));
    }

    @Test
    void updateDepartement_shouldThrowExceptionWhenDepartementNotExists() {
        // Arrange
        when(departementRepository.existsById(99)).thenReturn(false);

        // Act & Assert
        Departement nonExistingDep = new Departement(99, "Non existant");
        assertThrows(RuntimeException.class, () -> departementService.updateDepartement(nonExistingDep));
        verify(departementRepository, never()).save(any(Departement.class));
    }

    @Test
    void retrieveDepartement_shouldReturnDepartementWhenExists() {
        // Arrange
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));

        // Act
        Departement result = departementService.retrieveDepartement(1);

        // Assert
        assertNotNull(result);
        assertEquals("Informatique", result.getNomDepart());
    }

    @Test
    void retrieveDepartement_shouldThrowExceptionWhenNotExists() {
        // Arrange
        when(departementRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> departementService.retrieveDepartement(99));
    }

    @Test
    void deleteDepartement_shouldDeleteWhenDepartementExists() {
        // Arrange
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        doNothing().when(departementRepository).delete(any(Departement.class));

        // Act
        departementService.deleteDepartement(1);

        // Assert
        verify(departementRepository, times(1)).delete(any(Departement.class));
    }

    @Test
    void departementExistsByName_shouldReturnTrueWhenExists() {
        // Arrange
        when(departementRepository.existsByNomDepart("Informatique")).thenReturn(true);

        // Act
        boolean result = departementService.departementExistsByName("Informatique");

        // Assert
        assertTrue(result);
    }

    @Test
    void departementExistsByName_shouldReturnFalseWhenNotExists() {
        // Arrange
        when(departementRepository.existsByNomDepart("Inconnu")).thenReturn(false);

        // Act
        boolean result = departementService.departementExistsByName("Inconnu");

        // Assert
        assertFalse(result);
    }
}