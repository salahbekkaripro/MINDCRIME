# Backend métier MINDCRIME — D2

Ce module fournit le parcours D2 : saisie React/TypeScript → API REST Spring Boot 4.0.8 → cas d'utilisation → moteur Java pur → stockage JDBC. Le profil local `demo` utilise une base H2 sur disque ; un profil PostgreSQL est fourni mais n'a pas été exécuté sur ce poste. Les contrats D2 et les référentiels de démonstration restent à rapprocher du modèle commun D1/D4. Le moteur ne dépend toujours pas de Spring ou du stockage.

## Prérequis et vérification

Un JDK 21 ou plus récent est nécessaire ; le compilateur cible Java 21 avec `--release 21`. Vérifier `java -version` et `JAVA_HOME` avant le build. Node 22.12+ et npm sont nécessaires pour construire l'interface. Le Java système n'est pas modifié par ce projet. Le Maven Wrapper Apache 3.3.4 télécharge Maven 3.9.11 au premier lancement ; un accès réseau est nécessaire pour les outils et dépendances. Le moteur seul utilise uniquement Java ; le serveur utilise les dépendances gérées par Spring Boot.

Depuis la racine, sous PowerShell, choisir au besoin un JDK seulement pour le processus courant :

```powershell
$env:JAVA_HOME = '<chemin-vers-votre-JDK-21-ou-superieur>'
Push-Location frontend
npm.cmd ci
npm.cmd run build
Pop-Location
.\backend\mvnw.cmd -f backend/pom.xml verify
& "$env:JAVA_HOME/bin/java.exe" -jar backend/target/mindcrime-backend-0.1.0-SNAPSHOT.jar
```

Sous un shell POSIX avec `JAVA_HOME` correctement défini :

```bash
npm --prefix frontend ci
npm --prefix frontend run build
sh backend/mvnw -f backend/pom.xml verify
"$JAVA_HOME/bin/java" -jar backend/target/mindcrime-backend-0.1.0-SNAPSHOT.jar
```

Construire le frontend avant le JAR : Maven copie `frontend/dist/` dans les ressources statiques. Puis ouvrir **http://127.0.0.1:8080/** et utiliser le compte local **demo / mindcrime-demo**, déjà indiqué dans l'écran. Le serveur écoute par défaut uniquement sur la boucle locale. Arrêter avec Ctrl+C ; les données restent dans `.data/mindcrime.mv.db`, relativement au dossier depuis lequel le serveur a été lancé. Les commandes ci-dessus supposent la racine du dépôt.

La commande `verify` compile les sources, exécute les tests et produit le JAR serveur avec ses dépendances. Elle échoue si aucun test n'est découvert. Les rapports se trouvent dans `backend/target/surefire-reports/`. Cette configuration ne mesure pas la couverture et ne constitue pas une CI distante. Pour l'ancienne démonstration console, après compilation : `java -cp backend/target/classes fr.miage.mindcrime.infrastructure.demo.DemonstrationContraintes`.

L'étape 5 avait exécuté 43 tests. L'étape 6 porte la suite à **56 tests** : 21 moteur, 16 contrats, 6 lecture/analyse, 12 tests HTTP réels et 1 fermeture/réouverture de base sur disque. Les tests HTTP vérifient notamment 201 malgré un conflit, les erreurs de requête, l'auteur non falsifiable, le contrôle CSRF, la visibilité et sa révision, la concurrence et les rollbacks. Les 120 permutations et les 2 801 séquences restent chacune dans un seul test JUnit. Le détail des exécutions et limites figure dans la fiche F2 de D2.

Sur ce poste, la vérification a utilisé le JDK 23.0.2 avec `--release 21`. Le magasin Java ne reconnaissait pas la chaîne de certificats utilisée pour Maven Central ; l'option suivante a permis d'utiliser les certificats Windows pour ce processus, sans désactiver TLS ni modifier le système :

```powershell
$env:MAVEN_OPTS = '-Djavax.net.ssl.trustStoreType=Windows-ROOT -Djavax.net.ssl.trustStore=NONE'
```

