package tn.esprit.spring.kaddem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;

import java.util.List;

@Slf4j
@Service
public class DepartementServiceImpl implements IDepartementService {

	@Autowired
	DepartementRepository departementRepository;

	public List<Departement> retrieveAllDepartements() {
		log.info("Début de la récupération de tous les départements");
		long startTime = System.currentTimeMillis();

		try {
			List<Departement> departements = (List<Departement>) departementRepository.findAll();
			log.debug("Nombre de départements récupérés: {}", departements.size());
			log.info("Récupération de tous les départements terminée avec succès");
			return departements;
		} catch (Exception e) {
			log.error("Erreur lors de la récupération de tous les départements", e);
			throw e;
		} finally {
			log.trace("Temps d'exécution de retrieveAllDepartements: {} ms",
					System.currentTimeMillis() - startTime);
		}
	}

	public Departement addDepartement(Departement d) {
		log.info("Tentative d'ajout d'un nouveau département: {}", d.getNomDepart());

		try {
			Departement savedDepartement = departementRepository.save(d);
			log.info("Département ajouté avec succès - ID: {}", savedDepartement.getIdDepart());
			return savedDepartement;
		} catch (Exception e) {
			log.error("Erreur lors de l'ajout du département {}", d.getNomDepart(), e);
			throw e;
		}
	}

	public Departement updateDepartement(Departement d) {
		log.info("Mise à jour du département ID: {}", d.getIdDepart());

		if (!departementRepository.existsById(d.getIdDepart())) {
			log.warn("Tentative de mise à jour d'un département inexistant - ID: {}", d.getIdDepart());
			throw new RuntimeException("Département non trouvé");
		}

		try {
			Departement updatedDepartement = departementRepository.save(d);
			log.info("Département ID {} mis à jour avec succès", d.getIdDepart());
			return updatedDepartement;
		} catch (Exception e) {
			log.error("Erreur lors de la mise à jour du département ID: {}", d.getIdDepart(), e);
			throw e;
		}
	}

	public Departement retrieveDepartement(Integer idDepart) {
		log.debug("Récupération du département ID: {}", idDepart);

		try {
			Departement departement = departementRepository.findById(idDepart)
					.orElseThrow(() -> {
						log.warn("Département non trouvé - ID: {}", idDepart);
						return new RuntimeException("Département non trouvé");
					});

			log.debug("Département récupéré: {}", departement.getNomDepart());
			return departement;
		} catch (Exception e) {
			log.error("Erreur lors de la récupération du département ID: {}", idDepart, e);
			throw e;
		}
	}

	public void deleteDepartement(Integer idDepartement) {
		log.info("Tentative de suppression du département ID: {}", idDepartement);

		try {
			Departement d = retrieveDepartement(idDepartement);
			departementRepository.delete(d);
			log.info("Département ID {} supprimé avec succès", idDepartement);
		} catch (Exception e) {
			log.error("Erreur lors de la suppression du département ID: {}", idDepartement, e);
			throw e;
		}
	}

	@Override
	public boolean departementExistsByName(String nomDepart) {
		log.debug("Vérification existence département par nom: {}", nomDepart);
		return departementRepository.existsByNomDepart(nomDepart);
	}
}