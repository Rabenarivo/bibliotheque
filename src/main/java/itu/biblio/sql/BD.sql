CREATE DATABASE bibliotheque;

\c bibliotheque;

CREATE TABLE livre(
    id SERIAL PRIMARY KEY,
    titre VARCHAR(255),
    auteur VARCHAR(255),
    age INT,
    image VARCHAR(50)
);
ALTER TABLE livre ADD COLUMN examplaire INT DEFAULT 1;
CREATE TABLE adherant(
    id SERIAL PRIMARY KEY,
    type VARCHAR(100),
    nbr_reservation INT,
    nbr_livre_pret INT,
    nbr_jrs_pret INT
);

CREATE TABLE livre_adherant(
    id SERIAL PRIMARY KEY,
    livre_id INT REFERENCES livre(id),
    adherant_id INT REFERENCES adherant(id)
);
CREATE TABLE genre(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255)
);

CREATE TABLE statut_livre(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50)
);

CREATE TABLE type_emprunt(
    id INT PRIMARY KEY,
    nom VARCHAR(50)
);

CREATE TABLE utilisateur(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255),
    prenom VARCHAR(255),
    date_naissance DATE,
    email VARCHAR(255),
    mdp VARCHAR(50),
    est_admin BOOLEAN,
    id_adherant INT REFERENCES adherant(id)
);

CREATE TABLE abonnement(
    id SERIAL PRIMARY KEY,
    utilisateur_id INT REFERENCES utilisateur(id),
    date_debut DATE,
    date_fin DATE
);

CREATE TABLE emprunt(
    id SERIAL PRIMARY KEY,
    utilisateur_id INT REFERENCES utilisateur(id) 
);

CREATE TABLE emprunt_detail(
    id SERIAL PRIMARY KEY,
    emprunt_id INT REFERENCES emprunt(id),
    livre_id INT REFERENCES livre(id),
    date_debut DATE,
    date_fin DATE,
    date_retour DATE,
    type_emprunt_id INT REFERENCES type_emprunt(id)
);

CREATE TABLE penalite(
    id SERIAL PRIMARY KEY,
    emprunt_id INT REFERENCES emprunt(id),
    sanction INT 
);

CREATE TABLE reservation(
    id SERIAL PRIMARY KEY,
    utilisateur_id INT REFERENCES utilisateur(id),
    livre_id INT REFERENCES livre(id),
    date_debut DATE
);

CREATE TABLE genre_livre(
    livre_id INT REFERENCES livre(id),
    genre_id INT REFERENCES genre(id)
);

CREATE TABLE historique_livre(
    id SERIAL PRIMARY KEY,
    livre_id INT REFERENCES livre(id),
    statut_id INT REFERENCES statut_livre(id),
    date_debut DATE
);



INSERT INTO adherant (type, nbr_reservation, nbr_livre_pret, nbr_jrs_pret)
VALUES 
  ('prof', 10, 8, 30),  
  ('etudiant', 5, 4, 15),
  ('pro', 7, 6, 20);     

-- Insertion dans la table utilisateur
-- On suppose que les ID générés automatiquement sont 1, 2, 3 pour les adhérents
INSERT INTO utilisateur (nom, prenom, date_naissance, email, mdp, est_admin, id_adherant)
VALUES 
  ('Rabenarivo', 'Raja', '2004-04-03', 'rajarabenarivo21@gmail.com', 'raja2004', FALSE, 1),
  ('Rabe', 'Sofia', '2001-11-22', 'sofia.rabe@example.com', 'passEtud456', FALSE, 2),
  ('Andria', 'Lova', '1990-03-10', 'lova.andria@example.com', 'passPro789', TRUE, 3);



  -- Insertion des livres
INSERT INTO livre (titre, auteur, age, image,examplaire)
VALUES 
  ('Introduction à Java', 'John Doe', 18, 'java.jpg',3),
  ('Bases de données avancées', 'Jane Smith', 21, 'bdd.jpg',2),
  ('Mathématiques pour informaticiens', 'Albert Einstein', 16, 'math.jpg',4),
  ('Design Patterns en Java', 'Gamma et al.', 25, 'design.jpg',4),
  ('Programmation Web avec Spring', 'Rod Johnson', 20, 'spring.jpg',3);

-- Insertion des relations livre_adherant
-- Les livres sont associés à des types d'adhérents (prof, étudiant, etc.)
INSERT INTO livre_adherant (livre_id, adherant_id)
VALUES 
  (1, 1),  
  (2, 1),  
  (3, 2),  
  (4, 1),  
  (5, 2),  
  (5, 3); 

SELECT livre.id, livre.titre, livre.auteur, livre.age, livre.image
FROM livre
JOIN livre_adherant ON livre.id = livre_adherant.livre_id
JOIN adherant ON livre_adherant.adherant_id = adherant.id
JOIN utilisateur ON utilisateur.id_adherant = adherant.id
WHERE utilisateur.id = 1;
