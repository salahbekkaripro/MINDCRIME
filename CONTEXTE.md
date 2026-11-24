# Contexte partagé — MINDCRIME

Ce document donne les repères communs aux assistants utilisés par **les cinq membres de l'équipe**. Il ne représente pas le contexte personnel de D5. Les fichiers et l'état Git de la copie courante priment sur une description devenue ancienne. Les règles de travail figurent dans [AGENTS.md](AGENTS.md).

## Projet et objectifs

Projet universitaire M1 MIAGE Apprentissage — Université Paris Nanterre, année 2026–2027. MINDCRIME est un jeu d'enquête à moteur de raisonnement algorithmique Java. Version initiale : **0.0.1**.

Dépôt principal : https://github.com/salahbekkaripro/MINDCRIME

- **Enquêteur** : génération d'un crime fictif, de suspects, alibis, preuves et chronologie. Le joueur enquête ; la solution doit être unique et déductible des indices accessibles.
- **Alibi** : les utilisateurs incarnent les suspects et peuvent mentir. Le moteur recherche les contradictions et les scénarios compatibles, sans forcer une conclusion si plusieurs possibilités restent ouvertes.
- Les deux modes utilisent un moteur commun Java. Aucune API d'IA n'est nécessaire au raisonnement du jeu.

## Utilisateurs, domaines et premières fonctionnalités

