package itu.biblio.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "genre_livre")
public class GenreLivre {
    @EmbeddedId
    private GenreLivreId id;

    @ManyToOne
    @MapsId("livreId")
    @JoinColumn(name = "livre_id")
    private Livre livre;

    @ManyToOne
    @MapsId("genreId")
    @JoinColumn(name = "genre_id")
    private Genre genre;
}

@Embeddable
class GenreLivreId implements Serializable {
    @Column(name = "livre_id")
    private Integer livreId;

    @Column(name = "genre_id")
    private Integer genreId;

    // Default constructor
    public GenreLivreId() {}

    public GenreLivreId(Integer livreId, Integer genreId) {
        this.livreId = livreId;
        this.genreId = genreId;
    }

    // equals() and hashCode() methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenreLivreId that = (GenreLivreId) o;
        return livreId.equals(that.livreId) && genreId.equals(that.genreId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(livreId, genreId);
    }
}