-- Script pour créer un utilisateur avec un quota de 3
-- Vérifier d'abord les types d'adhérents disponibles
SELECT * FROM adherant;

-- Créer un nouvel utilisateur avec un quota de 3
-- Utilisateur étudiant (type adhérent 1) avec quota manuel de 3
INSERT INTO utilisateur(nom, prenom, date_naissance, email, mdp, est_admin, id_adherant, quota_actuel) VALUES 
('Dupont', 'Marie', '1998-03-15', 'marie.dupont@email.com', 'password123', false, 1, 3);

-- Vérifier que l'utilisateur a été créé
SELECT 
    u.id,
    u.nom,
    u.prenom,
    u.email,
    u.quota_actuel,
    a.type as type_adherant,
    a.nbr_livre_pret as quota_max_adherant
FROM utilisateur u
LEFT JOIN adherant a ON u.id_adherant = a.id
WHERE u.email = 'marie.dupont@email.com';

-- Créer un abonnement pour cet utilisateur (optionnel)
-- Récupérer l'ID de l'utilisateur créé
SET @user_id = (SELECT id FROM utilisateur WHERE email = 'marie.dupont@email.com');

-- Créer un abonnement valide pour l'année en cours
INSERT INTO abonnement(utilisateur_id, date_debut, date_fin) VALUES 
(@user_id, '2025-01-01', '2025-12-31');

-- Vérifier l'abonnement créé
SELECT 
    a.id,
    a.utilisateur_id,
    a.date_debut,
    a.date_fin,
    u.nom,
    u.prenom,
    u.quota_actuel
FROM abonnement a
JOIN utilisateur u ON a.utilisateur_id = u.id
WHERE u.email = 'marie.dupont@email.com'; 