Cette option est propre à Windows et n'est pas nécessaire si votre JDK reconnaît déjà les certificats de votre environnement.

## Organisation et limites

- `moteur/contraintes/` : déclarations, domaines symboliques, index de faits et provenance, conflits, résultat immuable et erreurs de contrat.
- `application/alibi/AnalyserTemoignages` : charge les déclarations accessibles via `PortDeclarations`, vérifie le contexte et appelle le moteur.
- `application/alibi/SoumettreTemoignage` : contrôle les références, enregistre et analyse dans une transaction fournie par port. `ConsulterTemoignages` expose les lectures autorisées.
- `infrastructure/persistence/DepotTemoignagesJdbc` : stockage relationnel et filtre de visibilité avant calcul. L'adaptateur JDBC est retenu pour cette tranche limitée, sans annotations dans le domaine ; un adaptateur JPA pourra ensuite implémenter les mêmes ports si le socle commun le retient.
- `infrastructure/memoire/DepotDeclarationsEnMemoire` : adaptateur **provisoire** de tests/démonstration. L'appelant de confiance prépare explicitement un instantané par partie et lecteur ; aucune politique d'accès réelle n'est implémentée. Un contexte absent échoue au lieu de simuler une partie vide.
- `infrastructure/demo/DemonstrationContraintes` : données fictives locales ; une présence seule puis deux présences incompatibles avec une répétition. Le résultat attendu est un seul conflit avec les trois identifiants sources répartis sur deux faits.

L'auteur et le suspect concerné sont distincts. Le temps est un `Instant` à la seconde, sans arrondi. La liste des lieux est considérée non exhaustive : des absences seules n'engendrent pas de contradiction. Les occurrences répétées conservent leur provenance et ne multiplient pas le conflit. Le moteur ne calcule ni trajet ni culpabilité ni noyau minimal général.

Les déclarations complètes sont des objets `Declaration`. Les données historiques non exploitables sont transmises explicitement sous forme d'identifiant et raisons ; leur décodage depuis une BD n'existe pas encore. La complétude de l'analyse est indépendante du verdict. Au-delà de 200 occurrences par défaut, une erreur explicite empêche toute troncature ; la limite est configurable au constructeur pour l'intégration et doit être revue avec l'équipe.

Les instantanés sont immuables et les index de calcul locaux. Le port garantit le chargement et la visibilité **avant** le calcul ; le moteur ne peut pas authentifier un utilisateur ni deviner qu'une donnée a été cachée. L'adaptateur JDBC calcule la révision par SHA-256 des seules déclarations visibles, dans un ordre stable. Ajouter une déclaration privée d'un autre auteur ne change ni cette révision, ni la liste, ni le verdict public. Le compte local unique est un adaptateur provisoire, pas l'implémentation D4 des participants.

## Persistance et migrations

Les témoignages conservent partie, auteur, suspect concerné, lieu, instant, nature et visibilité. Les contradictions sont **recalculées**, pour éviter des résultats stockés devenus périmés. Les occurrences identiques sont conservées avec leur provenance ; leur regroupement est réalisé par le moteur.

Flyway utilise le dossier module `db/migration/contraintes/`. La migration locale `V2026112401__d2_temoignages.sql` crée les tables préfixées `d2_` et des clés étrangères composées pour empêcher les références entre parties. Ce numéro et les référentiels provisoires n'ont pas été coordonnés avec les autres membres : les vérifier avant une PR commune. Aucune migration publiée n'a été réécrite.

La soumission verrouille la ligne du dossier avec `SELECT ... FOR UPDATE`, contrôle les références, insère et analyse le nouvel ensemble dans une même transaction. Une contradiction ne provoque pas de rollback ; une erreur technique ou la limite dépassée annule l'insertion. Toutes les futures voies d'écriture communes devront respecter ce verrou pour conserver la garantie sous concurrence.

