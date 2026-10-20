# Protocole d'utilisation de l'IA

MINDCRIME — M1 MIAGE Apprentissage, Université Paris Nanterre.

**Développement assisté par IA avec conception, revue et validation humaines systématiques.**

## Cadre pédagogique et statut

Ce protocole décrit le fonctionnement souhaité par l'équipe ; il s’inscrit dans les règles définies en cours. Les outils envisagés sont ChatGPT, Codex et GitHub Copilot.

La fiche pédagogique « Projet », code ELP **5E7MAPRO**, année **2026-2027**, transmise par l'équipe, indique dans sa description une autorisation possible des IA génératives selon les règles du cours, avec maîtrise de l'outil, de la qualité des livraisons et de l'explicabilité du code. Sa rubrique évaluation indique également : **« L’utilisation de l’Intelligence Artificielle est interdite lors des évaluations. »**

Ces indications doivent être distinguées : ce protocole propose une assistance pendant la préparation et le développement du projet, dans le périmètre effectivement autorisé ; il ne vaut pas autorisation pendant les évaluations. Les consignes applicables à chaque livrable évalué encadrent son utilisation. Aucune dérogation n'est présumée pour le dossier R1, les livraisons ou la soutenance. Les étudiants devront pouvoir présenter et expliquer leur travail sans assistance lors des évaluations.

