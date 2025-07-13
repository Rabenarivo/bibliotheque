-- Script pour créer la table demande_prolongement
CREATE TABLE IF NOT EXISTS demande_prolongement (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    emprunt_id INTEGER NOT NULL,
    utilisateur_id INTEGER NOT NULL,
    date_demande DATE NOT NULL,
    date_prolongement_demandee DATE NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'en_attente',
    motif TEXT NOT NULL,
    date_validation DATE NULL,
    admin_validateur_id INTEGER NULL,
    FOREIGN KEY (emprunt_id) REFERENCES emprunt(id),
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id),
    FOREIGN KEY (admin_validateur_id) REFERENCES utilisateur(id)
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_demande_prolongement_utilisateur ON demande_prolongement(utilisateur_id);
CREATE INDEX IF NOT EXISTS idx_demande_prolongement_statut ON demande_prolongement(statut);
CREATE INDEX IF NOT EXISTS idx_demande_prolongement_emprunt ON demande_prolongement(emprunt_id);

-- Contraintes de validation
ALTER TABLE demande_prolongement 
ADD CONSTRAINT chk_statut 
CHECK (statut IN ('en_attente', 'acceptee', 'refusee'));

ALTER TABLE demande_prolongement 
ADD CONSTRAINT chk_date_prolongement 
CHECK (date_prolongement_demandee > date_demande);

-- Commentaires sur la table
COMMENT ON TABLE demande_prolongement IS 'Table pour gérer les demandes de prolongement d''emprunts';
COMMENT ON COLUMN demande_prolongement.id IS 'Identifiant unique de la demande';
COMMENT ON COLUMN demande_prolongement.emprunt_id IS 'Référence vers l''emprunt concerné';
COMMENT ON COLUMN demande_prolongement.utilisateur_id IS 'Utilisateur qui fait la demande';
COMMENT ON COLUMN demande_prolongement.date_demande IS 'Date de soumission de la demande';
COMMENT ON COLUMN demande_prolongement.date_prolongement_demandee IS 'Nouvelle date de retour souhaitée';
COMMENT ON COLUMN demande_prolongement.statut IS 'Statut de la demande: en_attente, acceptee, refusee';
COMMENT ON COLUMN demande_prolongement.motif IS 'Motif de la demande de prolongement';
COMMENT ON COLUMN demande_prolongement.date_validation IS 'Date de validation par l''admin';
COMMENT ON COLUMN demande_prolongement.admin_validateur_id IS 'Admin qui a validé la demande'; 