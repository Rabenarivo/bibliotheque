package itu.biblio.projection;

public interface LivreDisponibiliteProjection {
    Integer getLivreId();
    String getTitre();
    Integer getTotalExemplaires();
    Integer getExemplairesIndisponibles();
    Integer getExemplairesDisponibles();
}