-- Script de test pour vérifier la correction de disponibilité
-- Ce script teste la nouvelle requête de disponibilité

-- 1. Créer des emprunts de test pour le livre "Bases de données avancées" (ID = 2)
-- (Ces emprunts existent déjà selon l'utilisateur)

-- 2. Vérifier l'état actuel des emprunts pour ce livre
SELECT 
    'État actuel des emprunts' as info,
    e.id as emprunt_id,
    e.statut_emprunt,
    l.titre,
    u.nom,
    u.prenom,
    e.date_emprunt,
    e.date_retour
FROM emprunt e
JOIN emprunt_detail ed ON e.id = ed.emprunt_id
JOIN livre l ON ed.livre_id = l.id
JOIN utilisateur u ON e.utilisateur_id = u.id
WHERE ed.livre_id = 2
ORDER BY e.id;

-- 3. Tester la nouvelle requête de disponibilité
SELECT 
    'Test de disponibilité corrigée' as test,
    l.id AS livreId,
    l.titre AS titre,
    l.examplaire AS totalExemplaires,
    (COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END) + 
     COUNT(CASE WHEN r.id IS NOT NULL THEN r.id END) +
     COUNT(CASE WHEN e.statut_emprunt IN ('en_cours', 'En cours') THEN ed.id END)) AS exemplairesIndisponibles,
    (l.examplaire - (COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END) + 
                    COUNT(CASE WHEN r.id IS NOT NULL THEN r.id END) +
                    COUNT(CASE WHEN e.statut_emprunt IN ('en_cours', 'En cours') THEN ed.id END))) AS exemplairesDisponibles
FROM 
    livre l
LEFT JOIN 
    historique_livre hl ON l.id = hl.livre_id
LEFT JOIN 
    statut_livre sl ON hl.statut_id = sl.id
LEFT JOIN 
    reservation r ON l.id = r.livre_id
LEFT JOIN 
    emprunt_detail ed ON l.id = ed.livre_id
LEFT JOIN 
    emprunt e ON ed.emprunt_id = e.id
WHERE 
    l.id = 2
GROUP BY 
    l.id, l.titre, l.examplaire;

-- 4. Détail des comptages pour comprendre le calcul
SELECT 
    'Détail des comptages' as detail,
    l.id,
    l.titre,
    l.examplaire as total,
    COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END) as historique_en_cours,
    COUNT(CASE WHEN r.id IS NOT NULL THEN r.id END) as reservations,
    COUNT(CASE WHEN e.statut_emprunt IN ('en_cours', 'En cours') THEN ed.id END) as emprunts_en_cours
FROM 
    livre l
LEFT JOIN 
    historique_livre hl ON l.id = hl.livre_id
LEFT JOIN 
    statut_livre sl ON hl.statut_id = sl.id
LEFT JOIN 
    reservation r ON l.id = r.livre_id
LEFT JOIN 
    emprunt_detail ed ON l.id = ed.livre_id
LEFT JOIN 
    emprunt e ON ed.emprunt_id = e.id
WHERE 
    l.id = 2
GROUP BY 
    l.id, l.titre, l.examplaire;

-- 5. Vérifier tous les livres avec leur disponibilité corrigée
SELECT 
    l.id AS livreId,
    l.titre AS titre,
    l.examplaire AS totalExemplaires,
    (COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END) + 
     COUNT(CASE WHEN r.id IS NOT NULL THEN r.id END) +
     COUNT(CASE WHEN e.statut_emprunt IN ('en_cours', 'En cours') THEN ed.id END)) AS exemplairesIndisponibles,
    (l.examplaire - (COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END) + 
                    COUNT(CASE WHEN r.id IS NOT NULL THEN r.id END) +
                    COUNT(CASE WHEN e.statut_emprunt IN ('en_cours', 'En cours') THEN ed.id END))) AS exemplairesDisponibles
FROM 
    livre l
LEFT JOIN 
    historique_livre hl ON l.id = hl.livre_id
LEFT JOIN 
    statut_livre sl ON hl.statut_id = sl.id
LEFT JOIN 
    reservation r ON l.id = r.livre_id
LEFT JOIN 
    emprunt_detail ed ON l.id = ed.livre_id
LEFT JOIN 
    emprunt e ON ed.emprunt_id = e.id
GROUP BY 
    l.id, l.titre, l.examplaire
ORDER BY 
    l.id; 