Pour PostgreSQL, fournir les variables `MINDCRIME_DB_URL` (URL JDBC), `MINDCRIME_DB_USER`, `MINDCRIME_DB_PASSWORD`, `MINDCRIME_USERNAME`, `MINDCRIME_PASSWORD`, `MINDCRIME_ACTEUR_ID`, puis lancer avec `--spring.profiles.active=postgres`. Ce profil n'insère pas les données de démonstration : il attend les référentiels et accès correspondants. Il reste à tester sur PostgreSQL réel ; le mode PostgreSQL de H2 ne prouve pas cette compatibilité. Aucun secret réel n'est enregistré dans le dépôt.

## Contrat REST implémenté

| Opération | Réponse |
| --- | --- |
| `GET /api/v1/session` | Identité du compte local, dossier de démonstration et jeton CSRF |
| `GET /api/v1/parties/{id}/referentiel` | Suspects, lieux et référence temporelle accessibles |
| `GET /api/v1/parties/{id}/temoignages` | Objet contenant `revision` et `declarations` |
| `GET /api/v1/parties/{id}/temoignages/{declarationId}` | Déclaration accessible, sinon 404 |
| `GET /api/v1/parties/{id}/contradictions` | Analyse calculée par Java, faits et sources des conflits |
| `POST /api/v1/parties/{id}/temoignages` | 201, `Location` consultable, déclaration créée et analyse, même en cas de contradiction |

Corps POST : `suspectConcerneId`, `lieuId`, `instant` complet UTC à la seconde, `nature` PRESENCE/ABSENCE. L'auteur est fourni par le contexte serveur. Les champs inconnus, notamment `auteurId` ou `visibilite`, sont rejetés. Les nouvelles déclarations sont publiques dans ce périmètre ; l'API n'offre pas d'édition des droits.

Authentification HTTP Basic pour l'adaptateur local, jeton anti-CSRF transmis dans `X-XSRF-TOKEN` et cookie associé. Les identifiants restent en mémoire de l'interface ; aucune identité n'est acceptée d'un simple en-tête UUID. La stratégie stateless garde le jeton entre les requêtes Basic successives, tout en validant le jeton à chaque écriture. Les erreurs sont en `application/problem+json` : 400 pour un corps illisible ou des valeurs/champs inconnus, 422 pour données manquantes, précision temporelle invalide ou limite dépassée, 401 sans authentification, 403 sans jeton CSRF valide, 404 pour ressource absente ou non accessible, 503 pour indisponibilité SQL. Aucun détail SQL n'est retourné.

## Démonstration

Sur une base de démonstration initialement vide (les saisies déjà réalisées sont conservées au redémarrage) :

1. Ouvrir le dossier avec le compte de démonstration.
2. Sélectionner Alicia Morel, Présence, Cuisine, le 24 novembre 2026 à 20:00:00 UTC ; enregistrer.
3. Conserver le suspect et l'instant, sélectionner Jardin, puis enregistrer.
4. Observer deux témoignages et une incompatibilité, avec les lieux et les références des déclarations.
5. Répéter un des témoignages : trois sources, toujours une seule incompatibilité de ce type.

Le frontend consomme les résultats Java ; il ne calcule pas les contradictions. Il compare les révisions des lectures pour éviter d'afficher une liste et une analyse de versions différentes. L'auteur et le suspect concerné sont affichés séparément. Les référentiels sont des données fictives fixes, pas le générateur D1 ou les comportements D4.

Références techniques consultées : [modules Spring Boot 4](https://docs.spring.io/spring-boot/4.0/reference/using/build-systems.html), [protection CSRF](https://docs.spring.io/spring-security/reference/7.0/servlet/exploits/csrf.html).

Le build minimal est ajouté parce qu'aucun backend n'était présent sur la branche de départ. Il est destiné à être rapproché du socle commun, sans importer le travail métier des autres domaines. Le Wrapper et son JAR proviennent de la distribution Apache officielle `org.apache.maven.wrapper:maven-wrapper-distribution:3.3.4:bin`. Le script Windows est adapté pour rechercher `.mvn` à partir de son propre dossier, comme le script POSIX, afin de fonctionner depuis la racine du dépôt. Le code D2 et les tests sont générés avec Codex à la demande de Yacine ; la traçabilité détaillée est dans sa fiche F2.
