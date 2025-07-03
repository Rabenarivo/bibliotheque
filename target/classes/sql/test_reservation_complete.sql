-- Script de test pour vérifier le fonctionnement complet des réservations
-- Ce script teste l'insertion, la projection et l'affichage des réservations

-- 1. Vérifier la structure de la table reservation
DESCRIBE reservation;

-- 2. Insérer une réservation de test avec tous les champs
INSERT INTO reservation (utilisateur_id, livre_id, date_debut, date_reservation, statut_reservation, est_validee)
VALUES (1, 1, CURRENT_DATE, CURRENT_DATE, false, false);

-- 3. Vérifier l'insertion
SELECT 
    'Réservation insérée' as info,
    id,
    utilisateur_id,
    livre_id,
    date_debut,
    date_reservation,
    statut_reservation,
    est_validee
FROM reservation
WHERE id = LASTVAL();

-- 4. Test de la requête de projection (simulation)
SELECT 
    'Test de projection' as info,
    r.id AS reservationId,
    u.nom AS utilisateurNom,
    u.prenom AS utilisateurPrenom,
    u.email AS utilisateurEmail,
    l.titre AS livreTitre,
    l.auteur AS livreAuteur,
    TO_CHAR(r.date_reservation, 'DD/MM/YYYY') AS dateReservation
FROM reservation r
LEFT JOIN utilisateur u ON r.utilisateur_id = u.id
LEFT JOIN livre l ON r.livre_id = l.id
WHERE r.est_validee = false
ORDER BY r.date_reservation DESC;

-- 5. Vérifier les réservations en attente
SELECT 
    'Réservations en attente' as info,
    COUNT(*) as total_en_attente
FROM reservation
WHERE est_validee = false;

-- 6. Vérifier les réservations validées
SELECT 
    'Réservations validées' as info,
    COUNT(*) as total_validees
FROM reservation
WHERE est_validee = true;

-- 7. Test de validation d'une réservation
UPDATE reservation 
SET est_validee = true, statut_reservation = true
WHERE id = LASTVAL();

-- 8. Vérifier après validation
SELECT 
    'Après validation' as info,
    id,
    utilisateur_id,
    livre_id,
    date_debut,
    date_reservation,
    statut_reservation,
    est_validee
FROM reservation
WHERE id = LASTVAL();

-- 9. Test de la requête de projection après validation
SELECT 
    'Projection après validation' as info,
    r.id AS reservationId,
    u.nom AS utilisateurNom,
    u.prenom AS utilisateurPrenom,
    u.email AS utilisateurEmail,
    l.titre AS livreTitre,
    l.auteur AS livreAuteur,
    TO_CHAR(r.date_reservation, 'DD/MM/YYYY') AS dateReservation
FROM reservation r
LEFT JOIN utilisateur u ON r.utilisateur_id = u.id
LEFT JOIN livre l ON r.livre_id = l.id
WHERE r.id = LASTVAL();

-- 10. Nettoyer les données de test
DELETE FROM reservation WHERE id = LASTVAL();

-- 11. Vérifier l'état final
SELECT 
    'État final' as info,
    COUNT(*) as total_reservations,
    SUM(CASE WHEN est_validee = false THEN 1 ELSE 0 END) as en_attente,
    SUM(CASE WHEN est_validee = true THEN 1 ELSE 0 END) as validees
FROM reservation; 