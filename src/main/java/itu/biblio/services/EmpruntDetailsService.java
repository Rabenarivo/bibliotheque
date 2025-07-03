package itu.biblio.services;

import itu.biblio.entities.EmpruntDetails;
import itu.biblio.repositories.EmpruntDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpruntDetailsService {
    @Autowired
    private EmpruntDetailsRepository empruntDetailsRepository;

    public List<EmpruntDetails> getAllEmpruntDetails() {
        return empruntDetailsRepository.findAll();
    }

    public Optional<EmpruntDetails> getEmpruntDetailsById(Integer id) {
        return empruntDetailsRepository.findById(id);
    }

    public EmpruntDetails saveEmpruntDetails(EmpruntDetails empruntDetails) {
        return empruntDetailsRepository.save(empruntDetails);
    }

    public void deleteEmpruntDetails(Integer id) {
        empruntDetailsRepository.deleteById(id);
    }
} 