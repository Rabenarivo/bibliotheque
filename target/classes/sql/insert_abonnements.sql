-- Script d'insertion d'abonnements de test
-- Assurez-vous que les utilisateurs existent avant d'exécuter ce script

-- Abonnement actif pour l'utilisateur 1 (si il existe)
INSERT INTO abonnement (utilisateur_id, date_debut, date_fin) 
VALUES (1, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year')
ON CONFLICT DO NOTHING;

-- Abonnement expiré pour l'utilisateur 2 (si il existe)
INSERT INTO abonnement (utilisateur_id, date_debut, date_fin) 
VALUES (2, CURRENT_DATE - INTERVAL '2 years', CURRENT_DATE - INTERVAL '1 year')
ON CONFLICT DO NOTHING;

-- Abonnement qui expire bientôt pour l'utilisateur 3 (si il existe)
INSERT INTO abonnement (utilisateur_id, date_debut, date_fin) 
VALUES (3, CURRENT_DATE - INTERVAL '11 months', CURRENT_DATE + INTERVAL '7 days')
ON CONFLICT DO NOTHING;

-- Abonnement en attente pour l'utilisateur 4 (si il existe)
INSERT INTO abonnement (utilisateur_id, date_debut, date_fin) 
VALUES (4, CURRENT_DATE + INTERVAL '1 month', CURRENT_DATE + INTERVAL '13 months')
ON CONFLICT DO NOTHING; 