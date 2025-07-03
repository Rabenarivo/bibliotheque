package itu.biblio.services;

import itu.biblio.entities.TypeEmprunt;
import itu.biblio.repositories.TypeEmpruntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeEmpruntService {
    @Autowired
    private TypeEmpruntRepository typeEmpruntRepository;

    public List<TypeEmprunt> getAllTypeEmprunts() {
        return typeEmpruntRepository.findAll();
    }

    public Optional<TypeEmprunt> getTypeEmpruntById(Integer id) {
        return typeEmpruntRepository.findById(id);
    }

    public TypeEmprunt saveTypeEmprunt(TypeEmprunt typeEmprunt) {
        return typeEmpruntRepository.save(typeEmprunt);
    }

    public void deleteTypeEmprunt(Integer id) {
        typeEmpruntRepository.deleteById(id);
    }
} 