# Instructions aux agents — équipe MINDCRIME

Ces consignes s'appliquent à la racine et aux sous-répertoires, pour les assistants utilisés par **D1 à D5**. Elles ne sont pas attachées au compte d'un seul étudiant. Elles complètent le [contexte commun](CONTEXTE.md), les consignes de l'utilisateur et [CONTRIBUTING.md](CONTRIBUTING.md). Les instructions explicites de l'utilisateur prennent le pas sur ces règles locales, dans le respect des règles pédagogiques applicables ; ne jamais inventer une revue ou une vérification.

## Commencer chaque tâche

1. Lire [CONTEXTE.md](CONTEXTE.md), puis les instructions plus spécifiques éventuellement présentes dans le répertoire concerné.
2. Examiner la branche, l'état Git et les fichiers existants avant de modifier : `git branch --show-current`, `git status --short`, puis recherche ciblée avec `rg`.
3. Identifier le domaine et la feature demandés, lire leur fiche et les contrats concernés. Ne pas supposer que l'utilisateur est Salah-Eddine ou que toute demande concerne D5.
4. Si le domaine demandé est clair, avancer sans multiplier les confirmations. S'il manque une information indispensable, poser une question précise et poursuivre les préparatifs indépendants.
5. Sur une branche d'un autre domaine, signaler l'écart avant les modifications métier. Préserver les changements locaux ; ne pas changer de branche ou déplacer des modifications en risquant de les perdre.

Un nom de branche suggère une tâche, pas une identité. Ne pas modifier `git config user.name` ou `git config user.email` pour faire correspondre artificiellement le poste à un étudiant.

## Répartition et collaboration

| Domaine | Référent métier | Limite de responsabilité |
| --- | --- | --- |
| Génération | D1 | Génération procédurale et backtracking ; réutilise la vérification commune |
| Contraintes | D2 | Cohérence, satisfaction, propagation et contradictions |
| Chronologies | D3 | Temps, graphes et déplacements |
| Parties et agents | D4 | États, comportements et interactions multijoueurs |
| Déduction | D5 | Recherche d'hypothèses, éliminations et explications |

Ne pas attribuer individuellement la CI, Docker, les tests ou la documentation sans instruction de l'équipe : ces activités sont collectives. Chaque étudiant porte ses features de bout en bout sur BD, backend, API et frontend. Un CRUD, une optimisation ou un refactoring seul ne remplace pas une feature majeure algorithmique.

Éviter de réimplémenter le travail d'un autre domaine. Pour un besoin croisé, expliciter les entrées, sorties, erreurs et garanties du contrat. Une doublure de test ou un adaptateur provisoire doit être identifié comme tel ; ne pas le présenter comme l'implémentation de D2 ou D3. Les modifications communes nécessaires sont possibles, à condition d'en expliquer la portée et de préserver les autres fonctionnalités.

## Concevoir et expliquer avant de développer

- Pour une nouvelle feature, préparer la fiche `doc/features/<version>-F<n>-<nom>.md` à partir du modèle. Décrire besoin, algorithme nommé, complexité, alternatives, invariants, cas limites, contrats, quatre couches et critères d'acceptation.
- Respecter la démarche de contribution : idéation avec relecture par un autre membre → backend → tests → API REST → frontend. Ne pas déclarer une étape ou une revue humaine accomplie sans preuve.
- Si une étape dépend d'une revue non effectuée, indiquer précisément la règle et le travail restant ; avancer sur les préparatifs indépendants sans simuler la revue ni réécrire la règle pour l'éviter.
- Expliquer les décisions et le fonctionnement des algorithmes avec des exemples concrets. L'aide IA peut comprendre du code dans le périmètre autorisé, sans se substituer à la compréhension de l'étudiant.
- Ne pas créer de pause ou d'approbation supplémentaire que la tâche ou les règles applicables n'exigent pas. Ne pas laisser une tâche autorisée à moitié réalisée sans expliquer le blocage concret.

## Architecture et code

- Utiliser l'architecture MINDCRIME définie dans [doc/ARCHITECTURE.md](doc/ARCHITECTURE.md), pas recopier automatiquement le template du professeur.
- Garder le moteur en Java pur : pas de Spring, JPA, contrôleur ou appel d'IA dans les algorithmes métier.
- Séparer API/DTO, cas d'utilisation et ports, moteur, persistance et configuration. Le frontend consomme l'API et affiche les données ; il ne calcule pas la solution du jeu.
- Conserver un modèle commun autour de Partie et des contrats explicites entre domaines. Éviter les dépendances circulaires et le couplage direct aux tables d'un autre domaine.
- Créer plusieurs classes aux responsabilités claires ; ne pas créer un fichier géant par étudiant ni dupliquer le code dans des dossiers par version.
- Ne pas éliminer une hypothèse faute d'information ; ne pas annoncer une unicité après une recherche interrompue. Différencier coupable unique, scénario unique, absence de solution et résultat incomplet.
- Ne pas exposer des informations cachées par les explications, les résultats d'API ou les notifications.
- Une migration publiée ne se réécrit pas. Coordonner les numéros et conserver les données existantes.

