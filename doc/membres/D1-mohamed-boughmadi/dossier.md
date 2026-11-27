# Contribution D1 — Mohamed BOUGHMADI

- Compte GitHub : [MohamedBoughmadi](https://github.com/MohamedBoughmadi)
- Domaine : Génération procédurale et backtracking
- Version en préparation : v0.1.0
- Statut : implémentation complète des 4 couches réalisée et vérifiée en local sur la branche `mohamed_boughmadi` ; validée par @salahbekkaripro et fusionnée sur main via https://github.com/salahbekkaripro/MINDCRIME/pull/12.

## Feature portée

**F1 — Générer une enquête à solution unique** (parcours autonome S1).  
Fiche détaillée : [v0.1.0-F1-enquete-solution-unique.md](features/v0.1.0-F1-enquete-solution-unique.md).

## Conception et algorithme

La conception complète (modèle Java pur, 6 invariants, structures de données, algorithme en deux phases — backtracking sous contraintes spatio-temporelles + sélection gloutonne d'indices —, exemple reproductible sur `seed = 42`, complexité, alternatives écartées et limites) est détaillée dans [la fiche F1](features/v0.1.0-F1-enquete-solution-unique.md).

## Réalisation dans les quatre couches

| Couche | Travail réalisé | Fichiers ou contrats |
| --- | --- | --- |
| BD | Migration Flyway créant les tables `partie`, `enquete`, `enquete_suspect`, `enquete_indice` avec clés étrangères et index ; entités JPA distinctes du modèle pur et adaptateur de persistance. | `backend/src/main/resources/db/migration/V1__d1_enquetes.sql`, `backend/src/main/resources/application.properties`, `backend/src/main/java/fr/miage/mindcrime/infrastructure/persistence/` (`PartieEntity`, `EnqueteEntity`, `EnqueteSuspectEntity`, `EnqueteIndiceEntity`, `SpringDataPartieRepository`, `SpringDataEnqueteRepository`, `EnqueteRepositoryAdapter`) |
| Backend | Moteur Java 21 pur (zéro import Spring/JPA), port sortant et cas d'utilisation hexagonaux, configuration Spring Boot 3.5.0. | `backend/pom.xml`, `backend/src/main/java/fr/miage/mindcrime/moteur/modele/` (9 types), `backend/src/main/java/fr/miage/mindcrime/moteur/generation/` (`ParametresGeneration`, `ResultatGeneration`, `VerificateurUnicite`, `GenerateurEnqueteBacktracking`), `backend/src/main/java/fr/miage/mindcrime/application/` (`EnqueteRepositoryPort`, `GenererEnqueteUseCase`, `ConsulterEnqueteUseCase`, exceptions), `EnqueteConfiguration.java`, `MindcrimeApplication.java` |
| API REST | Contrôleur `/api/v1` avec DTOs filtrant le coupable et la vérité terrain, validation Bean Validation et gestion des erreurs au format `application/problem+json` (`400`, `404`, `422`). | `POST /api/v1/parties`, `POST /api/v1/parties/{partieId}/enquetes`, `GET /api/v1/parties/{partieId}/enquetes/{enqueteId}` — `backend/src/main/java/fr/miage/mindcrime/api/rest/` (`EnqueteController`, `ApiExceptionHandler`) et `api/dto/` (`EnqueteVisibleDto`, `GenererEnqueteRequest`, etc.) |
| Frontend | Écran React + TypeScript (Vite) permettant de créer une partie, saisir une graine et un nombre de suspects (3 à 6), générer une enquête, la recharger via `GET` et afficher crime, suspects et indices visibles sans logique métier côté client. | `frontend/package.json`, `frontend/tsconfig.json`, `frontend/vite.config.ts`, `frontend/index.html`, `frontend/src/` (`App.tsx`, `main.tsx`, `styles.css`, `api/enqueteApi.ts`, `types/enquete.ts`) |

## Vérifications

- **Tests unitaires, ArchUnit et intégration REST :**
  - Commande : `mvn -f backend/pom.xml test` (Maven 3.9.9, OpenJDK 25 LTS compilé avec `--release 21`)
  - Résultat constaté : `Tests run: 10, Failures: 0, Errors: 0, Skipped: 0` — `BUILD SUCCESS` (`GenerateurEnqueteBacktrackingTest` : 5, `ArchitectureMoteurTest` : 1, `EnqueteControllerIntegrationTest` : 4).
- **Build TypeScript + Vite :**
  - Commande : `npm --prefix frontend run build` (Node v25.8.2, npm 11.11.1)
  - Résultat constaté : compilation `tsc -b` sans erreur, `28 modules transformed`, `built in 588ms`.
- **Démonstration en live (parcours S1) :**
  - Commandes : `mvn -f backend/pom.xml spring-boot:run` (`http://localhost:8080`) et `npm --prefix frontend run dev` (`http://localhost:5173`).
  - Constats dans le navigateur et DevTools Network : création de partie (`POST /api/v1/parties` → `201`, `PARTIE-ea2902d9`), génération (`POST /api/v1/parties/PARTIE-ea2902d9/enquetes` → `201`, `ENQ-PARTIE-ea2902d9-42`, `seed = 42`, `5 suspects`), consultation (`GET` → `200`). Inspection du JSON retourné : crime en `Jardin (L4)` à `20h`, 5 suspects, 5 indices visibles, `statutUnicite = COUPABLE_UNIQUE`, aucune fuite de `coupable` ni `veriteTerrain`, déduction unique confirmée (`S1` — Alice Moreau).

## Usage de l'IA et validation humaine

Le bilan détaillé de cette contribution se trouve dans [part-ia.json](part-ia.json) et dans la section Bilan de [la fiche F1](features/v0.1.0-F1-enquete-solution-unique.md).  
Revue humaine : validée par @salahbekkaripro sur la Pull Request https://github.com/salahbekkaripro/MINDCRIME/pull/12.

## Références de livraison

- Issue : #11 (`[D1][v0.1.0] F1 — Enquête à solution unique`)
- Branche : `mohamed_boughmadi`
- Pull request : https://github.com/salahbekkaripro/MINDCRIME/pull/12
- Commits principaux : `be4f7e0` (conception), `dcc52db` (moteur pur + JUnit/ArchUnit), `e907b19` (persistance Flyway/JPA + API REST v1), complétés par le frontend React et la documentation bilan.
- Limites et prochaine étape : faire relire la conception et la PR par un coéquipier, coordonner la numérotation de la migration Flyway (`V1__d1_enquetes.sql`) avec le socle commun sur `main`, puis préparer `D1-B` (v0.2.0 : difficulté et budget d'indices paramétrables).