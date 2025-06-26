
# 📊 Projet Kafka Streams – KazaaMovies (Java)

## 🎯 Objectif
Créer une application Java utilisant **Kafka Streams** pour analyser en temps réel les statistiques de visionnage et de notation de films, et exposer les résultats via une **API REST**.

---

## 👥 Répartition des rôles (équipe de 5)

### 👤 1. Responsable **Vues** (Kafka Stream `views`)
**Objectif** : Traiter le topic `views` pour chaque film et produire :
- Le **nombre total de vues** par catégorie : `start_only`, `half`, `full`
- Les **vues sur les 5 dernières minutes** (fenêtre temporelle glissante)
- Stocker les résultats dans un **KeyValueStore** Kafka
- Fournir des méthodes `getViewsStats(filmId)` accessibles par l’API

### 👤 2. Responsable **Likes** (Kafka Stream `likes`)
**Objectif** : Traiter le topic `likes` pour chaque film :
- Calculer le **score moyen** par film
- Maintenir des **top 10 / worst 10** selon le score
- Croiser les données avec les vues pour exposer les films les plus / moins vus
- Gérer des stores pour rendre les résultats exploitables

### 👤 3. Responsable **API REST**
**Objectif** : Créer l'API pour exposer les données :
- `GET /movies/:id` → vues globales + 5 min + score
- `GET /stats/ten/best/score` → top 10 meilleurs scores
- `GET /stats/ten/worst/score` → 10 pires scores
- `GET /stats/ten/best/views` → 10 films les plus vus
- `GET /stats/ten/worst/views` → 10 films les moins vus
- Brancher l’API sur les stores Kafka (`ReadOnlyKeyValueStore`)

### 👤 4. Responsable **Tests**
**Objectif** : Garantir la qualité et stabilité du code
- Écrire des tests unitaires sur les topologies Kafka Streams (`TopologyTestDriver`)
- Couvrir les flux `views` et `likes`
- Tester les réponses des endpoints REST
- Générer un rapport de couverture

### 👤 5. Responsable **Infrastructure & Intégration**
**Objectif** : Préparer un environnement de dev fonctionnel
- Gérer `docker-compose.yml` avec :
  - Kafka, Zookeeper
  - Simulateur `movies-view-injector`
- Ajouter un service `Kafdrop` pour le debug (optionnel)
- S'assurer que l’application Java (`Main.java`) démarre correctement
- Documenter l'utilisation dans un `README.md`

---

## 🧱 Structure projet Java utilisée

```
kafka-stream/
├── src/
│   ├── main/
│   │   ├── java/org/esgi/project/java/streaming/
│   │   │   ├── StreamProcessing.java
│   │   │   └── Main.java
│   ├── test/...
├── resources/
│   └── kafka.properties
├── docker-compose.yml
├── pom.xml
```

---

## 📦 Livrables
- Code source complet (Kafka Streams + API REST)
- Tests unitaires fonctionnels
- Docker opérationnel (Kafka + simulateur)
- README avec instructions de lancement et endpoints
