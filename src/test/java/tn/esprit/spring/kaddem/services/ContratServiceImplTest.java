package tn.esprit.spring.kaddem.services;

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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    void setUp() {
        // Configuration initiale des objets de test
        contrat = new Contrat();
        contrat.setIdContrat(1);
        contrat.setDateDebutContrat(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 6);
        contrat.setDateFinContrat(calendar.getTime());
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        contrat.setMontantContrat(3000);

        etudiant = new Etudiant();
        etudiant.setIdEtudiant(1);
        etudiant.setNomE("Doe");
        etudiant.setPrenomE("John");
        etudiant.setContrats(new HashSet<>());
    }

    @Test
    void testAffectContratToEtudiant_Success() {
        // Given
        when(etudiantRepository.findByNomEAndPrenomE("Doe", "John")).thenReturn(etudiant);
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
        when(contratRepository.save(any(Contrat.class))).thenReturn(contrat);

        // When
        Contrat result = contratService.affectContratToEtudiant(1, "Doe", "John");

        // Then
        assertNotNull(result);
        assertEquals(etudiant, result.getEtudiant());
        verify(etudiantRepository, times(1)).findByNomEAndPrenomE("Doe", "John");
        verify(contratRepository, times(1)).findById(1);
        verify(contratRepository, times(1)).save(contrat);
    }

    @Test
    void testAffectContratToEtudiant_TooManyContracts() {
        // Given
        Set<Contrat> contratsActifs = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            Contrat c = new Contrat();
            c.setIdContrat(i + 10);
            c.setArchive(false);
            contratsActifs.add(c);
        }
        etudiant.setContrats(contratsActifs);

        when(etudiantRepository.findByNomEAndPrenomE("Doe", "John")).thenReturn(etudiant);
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));

        // When
        Contrat result = contratService.affectContratToEtudiant(1, "Doe", "John");

        // Then
        assertNotNull(result);
        // Le contrat ne devrait pas être affecté à l'étudiant
        assertNull(result.getEtudiant());
        verify(contratRepository, never()).save(any(Contrat.class));
    }

    @Test
    void testAffectContratToEtudiant_EtudiantNotFound() {
        // Given
        when(etudiantRepository.findByNomEAndPrenomE("Unknown", "Student")).thenReturn(null);
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));

        // When
        Contrat result = contratService.affectContratToEtudiant(1, "Unknown", "Student");

        // Then
        assertNotNull(result);
        assertNull(result.getEtudiant());
        verify(contratRepository, never()).save(any(Contrat.class));
    }

    @Test
    void testAffectContratToEtudiant_ContratNotFound() {
        // Given
        when(etudiantRepository.findByNomEAndPrenomE("Doe", "John")).thenReturn(etudiant);
        when(contratRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Contrat result = contratService.affectContratToEtudiant(999, "Doe", "John");

        // Then
        assertNull(result);
        verify(contratRepository, never()).save(any(Contrat.class));
    }

    @Test
    void testRetrieveAndUpdateStatusContrat() {
        // Given
        List<Contrat> contrats = new ArrayList<>();

        // Contrat expiré (15 jours dans le passé)
        Contrat contratExpire = new Contrat();
        contratExpire.setIdContrat(1);
        Calendar calExpire = Calendar.getInstance();
        calExpire.add(Calendar.DAY_OF_MONTH, -15);
        contratExpire.setDateFinContrat(calExpire.getTime());
        contratExpire.setArchive(false);
        contrats.add(contratExpire);

        // Contrat qui expire aujourd'hui
        Contrat contratExpireAujourdhui = new Contrat();
        contratExpireAujourdhui.setIdContrat(2);
        contratExpireAujourdhui.setDateFinContrat(new Date());
        contratExpireAujourdhui.setArchive(false);
        contrats.add(contratExpireAujourdhui);

        // Contrat qui expire dans 15 jours exactement
        Contrat contratExpireDans15Jours = new Contrat();
        contratExpireDans15Jours.setIdContrat(3);
        Calendar calFutur = Calendar.getInstance();
        calFutur.add(Calendar.DAY_OF_MONTH, 15);
        contratExpireDans15Jours.setDateFinContrat(calFutur.getTime());
        contratExpireDans15Jours.setArchive(false);
        contrats.add(contratExpireDans15Jours);

        // Contrat futur
        Contrat contratFutur = new Contrat();
        contratFutur.setIdContrat(4);
        Calendar calFutur2 = Calendar.getInstance();
        calFutur2.add(Calendar.MONTH, 1);
        contratFutur.setDateFinContrat(calFutur2.getTime());
        contratFutur.setArchive(false);
        contrats.add(contratFutur);

        // Contrat déjà archivé
        Contrat contratDejaArchive = new Contrat();
        contratDejaArchive.setIdContrat(5);
        contratDejaArchive.setDateFinContrat(calExpire.getTime());
        contratDejaArchive.setArchive(true);
        contrats.add(contratDejaArchive);

        // Contrat sans date de fin
        Contrat contratSansDateFin = new Contrat();
        contratSansDateFin.setIdContrat(6);
        contratSansDateFin.setDateFinContrat(null);
        contratSansDateFin.setArchive(false);
        contrats.add(contratSansDateFin);

        when(contratRepository.findAll()).thenReturn(contrats);
        when(contratRepository.save(any(Contrat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        contratService.retrieveAndUpdateStatusContrat();

        // Then
        // Les contrats expirés et ceux qui expirent aujourd'hui devraient être archivés
        verify(contratRepository, times(1)).save(contratExpire);
        verify(contratRepository, times(1)).save(contratExpireAujourdhui);

        // Vérifier que les contrats ont été archivés
        assertTrue(contratExpire.getArchive());
        assertTrue(contratExpireAujourdhui.getArchive());

        // Les autres contrats ne devraient pas être archivés ou modifiés
        assertFalse(contratExpireDans15Jours.getArchive());
        assertFalse(contratFutur.getArchive());
        assertTrue(contratDejaArchive.getArchive());

        // Les contrats déjà archivés ou sans date de fin ne devraient pas être sauvegardés
        verify(contratRepository, never()).save(contratDejaArchive);
        verify(contratRepository, never()).save(contratSansDateFin);
    }

    @Test
    void testGetChiffreAffaireEntreDeuxDates() {
        // Given
        List<Contrat> contrats = new ArrayList<>();

        // Contrat IA
        Contrat contratIA = new Contrat();
        contratIA.setSpecialite(Specialite.IA);
        contrats.add(contratIA);

        // Contrat CLOUD
        Contrat contratCLOUD = new Contrat();
        contratCLOUD.setSpecialite(Specialite.CLOUD);
        contrats.add(contratCLOUD);

        // Contrat RESEAUX
        Contrat contratRESEAUX = new Contrat();
        contratRESEAUX.setSpecialite(Specialite.RESEAUX);
        contrats.add(contratRESEAUX);

        // Contrat SECURITE
        Contrat contratSECURITE = new Contrat();
        contratSECURITE.setSpecialite(Specialite.SECURITE);
        contrats.add(contratSECURITE);

        when(contratRepository.findAll()).thenReturn(contrats);

        // Période d'un mois (30 jours)
        Calendar startCalendar = Calendar.getInstance();
        Date startDate = startCalendar.getTime();

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.MONTH, 1);
        Date endDate = endCalendar.getTime();

        // When
        float result = contratService.getChiffreAffaireEntreDeuxDates(startDate, endDate);

        // Then
        // Calcul attendu: (1 mois * (300 + 400 + 350 + 450)) = 1500
        assertEquals(1500.0f, result, 0.01);
        verify(contratRepository, times(1)).findAll();
    }

    @Test
    void testGetChiffreAffaireEntreDeuxDates_MultipleMonths() {
        // Given
        List<Contrat> contrats = new ArrayList<>();

        // Un contrat de chaque spécialité
        Contrat contratIA = new Contrat();
        contratIA.setSpecialite(Specialite.IA);
        contrats.add(contratIA);

        Contrat contratCLOUD = new Contrat();
        contratCLOUD.setSpecialite(Specialite.CLOUD);
        contrats.add(contratCLOUD);

        Contrat contratRESEAUX = new Contrat();
        contratRESEAUX.setSpecialite(Specialite.RESEAUX);
        contrats.add(contratRESEAUX);

        Contrat contratSECURITE = new Contrat();
        contratSECURITE.setSpecialite(Specialite.SECURITE);
        contrats.add(contratSECURITE);

        when(contratRepository.findAll()).thenReturn(contrats);

        // Période de 3 mois
        Calendar startCalendar = Calendar.getInstance();
        Date startDate = startCalendar.getTime();

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.MONTH, 3);
        Date endDate = endCalendar.getTime();

        // When
        float result = contratService.getChiffreAffaireEntreDeuxDates(startDate, endDate);

        // Then
        // Calcul attendu: (3 mois * (300 + 400 + 350 + 450)) = 4500
        assertEquals(4500.0f, result, 0.01);
        verify(contratRepository, times(1)).findAll();
    }

    @Test
    void testNbContratsValides() {
        // Given
        Date startDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();
        Integer expectedCount = 5;

        when(contratRepository.getnbContratsValides(startDate, endDate)).thenReturn(expectedCount);

        // When
        Integer result = contratService.nbContratsValides(startDate, endDate);

        // Then
        assertEquals(expectedCount, result);
        verify(contratRepository, times(1)).getnbContratsValides(startDate, endDate);
    }
}