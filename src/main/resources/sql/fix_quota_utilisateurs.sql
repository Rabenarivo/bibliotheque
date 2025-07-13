-- Script pour corriger le quota des utilisateurs
-- Met à jour le quota_actuel pour tous les utilisateurs qui ont un quota null

UPDATE utilisateur 
SET quota_actuel = (
    SELECT a.nbr_livre_pret 
    FROM adherant a 
    WHERE a.id = utilisateur.id_adherant
)
WHERE quota_actuel IS NULL 
AND id_adherant IS NOT NULL;

-- Vérification des utilisateurs après correction
SELECT 
    u.id,
    u.nom,
    u.prenom,
    u.email,
    u.quota_actuel,
    a.nbr_livre_pret as limite_adherant
FROM utilisateur u
LEFT JOIN adherant a ON u.id_adherant = a.id
ORDER BY u.id; 