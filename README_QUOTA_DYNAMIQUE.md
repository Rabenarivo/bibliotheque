# Système de Quota Dynamique - Bibliothèque

## Vue d'ensemble

Le système de quota dynamique permet de gérer les emprunts de livres de manière plus flexible. Contrairement au système précédent qui comptait les emprunts en cours, le nouveau système utilise un quota qui diminue à chaque emprunt et augmente à chaque retour.

## Fonctionnement

### Principe du Quota Dynamique

1. **Quota initial** : Chaque utilisateur commence avec un quota égal à sa limite d'adhérent (`nbr_livre_pret`)
2. **Diminution du quota** : À chaque emprunt d'un livre, le quota diminue de 1
3. **Augmentation du quota** : À chaque retour d'un livre, le quota augmente de 1
4. **Limite maximale** : Le quota ne peut jamais dépasser la limite de l'adhérent

### Exemple Concret

Un utilisateur avec un quota de **2 livres** :

- **État initial** : Quota = 2
- **Après 1er emprunt** : Quota = 1
- **Après 2ème emprunt** : Quota = 0 (ne peut plus emprunter)
- **Après 1er retour** : Quota = 1 (peut emprunter à nouveau)
- **Après 2ème retour** : Quota = 2 (quota restauré)

## Modifications Apportées

### 1. Entité Utilisateur

- **Nouveau champ** : `quotaActuel` (Integer)
- **Nouvelles méthodes** :
  - `initialiserQuota()` : Initialise le quota avec la limite de l'adhérent
  - `diminuerQuota()` : Diminue le quota de 1
  - `augmenterQuota()` : Augmente le quota de 1
  - `peutEmprunter()` : Vérifie si l'utilisateur peut emprunter

### 2. Service AbonnementService

- **Nouvelles méthodes** :
  - `diminuerQuota(Integer userId)` : Diminue le quota lors d'un emprunt
  - `augmenterQuota(Integer userId)` : Augmente le quota lors d'un retour
  - `initialiserQuota(Integer userId)` : Initialise le quota d'un utilisateur
- **Modification** : `canEmprunterPlus()` utilise maintenant le quota dynamique

### 3. Contrôleurs

- **AdminEmpruntController** : Gestion du quota lors des emprunts et retours
- **ReservationController** : Gestion du quota lors de la validation des réservations

### 4. Interface Utilisateur

- **Nouvelle section** : Affichage du quota actuel dans le formulaire de création d'emprunt
- **Validation** : Le bouton de soumission est désactivé si le quota est épuisé

## Scripts de Migration

### 1. Mise à jour de la base de données

Exécuter le script `src/main/resources/sql/update_quota_system.sql` :

```sql
-- Ajouter la colonne quota_actuel
ALTER TABLE utilisateur ADD COLUMN quota_actuel INT;

-- Initialiser les quotas existants
UPDATE utilisateur 
SET quota_actuel = (
    SELECT a.nbr_livre_pret 
    FROM adherant a 
    WHERE a.id = utilisateur.id_adherant
)
WHERE id_adherant IS NOT NULL;

-- Ajuster les quotas selon les emprunts en cours
UPDATE utilisateur 
SET quota_actuel = quota_actuel - (
    SELECT COUNT(e.id) 
    FROM emprunt e 
    WHERE e.utilisateur_id = utilisateur.id 
    AND e.statut_emprunt IN ('en_cours', 'En cours')
)
WHERE quota_actuel IS NOT NULL;
```

### 2. Test du système

Exécuter le script `src/main/resources/sql/test_quota_dynamique.sql` pour vérifier le bon fonctionnement.

## Avantages du Nouveau Système

1. **Simplicité** : Plus besoin de compter les emprunts en cours
2. **Performance** : Moins de requêtes complexes
3. **Flexibilité** : Gestion plus intuitive du quota
4. **Cohérence** : Le quota reflète directement la capacité d'emprunt

## Points d'Attention

1. **Initialisation** : Les utilisateurs existants doivent avoir leur quota initialisé
2. **Cohérence** : Le quota doit rester cohérent avec les emprunts en cours
3. **Limite** : Le quota ne peut jamais dépasser la limite de l'adhérent
4. **Suppression d'emprunt** : Si un emprunt en cours est supprimé, le quota doit être restauré

## API Endpoints

### Vérification du quota

```
GET /admin/emprunts/check-quota/{userId}
```

**Réponse** :
```json
{
  "canEmprunter": true,
  "message": " 2 emprunt(s) possible(s) (2/3)"
}
```

## Messages d'Interface

- **Quota disponible** : " 2 emprunt(s) possible(s) (2/3)"
- **Quota faible** : " Plus que 1 emprunt(s) possible(s) (1/3)"
- **Quota épuisé** : " Quota d'emprunts épuisé (0/3)"

## Validation

Le système valide automatiquement :
- Que le quota ne soit pas négatif
- Que le quota ne dépasse pas la limite de l'adhérent
- Que l'utilisateur puisse emprunter avant de créer un emprunt
- Que le quota soit restauré lors des retours 