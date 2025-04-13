package tn.esprit.spring.kaddem.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Specialite;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ContratServiceImpl implements IContratService {

	@Autowired
	private ContratRepository contratRepository;

	@Autowired
	private EtudiantRepository etudiantRepository;

	@Override
	public List<Contrat> retrieveAllContrats() {
		return contratRepository.findAll();
	}

	@Override
	public Contrat updateContrat(Contrat ce) {
		return contratRepository.save(ce);
	}

	@Override
	public Contrat addContrat(Contrat ce) {
		return contratRepository.save(ce);
	}

	@Override
	public Contrat retrieveContrat(Integer idContrat) {
		return contratRepository.findById(idContrat).orElse(null);
	}

	@Override
	public void removeContrat(Integer idContrat) {
		Contrat c = retrieveContrat(idContrat);
		if (c != null) {
			contratRepository.delete(c);
		}
	}

	@Override
	public Contrat affectContratToEtudiant(Integer idContrat, String nomE, String prenomE) {
		Etudiant e = etudiantRepository.findByNomEAndPrenomE(nomE, prenomE);
		Contrat ce = contratRepository.findById(idContrat).orElse(null);
		if (e == null || ce == null) {
			return ce;
		}
		Set<Contrat> contrats = e.getContrats();
		long nbContratsActifs = contrats.stream()
				.filter(c -> c.getArchive() == null || !c.getArchive())
				.count();
		if (nbContratsActifs <= 4) {
			ce.setEtudiant(e);
			contratRepository.save(ce);
		}
		return ce;
	}

	@Override
	public Integer nbContratsValides(Date startDate, Date endDate) {
		return contratRepository.getnbContratsValides(startDate, endDate);
	}

	@Override
	public void retrieveAndUpdateStatusContrat() {
		List<Contrat> contrats = contratRepository.findAll();
		List<Contrat> contrats15j = new ArrayList<>();
		List<Contrat> contratsAarchiver = new ArrayList<>();
		Date dateSysteme = new Date();
		for (Contrat contrat : contrats) {
			if (contrat.getDateFinContrat() == null || contrat.getArchive() != null && contrat.getArchive()) {
				continue;
			}
			long differenceInTime = dateSysteme.getTime() - contrat.getDateFinContrat().getTime();
			long differenceInDays = differenceInTime / (1000 * 60 * 60 * 24);
			if (differenceInDays == 15) {
				contrats15j.add(contrat);
				log.info("Contrat nearing end (15 days): {}", contrat);
			}
			if (differenceInDays >= 0) {
				contratsAarchiver.add(contrat);
				contrat.setArchive(true);
				contratRepository.save(contrat);
			}
		}
	}

	@Override
	public float getChiffreAffaireEntreDeuxDates(Date startDate, Date endDate) {
		float differenceInTime = endDate.getTime() - startDate.getTime();
		float differenceInDays = differenceInTime / (1000 * 60 * 60 * 24);
		float differenceInMonths = differenceInDays / 30;
		List<Contrat> contrats = contratRepository.findAll();
		float chiffreAffaire = 0;
		for (Contrat contrat : contrats) {
			if (contrat.getSpecialite() == Specialite.IA) {
				chiffreAffaire += (differenceInMonths * 300);
			} else if (contrat.getSpecialite() == Specialite.CLOUD) {
				chiffreAffaire += (differenceInMonths * 400);
			} else if (contrat.getSpecialite() == Specialite.RESEAUX) {
				chiffreAffaire += (differenceInMonths * 350);
			} else if (contrat.getSpecialite() == Specialite.SECURITE) {
				chiffreAffaire += (differenceInMonths * 450);
			}
		}
		return chiffreAffaire;
	}
}