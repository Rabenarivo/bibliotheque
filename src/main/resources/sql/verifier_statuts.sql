-- Script pour vérifier et corriger les statuts de livres

-- 1. Voir tous les statuts disponibles
SELECT * FROM statut_livre;

-- 2. Voir les livres avec leur statut actuel
SELECT 
    l.id,
    l.titre,
    sl.nom AS statut_actuel
FROM livre l
LEFT JOIN statut_livre sl ON l.statut_id = sl.id
ORDER BY l.id;

-- 3. Voir l'historique des statuts
SELECT 
    hl.id,
    l.titre AS livre,
    sl.nom AS statut,
    hl.date_debut
FROM historique_livre hl
JOIN livre l ON hl.livre_id = l.id
JOIN statut_livre sl ON hl.statut_id = sl.id
ORDER BY hl.date_debut DESC;

-- 4. Si le statut "dispo" n'existe pas, l'ajouter
INSERT INTO statut_livre (id, nom) 
VALUES (1, 'dispo') 
ON CONFLICT (id) DO NOTHING;

-- 5. Si le statut "en_cours_de_pret" n'existe pas, l'ajouter
INSERT INTO statut_livre (id, nom) 
VALUES (2, 'en_cours_de_pret') 
ON CONFLICT (id) DO NOTHING;

-- 6. Mettre à jour les livres sans statut vers "dispo"
UPDATE livre 
SET statut_id = (SELECT id FROM statut_livre WHERE nom = 'dispo')
WHERE statut_id IS NULL;

-- 7. Vérifier les emprunts en cours
SELECT 
    e.id AS emprunt_id,
    u.nom AS utilisateur_nom,
    l.titre AS livre_titre,
    e.statut_emprunt,
    e.date_emprunt,
    e.date_retour
FROM emprunt e
JOIN utilisateur u ON e.utilisateur_id = u.id
JOIN emprunt_detail ed ON e.id = ed.emprunt_id
JOIN livre l ON ed.livre_id = l.id
WHERE e.statut_emprunt IN ('en_cours', 'En cours')
ORDER BY e.date_emprunt DESC; 