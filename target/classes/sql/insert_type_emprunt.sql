-- Script SQL pour insérer les types d'emprunt
-- Supprimer les données existantes si nécessaire
DELETE FROM type_emprunt;

-- Réinitialiser l'auto-increment
ALTER TABLE type_emprunt AUTO_INCREMENT = 1;

-- Insérer les types d'emprunt
INSERT INTO type_emprunt (nom, description, duree_jours) VALUES
('Sur place', 'Consultation et lecture sur place uniquement', 0),
('Maison', 'Emprunt pour usage à domicile', 14);

-- Vérifier l'insertion
SELECT * FROM type_emprunt; 