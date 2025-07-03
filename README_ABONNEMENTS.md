# Gestion des Abonnements - Bibliothèque

## Vue d'ensemble

Le système de gestion des abonnements permet de contrôler l'accès aux emprunts de livres en fonction de la validité de l'abonnement de chaque utilisateur.

## Fonctionnalités

### 1. Vérification d'Abonnement
- **Vérification automatique** : Lors de la création d'un emprunt, le système vérifie automatiquement si l'utilisateur a un abonnement valide
- **Vérification d'expiration** : Le système vérifie que l'abonnement ne expire pas avant la date de retour du livre
- **Vérification de limite** : Le système vérifie que l'utilisateur n'a pas atteint sa limite d'emprunts selon son type d'adhérent
- **Messages informatifs** : Affichage de messages clairs sur le statut de l'abonnement et des emprunts
- **Prévention d'emprunt** : Empêche la création d'emprunts pour les utilisateurs sans abonnement valide, avec abonnement expirant avant le retour, ou ayant atteint leur limite

### 2. Types de Statuts d'Abonnement
- ✅ **Abonnement valide** : L'utilisateur peut emprunter
- ⚠️ **Abonnement expirant bientôt** : Avertissement si l'abonnement expire dans moins de 7 jours
- ❌ **Abonnement expiré** : L'utilisateur ne peut pas emprunter
- ❌ **Aucun abonnement** : L'utilisateur n'a pas d'abonnement actif

### 3. Interface d'Administration

#### Dashboard Admin
- Accès rapide à la gestion des abonnements
- Statistiques sur les abonnements

#### Gestion des Abonnements (`/admin/abonnements`)
- **Liste des abonnements** : Vue d'ensemble de tous les abonnements avec statut
- **Création d'abonnement** : Formulaire pour créer un nouvel abonnement
- **Modification d'abonnement** : Édition des dates de début et fin
- **Suppression d'abonnement** : Suppression avec confirmation
- **Détails d'abonnement** : Vue détaillée d'un abonnement spécifique

#### Création d'Emprunt Améliorée
- **Vérification en temps réel** : Affichage du statut d'abonnement lors de la sélection d'un utilisateur
- **Validation automatique** : Le bouton de création est désactivé si l'abonnement n'est pas valide
- **Messages contextuels** : Informations claires sur pourquoi un emprunt ne peut pas être créé

## Structure de la Base de Données

### Table `abonnement`
```sql
CREATE TABLE abonnement (
    id SERIAL PRIMARY KEY,
    utilisateur_id INTEGER REFERENCES utilisateur(id),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL
);
```

### Relations
- Un utilisateur peut avoir plusieurs abonnements
- Seul l'abonnement actif (date actuelle entre date_debut et date_fin) est pris en compte

## API Endpoints

### Vérification d'Abonnement
```
GET /admin/emprunts/check-subscription/{userId}
```
Retourne :
```json
{
    "canEmprunter": true,
    "message": "✅ Abonnement valide jusqu'au 31/12/2024 (180 jours restants)"
}
```

### Vérification d'Emprunts
```
GET /admin/emprunts/check-emprunts/{userId}
```
Retourne :
```json
{
    "canEmprunterPlus": true,
    "message": "✅ 3 emprunt(s) possible(s) (2/5)"
}
```

### Gestion des Abonnements
```
GET    /admin/abonnements              # Liste des abonnements
GET    /admin/abonnements/create       # Formulaire de création
POST   /admin/abonnements/create       # Création d'abonnement
GET    /admin/abonnements/{id}         # Détails d'un abonnement
GET    /admin/abonnements/{id}/edit    # Formulaire de modification
POST   /admin/abonnements/{id}/edit    # Modification d'abonnement
POST   /admin/abonnements/{id}/delete  # Suppression d'abonnement
```

## Utilisation

### 1. Créer un Abonnement
1. Aller dans `/admin/abonnements`
2. Cliquer sur "Nouvel Abonnement"
3. Sélectionner l'utilisateur
4. Définir les dates de début et fin
5. Valider la création

### 2. Créer un Emprunt avec Vérification
1. Aller dans `/admin/emprunts/create`
2. Sélectionner un utilisateur
3. Le système affiche automatiquement le statut de l'abonnement
4. Si l'abonnement est valide, sélectionner un livre
5. Compléter les autres informations
6. Créer l'emprunt

### 3. Gérer les Abonnements Existants
1. Aller dans `/admin/abonnements`
2. Utiliser les actions disponibles (voir, modifier, supprimer)
3. Les statuts sont affichés en temps réel

## Messages d'Abonnement

### Messages Positifs
- `✅ Abonnement valide jusqu'au DD/MM/YYYY (X jours restants)`

