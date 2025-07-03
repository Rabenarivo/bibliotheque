# Correction du Problème de Disponibilité des Livres

## Problème Identifié

Le système affichait incorrectement la disponibilité des livres :
- **Exemple** : Livre "Bases de données avancées" avec 2 exemplaires
- **Affichage incorrect** : 8 exemplaires indisponibles sur 2 (impossible !)
- **Résultat** : -6 exemplaires disponibles (négatif, impossible !)

**Problème supplémentaire** : Côté client, les utilisateurs ne pouvaient plus réserver de livres même si des exemplaires étaient disponibles.

## Cause du Problème

### 1. Problème Principal (Admin)
Le problème venait de la requête SQL qui utilisait des `LEFT JOIN` multiples avec des `COUNT()` dans une seule requête. Cela causait des **doublons** dans le comptage car :

1. Un emprunt peut avoir plusieurs détails
2. Un livre peut avoir plusieurs réservations
3. Les JOINs créaient des lignes multiples pour le même emprunt

### 2. Problème Côté Client
La requête `findAvailableLivres` utilisée côté client ne calculait **pas** la disponibilité des livres. Elle retournait seulement les livres accessibles à l'adhérent, mais sans information sur le nombre d'exemplaires disponibles.

### Requête Problématique (Ancienne)
```sql
SELECT l.id AS id, l.titre AS titre, l.auteur AS auteur, l.age AS age, l.image AS image 
FROM Livre l 
JOIN LivreAdherant la ON l.id = la.livre.id 
JOIN Adherant a ON la.adherant.id = a.id 
JOIN Utilisateur u ON u.idAdherant.id = a.id 
WHERE u.id = :id
```

## Solution Implémentée

### 1. Correction Admin (Requête Corrigée)
```sql
SELECT 
    l.id AS livreId,
    l.titre AS titre,
    l.examplaire AS totalExemplaires,
    (COALESCE(historique_count, 0) + COALESCE(reservation_count, 0) + COALESCE(emprunt_count, 0)) AS exemplairesIndisponibles,
    (l.examplaire - (COALESCE(historique_count, 0) + COALESCE(reservation_count, 0) + COALESCE(emprunt_count, 0))) AS exemplairesDisponibles
FROM livre l
LEFT JOIN (
    SELECT 
        hl.livre_id,
        COUNT(hl.id) as historique_count
    FROM historique_livre hl
    JOIN statut_livre sl ON hl.statut_id = sl.id
    WHERE sl.nom = 'en_cours_de_pret'
    GROUP BY hl.livre_id
) h ON l.id = h.livre_id
LEFT JOIN (
    SELECT 
        r.livre_id,
        COUNT(r.id) as reservation_count
    FROM reservation r
    WHERE r.est_validee = false
    GROUP BY r.livre_id
) res ON l.id = res.livre_id
LEFT JOIN (
    SELECT 
        ed.livre_id,
        COUNT(ed.id) as emprunt_count
    FROM emprunt_detail ed
    JOIN emprunt e ON ed.emprunt_id = e.id
    WHERE e.statut_emprunt IN ('en_cours', 'En cours')
    GROUP BY ed.livre_id
) emp ON l.id = emp.livre_id
WHERE l.id = :idlivre
```

### 2. Correction Côté Client (Requête Corrigée)
```sql
SELECT 
    l.id AS id,
    l.titre AS titre,
    l.auteur AS auteur,
    l.age AS age,
    l.image AS image,
    (l.examplaire - (COALESCE(historique_count, 0) + COALESCE(reservation_count, 0) + COALESCE(emprunt_count, 0))) AS exemplairesDisponibles
FROM 
    livre l
JOIN 
    livre_adherant la ON l.id = la.livre_id
JOIN 
    adherant a ON la.adherant_id = a.id
JOIN 
    utilisateur u ON u.id_adherant = a.id
LEFT JOIN (
    SELECT 
        hl.livre_id,
        COUNT(hl.id) as historique_count
    FROM historique_livre hl
    JOIN statut_livre sl ON hl.statut_id = sl.id
    WHERE sl.nom = 'en_cours_de_pret'
    GROUP BY hl.livre_id
) h ON l.id = h.livre_id
LEFT JOIN (
    SELECT 
        r.livre_id,
        COUNT(r.id) as reservation_count
    FROM reservation r
    WHERE r.est_validee = false
    GROUP BY r.livre_id
) res ON l.id = res.livre_id
LEFT JOIN (
    SELECT 
        ed.livre_id,
        COUNT(ed.id) as emprunt_count
    FROM emprunt_detail ed
    JOIN emprunt e ON ed.emprunt_id = e.id
    WHERE e.statut_emprunt IN ('en_cours', 'En cours')
    GROUP BY ed.livre_id
) emp ON l.id = emp.livre_id
WHERE 
    u.id = :id
```

