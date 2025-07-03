package itu.biblio.projection;

public interface ReservationProjection {
     Integer getReservationId();
     String getUtilisateurNom();
     String getUtilisateurPrenom();
     String getUtilisateurEmail();
     String getLivreTitre();
     String getLivreAuteur();
     String getDateReservation();
}