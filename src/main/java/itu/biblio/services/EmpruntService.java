package itu.biblio.services;

import itu.biblio.entities.Emprunt;
import itu.biblio.projection.EmpruntProjection;
import itu.biblio.repositories.EmpruntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmpruntService {
    @Autowired
    private EmpruntRepository empruntRepository;

    public List<Emprunt> getAllEmprunts() {
        return empruntRepository.findAll();
    }

    public Optional<Emprunt> getEmpruntById(Integer id) {
        return empruntRepository.findById(id);
    }

    public Emprunt saveEmprunt(Emprunt emprunt) {
        return empruntRepository.save(emprunt);
    }

    public void deleteEmprunt(Integer id) {
        empruntRepository.deleteById(id);
    }

    public List<EmpruntProjection> getAllEmpruntsWithDetails() {
        return empruntRepository.findAllEmpruntsWithDetails();
    }

    public EmpruntProjection getEmpruntByIdWithDetails(Integer id) {
        return empruntRepository.findEmpruntByIdWithDetails(id).orElse(null);
    }

    public long countEmpruntsByStatut(String statut) {
        return empruntRepository.countByStatutEmprunt(statut);
    }

    public long countEmpruntsEnRetard() {
        return empruntRepository.countEmpruntsEnRetard();
    }

//retard
    public void detecterEtMettreAJourRetards() {
        List<Emprunt> empruntsEnCours = empruntRepository.findByStatutEmpruntIn(List.of("en_cours", "En cours"));
        LocalDate aujourdhui = LocalDate.now();
        
        for (Emprunt emprunt : empruntsEnCours) {
            if (emprunt.getDateRetour() != null && emprunt.getDateRetour().isBefore(aujourdhui)) {
                emprunt.setStatutEmprunt("retard");
                empruntRepository.save(emprunt);
            }
        }
    }

//get_reatard
    public List<Emprunt> getEmpruntsEnRetard() {
        return empruntRepository.findByStatutEmprunt("retard");
    }

//nbr_jours_reatard
    public int getJoursDeRetard(Integer empruntId) {
        Optional<Emprunt> empruntOpt = empruntRepository.findById(empruntId);
        if (empruntOpt.isPresent()) {
            Emprunt emprunt = empruntOpt.get();
            if (emprunt.getDateRetour() != null) {
                LocalDate aujourdhui = LocalDate.now();
                if (emprunt.getDateRetour().isBefore(aujourdhui)) {
                    return (int) emprunt.getDateRetour().until(aujourdhui).getDays();
                }
            }
        }
        return 0;
    }

//emprunt_retard
    public boolean isEmpruntEnRetard(Integer empruntId) {
        return getJoursDeRetard(empruntId) > 0;
    }
} 