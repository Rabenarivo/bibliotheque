package itu.biblio.projection;

public interface ReservatioProjection {

     Integer reservationId();
     String utilisateurNom();
     String utilisateurPrenom();
     String utilisateurEmail();
     String livreTitre();
     String livreAuteur();
     String dateReservation();
}