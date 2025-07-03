package itu.biblio.projection;

public interface ListeLivreParAdherantProjection {
    Integer getId();
    String getTitre();
    String getAuteur();
    Integer getAge();
    String getImage();
    Integer getExemplairesDisponibles();  // Add this field
}