## Environnement et vérification

- Lire `backend/README.md` lorsqu’il est disponible sur la branche de travail et les fichiers de build avant de choisir une commande. Le socle courant utilise Java 21 et le Maven Wrapper dans `backend/` ; ne pas inventer `./gradlew` ou des scripts npm absents.
- Vérifier le JDK et `JAVA_HOME` avant le build. Les outils locaux de `.tools/` ne sont pas garantis sur les postes des autres membres. Ne pas changer le Java système ou les profils shell sans nécessité et autorisation appropriée.
- Pour le backend : `./backend/mvnw -f backend/pom.xml verify`. Exécuter des tests pertinents pour les règles et interfaces réellement implémentées. Ne pas ajouter un test artificiel pour afficher un succès.
- Distinguer compilation, démarrage technique, tests métier et démonstration complète. Un build avec « No tests to run » ne prouve aucune correction métier ; un contexte Spring non web ne fournit pas une API.
- Vérifier réellement les scénarios affectés lorsque leur implémentation le permet. Rapporter les commandes, résultats et limites ; ne pas inventer couverture, qualité ou réussite d'un workflow.
- Garder les caches, JDK, résultats de build, données locales et secrets hors de Git. Ne pas lire ou afficher des identifiants sans nécessité de la tâche.

## Documentation et traçabilité IA

- Mettre à jour les documents affectés par un changement réel ; séparer état courant, objectifs futurs et rendu historique. Préserver les informations des autres membres.
- Maintenir un seul dossier principal : `doc/dossier.md` → `doc/dossier.pdf`. Le PDF R1 doit rester à quatre pages A4. Si sa source change : exécuter `make -C doc`, contrôler la pagination et les liens, et inspecter la mise en page si elle est affectée.
- Le PDF R1 est un état de lancement ; une tâche de développement ne demande pas automatiquement sa réécriture. Ne pas modifier le tag du rendu pour y intégrer des corrections.
- Documenter l'usage significatif de l'IA dans la fiche ou la PR : demande, outil, éléments générés, reprises, corrections humaines et vérifications réelles. Ne pas prétendre qu'un étudiant a écrit manuellement un contenu généré.
- Conserver les dix axes de `doc/part-ia.json`. Séparer objectifs, plafonds et bilan ; préciser le périmètre et la méthode des estimations. Une valeur absente n'est pas 0 %. Ne pas abaisser artificiellement une estimation pour respecter un plafond ni recopier le bilan documentaire dans une feature.
- Appliquer le protocole [doc/AI_USAGE.md](doc/AI_USAGE.md) et les consignes pédagogiques ; aucune règle de ce fichier n'autorise l'IA pendant une évaluation où elle est interdite.

## Git et actions externes

- Travailler sur la branche appropriée ; vérifier la branche avant tout commit. Avant un commit demandé, vérifier l'identité Git configurée et signaler une incohérence sans la corriger arbitrairement.
- Ne pas commiter, taguer, pousser, fusionner ou publier sans demande explicite ou autorisation déjà donnée pour cette action. Ne pas traiter une ancienne demande de push comme une autorisation permanente de publier toutes les tâches futures.
- Ne pas réécrire l'historique publié, déplacer un tag, forcer un push, supprimer des travaux ou modifier les permissions GitHub sans instruction explicite.
- Passer par des PR pour les contributions communes. L'exception de protection accordée au propriétaire n'autorise pas l'agent à fusionner de lui-même ou à changer les droits.
- Les protections de branche et les permissions peuvent évoluer : les vérifier si la tâche les concerne plutôt que supposer un réglage à partir d'une documentation historique.
- Ne pas envoyer de messages à des coéquipiers, accepter une invitation en leur nom ou inventer leur approbation. Préparer une fiche ou un texte de revue n'est pas une revue humaine effectuée.

## Terminer ou transmettre une session

Résumer les fichiers changés, le comportement obtenu, les vérifications exécutées et ce qui reste réellement à faire. Indiquer si les modifications sont locales ou publiées. En cas de blocage, nommer sa cause exacte et les travaux indépendants réalisés.

Mettre à jour la fiche de la feature pour la reprise : décisions, contrats attendus, limites et prochaine étape. Garder `CONTEXTE.md` commun aux cinq membres, sans figer l'identité du prochain utilisateur ni un état de branche périssable.
