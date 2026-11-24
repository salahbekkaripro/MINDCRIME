# Contribution D2 — Yacine FERDJALLAH

- Compte GitHub : [yaciiine78](https://github.com/yaciiine78)
- Domaine : Satisfaction et propagation de contraintes
- Version en préparation : v0.1.0
- Statut : parcours local complet de saisie et détection : moteur Java pur, persistance JDBC/H2, migration Flyway, API REST et interface React/TypeScript. 56 tests réussis, parcours Edge et redémarrage vérifiés. PostgreSQL et intégration commune restent à valider.

## Feature portée

Détecter les contradictions de présence et d'absence à un instant précis. Le périmètre, les cas attendus et les limites sont décrits dans la [fiche F2](features/v0.1.0-F2-contradictions.md), préparée à partir du [modèle commun](../../modeles/feature.md).

Le contrôle cible deux conflits : la présence d'un même suspect dans deux lieux distincts au même instant, et sa présence et son absence simultanées au même lieu. Les données manquantes ne prouvent pas une contradiction. Les déplacements relèvent de D3 ; les groupes minimaux incompatibles et les corrections restent prévus pour les versions suivantes.

## Conception et algorithme

La fiche propose une propagation de contraintes unaires sur des domaines de lieux représentés symboliquement. L'indexation regroupe les faits identiques, conserve leurs sources et restitue les conflits directs sans multiplication par les doublons. Une possibilité AUTRE_LIEU évite de considérer les lieux connus comme exhaustifs. Le coût attendu est O(T + K) en temps et mémoire, pour T occurrences et K conflits, qui peuvent être quadratiques ; les hypothèses sont détaillées dans la fiche.

L'étape 3 précise les structures des domaines, le pseudo-code de référence, l'application successive des contraintes et la justification par l'associativité, la commutativité et l'idempotence de l'intersection. L'étape 4 les implémente dans `AnalyseurContraintesUnaires` et `DomainePresence`. Les sources sont conservées sans promettre un groupe incompatible minimal. Les complexités sont des analyses théoriques, pas des mesures de performance.

Les contrats proposés précisent les identifiants, l'auteur distinct du suspect concerné, les instants, les résultats, la complétude, les erreurs et les frontières de visibilité. Les impacts BD/backend/API/frontend et les tests sont décrits. Aucun accord des autres domaines n'est déclaré obtenu.

## Réalisation dans les quatre couches

| Couche | Travail réalisé | Fichiers ou contrats |
| --- | --- | --- |
| BD | H2 sur disque, migration Flyway D2, références composées, verrou de partie et rollback ; profil PostgreSQL non testé | `db/migration/contraintes/`, `infrastructure/persistence/DepotTemoignagesJdbc` |
| Backend | Moteur, contrats provisoires immuables, ports, consultation, soumission et analyse | `moteur/contraintes/`, `application/alibi/`, `application/port/` |
| API REST | Soumission, liste, déclaration accessible, référentiel et analyse ; erreurs problem+json, compte local et CSRF | `api/rest/`, `api/dto/`, configuration Spring |
| Frontend | Saisie, verdict Java, déclarations sources et erreurs ; aucun solveur dans le navigateur | `frontend/src/` |

Les instructions d'exécution sont dans le [README backend](../../../backend/README.md) et le [README frontend](../../../frontend/README.md). Le build initial est complété par Spring Boot 4.0.8, JDBC, Flyway et le frontend embarqué. Le rapprochement avec le travail commun reste à faire, sans prétendre que les référentiels et le numéro de migration proposés ont déjà été approuvés. Les contradictions sont recalculées à partir des témoignages conservés ; aucune table de résultats périmés n'est entretenue.

## Vérifications — historique des étapes

Le 24 novembre 2026, après sélection du JDK 23.0.2 dans le processus de commande, `.\backend\mvnw.cmd -f backend/pom.xml verify` réussit : **32 tests, zéro échec, zéro erreur, aucun ignoré**, JAR produit. Le compilateur utilise `--release 21` ; le format des classes Java 21 (65) est confirmé. Aucun JDK 21 n'a été identifié parmi les installations inspectées et l'exécution sur ce runtime précis n'a pas été vérifiée.

Les tests comprennent C01 à C10, provenance et doublons, erreurs d'identité/partie, données incomplètes, immutabilité, réutilisation du moteur, orchestration et isolation des ensembles visibles préparés. Un test Java compare 2 801 séquences à une intersection de domaines explicites et à un oracle par paires ; il prolonge l'expérience Python de conception des étapes précédentes.

La démonstration `java -jar backend/target/mindcrime-backend-0.1.0-SNAPSHOT.jar`, lancée avec le JDK 23, produit le verdict attendu puis un seul conflit pour deux présences incompatibles avec une répétition, et restitue les trois sources. `jdeps --print-module-deps` ne signale que `java.base`. Il s'agit d'une démonstration console, sans API ni frontend.

Le premier lancement a révélé un chemin de base incorrect dans le Wrapper Windows ; le script a été adapté pour fonctionner depuis la racine. Le téléchargement Maven a ensuite rencontré un certificat non reconnu par le magasin Java ; l'option de processus `-Djavax.net.ssl.trustStoreType=Windows-ROOT -Djavax.net.ssl.trustStore=NONE` a permis d'utiliser les certificats Windows sans désactiver TLS ni modifier le Java système. Voir le README backend.

Contrôles documentaires : `git diff --check`, liens relatifs et JSON individuel à dix axes. Aucun PDF collectif ou dossier de release régénéré. Aucun taux de couverture, résultat qualité distant ou exécution de CI n'est annoncé.

À l'étape 5, nouvelle exécution de `.\backend\mvnw.cmd -B -ntp -f backend/pom.xml verify` après renforcement des tests : **43 tests, zéro échec, zéro erreur, aucun ignoré ; BUILD SUCCESS** (21 moteur, 16 contrats, 6 application). Les sept champs obligatoires manquants sont testés individuellement ; les motifs et sources de chaque famille de conflit sont vérifiés exactement ; les 120 permutations d'un jeu de cinq déclarations conservent les résultats. Les 2 801 séquences comparées à des domaines explicites et à un oracle sont toujours vérifiées dans un test JUnit distinct. La matrice complète des exigences et les limites figurent dans la fiche F2. Aucun code de production modifié à cette étape.

Java et `JAVA_HOME` ont été contrôlés de nouveau : Java 8 par défaut, vérification avec JDK 23.0.2 et cible de compilation Java 21. Une tentative de récupération du JDK 21 auprès d'Adoptium a échoué sur HTTP 403 ; aucun runtime 21 exact n'a donc été testé ni installé. Les rapports Surefire sont générés sous `backend/target/`, ignoré par Git.

À l'étape 6, `npm.cmd run build` réussit, puis `.\backend\mvnw.cmd -B -ntp -f backend/pom.xml verify` réussit avec **56 tests, zéro échec, zéro erreur, aucun ignoré** : les 43 tests précédents, 12 tests HTTP et 1 test de fermeture/réouverture de base sur disque. Les vérifications comprennent les erreurs REST, la conservation des contradictions avec 201, la confidentialité de la liste et du verdict jusque dans la révision visible, la consultation de la ressource créée, la concurrence et les rollbacks. Le moteur reste indépendant de Spring.

Le parcours est réellement exécuté dans Edge : Alicia Morel est déclarée présente en cuisine puis au jardin à 20:00:00 UTC le 24 novembre 2026. L'écran affiche deux témoignages et une incompatibilité, les lieux et leurs références. Après arrêt et redémarrage du serveur, les lectures HTTP confirment les mêmes données et la même révision. Les deux saisies restent dans `.data/`, ignoré par Git. Les détails et limites sont consignés dans la fiche F2.

PostgreSQL n'est pas présent parmi les outils identifiés et Docker n'a pas été trouvé ; le profil PostgreSQL est fourni sans prétendre être testé. La migration D2 locale `V2026112401` reste à coordonner avant intégration. Le compte et les référentiels fixes sont des adaptateurs provisoires, pas des fonctionnalités D1/D4. La vérification a utilisé le JDK 23.0.2 ciblant Java 21 et Node 22.13.1. Aucun test mobile automatisé, taux de couverture, publication ou CI distante n'est annoncé.

## Usage de l'IA et validation humaine

Le bilan détaillé de cette contribution se trouve dans [part-ia.json](part-ia.json). Le 24 novembre 2026, Codex a formalisé le périmètre à partir des situations fournies par Yacine, puis rédigé la proposition de conception et effectué une revue technique à sa demande. Cette revue du même assistant n'est pas une revue indépendante ni une relecture humaine par un coéquipier. Les pourcentages restent non renseignés ; les accords d'équipe et la revue humaine restent à renseigner lorsqu'ils auront réellement eu lieu.

Apports constatés de Yacine : cadrage de la demande sur sa partie D2, périmètre et cas retenus dans les échanges, pilotage des étapes et demande d'explications sur le fonctionnement et les limites. Assistance déléguée à Codex : rédaction et structuration des documents, propositions techniques, pseudo-code et contrôles automatiques. Les propositions et explications algorithmiques générées restent attribuées à l'assistant ; aucune contribution manuelle ou validation humaine supplémentaire n'est inventée.

Pour l'étape 4, Yacine a demandé la réalisation du backend métier indépendant et des adaptateurs provisoires. Codex a généré les sources Java, les tests et la démonstration, mis en place le build, repris le Wrapper Apache officiel et corrigé son lancement Windows, puis exécuté le build et les vérifications rapportés. L'usage est tracé sans attribuer cette génération à une saisie manuelle de Yacine.

Pour l'étape 5, Codex a complété les tests à partir de la liste d'exigences transmise par Yacine, exécuté la suite et documenté les résultats réels et les limites d'environnement. Aucun accord ou travail manuel supplémentaire d'un coéquipier n'est présumé.

Pour l'étape 6, Yacine a demandé le parcours persistant de bout en bout. Codex a généré le SQL, l'adaptateur JDBC, l'API, le compte local provisoire, l'interface et les tests supplémentaires, corrigé les problèmes révélés par les tests et vérifié les builds, les saisies dans Edge et le redémarrage. Les fonctionnalités ne sont pas présentées comme une saisie manuelle de Yacine ou comme une intégration approuvée par les autres membres.

## Références de livraison

- Issue : à créer ou renseigner
- Pull request : à créer ou renseigner
- Commit de contribution : `feat(d2): ajouter le parcours de contradictions de temoignages` ; hash communiqué après création. Le commit de release commun reste à convenir avec l'équipe.
- Limites et prochaine étape : revue et intégration des référentiels/participants D1/D4, temps D3, résultats D5, numéro de migration et verrou de partie ; vérification sur PostgreSQL réel ; contribution aux exigences communes README/badges/CI. Le compte local ne constitue pas une gestion des participants multijoueurs. Contrats communs encore proposés et revue humaine non effectuée ; Yacine a demandé la vérification technique par Codex. Contribution préparée pour un commit local sur `yacine_ferdjallah` à sa demande ; aucun push ni changement du tag v0.1.0.
