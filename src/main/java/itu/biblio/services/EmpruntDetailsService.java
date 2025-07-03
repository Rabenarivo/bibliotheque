package itu.biblio.services;

import itu.biblio.entities.EmpruntDetail;
import itu.biblio.repositories.EmpruntDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpruntDetailsService {
    @Autowired
    private EmpruntDetailsRepository empruntDetailsRepository;

    public List<EmpruntDetail> getAllEmpruntDetails() {
        return empruntDetailsRepository.findAll();
    }

    public Optional<EmpruntDetail> getEmpruntDetailsById(Integer id) {
        return empruntDetailsRepository.findById(id);
    }

    public EmpruntDetail saveEmpruntDetails(EmpruntDetail empruntDetails) {
        return empruntDetailsRepository.save(empruntDetails);
    }

    public void deleteEmpruntDetails(Integer id) {
        empruntDetailsRepository.deleteById(id);
    }

    public List<EmpruntDetail> getEmpruntDetailsByEmpruntId(Integer empruntId) {
        return empruntDetailsRepository.findByEmpruntId(empruntId);
    }
} 