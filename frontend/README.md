# Interface D2 — témoignages

Interface React 19 / TypeScript / Vite. Elle permet de sélectionner le suspect concerné, la présence ou l'absence, le lieu et l'instant, puis d'afficher l'analyse calculée par Java et ses déclarations sources. Elle ne contient aucun solveur de contraintes.

## Développement local

Depuis `frontend/`, avec Node 22.12+ et npm :

```powershell
npm.cmd ci
npm.cmd run dev
```

Le serveur Vite affiche son URL locale et relaie `/api` vers `http://127.0.0.1:8080`. Le backend doit donc être lancé séparément. Le compte du profil `demo` est `demo / mindcrime-demo`. Les identifiants d'accès sont utilisés en mémoire, sans enregistrement dans localStorage. Ne pas réutiliser ce compte fictif pour un service partagé.

Pour construire l'interface :

```powershell
npm.cmd run build
```

Cette commande vérifie TypeScript et produit `dist/`, ignoré par Git. Construire ensuite le JAR backend pour y embarquer ces fichiers. Le parcours complet, les commandes depuis la racine et les limites de la démonstration sont décrits dans le [README backend](../backend/README.md).

L'écran distingue erreur de requête, indisponibilité, analyse incomplète et incompatibilité métier. Une déclaration enregistrée reste conservée si l'analyse révèle un conflit. Les faits répétés sont regroupés dans les conflits, avec leurs références. La grille et les contrôles disposent de règles CSS pour petits écrans ; la validation réelle en navigateur est consignée dans la fiche F2, sans prétendre qu'un test mobile automatisé a été exécuté.
