-- Script de test pour vérifier l'insertion des réservations
-- Ce script teste que tous les champs sont correctement insérés

-- 1. Vérifier la structure de la table reservation
DESCRIBE reservation;

-- 2. Vérifier les réservations existantes
SELECT 
    'Réservations existantes' as info,
    id,
    utilisateur_id,
    livre_id,
    date_debut,
    date_reservation,
    statut_reservation,
    est_validee
FROM reservation
ORDER BY id DESC
LIMIT 5;

-- 3. Test d'insertion manuelle (pour comparaison)
INSERT INTO reservation (utilisateur_id, livre_id, date_debut, date_reservation, statut_reservation, est_validee)
VALUES (1, 1, CURDATE(), CURDATE(), false, false);

-- 4. Vérifier l'insertion
SELECT 
    'Après insertion manuelle' as info,
    id,
    utilisateur_id,
    livre_id,
    date_debut,
    date_reservation,
    statut_reservation,
    est_validee
FROM reservation
WHERE id = LAST_INSERT_ID();

-- 5. Vérifier les contraintes de la table
SELECT 
    'Contraintes de la table' as info,
    CONSTRAINT_NAME,
    CONSTRAINT_TYPE,
    COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'reservation'
AND TABLE_SCHEMA = DATABASE();

-- 6. Vérifier les valeurs par défaut
SELECT 
    'Valeurs par défaut' as info,
    COLUMN_NAME,
    COLUMN_DEFAULT,
    IS_NULLABLE,
    DATA_TYPE
FROM information_schema.COLUMNS
WHERE TABLE_NAME = 'reservation'
AND TABLE_SCHEMA = DATABASE()
ORDER BY ORDINAL_POSITION;

-- 7. Test avec des valeurs NULL pour voir le comportement
INSERT INTO reservation (utilisateur_id, livre_id, date_debut, date_reservation, statut_reservation, est_validee)
VALUES (1, 2, CURDATE(), NULL, NULL, NULL);

-- 8. Vérifier l'insertion avec NULL
SELECT 
    'Après insertion avec NULL' as info,
    id,
    utilisateur_id,
    livre_id,
    date_debut,
    date_reservation,
    statut_reservation,
    est_validee
FROM reservation
WHERE id = LAST_INSERT_ID();

-- 9. Nettoyer les données de test
DELETE FROM reservation WHERE id = LAST_INSERT_ID();
DELETE FROM reservation WHERE id = (SELECT MAX(id) FROM reservation WHERE utilisateur_id = 1 AND livre_id = 1);

-- 10. Vérifier l'état final
SELECT 
    'État final' as info,
    COUNT(*) as total_reservations
FROM reservation; 