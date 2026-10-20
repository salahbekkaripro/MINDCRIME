# Contribution à MINDCRIME

Règles de travail prévues, adaptées du [guide du professeur](https://github.com/pascalpoizat/template-projet-m1-miage/blob/main/documentation.md) et de son [CONTRIBUTING](https://github.com/pascalpoizat/template-projet-m1-miage/blob/main/CONTRIBUTING.md). L'application et sa chaîne d'intégration restent à construire.

Chaque étudiant porte une feature algorithmique identifiable par itération, avec effets sur BD, backend Java, API REST et frontend. Voir le [plan](doc/PLAN_FEATURES.md). Une simple optimisation, du CRUD ou la stabilisation ne suffit pas.

La démarche suit cet ordre, avec validation de chaque étape avant la suivante :

1. Idéation : issue et fiche `doc/features/<version>-F<n>-<nom>.md`, besoin, scénario, algorithme, complexité, alternatives, impacts et critères d'acceptation ; relecture par un autre membre.
2. Back : schéma versionné, domaine Java pur, ports et services, adaptateurs et assemblage. Vérifier compilation et règles d'architecture une fois disponibles.
3. Tests : cas nominaux, limites, invariants, propriétés à graines fixes ; services avec faux adaptateurs, intégration de la persistance. Chaque critère devra être couvert ; commandes et résultats réels seront consignés.
4. API REST : ressources `/api/v1`, DTO validés, erreurs `problem+json`, OpenAPI et tests d'intégration, y compris les erreurs.
5. Front : uniquement via l'API, scénario réellement jouable, PR relue et bilan IA complété.

Organisation prévue : `feature/<issue>-<nom>` pour une feature, `develop` pour l'intégration, `main` pour les versions taguées. Ne pas réécrire une migration déjà publiée ; coordonner les numéros Flyway. Prévoir un jeu de démonstration idempotent. Les commandes Gradle et migrations du template ne sont pas disponibles dans ce dépôt.

Chaque membre doit vérifier son identité avant son premier commit :

```bash
git config user.name
git config user.email
```

L'email doit être associé à son compte GitHub. Les identités individuelles ne sont pas encore vérifiées ; aucun email n'est inventé dans le dossier.

En fin de séance, prévoir un commit et un tag de pré-livraison tel que `v0.1.0-SNAPSHOT`. Avant une livraison : dossier, araignée et page de projet actualisés, build et tests applicatifs réellement vérifiés, `make -C doc` exécuté. La CI devra ensuite produire la documentation et joindre JAR + PDF à la release ; ces automatismes restent à implémenter. Chaque membre dépose individuellement l’URL du commit exact dans le devoir. R1 : 23 h 59 ; les devoirs R2–R4 affichent 12 h 50, horaire retenu pour organiser le dépôt malgré le planning général à 23 h 59. Étape intermédiaire le 1er décembre 2026, transparents le 18 janvier 2027 à 23 h 59. Voir le [planning des livraisons](doc/RELEASES.md).

Chaque PR devra comporter une section « Usage de l'IA » : outil, demande, éléments repris/modifiés/rejetés, revue humaine, vérifications et part par compétence. Voir [AI_USAGE.md](doc/AI_USAGE.md). Le protocole et les plafonds décrivent l’organisation prévisionnelle. Les publications sont effectuées sur instruction explicite ; les corrections passent par une PR et conservent les commits et tags déjà publiés.

## Protection de main

La règle active `protect-main` impose une pull request et une approbation, bloque la suppression et les force push, sans acteur de contournement. Les nouveaux commits annulent les approbations précédentes ; les discussions doivent être résolues avant fusion. Aucun contrôle CI n’est encore obligatoire : les workflows restent à implémenter. Réglages vérifiés le 20 octobre 2026.
