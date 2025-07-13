
TRUNCATE TABLE 
    demande_prolongement,
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



INSERT INTO genre(nom) VALUES 
('Science-Fiction'), 
('Romance'), 
('Policier');


INSERT INTO statut_livre (id, nom) VALUES 
(1, 'dispo'),
(2, 'en_cours_de_pret'),
(3, 'en_retard');

INSERT INTO type_emprunt(id, nom) VALUES 
(1, 'Sur place'), 
(2, 'a domicile');

INSERT INTO adherant(type, nbr_reservation, nbr_livre_pret, nbr_jrs_pret) VALUES 
('Etudiant', 3, 2, 15), 
('Prof', 5, 5, 30);


INSERT INTO utilisateur(nom, prenom, date_naissance, email, mdp, est_admin, id_adherant) VALUES 
('Rabenarivo', 'raja', '1995-05-21', 'rabenarivoraja21@gmail.com', 'raja2004', false, 1),
('Rabenarivo', 'raja', '1995-05-21', 'rabenarivor50@gmail.com', 'raja2004', true, 2);


INSERT INTO livre(titre, auteur, age, image, examplaire) VALUES
('Les Miserables', 'Victor Hugo', 14, 'lesmiserables.jpg', 2),
('etranger', 'Albert Camus', 16, 'etranger.jpg', 3),
('Le Seigneur des Anneaux', 'J.R.R. Tolkien', 12, 'seigneur.jpg', 4),
('Harry Potter a l''ecole des sorciers', 'J.K. Rowling', 10, 'hp1.jpg', 5),
('Fahrenheit 451', 'Ray Bradbury', 15, 'fahrenheit451.jpg', 2),
('Le Meilleur des mondes', 'Aldous Huxley', 16, 'meilleurmondes.jpg', 2),
('Orgueil et Prejuges', 'Jane Austen', 14, 'orgueiletp.jpg', 3),
('Le Comte de Monte-Cristo', 'Alexandre Dumas', 15, 'montecristo.jpg', 2),
('Le Silence des Agneaux', 'Thomas Harris', 18, 'silenceagneaux.jpg', 1),
('Les Fleurs du mal', 'Charles Baudelaire', 17, 'fleursdumal.jpg', 2),
('Le Parfum', 'Patrick Suskind', 16, 'parfum.jpg', 2),
('Les Piliers de la Terre', 'Ken Follett', 15, 'piliers.jpg', 2),
('Millenium - Les hommes qui n''aimaient pas les femmes', 'Stieg Larsson', 18, 'millenium1.jpg', 3),
('La Nuit', 'Elie Wiesel', 16, 'lanuit.jpg', 1),
('Bel-Ami', 'Guy de Maupassant', 16, 'belami.jpg', 2),
('La Peste', 'Albert Camus', 16, 'lapeste.jpg', 2),
('Le Livre de ma mere', 'Albert Cohen', 14, 'livremere.jpg', 1),
('La Condition humaine', 'Andre Malraux', 17, 'conditionhumaine.jpg', 1),
('Voyage au centre de la Terre', 'Jules Verne', 12, 'voyagecentre.jpg', 4),
('Ile mysterieuse', 'Jules Verne', 12, 'ilemysterieuse.jpg', 3);


INSERT INTO livre_adherant(livre_id, adherant_id) VALUES
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1), (8, 1), (9, 1), (10, 1),
(11, 2), (12, 2), (13, 2), (14, 2), (15, 2), (16, 2), (17, 2), (18, 2), (19, 2), (20, 2);

INSERT INTO genre_livre(livre_id, genre_id) VALUES
(1, 2), 
(2, 3), 
(3, 1), 
(4, 1),
(5, 1),
(6, 1),  
(7, 2), 
(8, 3), 
(9, 3), 
(10, 2), 
(11, 3),
(12, 1),  
(13, 3),  
(14, 1),  
(15, 2),  
(16, 1),  
(17, 2), 
(18, 1), 
(19, 1),  
(20, 1);  
