package tn.esprit.spring.kaddem.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.services.IDepartementService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/departement")
public class DepartementRestController {
	IDepartementService departementService;

	// http://localhost:8089/Kaddem/departement/retrieve-all-departements
	@GetMapping("/retrieve-all-departements")
	public List<Departement> getDepartements() {
		log.info("Début récupération tous les départements");
		List<Departement> listDepartements = departementService.retrieveAllDepartements();
		log.info("Fin récupération - {} départements trouvés", listDepartements.size());
		return listDepartements;
	}

	// http://localhost:8089/Kaddem/departement/retrieve-departement/8
	@GetMapping("/retrieve-departement/{departement-id}")
	public Departement retrieveDepartement(@PathVariable("departement-id") Integer departementId) {
		log.debug("Récupération département ID: {}", departementId);
		Departement departement = departementService.retrieveDepartement(departementId);
		log.debug("Département trouvé: {}", departement.getNomDepart());
		return departement;
	}

	// http://localhost:8089/Kaddem/departement/add-departement
	@PostMapping("/add-departement")
	public Departement addDepartement(@RequestBody Departement d) {
		log.info("Ajout nouveau département: {}", d.getNomDepart());
		Departement departement = departementService.addDepartement(d);
		log.info("Département ajouté - ID: {}", departement.getIdDepart());
		return departement;
	}

	// http://localhost:8089/Kaddem/departement/remove-departement/1
	@DeleteMapping("/remove-departement/{departement-id}")
	public void removeDepartement(@PathVariable("departement-id") Integer departementId) {
		log.warn("Suppression département ID: {}", departementId);
		departementService.deleteDepartement(departementId);
		log.warn("Département ID {} supprimé", departementId);
	}

	// http://localhost:8089/Kaddem/departement/update-departement
	@PutMapping("/update-departement")
	public Departement updateDepartement(@RequestBody Departement e) {
		log.info("Mise à jour département ID: {}", e.getIdDepart());
		Departement departement= departementService.updateDepartement(e);
		log.info("Département ID {} mis à jour", departement.getIdDepart());
		return departement;
	}

	// http://localhost:8089/Kaddem/departement/exists-by-name?name={name}
	@GetMapping("/exists-by-name")
	public ResponseEntity<Boolean> existsByName(@RequestParam String name) {
		log.info("Vérification existence département par nom: {}", name);
		return ResponseEntity.ok(departementService.departementExistsByName(name));
	}
}