package itu.biblio.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "email")
    private String email;

    @Column(name = "mdp", length = 50)
    private String mdp;

    @Column(name = "est_admin")
    private Boolean estAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adherant")
    private Adherant idAdherant;

    @Column(name = "quota_actuel")
    private Integer quotaActuel;

    public boolean isAdmin() {
        return Boolean.TRUE.equals(estAdmin);
    }
    
    public void initialiserQuota() {
        if (this.idAdherant != null && this.idAdherant.getNbrLivrePret() != null) {
            this.quotaActuel = this.idAdherant.getNbrLivrePret();
        }
    }
    
    public void diminuerQuota() {
        if (this.quotaActuel != null && this.quotaActuel > 0) {
            this.quotaActuel--;
        }
    }
    
    public void augmenterQuota() {
        if (this.idAdherant != null && this.idAdherant.getNbrLivrePret() != null) {
            if (this.quotaActuel == null) {
                this.quotaActuel = this.idAdherant.getNbrLivrePret();
            } else if (this.quotaActuel < this.idAdherant.getNbrLivrePret()) {
                this.quotaActuel++;
            }
        }
    }
    
    public boolean peutEmprunter() {
        return this.quotaActuel != null && this.quotaActuel > 0;
    }
}

