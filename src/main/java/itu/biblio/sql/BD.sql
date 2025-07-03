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


INSERT INTO utilisateur (nom, prenom, date_naissance, email, mdp, est_admin, id_adherant)
VALUES
*('Rabenarivo', 'Raja', '2004-04-03', 'rajarabenarivo21@gmail.com', 'raja2004', FALSE, 1); 
  ('Rabenarivo', 'Raja', '2004-04-03', 'rajarabenarivo21@gmail.com', 'raja2004', FALSE, 1);




INSERT INTO livre (titre, auteur, age, image,examplaire)
VALUES 
  ('Introduction à Java', 'John Doe', 18, 'java.jpg',3),
  ('Bases de données avancées', 'Jane Smith', 21, 'bdd.jpg',2),
  ('Mathématiques pour informaticiens', 'Albert Einstein', 16, 'math.jpg',4),
  ('Design Patterns en Java', 'Gamma et al.', 25, 'design.jpg',4),
  ('Programmation Web avec Spring', 'Rod Johnson', 20, 'spring.jpg',3);


INSERT INTO livre_adherant (livre_id, adherant_id)
VALUES 
  (1, 1),  
  (2, 1),  
  (3, 2),  
  (4, 1),  
  (5, 2),  
  (5, 3); 


INSERT INTO historique_livre (livre_id, statut_id, date_debut) VALUES
(2, 2, '2023-09-01'),
(2, 2, '2023-09-15');

(1, 1, '2023-09-01'),
(2, 2, '2023-09-15'),
(3, 1, '2023-09-20');
(2,1,'2023-10-01'),

(1,1,'2024-01-01'),
(1,1,'2024-01-02');
(1,2,'2023-12-03');

INSERT INTO statut_livre (id, nom) VALUES
(1, 'dispo'),     
(2, 'en_cours_de_pret');

SELECT livre.id, livre.titre, livre.auteur, livre.age, livre.image
FROM livre
JOIN livre_adherant ON livre.id = livre_adherant.livre_id
JOIN adherant ON livre_adherant.adherant_id = adherant.id
JOIN utilisateur ON utilisateur.id_adherant = adherant.id
WHERE utilisateur.id = 1;







-- SELECT 
--     l.id AS livre_id,
--     l.titre,
--     l.examplaire AS total_exemplaires,
--     COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END) AS exemplaires_indisponibles,
--     (l.examplaire - COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END)) AS exemplaires_disponibles
-- FROM 
--     livre l
-- LEFT JOIN 
--     historique_livre hl ON l.id = hl.livre_id
-- LEFT JOIN 
--     statut_livre sl ON hl.statut_id = sl.id
-- WHERE 
--     l.id = 1
-- GROUP BY 
--     l.id, l.titre, l.examplaire;



SELECT 
    l.id AS livre_id,
    l.titre,
    l.examplaire AS total_exemplaires,
    COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END) AS exemplaires_indisponibles,
    (l.examplaire - COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END)) AS exemplaires_disponibles
FROM livre l
LEFT JOIN historique_livre hl ON l.id = hl.livre_id
LEFT JOIN statut_livre sl ON hl.statut_id = sl.id
WHERE hl.date_debut = (
    SELECT MAX(h.date_debut)
    FROM historique_livre h
    WHERE h.livre_id = l.id
)
GROUP BY l.id, l.titre, l.examplaire;



SELECT 
    l.id AS livre_id,
    l.titre,
    l.examplaire AS total_exemplaires,
    COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END) AS exemplaires_indisponibles,
    (l.examplaire - COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END)) AS exemplaires_disponibles
FROM 
    livre l
LEFT JOIN 
    historique_livre hl ON l.id = hl.livre_id
LEFT JOIN 
    statut_livre sl ON hl.statut_id = sl.id
WHERE 
    l.id = 2
GROUP BY 
    l.id, l.titre, l.examplaire;



SELECT 
    r.id AS reservation_id,
    u.nom AS utilisateur_nom,
    u.prenom AS utilisateur_prenom,
    u.email AS utilisateur_email,
    l.titre AS livre_titre,
    l.auteur AS livre_auteur,
    r.date_debut AS date_reservation
FROM 
    reservation r
JOIN 
    utilisateur u ON r.utilisateur_id = u.id
JOIN 
    livre l ON r.livre_id = l.id;