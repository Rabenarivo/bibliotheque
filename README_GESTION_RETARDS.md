# Gestion des Retards - Système de Bibliothèque

## Vue d'ensemble

Le système de gestion des retards permet de détecter automatiquement les emprunts en retard et de fournir des outils pour les gérer efficacement.

## Fonctionnalités

### 1. Détection Automatique des Retards

- **Détection en temps réel** : À chaque accès à la page des emprunts, le système vérifie automatiquement les retards
- **Mise à jour automatique** : Les emprunts en retard voient leur statut passer de "en_cours" à "retard"
- **Calcul des jours de retard** : Affichage du nombre exact de jours de retard

### 2. Interface Utilisateur

#### Tableau des Emprunts
- **Colonne "Jours de retard"** : Affiche le nombre de jours de retard pour chaque emprunt
- **Statut visuel** : Les emprunts en retard sont clairement identifiés
- **Bouton spécial** : "Retourner (Retard)" pour les emprunts en retard

#### Alertes
- **Notification des retards** : Affichage du nombre total d'emprunts en retard
- **Message d'alerte** : ⚠️ Attention avec le nombre d'emprunts en retard

### 3. Actions Disponibles

#### Pour les Emprunts en Cours
- Bouton "Retourner" (vert) : Retour normal d'un livre

#### Pour les Emprunts en Retard
- Bouton "Retourner (Retard)" (rouge) : Retour avec indication du retard
- Message de confirmation spécial
- Affichage du nombre de jours de retard dans le message de succès

## Implémentation Technique

### Services

#### EmpruntService
```java
// Détection automatique des retards
public void detecterEtMettreAJourRetards()

// Récupération des emprunts en retard
public List<Emprunt> getEmpruntsEnRetard()

// Calcul des jours de retard
public int getJoursDeRetard(Integer empruntId)

// Vérification si un emprunt est en retard
public boolean isEmpruntEnRetard(Integer empruntId)
```

#### Repository
```java
// Requêtes optimisées pour les retards
@Query("SELECT COUNT(e) FROM Emprunt e WHERE e.dateRetour < CURRENT_DATE AND e.statutEmprunt IN ('en_cours', 'En cours')")
long countEmpruntsEnRetard()

// Calcul des jours de retard dans la projection
CASE WHEN e.dateRetour < CURRENT_DATE AND e.statutEmprunt IN ('en_cours', 'En cours') 
THEN CAST(CURRENT_DATE - e.dateRetour AS integer) ELSE 0 END AS joursDeRetard
```

### Contrôleurs

#### AdminEmpruntController
- **Détection automatique** : À chaque chargement de la liste
- **Endpoint spécial** : `/admin/emprunts/{id}/return-retard` pour les retards
- **Messages personnalisés** : Incluant le nombre de jours de retard

### Templates

#### list.html
- **Affichage conditionnel** : Boutons différents selon le statut
- **Calcul des retards** : Affichage en temps réel
- **Styles CSS** : Couleurs distinctives pour les retards

## Base de Données

### Statut "retard"
Le statut "retard" doit être ajouté dans la table `statut_livre` :

```sql
INSERT INTO statut_livre (nom) VALUES ('retard');
```

### Requêtes de Diagnostic
Voir les fichiers :
- `src/main/resources/sql/insert_statut_retard.sql`
- `src/main/resources/sql/test_retards.sql`

## Utilisation

### 1. Configuration Initiale
```bash
# Exécuter le script pour ajouter le statut "retard"
psql -d votre_base -f src/main/resources/sql/insert_statut_retard.sql
```

### 2. Test de la Fonctionnalité
```bash
# Exécuter le script de test
psql -d votre_base -f src/main/resources/sql/test_retards.sql
```

### 3. Utilisation Quotidienne
1. Accéder à `/admin/emprunts`
2. Les retards sont détectés automatiquement
3. Utiliser le bouton "Retourner (Retard)" pour les emprunts en retard
4. Vérifier les messages de confirmation

## Avantages

### Pour l'Administrateur
- **Visibilité immédiate** : Voir tous les retards d'un coup d'œil
- **Gestion simplifiée** : Boutons dédiés pour les retards
- **Suivi précis** : Nombre exact de jours de retard

### Pour le Système
- **Automatisation** : Détection sans intervention manuelle
- **Cohérence** : Statuts mis à jour automatiquement
- **Performance** : Requêtes optimisées

## Maintenance

### Vérifications Régulières
1. **Statuts de livre** : S'assurer que le statut "retard" existe
2. **Requêtes de performance** : Vérifier les temps de réponse
3. **Logs** : Surveiller les erreurs de mise à jour

### Scripts de Maintenance
```sql
-- Vérifier les retards non détectés
SELECT COUNT(*) FROM emprunt 
WHERE date_retour < CURRENT_DATE 
AND statut_emprunt IN ('en_cours', 'En cours');

-- Nettoyer les anciens retards
UPDATE emprunt 
SET statut_emprunt = 'retard' 
WHERE date_retour < CURRENT_DATE 
AND statut_emprunt IN ('en_cours', 'En cours');
```

## Sécurité

### Validation des Données
- Vérification de l'existence de l'emprunt
- Validation des dates
- Gestion des erreurs avec messages appropriés

### Permissions
- Accès réservé aux administrateurs
- Vérification des droits avant modification

## Évolutions Futures

### Fonctionnalités Possibles
1. **Notifications automatiques** : Emails aux utilisateurs en retard
2. **Amendes** : Calcul automatique des pénalités
3. **Rapports** : Statistiques détaillées des retards
4. **Rappels** : Notifications avant la date de retour

### Améliorations Techniques
1. **Tâches planifiées** : Détection automatique quotidienne
2. **Cache** : Optimisation des performances
3. **API REST** : Endpoints pour les applications mobiles 