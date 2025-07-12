-- Script pour ajouter le statut "retard" dans la table statut_livre
-- Ce script doit être exécuté pour permettre la gestion des retards

-- Vérifier si le statut "retard" existe déjà
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM statut_livre WHERE nom = 'retard') THEN
        -- Insérer le statut "retard"
        INSERT INTO statut_livre (nom) VALUES ('retard');
        RAISE NOTICE 'Statut "retard" ajouté avec succès';
    ELSE
        RAISE NOTICE 'Le statut "retard" existe déjà';
    END IF;
END $$;

-- Afficher tous les statuts disponibles
SELECT id, nom FROM statut_livre ORDER BY id;

-- Vérifier les emprunts qui pourraient être en retard
SELECT 
    e.id as emprunt_id,
    u.nom || ' ' || u.prenom as utilisateur,
    l.titre as livre,
    e.date_emprunt,
    e.date_retour,
    e.statut_emprunt,
    CASE 
        WHEN e.date_retour < CURRENT_DATE AND e.statut_emprunt IN ('en_cours', 'En cours') 
        THEN CURRENT_DATE - e.date_retour 
        ELSE 0 
    END as jours_de_retard
FROM emprunt e
LEFT JOIN utilisateur u ON e.utilisateur_id = u.id
LEFT JOIN emprunt_detail ed ON ed.emprunt_id = e.id
LEFT JOIN livre l ON ed.livre_id = l.id
WHERE e.date_retour < CURRENT_DATE AND e.statut_emprunt IN ('en_cours', 'En cours')
ORDER BY e.date_retour; 