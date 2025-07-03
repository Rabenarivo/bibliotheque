-- Script de test pour la vérification d'expiration d'abonnement
-- Ce script crée des abonnements avec différentes dates d'expiration pour tester la fonctionnalité

-- 1. Créer un abonnement qui expire dans 5 jours (devrait permettre l'emprunt)
INSERT INTO abonnement (utilisateur_id, date_debut, date_fin, type_abonnement, statut)
VALUES (1, CURRENT_DATE - 30, CURRENT_DATE + 5, 'Premium', 'actif');

-- 2. Créer un abonnement qui expire dans 2 jours (devrait permettre l'emprunt)
INSERT INTO abonnement (utilisateur_id, date_debut, date_fin, type_abonnement, statut)
VALUES (2, CURRENT_DATE - 15, CURRENT_DATE + 2, 'Standard', 'actif');

-- 3. Créer un abonnement qui expire aujourd'hui (devrait permettre l'emprunt)
INSERT INTO abonnement (utilisateur_id, date_debut, date_fin, type_abonnement, statut)
VALUES (3, CURRENT_DATE - 10, CURRENT_DATE, 'Premium', 'actif');

-- 4. Créer un abonnement qui a expiré hier (ne devrait PAS permettre l'emprunt)
INSERT INTO abonnement (utilisateur_id, date_debut, date_fin, type_abonnement, statut)
VALUES (4, CURRENT_DATE - 20, CURRENT_DATE - 1, 'Standard', 'actif');

-- 5. Créer un abonnement qui expire dans 30 jours (devrait permettre l'emprunt)
INSERT INTO abonnement (utilisateur_id, date_debut, date_fin, type_abonnement, statut)
VALUES (5, CURRENT_DATE - 5, CURRENT_DATE + 30, 'Premium', 'actif');

-- Vérification des abonnements créés
SELECT 
    a.id,
    u.nom,
    u.prenom,
    a.date_debut,
    a.date_fin,
    a.type_abonnement,
    a.statut,
    CASE 
        WHEN a.date_fin >= CURRENT_DATE THEN 'Valide'
        ELSE 'Expiré'
    END as statut_expiration,
    CASE 
        WHEN a.date_fin >= CURRENT_DATE + 7 THEN 'Peut emprunter 7 jours'
        WHEN a.date_fin >= CURRENT_DATE THEN 'Peut emprunter jusqu\'à expiration'
        ELSE 'Ne peut pas emprunter'
    END as capacite_emprunt
FROM abonnement a
JOIN utilisateur u ON a.utilisateur_id = u.id
ORDER BY a.date_fin;

-- Test de cas d'usage : vérifier si un utilisateur peut emprunter jusqu'à une date spécifique
-- Exemple : Utilisateur 1 veut emprunter jusqu'au 2025-07-10
SELECT 
    u.nom,
    u.prenom,
    a.date_fin as expiration_abonnement,
    '2025-07-10' as date_retour_souhaitee,
    CASE 
        WHEN a.date_fin >= '2025-07-10' THEN 'Peut emprunter'
        ELSE 'Ne peut pas emprunter - abonnement expire avant'
    END as resultat
FROM utilisateur u
JOIN abonnement a ON u.id = a.utilisateur_id
WHERE u.id = 1 AND a.statut = 'actif'; 