| ID | Étudiant et compte | Domaine principal | Fonctionnalité prévue pour v0.1.0 |
| --- | --- | --- | --- |
| D1 | Mohamed BOUGHMADI — [MohamedBoughmadi](https://github.com/MohamedBoughmadi) | Génération procédurale et backtracking | Générer une enquête à solution unique |
| D2 | Yacine FERDJALLAH — [yaciiine78](https://github.com/yaciiine78) | Satisfaction et propagation de contraintes | Détecter les contradictions de témoignages |
| D3 | Melissa BENZIDANE — [mbenzidane17](https://github.com/mbenzidane17) | Graphes temporels et Dijkstra | Vérifier la faisabilité d'un alibi et des déplacements |
| D4 | Mohamet THIAM — [mhtthiam11](https://github.com/mhtthiam11) | Comportements des suspects et du détective | Interrogatoire cohérent des suspects |
| D5 | Salah-Eddine BEKKARI — [salahbekkaripro](https://github.com/salahbekkaripro) | Recherche d'hypothèses et explications | Éliminer les scénarios impossibles et expliquer les déductions |

Ces domaines identifient les contributions individuelles, sans réserver une couche technique à une personne. Tous participent aux tests, aux revues, à la documentation, à la CI/CD et aux livraisons. Chaque feature doit toucher **BD, backend, API REST et frontend**. Le backend et ses tests sont développés en priorité.

## Identifier la session de travail

L'utilisateur de l'assistant peut être D1, D2, D3, D4 ou D5. Le nom de branche et la demande fournissent des indices ; ils ne constituent pas une preuve d'identité. L'identité Git peut être mal configurée ou provenir d'un autre poste.

Pour démarrer une session sans ambiguïté, un membre peut écrire :

> Je suis D2, Yacine. Je travaille sur les contradictions pour v0.1.0. Lis AGENTS.md et CONTEXTE.md, puis examine ma branche et les fichiers avant de commencer.

Une branche par fonctionnalité est prévue. `feature/d5-deduction` existe lors de la rédaction de ce contexte ; les branches des autres membres doivent être vérifiées plutôt qu'inventées. Les noms de classes et packages décrivent le métier, pas l'étudiant ou la release. Le même module évolue entre les versions.

## Architecture cible propre au projet

**React + TypeScript → API REST → backend Java / Spring Boot → PostgreSQL**

Un monolithe modulaire, avec un moteur métier commun, séparé de Spring et de JPA :

```text
backend/src/main/java/fr/miage/mindcrime/
├── api/                         REST, DTO et notifications WebSocket
├── application/                 cas d'utilisation des deux modes, ports
├── moteur/
│   ├── modele/                  Partie et modèles métier communs
│   ├── generation/              D1
│   ├── contraintes/             D2
│   ├── chronologies/            D3
│   ├── parties/                 D4
│   ├── agents/                  D4
│   └── deduction/               D5
└── infrastructure/              persistance et configuration Spring
frontend/                        écran React/TypeScript D2 ; autres parcours à intégrer
doc/                             conception, dossier et suivi des livraisons
```

Ce découpage est une cible : vérifier quels répertoires existent réellement. **Partie** est l'entité pivot. D2 fournit la cohérence logique, D3 la faisabilité temporelle et D5 la recherche et les explications ; les cas d'utilisation coordonnent leurs échanges. D1 utilise cette vérification commune pour ses enquêtes. D4 porte les règles de parties et d'agents.

REST est prévu sous `/api/v1`, avec OpenAPI et erreurs `problem+json`. WebSocket complète les notifications ; le frontend ne doit contenir aucun algorithme métier. PostgreSQL, JPA, Flyway et les outils qualité sont prévus, mais ne doivent pas être présentés comme opérationnels sans inspection.

## État au moment de la rédaction

- Le premier dossier R1 est publié au commit `3f51020`, tagué `v0.0.1`. Ne pas modifier ce tag ou réécrire ce commit.
- Le dossier R1 historique est [doc/equipe/dossier.md](doc/equipe/dossier.md), compilé en [doc/equipe/dossier.pdf](doc/equipe/dossier.pdf), de quatre pages A4. Les contributions courantes sont séparées dans `doc/membres/` et seront assemblées sous `doc/releases/<version>/` après intégration.
- Un **socle backend non web** est préparé sur la branche D5 : Java 21, Spring Boot 4.0.8, Maven Wrapper 3.3.4 utilisant Maven 3.9.11. Il compile, produit un JAR et initialise le contexte Spring ; il ne fournit pas encore de serveur REST ou de partie jouable.
- Les sources fournissent désormais le parcours D2 : moteur de contradictions Java pur, cas d'utilisation, stockage JDBC, migrations Flyway, API REST Spring Boot et écran React/TypeScript. Le profil local utilise H2 sur disque ; PostgreSQL reste un profil à tester réellement. Le compte et les référentiels de démonstration sont des adaptateurs provisoires, pas le travail de D1 ou D4. La cible Java reste 21 ; les types et le numéro de migration D2 ne sont pas des contrats communs approuvés. Voir `backend/README.md` et la fiche F2 de D2 pour les commandes, résultats vérifiés et limites.
- La fiche D5-A, lorsqu'elle est présente sur une branche de travail, appartient à `doc/membres/D5-salah-eddine-bekkari/features/`. Sa revue par un autre membre ne doit pas être déclarée effectuée sans preuve. Les fiches des autres domaines appartiennent aux dossiers de leurs membres.
- Les ajouts présents dans un répertoire de travail ne sont pas nécessairement commités, publiés ou disponibles sur les branches des autres membres. Toujours vérifier.

Les réglages GitHub et invitations peuvent évoluer indépendamment du code. Ils ne se déduisent pas de ce fichier : ne pas affirmer leur état sans vérification si la tâche porte dessus.

## Commandes et environnement

Depuis la racine, avec `JAVA_HOME` pointant sur un JDK 21 :

```bash
./backend/mvnw -f backend/pom.xml verify
"$JAVA_HOME/bin/java" -jar backend/target/mindcrime-backend-0.1.0-SNAPSHOT.jar
make -C doc equipe
pdfinfo doc/equipe/dossier.pdf
```

Le JDK de la session initiale était dans `.tools/jdk-21`, ignoré par Git : sa présence n'est pas garantie chez les autres membres. Ne pas copier un chemin absolu personnel dans les commandes communes. Construire le frontend avant le JAR pour y embarquer l'écran ; voir `backend/README.md` pour les commandes Windows/POSIX, les JDK compatibles et les limites. Le JAR actuel démarre le serveur et le parcours D2 local. L'ancien build du socle sans tests ne validait aucune feature ; les vérifications réelles sont consignées dans le dossier D2, sans annoncer un jeu multijoueur complet.

## Versions et rendus

| Livraison | Date | Objectifs |
| --- | --- | --- |
| R1 | 20 octobre 2026 | Équipe, comptes, concept, usages, architecture et protocole IA ; quatre pages PDF |
| v0.1.0 / R2 | 24 novembre 2026 | Première feature majeure par étudiant, deux modes visés, CI et dossier actualisé |
| v0.2.0 / R3 | 8 décembre 2026 | Nouvelle feature par étudiant et conteneurisation |
| v1.0.0 / R4 | 19 janvier 2027 | Fonctionnalités finales prévues, stabilisation et proof-of-concept |
| R5 | 19 janvier 2027 | Soutenance ; transparents le 18 janvier à 23 h 59 |

Le point intermédiaire est le 1er décembre. R1 est dû à 23 h 59 ; les devoirs R2–R4 affichent 12 h 50 malgré un planning général à 23 h 59. Prévoir le dépôt à l'heure la plus tôt et consulter le devoir. Chaque membre dépose individuellement l'URL du commit exact retenu. Voir [doc/equipe/RELEASES.md](doc/equipe/RELEASES.md) pour les détails et [doc/equipe/PLAN_FEATURES.md](doc/equipe/PLAN_FEATURES.md) pour les quinze objectifs prévisionnels.

## Assistance IA et limites pédagogiques

Les étudiants fournissent idées, contenus, consignes et décisions. L'IA peut expliquer, générer et aider à corriger du texte ou du code, y compris l'algorithmique dans le cadre autorisé. Les étudiants relisent, comprennent, corrigent, optimisent et complètent manuellement ; ils doivent pouvoir expliquer leur travail sans aide.

Le protocole détaillé est [doc/equipe/AI_USAGE.md](doc/equipe/AI_USAGE.md). La fiche pédagogique fournie interdit l'IA lors des évaluations ; les consignes de l'enseignant encadrent les usages pendant le projet. Les objectifs d'assistance et les estimations du bilan documentaire R1 sont distincts ; chaque membre renseigne son propre bilan dans `doc/membres/<membre>/part-ia.json`.

## Mise à jour de ce contexte

Après un changement réel du projet, actualiser les rubriques concernées avec le périmètre et les vérifications disponibles. Ne pas inscrire comme terminé un travail seulement planifié. Pour une reprise de session, noter dans la fiche de feature le travail réalisé, les décisions, les tests exécutés et les étapes restantes ; garder ce fichier commun à l'équipe, sans journal privé ni secret.
