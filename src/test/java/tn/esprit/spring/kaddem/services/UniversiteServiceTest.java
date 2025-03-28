package tn.esprit.spring.kaddem.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;

class UniversiteServiceTest {

    @Mock
    private UniversiteRepository universiteRepository;

    @Mock
    private DepartementRepository departementRepository;

    @InjectMocks
    private UniversiteServiceImpl universiteService;

    private Universite universite;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        universite = new Universite(1, "Test University");
        universite.setDepartements(new HashSet<>());
    }

    @Test
    void testRetrieveAllUniversites() {
        when(universiteRepository.findAll()).thenReturn(List.of(universite));

        List<Universite> universites = universiteService.retrieveAllUniversites();

        assertNotNull(universites);
        assertEquals(1, universites.size());
        verify(universiteRepository, times(1)).findAll();
    }

    @Test
    void testAddUniversite() {
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        Universite savedUniv = universiteService.addUniversite(universite);

        assertNotNull(savedUniv);
        assertEquals("Test University", savedUniv.getNomUniv());
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testRetrieveUniversite() {
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));

        Universite retrievedUniv = universiteService.retrieveUniversite(1);

        assertNotNull(retrievedUniv);
        assertEquals(1, retrievedUniv.getIdUniv());
        verify(universiteRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteUniversite() {
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));
        doNothing().when(universiteRepository).delete(universite);

        universiteService.deleteUniversite(1);

        verify(universiteRepository, times(1)).delete(universite);
    }

    @Test
    void testAssignUniversiteToDepartement() {
        Departement departement = new Departement();
        departement.setIdDepart(1);

        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);

        universiteService.assignUniversiteToDepartement(1, 1);

        assertEquals(1, universite.getDepartements().size());
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testRetrieveDepartementsByUniversite() {
        Departement departement = new Departement();
        departement.setIdDepart(1);
        Set<Departement> departements = new HashSet<>();
        departements.add(departement);
        universite.setDepartements(departements);

        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));

        Set<Departement> retrievedDepartements = universiteService.retrieveDepartementsByUniversite(1);

        assertNotNull(retrievedDepartements);
        assertEquals(1, retrievedDepartements.size());
        verify(universiteRepository, times(1)).findById(1);
    }
}
