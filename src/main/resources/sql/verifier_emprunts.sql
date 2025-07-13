-- Script pour vérifier les emprunts existants
SELECT 
    e.id as emprunt_id,
    e.date_emprunt,
    e.date_retour,
    e.statut_emprunt,
    u.nom as utilisateur_nom,
    u.prenom as utilisateur_prenom,
    u.email as utilisateur_email
FROM emprunt e
LEFT JOIN utilisateur u ON e.utilisateur_id = u.id
ORDER BY e.id;

-- Vérifier les détails d'emprunt
SELECT 
    ed.id as detail_id,
    ed.emprunt_id,
    ed.livre_id,
    ed.date_debut,
    ed.date_fin,
    ed.date_retour,
    l.titre as livre_titre,
    l.auteur as livre_auteur
FROM emprunt_detail ed
LEFT JOIN livre l ON ed.livre_id = l.id
ORDER BY ed.emprunt_id;

-- Compter le nombre total d'emprunts
SELECT COUNT(*) as total_emprunts FROM emprunt;

-- Vérifier les emprunts avec l'ID 18 spécifiquement
SELECT 
    e.id as emprunt_id,
    e.date_emprunt,
    e.date_retour,
    e.statut_emprunt,
    u.nom as utilisateur_nom,
    u.prenom as utilisateur_prenom
FROM emprunt e
LEFT JOIN utilisateur u ON e.utilisateur_id = u.id
WHERE e.id = 18; 