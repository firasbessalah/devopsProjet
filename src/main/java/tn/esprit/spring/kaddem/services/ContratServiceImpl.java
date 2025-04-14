package tn.esprit.spring.kaddem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Specialite;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
	public Contrat retrieveContrat(Integer idContrat) {
		return contratRepository.findById(idContrat).orElse(null);
	}

	@Override
	public Contrat addContrat(Contrat ce) {
		return contratRepository.save(ce);
	}

	@Override
	public Contrat updateContrat(Contrat ce) {
		if (contratRepository.existsById(ce.getIdContrat())) {
			return contratRepository.save(ce);
		}
		return null;
	}

	@Override
	public void removeContrat(Integer idContrat) {
		contratRepository.deleteById(idContrat);
	}

	@Override
	public Contrat affectContratToEtudiant(Integer idContrat, String nomE, String prenomE) {
		Etudiant etudiant = etudiantRepository.findByNomEAndPrenomE(nomE, prenomE);
		if (etudiant == null) {
			return contratRepository.findById(idContrat).orElse(null);
		}
		if (etudiant.getContrats().stream().filter(c -> !c.getArchive()).count() >= 5) {
			return contratRepository.findById(idContrat).orElse(null);
		}
		Contrat contrat = contratRepository.findById(idContrat).orElse(null);
		if (contrat != null) {
			contrat.setEtudiant(etudiant);
			return contratRepository.save(contrat);
		}
		return null;
	}

	@Override
	public float getChiffreAffaireEntreDeuxDates(Date startDate, Date endDate) {
		List<Contrat> contrats = contratRepository.findAll();
		float total = 0;

		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		int months = (endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)) * 12 +
				(endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH));
		if (months <= 0) {
			months = 1;
		}

		for (Contrat contrat : contrats) {
			if (contrat.getSpecialite() != null && !contrat.getArchive() &&
					!contrat.getDateFinContrat().before(startDate) &&
					!contrat.getDateDebutContrat().after(endDate)) {
				switch (contrat.getSpecialite()) {
					case IA:
						total += 300 * months;
						break;
					case CLOUD:
						total += 400 * months;
						break;
					case RESEAUX:
						total += 350 * months;
						break;
					case SECURITE:
						total += 450 * months;
						break;
				}
			}
		}
		return total;
	}

	@Override
	public void retrieveAndUpdateStatusContrat() {
		List<Contrat> contrats = contratRepository.findAll();
		Date today = new Date();
		Calendar fifteenDaysFromNow = Calendar.getInstance();
		fifteenDaysFromNow.add(Calendar.DAY_OF_MONTH, 15);

		for (Contrat contrat : contrats) {
			if (contrat.getDateFinContrat() != null && !contrat.getArchive()) {
				if (contrat.getDateFinContrat().before(today) ||
						contrat.getDateFinContrat().equals(today)) {
					contrat.setArchive(true);
					contratRepository.save(contrat);
				} else if (contrat.getDateFinContrat().before(fifteenDaysFromNow.getTime())) {
					System.out.println("Contrat nearing end (15 days): " + contrat.getIdContrat());
				}
			}
		}
	}

	@Override
	public Integer nbContratsValides(Date startDate, Date endDate) {
		return contratRepository.getnbContratsValides(startDate, endDate);
	}
}