## Améliorations Apportées

### 1. **Sous-requêtes avec GROUP BY**
- Chaque type de comptage (emprunts, réservations, historique) est fait dans une sous-requête séparée
- `GROUP BY` évite les doublons dans chaque catégorie

### 2. **COALESCE pour Gérer les NULL**
- `COALESCE(historique_count, 0)` remplace NULL par 0
- Évite les erreurs de calcul avec des valeurs NULL

### 3. **JOINs Conditionnels**
- Les sous-requêtes filtrent les données avant le JOIN principal
- Réduit la complexité et améliore les performances

### 4. **Calcul de Disponibilité Côté Client**
- La requête `findAvailableLivres` calcule maintenant la disponibilité
- Les utilisateurs voient le bon nombre d'exemplaires disponibles
- Le bouton de réservation s'affiche correctement

## Fichiers Modifiés

1. **`LivreRepository.java`**
   - `findLivreDisponibiliteById()` - Requête corrigée (admin)
   - `findLivreDisponibiliteReelleById()` - Requête corrigée (admin)
   - `findAvailableLivres()` - **NOUVELLE** requête corrigée (client)

2. **`test_disponibilite_corrigee_v2.sql`**
   - Script de test pour vérifier la correction admin
   - Comparaison entre ancienne et nouvelle requête

3. **`test_disponibilite_client.sql`** - **NOUVEAU**
   - Script de test pour vérifier la correction côté client
   - Test de la requête `findAvailableLivres`

## Test de la Correction

### Avant la Correction
```
Admin:
Livre: "Bases de données avancées"
Total exemplaires: 2
Exemplaires indisponibles: 8 (❌ Incorrect)
Exemplaires disponibles: -6 (❌ Impossible)

Client:
Bouton de réservation: Désactivé (❌ Incorrect)
Message: "Indisponible" (❌ Incorrect)
```

### Après la Correction
```
Admin:
Livre: "Bases de données avancées"
Total exemplaires: 2
Exemplaires indisponibles: 2 (✅ Correct)
Exemplaires disponibles: 0 (✅ Correct)

Client:
Bouton de réservation: Activé si exemplaires > 0 (✅ Correct)
Message: "X exemplaire(s) disponible(s)" (✅ Correct)
```

## Vérification

Pour tester la correction :

1. **Exécuter les scripts de test** :
   ```bash
   # Test admin
   source src/main/resources/sql/test_disponibilite_corrigee_v2.sql
   
   # Test client
   source src/main/resources/sql/test_disponibilite_client.sql
   ```

2. **Vérifier dans l'interface admin** :
   - Aller sur `/admin/emprunts/create`
   - Sélectionner un utilisateur
   - Vérifier que la disponibilité des livres est correcte

3. **Vérifier dans l'interface client** :
   - Aller sur `/livres`
   - Vérifier que les livres affichent le bon nombre d'exemplaires disponibles
   - Vérifier que le bouton de réservation s'affiche correctement

4. **Vérifier les logs** :
   - Les requêtes SQL ne doivent plus afficher de comptages impossibles
   - Les valeurs doivent être cohérentes avec le nombre d'exemplaires

## Impact sur le Système

- ✅ **Disponibilité correcte** : Les livres affichent maintenant le bon nombre d'exemplaires disponibles
- ✅ **Validation des emprunts** : Le système empêche correctement les emprunts de livres non disponibles
- ✅ **Interface utilisateur** : Les messages d'erreur sont maintenant cohérents
- ✅ **Performance** : Les requêtes sont plus efficaces avec moins de JOINs complexes
- ✅ **Réservations côté client** : Les utilisateurs peuvent maintenant réserver des livres disponibles
- ✅ **Cohérence** : Admin et client utilisent la même logique de calcul de disponibilité

## Maintenance

Pour éviter ce type de problème à l'avenir :

1. **Toujours tester les requêtes complexes** avec des données réelles
2. **Utiliser des sous-requêtes** pour les comptages multiples
3. **Vérifier la cohérence** des résultats (pas de valeurs négatives ou impossibles)
4. **Documenter les requêtes complexes** avec des exemples de données
5. **Tester les deux interfaces** (admin et client) après chaque modification
6. **Vérifier que les projections** incluent tous les champs nécessaires 