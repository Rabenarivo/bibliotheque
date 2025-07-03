package itu.biblio.services;

import itu.biblio.entities.Emprunt;
import itu.biblio.projection.EmpruntProjection;
import itu.biblio.repositories.EmpruntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
} 