### Messages d'Avertissement
- `⚠️ Abonnement expire dans X jour(s) - DD/MM/YYYY`

### Messages d'Erreur
- `❌ Aucun abonnement actif trouvé`
- `❌ Abonnement expiré le DD/MM/YYYY`

## Messages d'Emprunts

### Messages Positifs
- `✅ X emprunt(s) possible(s) (actuels/limite)`

### Messages d'Avertissement
- `⚠️ Plus que X emprunt(s) possible(s) (actuels/limite)`

### Messages d'Erreur
- `❌ Limite d'emprunts atteinte (actuels/limite)`
- `❌ Utilisateur ou adhérent non trouvé`
- `❌ Limite d'emprunts non définie`

## Validation

### Côté Client (JavaScript)
- Vérification en temps réel lors de la sélection d'utilisateur
- Désactivation du formulaire si l'abonnement n'est pas valide
- Affichage de messages informatifs

### Côté Serveur (Java)
- Vérification obligatoire lors de la création d'emprunt
- Validation des dates d'abonnement
- Gestion des erreurs avec messages explicites

## Sécurité

- Seuls les administrateurs peuvent gérer les abonnements
- Vérification obligatoire avant tout emprunt
- Validation des dates côté serveur
- Protection contre les abonnements invalides

## Maintenance

### Scripts SQL Utiles

#### Insérer des Abonnements de Test
```sql
-- Voir le fichier src/main/resources/sql/insert_abonnements.sql
```

#### Vérifier les Abonnements Actifs
```sql
SELECT u.nom, u.prenom, a.date_debut, a.date_fin
FROM abonnement a
JOIN utilisateur u ON a.utilisateur_id = u.id
WHERE CURRENT_DATE BETWEEN a.date_debut AND a.date_fin;
```

#### Trouver les Abonnements Expirés
```sql
SELECT u.nom, u.prenom, a.date_fin
FROM abonnement a
JOIN utilisateur u ON a.utilisateur_id = u.id
WHERE a.date_fin < CURRENT_DATE;
```

## Améliorations CSS

### Pages Améliorées
- **Page d'inscription** : Design moderne avec gradient et animations
- **Page de profil** : Interface utilisateur avec avatar et statistiques
- **Page de réservation** : Cartes modernes avec statuts visuels
- **Page des livres** : Grille responsive avec cartes de livres
- **Page de gestion des réservations** : Interface admin améliorée

### Fonctionnalités CSS
- **Design responsive** : Adaptation automatique aux différentes tailles d'écran
- **Animations** : Effets de survol et transitions fluides
- **Icônes Font Awesome** : Interface plus intuitive avec icônes
- **Couleurs cohérentes** : Palette de couleurs unifiée
- **Cartes modernes** : Remplacement des tableaux par des cartes élégantes

## Évolutions Futures

- [ ] Notifications automatiques pour les abonnements expirant
- [ ] Renouvellement automatique d'abonnements
- [ ] Types d'abonnements (mensuel, annuel, etc.)
- [ ] Historique des abonnements
- [ ] Rapports sur les abonnements
- [ ] Intégration avec un système de paiement

## Corrections Apportées

### Disponibilité des Livres
- **Problème** : La vérification de disponibilité ne prenait pas en compte les réservations ET les emprunts en cours
- **Solution** : Modification de la requête SQL pour inclure les réservations ET les emprunts actifs dans le calcul
- **Résultat** : La disponibilité affiche maintenant le nombre réel d'exemplaires disponibles (total - empruntés en cours - réservés)

### Vérification d'Abonnement
- **Problème** : Erreur de template Thymeleaf avec les dates
- **Solution** : Conversion de LocalDateTime en LocalDate pour la comparaison
- **Résultat** : Affichage correct du statut des abonnements

### Retour de Livres
- **Problème** : Erreur "Statut non trouvé" lors du retour de livres
- **Solution** : Correction du nom de statut de "disponible" vers "dispo" (selon la base de données)
- **Résultat** : Retour de livres fonctionne correctement

### Vérification d'Expiration d'Abonnement
- **Problème** : Les emprunts pouvaient être créés même si l'abonnement expirait avant la date de retour
- **Solution** : Ajout de la méthode `canEmprunterUntil(userId, dateRetour)` qui vérifie la validité jusqu'à la date de retour
- **Résultat** : Le système empêche maintenant la création d'emprunts impossibles à retourner

### Vérification de Limite d'Emprunts
- **Problème** : Les utilisateurs pouvaient emprunter plus de livres que leur limite autorisée
- **Solution** : Ajout de la méthode `canEmprunterPlus(userId)` qui vérifie le nombre d'emprunts en cours vs la limite de l'adhérent
- **Résultat** : Le système empêche maintenant la création d'emprunts au-delà de la limite autorisée 