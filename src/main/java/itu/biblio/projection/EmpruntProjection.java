package itu.biblio.projection;

import java.time.LocalDate;

public interface EmpruntProjection {
    Integer getEmpruntId();
    String getUtilisateurNom();
    String getUtilisateurPrenom();
    String getUtilisateurEmail();
    String getLivreTitre();
    String getLivreAuteur();
    LocalDate getDateEmprunt();
    LocalDate getDateRetour();
    String getStatutEmprunt();
    LocalDate getDateRetourEffective();
    Integer getJoursDeRetard();
} 