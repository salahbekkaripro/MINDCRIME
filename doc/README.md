# Documentation MINDCRIME

Chaque membre peut documenter sa feature sur sa branche.

- `equipe/` contient les documents communs et l'ancien dossier R1, conservé avec son PDF et son araignée.
- `membres/` contient un dossier et un bilan IA indépendants pour chaque membre.
- `modeles/` contient les modèles à utiliser pour une nouvelle contribution.
- `releases/` reçoit les dossiers figés produits après l'intégration des branches.
- `outils/` contient les scripts de génération.

## Travail sur une feature

Chaque membre travaille dans `doc/membres/<identifiant>/` sur sa branche `feature/...`. Il complète `dossier.md`, `part-ia.json` et ajoute ses fiches dans `features/`. Les documents des autres membres et les fichiers de `releases/` ne sont pas modifiés pendant ce travail.

Les dix axes de `part-ia.json` restent dans le même ordre. `null` signifie qu'aucune estimation applicable n'est encore renseignée ; cette valeur ne doit pas être remplacée arbitrairement par `0`.

## Construire le dossier R1 historique

```bash
make -C doc equipe
pdfinfo doc/equipe/dossier.pdf
```

Cette commande reconstruit uniquement le dossier collectif R1 et vérifie qu'il contient toujours quatre pages A4.

## Préparer une release

Après la fusion des cinq contributions dans la branche d'intégration :

```bash
make -C doc release VERSION=v0.1.0
```

La commande vérifie que les cinq bilans IA portent la version demandée. Elle crée `doc/releases/v0.1.0/` avec les documents communs, les cinq contributions, le dossier assemblé en Markdown et PDF, le manifeste IA et l'araignée SVG. Elle refuse d'écraser un dossier de release existant.

Le dossier généré est une photographie de la documentation intégrée. Il ne doit pas être modifié à la main : les corrections se font dans `equipe/` ou `membres/`, puis une nouvelle version est générée.
