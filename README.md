# MINDCRIME

![Version initiale 0.0.1](https://img.shields.io/badge/version-0.0.1-blue)
![Licence à définir](https://img.shields.io/badge/licence-à_définir-lightgrey)
![GitHub Actions à configurer](https://img.shields.io/badge/GitHub_Actions-à_configurer-lightgrey)
![JaCoCo à configurer](https://img.shields.io/badge/JaCoCo-à_configurer-lightgrey)
![Checkstyle à configurer](https://img.shields.io/badge/Checkstyle-à_configurer-lightgrey)

Projet universitaire **M1 MIAGE Apprentissage — Université Paris Nanterre**.
Version initiale : **0.0.1**. Dépôt : [salahbekkaripro/MINDCRIME](https://github.com/salahbekkaripro/MINDCRIME).

MINDCRIME est un projet de jeu d'enquête interactif reposant sur un moteur de raisonnement algorithmique en Java. Au lancement R1, le dépôt contient la documentation de conception ; les fonctionnalités, technologies et livraisons ci-dessous constituent des objectifs prévisionnels. Aucune application, suite de tests ou chaîne CI/CD n'est encore implémentée.

## Deux modes de jeu prévus

- **Enquêteur** : le système devra générer un crime fictif, des suspects, des alibis, des preuves et une chronologie. Le joueur interrogera les suspects et analysera les indices pour identifier le coupable. Le moteur devra garantir une solution unique déductible des indices accessibles.
- **Alibi** : les utilisateurs incarneront les suspects, construiront leurs témoignages et répondront aux interrogatoires, avec la possibilité de mentir. Le moteur Java jouera le détective : il recherchera les contradictions et reconstruira les scénarios possibles.

Le caractère innovant recherché tient à l'inversion des rôles joueur/moteur et à un moteur logique commun aux deux modes, capable de fournir des explications fondées sur les contraintes. Cette ambition reste à implémenter et à évaluer.

## Architecture MINDCRIME envisagée

**Front → API REST → Back → BD**

Le backend sera une application Spring Boot unique, organisée en modules métier Java : un **monolithe modulaire**. Son moteur commun alimentera les deux modes. Ce choix permet de tester et d’intégrer les algorithmes de D1 à D5 dans un même programme.

| Couche | Choix MINDCRIME envisagé |
| --- | --- |
| Front | React + TypeScript : enquêtes, interrogatoires et chronologies |
| API REST | Spring Boot 4 / Spring MVC, /api/v1, OpenAPI / Swagger UI ; WebSocket pour les notifications |
| Back | Java 21 : moteur métier pur, cas d’utilisation et interfaces de persistance |
| BD | PostgreSQL 17, Spring Data JPA, migrations Flyway ; Bean Validation aux entrées API |
| Qualité | JUnit 5, Mockito, AssertJ, ArchUnit, JaCoCo, Checkstyle ; GitHub Actions, Codecov et SonarCloud prévus |

Arborescence cible propre au projet, à implémenter :

```text
backend/src/main/java/fr/miage/mindcrime/
├── api/
│   ├── rest/                 actions des joueurs et données visibles
│   ├── websocket/            notifications de partie
│   └── dto/                  entrées, réponses et erreurs API
├── application/
│   ├── enqueteur/            générer, interroger, accuser
│   ├── alibi/                témoigner, interroger, analyser
│   └── port/                 contrats d’accès au stockage
├── moteur/                   domaine commun, Java pur
│   ├── modele/               Partie, Enquete, Suspect, Preuve...
│   ├── generation/           D1 : génération et backtracking
│   ├── contraintes/          D2 : cohérence et contradictions
│   ├── chronologies/         D3 : temps, graphes et déplacements
│   ├── parties/              D4 : règles et états de partie
│   ├── agents/               D4 : comportements et interrogatoires
│   └── deduction/            D5 : hypothèses et explications
└── infrastructure/
    ├── persistence/          adaptateurs JPA, entités et dépôts
    └── configuration/        assemblage Spring, CORS, WebSocket
backend/src/main/resources/db/migration/  schéma Flyway
backend/src/test/java/fr/miage/mindcrime/  tests métier et intégration
frontend/src/                 écrans React, appels API, événements
doc/                          dossier, features, objectifs IA
site/                         page de projet prévue
```

Le moteur ne dépendra ni de Spring, ni de JPA, ni de l’interface. Les cas d’utilisation appelleront le moteur et les ports de stockage ; les adaptateurs JPA implémenteront ces ports. L’API appellera les cas d’utilisation et ne contiendra pas d’algorithmes métier. Des tests ArchUnit devront vérifier ces frontières. Cette séparation par ports et adaptateurs conserve les principes hexagonaux tout en organisant le code autour des besoins de MINDCRIME.

L’entité pivot sera **Partie**. Le backend et ses tests seront développés en premier, puis chaque feature sera reliée à sa persistance, à son API REST et à son écran. Les packages décrivent les responsabilités, pas les étudiants ni les releases ; les versions seront conservées par Git. Voir [l’architecture détaillée](doc/ARCHITECTURE.md).

## Alignement sur le modèle du professeur

Référence : [template-projet-m1-miage](https://github.com/pascalpoizat/template-projet-m1-miage), [guide](https://github.com/pascalpoizat/template-projet-m1-miage/blob/main/documentation.md), consultés le 20 octobre 2026 (commit `3a11a780f5912bb3876e7ed7902b5cd8f377eb8e`). Le modèle, ses outils et son `CLAUDE.md` sont facultatifs ; les règles du cours sont distinguées dans le guide.

Chaque étudiant doit porter **une feature par itération**, avec un effet sur **BD, backend, API REST et frontend**, et un algorithme non trivial identifié. Un CRUD, une optimisation seule ou une stabilisation ne suffit pas. Le [plan détaillé](doc/PLAN_FEATURES.md) propose quinze features, dont celles de v1.0.0 ; ces fonctionnalités sont prévisionnelles.

La démarche suivra l’ordre **idéation → back → tests → API REST → front**, avec validation de chaque étape avant la suivante. Chaque fiche précisera complexité, invariants, cas limites, alternatives, impacts des quatre couches et critères d’acceptation. Voir le [modèle de fiche](doc/features/MODELE-feature.md) et les [règles de contribution](CONTRIBUTING.md).

| Attente du cours | État local et suite prévue |
| --- | --- |
| Dossier Markdown/PDF, construction `make -C doc` | Disponible et vérifié localement ; quatre pages A4 |
| Araignée IA, dix axes, dans le dossier et par livraison | Générée depuis `doc/part-ia.json` ; objectifs prévisionnels, plafonds et bilan documentaire R1 estimé affichés séparément |
| CI, couverture et qualité | À implémenter ; aucun résultat annoncé |
| Documentation construite en CI, jointe aux releases avec le JAR | À implémenter ; aucune release automatique disponible |
| Page de projet publiée | À créer et publier ; adresse cible `https://salahbekkaripro.github.io/MINDCRIME/`, non annoncée comme active |
| Conteneurisation en v0.2.0 | Prévue ; PostgreSQL sous Compose envisagé dès v0.1.0 |
| Attribution des commits aux comptes des étudiants | Chaque membre doit vérifier son identité Git ; emails non fournis et non vérifiés |

React + TypeScript reste le choix de MINDCRIME : le frontend natif du modèle n’est pas imposé. L’organisation modulaire ci-dessus est conçue pour MINDCRIME ; les technologies et contrôles restent à mettre en place. Checkstyle complète les contrôles envisagés ; il ne remplace pas les tests.

## Équipe et répartition des algorithmes

| ID | Étudiant et compte développeur | Responsabilité | Algorithmes prévus |
| --- | --- | --- | --- |
| D1 | [Mohamed BOUGHMADI](https://github.com/boughbough) | Génération automatique des enquêtes | Génération procédurale, backtracking |
| D2 | [Yacine FERDJALLAH](https://github.com/yaciiine78) | Moteur logique et contradictions | Satisfaction et propagation de contraintes |
| D3 | [Melissa BENZIDANE](https://github.com/mbenzidane17) | Chronologies et déplacements | Graphes temporels, Dijkstra |
| D4 | [Mohamet THIAM](https://github.com/mhtthiam11) | Comportements des personnages, moteur de jeu et interactions multijoueurs | Recherche sous contraintes, expectimax, appariement biparti |
| D5 | [Salah-Eddine BEKKARI](https://github.com/salahbekkaripro) | Moteur de déduction et explications | Recherche d'hypothèses, élimination de scénarios impossibles |

Les cinq membres participeront également aux tests, aux revues de code et à l'intégration.

## Planning prévisionnel

| Livraison | Date | Objectif |
| --- | --- | --- |
| R1 | 20 octobre 2026 | Dossier de lancement, architecture, équipe, scénarios, protocole IA et dépôt Git |
| v0.1.0 | 24 novembre 2026 | Première version fonctionnelle des deux modes ; une fonctionnalité majeure identifiable par étudiant |
| v0.2.0 | 8 décembre 2026 | Amélioration algorithmique, nouvelles fonctionnalités et conteneurisation ; une nouvelle fonctionnalité majeure identifiable par étudiant |
| v1.0.0 | 19 janvier 2027 | Troisième feature algorithmique par étudiant, stabilisation et soutenance |

Le [planning détaillé des livraisons](doc/RELEASES.md) précise les fonctionnalités de chacun, les tâches collectives et les critères de chaque version. Nous préparons actuellement **R1**, version initiale **0.0.1** ; aucune release future n’est créée.

R1 et les identifiants développeurs sont dus le **20 octobre 2026 à 23 h 59**. Les devoirs R2, R3 et R4 affichent **12 h 50**, alors que le planning général mentionne 23 h 59 : l’équipe prévoit de déposer à l’heure la plus tôt et vérifiera les horaires des devoirs. Étape intermédiaire le **1er décembre 2026** ; transparents le **18 janvier 2027 à 23 h 59**, soutenance en séance le 19 janvier.

Chaque rendu est individuel et demande l’**URL du commit exact**. Premier état R1 publié : [commit `3f51020`](https://github.com/salahbekkaripro/MINDCRIME/commit/3f510204be40aec7ed44b90e1880e4603c6a38c2), tagué `v0.0.1`. Toute correction ultérieure doit être déposée avec son propre commit si elle est retenue pour le rendu. Le devoir des identifiants demande l’URL du profil GitHub. À la sortie de chaque séance, un commit et un tag SNAPSHOT sont attendus ; les livraisons seront figées avec les tags `v0.1.0`, `v0.2.0` et `v1.0.0`. Le commit et le tag initiaux sont conservés ; les corrections documentaires sont proposées par pull request.

Coefficients : R1 10 %, R2 20 %, R3 20 %, R4 25 %, R5 25 %. Le dossier est limité à quatre pages pour R1 ; il évoluera ensuite sans limite de pages pour R2/R3.

## Installation et exécution

Il n'existe pas encore d'instructions d'installation applicative : aucun backend, frontend, manifeste de dépendances ou fichier Docker Compose n'est présent au lancement. Les commandes de lancement et de test seront documentées lors de leur implémentation.

Pour régénérer uniquement le dossier PDF, utiliser Python 3 et ReportLab (déjà disponible dans l'environnement de rédaction) :

```bash
make -C doc
pdfinfo doc/dossier.pdf
```

Le contrat du modèle est respecté : `make -C doc` compile la source unique `doc/dossier.md` en `doc/dossier.pdf`, régénère l’araignée et refuse une sortie qui ne contient pas exactement quatre pages A4. Python 3, ReportLab et les polices DejaVu Sans sont nécessaires ; GNU Make fournit la commande commune. Sur un autre environnement, la dépendance Python `reportlab` peut être installée dans un environnement virtuel. `pdfinfo` permet de vérifier indépendamment le résultat.

## Mise en place et attendus du rendu R1

Le dossier couvre l’équipe de cinq étudiants et ses comptes développeurs, le projet et son caractère innovant, les scénarios d’usage, l’architecture front → API → back → BD, les frameworks envisagés, le protocole IA et l’URL du dépôt principal. Il est conservé en Markdown compilé en PDF dans `/doc` et devra être versionné puis actualisé avec chaque livraison. Un premier état R1 est publié au commit `3f51020`, tagué `v0.0.1` ; les corrections suivantes conservent cet historique.

La fiche pédagogique « Projet » **5E7MAPRO**, année **2026-2027**, transmise par l’équipe, vise un proof-of-concept livré en v1.0.0, un travail itératif, une architecture structurée, une chaîne CI/CD et une évaluation individuelle au sein du groupe. Son descriptif prévoit une autorisation possible de l’IA selon les règles du cours ; sa rubrique évaluation **interdit l’IA lors des évaluations**. Le protocole proposé ne vaut pas dérogation : les consignes propres à chaque livrable évalué encadrent l’assistance autorisée.

Le détail de la mise en place prévue et des responsabilités figure dans [ARCHITECTURE.md](doc/ARCHITECTURE.md). Deux graphiques présentent séparément les objectifs prévisionnels avec leurs plafonds et l’usage documentaire R1 estimé pour la rédaction de R1 ; un tableau rassemble les pourcentages. Le bilan est limité aux documents et au générateur PDF, sans code applicatif ; les axes sans activité sont N/A et les dépassements sont expliqués dans le protocole IA. Après chaque feature et à chaque livraison, l’équipe mettra les données à jour ; `make -C doc` régénérera le graphique et le PDF. Les pourcentages ne sont pas calculés automatiquement.

## Documentation et protocole IA

- [Dossier de lancement R1 — Markdown](doc/dossier.md)
- [Dossier de lancement R1 — PDF A4, quatre pages](doc/dossier.pdf)
- [Protocole d'utilisation de l'IA](doc/AI_USAGE.md)
- [Plan des quinze features et impacts sur les quatre couches](doc/PLAN_FEATURES.md)
- [Livraisons, échéances et responsabilités](doc/RELEASES.md)
- [Araignée IA — données](doc/part-ia.json) et [graphique](doc/part-ia.svg)

**Développement assisté par IA avec conception, revue et validation humaines systématiques.** Le guide autorise l’IA sans l’imposer. ChatGPT, Codex et GitHub Copilot sont envisagés pour proposer des solutions, générer du texte ou du code, aider au débogage et suggérer des tests. Les étudiants fournissent les idées, les contenus et les consignes, puis pilotent la génération. Ils relisent chaque proposition, corrigent, optimisent et peuvent coder des parties à la main. L’IA pourra aussi aider à concevoir et implémenter les algorithmes dans le périmètre autorisé ; les étudiants devront en maîtriser le fonctionnement, la complexité et les limites. Chaque contribution devra être comprise, revue, testée puis validée par des humains ; les usages significatifs seront documentés. Chaque étudiant devra pouvoir expliquer son code. Les algorithmes métier devront fonctionner en Java sans dépendance obligatoire à une API d'IA.

Ce protocole décrit l’organisation prévue de l’équipe. Aucun usage ne devra dépasser les autorisations pédagogiques applicables. Les badges affichent des intentions et des états à configurer, sans attester de résultats de tests, de couverture ou de qualité. La licence reste à définir.