Le [guide du professeur](https://github.com/pascalpoizat/template-projet-m1-miage/blob/main/documentation.md), consulté le 20 octobre 2026 (commit `3a11a780f5912bb3876e7ed7902b5cd8f377eb8e`), présente le template, ses outils et son `CLAUDE.md` comme facultatifs. MINDCRIME propose donc sa propre répartition des rôles ; celle-ci ne remplace pas les consignes pédagogiques applicables.

## Fonctionnement de l'équipe

Les étudiants apportent le sujet, les contenus, les besoins, les consignes et les choix à examiner. Ils rédigent des éléments et demandent à l'IA de les développer, de les reformuler ou de générer un premier jet. Ils restent responsables des textes et du logiciel qu'ils rendent. Une rédaction pilotée par les étudiants peut ainsi comprendre des passages générés par IA ; leur origine doit être déclarée honnêtement.

Pour le code, les étudiants précisent les comportements attendus et pilotent les demandes. L'IA pourra proposer ou générer du code, y compris une aide à l'implémentation algorithmique **si cette aide est autorisée par l'enseignant**. Les étudiants reliront chaque contribution avant intégration, expliqueront son fonctionnement et apporteront corrections, optimisations ou compléments. Ils pourront également écrire directement certaines parties à la main. Il n'est pas affirmé que ces revues ou contributions manuelles ont déjà eu lieu pour du code applicatif absent du dépôt.

| Activité | Contribution humaine prévue | Assistance IA envisagée |
| --- | --- | --- |
| Idéation et conception | Apporter les idées, définir les besoins, décider et justifier les choix | Critiquer, comparer des options, proposer des pistes |
| Rédaction | Fournir et rédiger les contenus, donner les consignes, reprendre les textes et approuver la version finale | Générer un brouillon, reformuler, structurer et mettre en forme |
| Code | Spécifier, programmer à la main selon les besoins, relire chaque proposition, corriger et optimiser | Générer une première version ciblée, proposer des corrections ou alternatives |
| Algorithmique | Comprendre le principe, dérouler un exemple, justifier complexité, invariants et limites, adapter l'implémentation | Expliquer, proposer des algorithmes, du pseudo-code ou du code dans le cadre autorisé |
| Tests et débogage | Définir les comportements attendus, reproduire les erreurs et vérifier réellement les résultats | Suggérer cas limites, tests, diagnostics et correctifs |
| CI/CD et documentation technique | Choisir les étapes, gérer les secrets, vérifier l'exécution et les faits | Proposer configurations, scripts et explications |
| Validation | Décider de l'acceptation après compréhension, revue et tests | Aider aux contrôles techniques sans remplacer la décision humaine |

## Processus de validation prévu

1. Réflexion et conception humaines : besoin, critères d'acceptation, contraintes et choix argumentés.
2. Travail mixte : rédaction ou programmation humaines et génération assistée ciblée selon les besoins.
3. Compréhension et revue humaines systématiques : relire, expliquer, corriger, optimiser si nécessaire et compléter à la main.
4. Tests unitaires et d'intégration : vérifier cas nominaux, limites, invariants et scénarios ; distinguer tests proposés et tests effectivement exécutés.
5. Validation humaine avant intégration : un autre membre relit la contribution, les résultats et les limites ; les éléments non compris ne sont pas intégrés comme validés.

La démarche de chaque feature reste idéation → back → tests → API REST → front, avec validation des étapes. Chaque étudiant devra pouvoir expliquer et modifier son travail sans assistance. Les algorithmes métier fonctionneront en Java sans dépendance obligatoire à une API d'IA.

## Traçabilité et araignée par compétence

Chaque usage significatif sera documenté dans la fiche de feature ou la PR : auteur humain, outil, objectif et consignes, éléments générés, repris, modifiés ou rejetés, contributions manuelles, relecteur, commandes et résultats réellement observés. Aucun secret ne devra être transmis à l'IA, aucune identité empruntée pour les commits, et une génération IA ne sera pas présentée comme une saisie intégralement humaine.

Les dix axes du modèle sont conservés dans [part-ia.json](part-ia.json) et l'[araignée](part-ia.svg) : Code, Tests, CI/CD, Rédaction, Débogage, Validation, Algorithmique, Spécification, Conception, Idéation. Les rôles humains et la génération automatique doivent être distingués : fournir des consignes et relire ne donne pas automatiquement un pourcentage de lignes humaines, et générer un texte ne signifie pas que l'IA a inventé le sujet ou décidé du contenu.

## Objectifs prévisionnels de développement

L’araignée présente le fonctionnement visé pour le projet : **objectifs**, **plafonds prévisionnels** et **parts réellement constatées** sont trois données différentes. Les objectifs ci-dessous constituent un cadrage prévisionnel établi pour cette documentation à partir du fonctionnement décrit par l’équipe ; ils ne sont pas issus d’une mesure et ne prétendent pas représenter un usage déjà réalisé.

| Compétence | Objectif de part IA | Plafond prévisionnel |
| --- | --- | --- |
| Code | 60 % | 70 % |
| Tests | 50 % | 60 % |
| CI/CD | 60 % | 80 % |
| Rédaction | 60 % | 70 % |
| Débogage | 50 % | 70 % |
| Validation | 10 % | 20 % |
| Algorithmique | 20 % | 30 % |
| Spécification | 20 % | 40 % |
| Conception | 20 % | 30 % |
| Idéation | 10 % | 30 % |

Ces objectifs privilégient l’aide à la production technique et à la mise en forme. Les étudiants apportent les contenus, donnent les consignes et gardent la décision ; leurs apports ne se réduisent pas à la proportion de lignes saisies à la main. La rédaction et le code pourront mêler génération, corrections, optimisations et contributions manuelles. L’objectif algorithmique de 20 % laisse une marge sous le plafond de 30 % et prévoit une aide à l’explication et à l’implémentation dans le cadre autorisé, avec maîtrise humaine du principe et des limites. Pour la validation, la faible part IA représente l’aide aux contrôles techniques ; l’acceptation finale reste une décision humaine.

Un objectif n’est pas un quota à atteindre : l’équipe pourra utiliser moins d’IA et réviser ses objectifs en motivant le changement. Les plafonds sont des limites prévisionnelles de fonctionnement, distinctes de toute autorisation pédagogique. Ils ne prouvent aucun taux effectivement réalisé.

## Bilan de l’usage réel pour R1

Le graphique de droite présente en orange un **bilan documentaire R1 estimé à partir de cette conversation**. Il ne s’agit pas du bilan du futur logiciel : aucun backend ni algorithme métier n’a été développé. Les chiffres sont des estimations de Codex, avec justification axe par axe dans le JSON ; ils ne sont pas des mesures de temps, un audit indépendant ou un comptage de commits.

| Compétence | Part IA estimée R1 | Périmètre et éléments observables |
| --- | --- | --- |
| Code | 100 % | Saisie et adaptations du générateur PDF par Codex ; besoins fournis humainement, aucun code applicatif |
| Tests | N/A | Aucune suite de tests applicatifs produite |
| CI/CD | N/A | Aucun workflow implémenté ; Makefile local seulement |
| Rédaction | 90 % | Production et reformulation des fichiers par Codex à partir des contenus, consignes et corrections de l’équipe |
| Débogage | N/A | Aucun débogage applicatif ; ajustements documentaires inclus dans l’outillage |
| Validation | 70 % | Contrôles techniques documentaires exécutés par l’agent, retours humains sur le contenu et la présentation |
| Algorithmique | N/A | Aucun algorithme métier implémenté ; discussions classées en conception |
| Spécification | 50 % | Formalisation assistée des exigences humaines, scénarios et critères |
| Conception | 70 % | Proposition assistée de l’architecture et des modules, dirigée et corrigée par l’équipe |
| Idéation | 20 % | Concept humain, propositions complémentaires de features par l’agent |

Les valeurs élevées ne retirent pas à l’équipe le rôle de pilotage, l’origine du sujet ou ses apports aux contenus. Une relecture ne transforme toutefois pas une génération IA en saisie humaine. Les estimations de rédaction, validation, spécification, conception et idéation sont qualitatives : leurs valeurs exactes ne peuvent pas être déduites automatiquement de la conversation.

Le bilan dépasse les plafonds prévisionnels sur Code, Rédaction, Validation, Spécification et Conception. Ces écarts sont déclarés : la préparation documentaire et son outillage sont fortement assistés, alors que les objectifs organisent le développement futur. Les périmètres diffèrent ; ce graphique ne prouve donc ni le respect des plafonds du futur logiciel ni une autorisation pédagogique. Les dépassements ne sont pas masqués en réduisant artificiellement l’usage réel.

## Mise à jour du graphique

Dans `part-ia.json`, `objectif` contient la part IA envisagée, `plafond` sa limite prévisionnelle et `ia` la part constatée ou estimée dans le périmètre déclaré. Chaque axe possède une justification. `null` signifie non applicable ou non renseigné, jamais 0 %. Un 0 % devra être renseigné explicitement lorsque l’activité aura été réalisée sans IA.

Le mode `bilan` présente deux graphiques côte à côte : les objectifs violets et plafonds verts pointillés à gauche, le bilan documentaire orange à droite. Les dix axes restent identiques, à la même échelle. Les noms des compétences figurent seuls autour des graphiques ; le tableau placé en dessous indique les pourcentages. Chaque valeur R1 applicable est un point orange relié au centre par son rayon : les axes N/A n’ont aucun point réel, aucun zéro fictif ni polygone reliant des valeurs manquantes.

Les quatre axes N/A (Tests, CI/CD, Débogage, Algorithmique) restent visibles pour conserver les dix compétences communes. Le graphique ne présente pas le bilan documentaire et les objectifs de développement comme des mesures d’un même périmètre. Le mode `previsionnel` masque les marqueurs du bilan ; les valeurs restent documentées dans le JSON.

Après chaque feature et à chaque livraison, l’équipe mettra les parts constatées à jour à partir des contributions et PR, avec périmètre, méthode, limites et écarts. Les objectifs et plafonds pourront évoluer avec justification ; Git conservera les états après enregistrement des commits. Le mode `previsionnel` permet de montrer seulement les objectifs si nécessaire.

`make -C doc` relit les données et régénère le SVG et l’unique PDF. La commande ne calcule aucun pourcentage à partir du code ou des prompts. Les objectifs ne remplacent pas les bilans réels demandés par le cours.

## Assistance documentaire effectivement utilisée

L'équipe a fourni le concept, les deux modes, les identités et rôles, les technologies envisagées, les échéances et les consignes du rendu. Elle a demandé des adaptations au modèle du professeur, puis précisé le fonctionnement souhaité de la rédaction, du code et de l'aide algorithmique ; elle a également transmis le texte de la fiche pédagogique.

Codex a généré et remanié les fichiers documentaires, proposé des pistes de features et produit l'outillage PDF. Les contrôles techniques locaux de génération et pagination ont été exécutés par l'agent ; ils ne valent ni approbation humaine de chaque passage ni validation pédagogique. Aucun code applicatif n'a été produit. Le bilan documentaire de cette session est désormais estimé et expliqué ci-dessus, séparément des objectifs de développement et sans l’attribuer au logiciel encore absent.
