# Plan prévisionnel des features

Ces propositions adaptent MINDCRIME au [guide du professeur](https://github.com/pascalpoizat/template-projet-m1-miage/blob/main/documentation.md), consulté le 20 octobre 2026 (commit `3a11a780f5912bb3876e7ed7902b5cd8f377eb8e`). Elles décrivent des objectifs prévisionnels, sans fonctionnalité déjà réalisée. La troisième itération prévoit aussi une feature par étudiant ; stabilisation, refactoring et optimisation ne remplacent pas ces features.

Les jalons, tâches collectives et modalités de dépôt sont détaillés dans [RELEASES.md](RELEASES.md).

Entité pivot proposée : **Partie**, identifiée par un identifiant commun aux modules. Chaque fonctionnalité devra préciser ses tables et migrations Flyway, ses ports métier, ses ressources REST sous `/api/v1` et son scénario frontend. Les noms ci-dessous sont des objets de conception, pas des tables ou endpoints existants.

| ID / version / porteur | Capacité et algorithme candidat | BD : données à prévoir | Back : calcul | API REST : opération à exposer | Front : interaction prévue |
| --- | --- | --- | --- | --- | --- |
| D1-A · v0.1.0 · Mohamed BOUGHMADI | Enquête à solution unique ; génération procédurale, backtracking | Enquête, suspects, indices rattachés à Partie | Générer puis vérifier une seule solution avec indices accessibles | Créer et consulter une enquête | Lancer une enquête et consulter ses indices |
| D2-A · v0.1.0 · Yacine FERDJALLAH | Contradictions de témoignages structurés ; satisfaction et propagation de contraintes | Déclarations de présence ou d'absence, auteurs, contraintes et résultats rattachés à Partie | Confronter les déclarations révélées et distinguer contradiction, compatibilité et indétermination | Soumettre une déclaration et consulter le résultat avec ses références autorisées | Choisir suspect, lieu, instant et présence ou absence, puis consulter les incompatibilités |
| D3-A · v0.1.0 · Melissa BENZIDANE | Faisabilité d'alibi ; graphe temporel, Dijkstra | Lieux, trajets, horaires et résultat de vérification | Comparer trajet minimal et durée disponible | Vérifier un déplacement déclaré | Consulter le trajet et sa faisabilité dans la chronologie |
| D4-A · v0.1.0 · Mohamet THIAM | Interrogatoire cohérent des suspects — recherche de réponse sous contraintes avec backtracking, machine à états pour le déroulé | Mémoire des suspects, déclarations, échanges | Choisir une réponse compatible avec la mémoire du suspect, ses déclarations passées et les preuves révélées | Poser une question à un suspect et obtenir sa réponse | Interroger un suspect et suivre ses déclarations |
| D5-A · v0.1.0 · Salah-Eddine BEKKARI | Hypothèses expliquées ; recherche et élimination de scénarios | Hypothèses, conclusions, liens aux indices | Énumérer et filtrer les scénarios compatibles | Consulter les hypothèses et explications | Examiner les déductions et leurs motifs |
| D1-B · v0.2.0 · Mohamed BOUGHMADI | Difficulté choisie ; recherche sous contraintes avec budget d'indices | Paramètres de difficulté et sélection d'indices | Chercher un ensemble respectant budget et unicité | Demander une enquête paramétrée | Choisir un niveau de difficulté défini par des critères explicites |
| D2-B · v0.2.0 · Yacine FERDJALLAH | Groupe minimal de déclarations incompatibles ; extraction de noyau insatisfiable | Groupes de contradictions et déclarations liées | Réduire un ensemble incompatible jusqu'à un noyau minimal par inclusion | Demander les déclarations responsables | Consulter le groupe minimal et son explication |
| D3-B · v0.2.0 · Melissa BENZIDANE | Itinéraire avec fenêtres horaires ; recherche de chemin temporel | Fenêtres, attentes et itinéraires | Trouver un trajet respectant horaires et durées | Rechercher un trajet temporel admissible | Explorer un itinéraire avec contraintes horaires |
| D4-B · v0.2.0 · Mohamet THIAM | Stratégie d'interrogatoire du détective (mode Alibi) — arbre de décision calculé par expectimax | Questions, réponses possibles et parcours d'interrogatoire | Choisir la question qui maximise l'information attendue sur plusieurs coups | Obtenir la question suivante et soumettre la réponse | Répondre au détective en mode Alibi |
| D5-B · v0.2.0 · Salah-Eddine BEKKARI | Prochain indice à examiner ; gain d'information | Candidats, partitions d'hypothèses et scores | Classer les indices selon une réduction attendue d'incertitude | Demander une recommandation d'indice | Choisir un indice à partir d'une recommandation expliquée |
| D1-C · v1.0.0 · Mohamed BOUGHMADI | Carte de lieux jouable ; génération de graphe connexe sous contraintes | Carte, lieux, arêtes et distances associés à la partie | Construire une carte connectée respectant les distances et revérifier la cohérence de l'enquête | Générer et consulter une carte de partie | Choisir les contraintes de carte et explorer les lieux générés |
| D2-C · v1.0.0 · Yacine FERDJALLAH | Correction minimale d'un témoignage ; recherche de sous-ensembles minimaux | Propositions de correction et coût de modification | Chercher quelles déclarations modifier pour restaurer la cohérence | Demander des corrections possibles | Comparer les propositions sans les appliquer automatiquement |
| D3-C · v1.0.0 · Melissa BENZIDANE | Ordres possibles d'événements ; tri topologique et détection de cycles | Relations de précédence et ordonnancements | Détecter les cycles et reconstruire les ordres compatibles | Calculer des chronologies possibles | Explorer les chronologies alternatives |
| D4-C · v1.0.0 · Mohamet THIAM | Attribution équilibrée des rôles — appariement biparti pondéré (algorithme hongrois) | Préférences, rôles, coûts et attribution | Affecter les rôles selon disponibilités et préférences | Proposer puis confirmer une attribution | Indiquer ses préférences et consulter son rôle |
| D5-C · v1.0.0 · Salah-Eddine BEKKARI | Indices suffisants pour une accusation ; couverture d'ensemble approchée | Ensembles justificatifs et scénarios exclus | Sélectionner des indices couvrant les alternatives à exclure | Demander une justification suffisante | Consulter les indices soutenant une accusation |

Les capacités B et C sont des pistes de conception, pas des engagements pédagogiques déjà approuvés. Elles pourront être réduites ou remplacées si leur faisabilité, leur intérêt utilisateur ou leur distinction algorithmique est insuffisante. D2-B explique une incompatibilité actuelle ; D2-C propose des changements pour la résoudre. D5-A expose le raisonnement existant ; D5-B aide à acquérir une information supplémentaire ; D5-C sélectionne un ensemble justificatif.

Le coût de difficulté de D1-B doit être défini : un simple budget d'indices n'est pas une mesure démontrée de difficulté cognitive. D1-C devra préciser les contraintes de connexité et de distance, leur satisfaction et leur compatibilité avec les trajets de D3. D2-B garantit un noyau minimal par inclusion, pas nécessairement de cardinalité minimale. D3-C peut produire un nombre exponentiel d'ordres : une limite explicite sera nécessaire. D5-B devra expliciter la pondération des hypothèses ; D5-C ne promet pas un minimum exact et devra annoncer sa garantie approchée.

Avant toute implémentation, une fiche par feature devra justifier l'algorithme non trivial, la complexité en temps et mémoire, les alternatives, les invariants, les cas limites, les impacts des quatre couches et les critères testables. Un seul responsable portera chaque feature ; les revues et l'intégration resteront collectives. Les commandes applicatives et résultats de tests ne seront ajoutés qu'après implémentation et exécution réelle.

## Cadrage D2 pour le dossier de lancement R1

Responsable : Yacine FERDJALLAH (`yaciiine78`). Ce cadrage précise le scénario S2 et le périmètre prévu. Il ne constitue ni une implémentation ni une fiche de conception validée.

Les témoignages seront d'abord saisis sous forme structurée : partie, auteur de la déclaration, suspect concerné, lieu, instant et présence ou absence. L'auteur pourra parler d'un autre suspect. L'analyse automatique de phrases libres n'est pas prévue dans ce premier périmètre. Les règles de visibilité de la partie détermineront les déclarations utilisables et les résultats consultables.

Exemple : une déclaration situe le suspect A dans la cuisine à 20 h, une autre dans le jardin au même instant. Ces lieux étant distincts, les deux affirmations ne peuvent pas être vraies ensemble. En revanche, un lieu inconnu ou deux déclarations identiques ne suffisent pas à établir une contradiction.

La piste algorithmique reste la satisfaction de contraintes : les présences et absences restreindront les lieux possibles d'un suspect à un instant donné. La propagation répercutera les restrictions. Si elle ne suffit pas à décider de la compatibilité globale, le résultat restera indéterminé tant qu'une vérification complémentaire n'aura pas conclu. Le choix précis de l'algorithme, sa complexité et les limites de recherche seront justifiés dans la fiche D2-A avant développement.

| Situation à couvrir dans les futurs tests | Résultat attendu |
| --- | --- |
| Même suspect, même instant, deux lieux distincts | Contradiction avec références aux déclarations concernées |
| Présence et absence déclarées pour le même suspect, lieu et instant | Contradiction |
| Deux déclarations identiques | Aucune contradiction créée par le doublon |
| Informations manquantes ou analyse interrompue | Aucune accusation déduite du manque d'information. Résultat indéterminé si la compatibilité n'est pas établie |
| Déclaration ou preuve cachée au destinataire | Aucune divulgation de son contenu, de son existence ou d'une conclusion qui la révèle |

D2-A détectera les incompatibilités, sans promettre un ensemble explicatif minimal. D2-B cherchera ensuite un noyau minimal par inclusion. D2-C proposera des corrections, sans modifier automatiquement les témoignages d'origine. La faisabilité des trajets restera dans D3, l'exploration des hypothèses et l'accusation dans D5.
