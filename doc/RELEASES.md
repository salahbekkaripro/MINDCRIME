# Livraisons et responsabilités

MINDCRIME — planning prévisionnel établi au rendu R1, le 20 octobre 2026. La version initiale est **0.0.1**. Les versions ci-dessous sont des objectifs de livraison, pas des releases déjà publiées. Le premier état documentaire R1 est publié au [commit `3f51020`](https://github.com/salahbekkaripro/MINDCRIME/commit/3f510204be40aec7ed44b90e1880e4603c6a38c2), tagué `v0.0.1`. Les corrections documentaires ultérieures ont leurs propres commits ; le tag initial est conservé.

Source des échéances : texte de CoursEnLigne transmis par l’équipe. Le [guide du professeur](https://github.com/pascalpoizat/template-projet-m1-miage/blob/main/documentation.md) complète les règles de travail. La lecture de `documentation.pdf` à la racine du template est demandée par le cours ; l’équipe doit s’appuyer sur ce guide pour ses livraisons.

## Calendrier des rendus

| Jalon | Échéance indiquée | Résultat attendu |
| --- | --- | --- |
| R1 · lancement | 20 octobre 2026, 23 h 59 | Dossier de quatre pages PDF et Markdown dans `/doc`, URL du commit de rendu ; dépôt individuel par chacun |
| Identifiants développeurs | 20 octobre 2026, 23 h 59 | Chaque étudiant dépose l’URL du compte avec lequel il fera ses commits |
| R2 · v0.1.0 | 24 novembre 2026, 12 h 50 dans le devoir | Une feature majeure par étudiant, deux modes fonctionnels visés, CI, README/badges et dossier actualisé |
| Étape intermédiaire | 1er décembre 2026, en séance | Point sur l’intégration, les difficultés et les fonctionnalités de v0.2.0 |
| R3 · v0.2.0 | 8 décembre 2026, 12 h 50 dans le devoir | Une nouvelle feature majeure par étudiant, conteneurisation et dossier actualisé |
| R4 · v1.0.0 | 19 janvier 2027, 12 h 50 dans le devoir | Proof-of-concept final, fonctionnalités prévues, stabilisation et livraison |
| R5 · transparents | 18 janvier 2027, 23 h 59 | Support de soutenance déposé la veille ; soutenance en séance le 19 janvier |

**Écart à prendre en compte :** le planning général annonce 23 h 59 pour les rendus, tandis que les devoirs R2, R3 et R4 affichent 12 h 50. Pour organiser le travail, l’équipe retiendra **12 h 50, l’heure la plus tôt**, et vérifiera les échéances affichées dans les devoirs avant chaque dépôt. L’horaire général n’est pas présenté comme une prolongation de celui du devoir. Le dossier pourra dépasser quatre pages après R1 : les mises à jour R2/R3 sont sans limite de pages ; le générateur local reste actuellement réglé sur les quatre pages de R1.

Coefficients : R1 **10 %**, R2 **20 %**, R3 **20 %**, R4 **25 %**, R5 **25 %**. Le travail est collectif mais l’évaluation est individualisée ; chaque membre dépose chaque rendu dans son propre espace devoir.

## Fonctionnalités par release et par personne

Les identifiants A, B et C désignent respectivement les trois itérations. Chaque feature doit avoir un effet sur BD, backend Java, API REST et frontend. Les algorithmes et impacts sont détaillés dans [PLAN_FEATURES.md](PLAN_FEATURES.md).

| Responsable | v0.1.0 — A | v0.2.0 — B | v1.0.0 — C |
| --- | --- | --- | --- |
| D1 · Mohamed BOUGHMADI | Enquête à solution unique | Difficulté choisie avec budget d’indices | Carte de lieux jouable sous contraintes |
| D2 · Yacine FERDJALLAH | Contradictions de témoignages | Groupe minimal de déclarations incompatibles | Correction minimale d’un témoignage |
| D3 · Melissa BENZIDANE | Faisabilité d’alibi et trajets | Itinéraire avec fenêtres horaires | Ordres possibles d’événements |
| D4 · Mohamet THIAM | Partie, états et interrogatoire initial | Interrogatoire adaptatif | Attribution équilibrée des rôles |
| D5 · Salah-Eddine BEKKARI | Hypothèses et explications | Choix du prochain indice | Indices suffisants pour une accusation |

Chaque porteur préparera la fiche de sa feature, son algorithme et ses tests en priorité, puis ses migrations, son cas d’utilisation, son API et son écran. Une revue par un autre membre et une démonstration du scénario seront prévues avant intégration. Les nouvelles fonctionnalités ne seront pas remplacées par une simple optimisation ou une stabilisation.

## Organisation du travail collectif

Chaque étudiant développe les fonctionnalités de son domaine, selon le tableau des releases. Les cinq membres participent collectivement aux tests, aux revues de code, à la documentation, à la CI/CD et aux livraisons. Aucune de ces activités communes n’est attribuée exclusivement à un étudiant.

Le backend et ses tests sont développés en priorité, puis chaque fonctionnalité est reliée à la base de données, à l’API REST et à l’interface. Les tâches partagées sont réparties au fil du travail ; elles ne remplacent pas les fonctionnalités majeures individuelles attendues à chaque itération.

## Préparation des versions

**R1 — état publié.** Le [premier commit R1 `3f51020`](https://github.com/salahbekkaripro/MINDCRIME/commit/3f510204be40aec7ed44b90e1880e4603c6a38c2) est publié sur GitHub et identifié par le tag `v0.0.1`. Le compte administrateur est `salahbekkaripro`, le collaborateur actif `yaciiine78` et les invitations restantes concernent `boughbough`, `mbenzidane17` et `mhtthiam11` lors de la vérification du 20 octobre 2026. Chaque destinataire doit accepter sa propre invitation. Les comptes et l’identité de commit devront correspondre à ceux réellement utilisés par chacun.

L’URL à déposer doit correspondre au **commit exact retenu** : utiliser le lien ci-dessus pour le premier état, ou le commit des corrections si celles-ci sont intégrées et retenues. Ne pas déplacer le tag initial ni réécrire son commit. Le dossier actualisé ne prétend pas que ses corrections figurent déjà dans le premier commit.

**v0.1.0.** Construire le backend et les tests en priorité, intégrer les cinq features A sur les quatre couches et démontrer les deux modes. Prévoir CI, mesures de couverture et qualité, README et badges fondés sur des résultats réels, dossier actualisé et déclaration de l’usage réel de l’IA. Publier la page de projet ; faire construire la documentation par la CI et la joindre à la release. Les objectifs et le bilan documentaire de R1 ne remplacent pas le bilan réel du développement livré.

**v0.2.0.** Ajouter les cinq features B, réaliser la conteneurisation, vérifier l’ensemble en environnement reproductible, actualiser le dossier, la page et le bilan IA. Le point du 1er décembre prépare cette intégration.

**v1.0.0.** Ajouter les capacités C prévues, stabiliser le proof-of-concept et préparer une démonstration. Chaque étudiant prépare l’explication de son code et de ses algorithmes. Déposer les transparents le 18 janvier et présenter le produit le 19 janvier.

## Figer et déposer un rendu

À la fin de chaque séance, un commit et un tag de pré-livraison sont demandés, par exemple `v0.1.0-SNAPSHOT`. Lors d’une livraison, le commit retenu doit être identifié par le tag final correspondant : `v0.1.0`, `v0.2.0` ou `v1.0.0`. Les tags d’une release finale figent le rendu ; les règles d’évolution des tags SNAPSHOT seront convenues sans réécrire silencieusement les références déjà publiées.

Avant le dépôt : vérifier les identités Git, les résultats applicatifs réellement disponibles, le dossier et son PDF, les badges, la déclaration IA, la page et les pièces de release. Puis consigner le SHA exact et fournir son URL dans le devoir. Chaque membre effectue son dépôt individuel. Le rendu des identifiants utilise, lui, l’URL du profil développeur, pas celle d’un commit.

À ce stade, aucune CI, publication de page, release automatique ou application n’est opérationnelle. Aucun tag futur ni historique Git n’est créé ou modifié par ce planning.
