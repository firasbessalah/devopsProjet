package tn.esprit.spring.kaddem.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EtudiantServiceImpl implements IEtudiantService{
	@Autowired
	EtudiantRepository etudiantRepository ;
	@Autowired
	ContratRepository contratRepository;
	@Autowired
	EquipeRepository equipeRepository;
    @Autowired
    DepartementRepository departementRepository;
	public List<Etudiant> retrieveAllEtudiants(){
	//	log.info("Retrieving all students");
		List<Etudiant> etudiants = (List<Etudiant>) etudiantRepository.findAll();
		//log.debug("Found {} students", etudiants.size());
	return etudiants;
	}

	public Etudiant addEtudiant (Etudiant e){
	//	log.info("Adding new student: {}", e);
		Etudiant savedEtudiant = etudiantRepository.save(e);
	//	log.info("Student added successfully with ID: {}", savedEtudiant.getIdEtudiant());
		return savedEtudiant;
	}

	public Etudiant updateEtudiant (Etudiant e){
	//	log.info("Updating student with ID: {}", e.getIdEtudiant());
		Etudiant updatedEtudiant = etudiantRepository.save(e);
	//	log.info("Student with ID {} updated successfully", updatedEtudiant.getIdEtudiant());
		return updatedEtudiant;
	}

	public Etudiant retrieveEtudiant(Integer  idEtudiant){
	//	log.debug("Retrieving student with ID: {}", idEtudiant);
		Optional<Etudiant> etudiant = etudiantRepository.findById(idEtudiant);
		if (etudiant.isEmpty()) {
		//	log.error("Student not found with ID: {}", idEtudiant);
			throw new RuntimeException("Student not found");
		}
		return etudiant.get();
	}

	public void removeEtudiant(Integer idEtudiant){
		//log.warn("Deleting student with ID: {}", idEtudiant);
		Etudiant e = retrieveEtudiant(idEtudiant);
		etudiantRepository.delete(e);
		//log.info("Student with ID {} deleted successfully", idEtudiant);
	}

	public void assignEtudiantToDepartement (Integer etudiantId, Integer departementId){
		//log.info("Assigning student {} to department {}", etudiantId, departementId);
		Etudiant etudiant = etudiantRepository.findById(etudiantId)
				.orElseThrow(() -> {
					//log.error("Student not found with ID: {}", etudiantId);
					return new RuntimeException("Student not found");
				});

		Departement departement = departementRepository.findById(departementId)
				.orElseThrow(() -> {
					//log.error("Department not found with ID: {}", departementId);
					return new RuntimeException("Department not found");
				});

		etudiant.setDepartement(departement);
		etudiantRepository.save(etudiant);
		log.info("Student {} successfully assigned to department {}",
				etudiant.getIdEtudiant(), departement.getIdDepart());
	}
	@Transactional
	public Etudiant addAndAssignEtudiantToEquipeAndContract(Etudiant e, Integer idContrat, Integer idEquipe){
		log.info("Adding and assigning student to contract {} and team {}", idContrat, idEquipe);

		Contrat contrat = contratRepository.findById(idContrat)
				.orElseThrow(() -> {
					log.error("Contract not found with ID: {}", idContrat);
					return new RuntimeException("Contract not found");
				});

		Equipe equipe = equipeRepository.findById(idEquipe)
				.orElseThrow(() -> {
					log.error("Team not found with ID: {}", idEquipe);
					return new RuntimeException("Team not found");
				});

		contrat.setEtudiant(e);
		equipe.getEtudiants().add(e);

		log.info("Student {} successfully assigned to contract {} and team {}",
				e.getIdEtudiant(), contrat.getIdContrat(), equipe.getIdEquipe());
		return etudiantRepository.save(e);
	}

	public 	List<Etudiant> getEtudiantsByDepartement (Integer idDepartement){
		log.debug("Retrieving students by department ID: {}", idDepartement);
		return etudiantRepository.findEtudiantsByDepartement_IdDepart(idDepartement);	}
}
