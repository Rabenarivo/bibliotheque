-- Table: genre
INSERT INTO genre(nom) VALUES 
('Science-Fiction'), 
('Romance'), 
('Policier');

-- Table: statut_livre (sans 'Réservé')
INSERT INTO statut_livre(nom) VALUES 
('Disponible'), 
('en_cours_de_pret');

-- Table: type_emprunt
INSERT INTO type_emprunt(id, nom) VALUES 
(1, 'Sur place'), 
(2, 'À domicile');

-- Table: adherant
INSERT INTO adherant(type, nbr_reservation, nbr_livre_pret, nbr_jrs_pret) VALUES 
('Etudiant', 2, 3, 15), 
('Prof', 5, 5, 30);

-- Table: livre
INSERT INTO livre(titre, auteur, age, image, examplaire) VALUES 
('Le Petit Prince', 'Antoine de Saint-Exupéry', 7, 'petitprince.jpg', 3),
('1984', 'George Orwell', 16, '1984.jpg', 2),
('Da Vinci Code', 'Dan Brown', 18, 'davinci.jpg', 1);

-- Table: utilisateur
INSERT INTO utilisateur(nom, prenom, date_naissance, email, mdp, est_admin, id_adherant) VALUES 
('Rabenarivo', 'raja', '1995-05-21', 'rabenarivoraja21@gmail.com', 'raja2004', false, 1),
('Rabenarivo', 'raja', '1995-05-21', 'rabenarivor50@gmail.com', 'raja2004', true, 2);

-- Table: abonnement
INSERT INTO abonnement(utilisateur_id, date_debut, date_fin) VALUES 
(1, '2025-01-01', '2025-12-31');

-- Table: emprunt
INSERT INTO emprunt(utilisateur_id) VALUES 
(1);

-- Table: emprunt_detail
INSERT INTO emprunt_detail(emprunt_id, livre_id, date_debut, date_fin, date_retour, type_emprunt_id) VALUES 
(1, 1, '2025-06-01', '2025-06-15', NULL, 2);

-- Table: penalite
INSERT INTO penalite(emprunt_id, sanction) VALUES 
(1, 500);

-- Table: reservation
INSERT INTO reservation(utilisateur_id, livre_id, date_debut) VALUES 
(1, 2, '2025-07-01');

-- Table: livre_adherant
INSERT INTO livre_adherant(livre_id, adherant_id) VALUES 
(1, 1), (2, 2);

-- Table: genre_livre
INSERT INTO genre_livre(livre_id, genre_id) VALUES 
(1, 2), (2, 1), (3, 3);

-- Table: historique_livre
INSERT INTO historique_livre(livre_id, statut_id, date_debut) VALUES 
(1, 1, '2025-06-01'), 
(1, 2, '2025-06-10');


-- Supprimer les données dans le bon ordre (dépendances)
TRUNCATE TABLE 
    genre_livre,
    historique_livre,
    reservation,
    penalite,
    emprunt_detail,
    emprunt,
    abonnement,
    utilisateur,
    livre_adherant,
    livre,
    adherant,
    genre,
    statut_livre,
    type_emprunt
RESTART IDENTITY CASCADE;
