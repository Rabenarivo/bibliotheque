# Système de Gestion des Emprunts - Côté Admin

## Vue d'ensemble

Ce système permet aux administrateurs de gérer complètement les emprunts de livres dans la bibliothèque avec des vérifications automatiques de disponibilité et d'accès utilisateur.

## Fonctionnalités

### 🔐 Vérifications Automatiques

1. **Vérification d'accès utilisateur** : Utilise la même fonction que côté client pour vérifier si un utilisateur peut accéder à un livre spécifique
2. **Vérification de disponibilité** : Utilise la même fonction que côté client pour vérifier si un livre est disponible en exemplaires

### 📚 Gestion Complète des Emprunts

- **Création d'emprunts** : Créer de nouveaux emprunts avec vérifications en temps réel
- **Modification d'emprunts** : Modifier les détails d'un emprunt existant
- **Retour de livres** : Marquer un livre comme retourné
- **Suppression d'emprunts** : Supprimer un emprunt (avec gestion des statuts)
- **Consultation des détails** : Voir tous les détails d'un emprunt

### 📊 Tableau de Bord Admin

- Statistiques en temps réel
- Navigation facile entre les différentes fonctionnalités
- Vue d'ensemble de l'activité de la bibliothèque

## Structure des Fichiers

### Contrôleurs
- `AdminEmpruntController.java` : Gestion CRUD des emprunts
- `AdminDashboardController.java` : Tableau de bord admin

### Services
- `EmpruntService.java` : Logique métier des emprunts
- `EmpruntDetailsService.java` : Gestion des détails d'emprunt
- `LivreServices.java` : Services pour les livres (vérifications)

### Repositories
- `EmpruntRepository.java` : Accès aux données des emprunts
- `EmpruntDetailsRepository.java` : Accès aux détails d'emprunt

### Templates HTML
- `admin/dashboard.html` : Tableau de bord principal
- `admin/emprunts/list.html` : Liste des emprunts
- `admin/emprunts/create.html` : Formulaire de création
- `admin/emprunts/edit.html` : Formulaire de modification
- `admin/emprunts/details.html` : Détails d'un emprunt

## Utilisation

### 1. Accès au Système
- Connectez-vous en tant qu'administrateur
- Vous serez redirigé vers le tableau de bord admin

### 2. Création d'un Emprunt
1. Cliquez sur "Nouvel Emprunt" ou "Créer un Emprunt"
2. Sélectionnez l'utilisateur
3. Sélectionnez le livre (vérifications automatiques)
4. Choisissez le type d'emprunt
5. La date de retour est calculée automatiquement
6. Validez l'emprunt

### 3. Gestion des Emprunts
- **Liste** : Voir tous les emprunts avec filtres par statut
- **Détails** : Consulter les informations complètes
- **Modifier** : Changer les paramètres d'un emprunt
- **Retourner** : Marquer un livre comme retourné
- **Supprimer** : Supprimer un emprunt

### 4. Vérifications Automatiques

#### Vérification d'Accès Utilisateur
- Utilise la fonction `getAllLivres(userId)` du côté client
- Vérifie si l'utilisateur a le droit d'emprunter ce livre
- Affichage en temps réel dans l'interface

#### Vérification de Disponibilité
- Utilise la fonction `getLivreDisponibiliteById(livreId)` du côté client
- Vérifie le nombre d'exemplaires disponibles
- Empêche la création d'emprunts si le livre n'est pas disponible

## API Endpoints

### Emprunts
- `GET /admin/emprunts` : Liste des emprunts
- `GET /admin/emprunts/create` : Formulaire de création
- `POST /admin/emprunts/create` : Créer un emprunt
- `GET /admin/emprunts/{id}` : Détails d'un emprunt
- `GET /admin/emprunts/{id}/edit` : Formulaire de modification
- `POST /admin/emprunts/{id}/edit` : Modifier un emprunt
- `POST /admin/emprunts/{id}/return` : Retourner un livre
- `POST /admin/emprunts/{id}/delete` : Supprimer un emprunt

### API de Vérification
- `GET /admin/emprunts/check-availability/{livreId}` : Vérifier disponibilité
- `GET /admin/emprunts/user-books/{userId}` : Livres accessibles à un utilisateur

### Tableau de Bord
- `GET /admin/dashboard` : Tableau de bord principal

## Sécurité

- Vérification de session admin sur toutes les routes
- Validation des données côté serveur
- Vérifications d'accès et de disponibilité obligatoires
- Gestion des erreurs avec messages appropriés

## Base de Données

### Tables Principales
- `emprunt` : Informations générales des emprunts
- `emprunt_detail` : Détails spécifiques (livre, dates)
- `utilisateur` : Informations des utilisateurs
- `livre` : Catalogue des livres
- `historique_livre` : Suivi des statuts des livres

### Relations
- Un emprunt peut avoir plusieurs détails (un par livre)
- Chaque détail est lié à un livre et un emprunt
- Les utilisateurs sont liés à des adhérents pour les droits d'accès

## Personnalisation

### Ajout de Nouvelles Vérifications
1. Créez une nouvelle méthode dans `LivreServices`
2. Ajoutez l'appel API dans le contrôleur
3. Intégrez la vérification dans les templates JavaScript

### Modification des Statuts
- Les statuts sont configurables dans la base de données
- Ajoutez de nouveaux statuts dans la table `statut_livre`
- Mettez à jour les templates pour afficher les nouveaux statuts

## Support

Pour toute question ou problème :
1. Vérifiez les logs de l'application
2. Consultez la documentation de la base de données
3. Testez les vérifications d'accès et de disponibilité
4. Vérifiez les permissions utilisateur 