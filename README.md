# Guide Utilisateur de PasteauDors

Bienvenue sur PasteauDors, votre nouvelle application de e-commerce mobile ! Ce guide vous aidera à naviguer et à utiliser toutes les fonctionnalités de l'application, développée dans le cadre du projet Android/Kotlin.

## Table des Matières

1.  [Présentation du Projet](#1-présentation-du-projet)
    *   [Objectifs du TP](#objectifs-du-tp)
    *   [API Utilisée](#api-utilisée)
2.  [Démarrage de l'Application](#2-démarrage-de-lapplication)
3.  [Fonctionnalités de Base (Contraintes du TP)](#3-fonctionnalités-de-base-contraintes-du-tp)
    *   [Rechercher un Article](#rechercher-un-article)
    *   [Afficher la Fiche d'un Article](#afficher-la-fiche-dun-article)
    *   [Scanner un QRCode Produit](#scanner-un-qrcode-produit)
    *   [Mettre un Article dans le Panier](#mettre-un-article-dans-le-panier)
    *   [Afficher et Gérer le Panier](#afficher-et-gérer-le-panier)
4.  [Fonctionnalités "Bonus" et Améliorations UX](#4-fonctionnalités-bonus-et-améliorations-ux)
    *   [Page d'Accueil Améliorée](#page-daccueil-améliorée)
    *   [Filtrage des Produits par Catégorie](#filtrage-des-produits-par-catégorie)
    *   [Gestion Avancée du Panier](#gestion-avancée-du-panier)
    *   [Persistance du Panier](#persistance-du-panier)
    *   [Thème Clair et Sombre (Mode Nuit)](#thème-clair-et-sombre-mode-nuit)
    *   [Interface Utilisateur Moderne et Soignée](#interface-utilisateur-moderne-et-soignée)
    *   [Synchronisation (Partielle) du Panier avec l'API](#synchronisation-partielle-du-panier-avec-lapi)
5.  [Détail du Fonctionnement](#5-détail-du-fonctionnement)

---

## 1. Présentation du Projet

### Objectifs du TP

L'objectif principal de ce projet était de développer une application mobile de e-commerce sur la plateforme Android en utilisant le langage Kotlin. Les fonctionnalités minimales requises étaient :
*   Recherche d'articles.
*   Affichage de la fiche d'un article.
*   Scan de QRCode pour visualiser un article.
*   Mise au panier d'un article.
*   Affichage du panier.

### API Utilisée

Toutes les données concernant les articles et la gestion (partielle) des paniers proviennent de l'API REST [FakeStoreAPI](https://fakestoreapi.com/).

## 2. Démarrage de l'Application

Lancez l'application PasteauDors depuis l'icône sur votre appareil. Vous arriverez directement sur la page d'accueil. Au premier lancement, l'application tentera de charger un panier existant sauvegardé localement et de récupérer des informations de panier du serveur (pour un utilisateur par défaut).

## 3. Fonctionnalités de Base (Contraintes du TP)

Cette section décrit les fonctionnalités qui répondent directement aux exigences du sujet du TP.

### Rechercher un Article

1.  Sur la page d'accueil, appuyez sur l'**icône Recherche (loupe)** dans la barre d'outils supérieure.
2.  Un champ de recherche s'active. Tapez les mots-clés (ex: "shirt", "jewelery").
3.  La liste des produits se met à jour **en temps réel** (dès 2 caractères saisis) pour afficher les articles dont le titre ou la description correspondent à votre recherche. La recherche tient également compte de la catégorie actuellement sélectionnée, le cas échéant.
4.  Pour soumettre explicitement ou effacer le champ, utilisez les options du clavier ou le bouton de fermeture du champ de recherche.

### Afficher la Fiche d'un Article

1.  Depuis la page d'accueil (ou les résultats de recherche/filtre), appuyez sur la vignette d'un produit.
2.  La page de détail du produit s'affiche, présentant :
    *   Image du produit (souvent détourée pour une meilleure intégration visuelle)
    *   Titre complet
    *   Prix
    *   Catégorie
    *   Note et nombre d'avis (si disponibles via l'API)
    *   Description détaillée
    *   Bouton "Ajouter au Panier"
3.  Utilisez la flèche "Retour" en haut à gauche pour revenir.

### Scanner un QRCode Produit

Cette fonctionnalité permet d'accéder directement à la fiche d'un article en scannant un QRCode contenant son identifiant.

1.  Sur la page d'accueil, appuyez sur l'**icône Scanner QRCode** dans la barre d'outils.
2.  Si nécessaire, accordez l'autorisation d'utiliser l'appareil photo.
3.  Pointez l'appareil photo vers un QRCode (générez des QRCodes contenant des ID de produits de 1 à 20 via un générateur en ligne, type "Texte").
4.  Après un scan réussi, la fiche de l'article correspondant s'affiche.

### Mettre un Article dans le Panier

1.  Depuis la fiche détaillée d'un article, appuyez sur le bouton **"Ajouter au Panier"**.
2.  Une boîte de dialogue apparaît, vous permettant de **choisir la quantité** souhaitée pour cet article.
3.  Confirmez en appuyant sur "Ajouter". L'article est ajouté à votre panier.

### Afficher et Gérer le Panier

1.  Accédez au panier en appuyant sur le **bouton flottant en forme de panier** en bas à droite de la page d'accueil.
2.  La page du panier liste les articles ajoutés, leur quantité et prix.
3.  Vous pouvez :
    *   **Modifier la quantité** d'un article avec les boutons `+` et `-`.
    *   **Supprimer un article** individuellement via l'icône poubelle.
    *   Voir le **total du panier**.

## 4. Fonctionnalités "Bonus" et Améliorations UX

En plus des exigences de base, plusieurs fonctionnalités et améliorations ont été implémentées pour enrichir l'expérience utilisateur et explorer des aspects plus avancés du développement Android.

### Page d'Accueil Améliorée

*   **Barre d'outils personnalisée :** La barre d'outils s'adapte pour ne pas se superposer à la barre de statut du système.
*   **Affichage en grille :** Les produits sont présentés dans une grille à deux colonnes pour une meilleure utilisation de l'espace. Les images des produits de l'API (qui ont souvent des fonds blancs) sont affichées de manière à ce qu'elles s'intègrent bien sur le fond clair de l'application, donnant un effet "détouré".
*   **Bouton Panier Flottant (FAB) :** Un accès rapide et visible au panier, stylisé en carré aux coins arrondis.

### Filtrage des Produits par Catégorie

1.  Appuyez sur l'**icône Catégories** dans la barre d'outils de la page d'accueil.
2.  Un menu déroulant liste toutes les catégories de produits récupérées dynamiquement depuis l'API.
3.  La sélection d'une catégorie filtre instantanément les produits affichés. Une option "Toutes les catégories" permet de réinitialiser le filtre.

### Gestion Avancée du Panier

*   **Modification de quantité :** Directement dans le panier, l'utilisateur peut augmenter ou diminuer la quantité de chaque article. Diminuer à zéro retire l'article.
*   **Suppression individuelle :** Chaque article du panier peut être supprimé.
*   **Vider le panier :** Un bouton permet de supprimer tous les articles en une seule action.
*   **Indication visuelle pour la validation :** Le bouton "Valider la commande" change de style (contour vs. plein) et est désactivé si le panier est vide, offrant un retour visuel clair.
*   **Affichage conditionnel :** Un message et une image (GIF animé d'un chien triste) s'affichent lorsque le panier est vide.

### Persistance du Panier

Le contenu du panier est **sauvegardé localement** sur l'appareil à l'aide de Jetpack DataStore. Si l'utilisateur ferme et rouvre l'application, les articles précédemment ajoutés au panier y sont restaurés.

### Thème Clair et Sombre (Mode Nuit)

L'application supporte intégralement le **mode clair et le mode sombre (nuit)** du système d'exploitation. L'interface s'adapte automatiquement avec une palette de couleurs optimisée pour chaque mode, utilisant les couleurs spécifiées (`#f8f8f8` pour le fond clair, `#4A4859` pour les textes/éléments foncés, et des équivalents pour le mode nuit).

### Interface Utilisateur Moderne et Soignée

*   **Palette de couleurs personnalisée :** Utilisation des couleurs `#f8f8f8` (fond), `#4A4859` (texte/icônes/boutons) pour un look moderne et minimaliste.
*   **Styles cohérents :** Application de styles pour les boutons, la Toolbar, et les cartes de produits pour une apparence unifiée.
*   **Intégration des images produits :** Les images des produits fournies par l'API ont des fonds unis (blancs). Sur le fond sombre de l'application, les images sont détourré automatiquement, s'intégrant de manière plus harmonieuse et professionnelle.
*   **Icônes Material Design :** Utilisation d'icônes vectorielles standards et personnalisées.
*   **Transitions et retours visuels :** Indicateurs de chargement, toasts pour les actions, et mises à jour dynamiques de l'interface.

### Synchronisation (Partielle) du Panier avec l'API

Bien que l'API FakeStoreAPI ait des limitations pour une gestion complète des paniers utilisateurs, une tentative de synchronisation a été implémentée :
*   Lors de l'ajout, de la modification ou de la suppression d'articles, l'application essaie de mettre à jour (créer, modifier, supprimer) un panier sur le serveur pour un utilisateur par défaut (ID fixe).
*   Au démarrage, l'application tente de récupérer l'ID d'un panier serveur existant pour cet utilisateur.
*   *Note : En raison de la nature de l'API (par exemple, le fait que les mises à jour écrasent tout le panier), cette synchronisation est indicative et n'implémente pas de stratégies de fusion complexes.*

## 5. Détail du Fonctionnement

*   **Récupération de données :** L'application utilise Retrofit pour communiquer avec l'API FakeStoreAPI et Gson pour désérialiser les réponses JSON.
*   **Affichage d'images :** Glide est utilisé pour charger et afficher efficacement les images des produits.
*   **Listes :** Les listes de produits et les articles du panier sont affichées à l'aide de RecyclerViews pour des performances optimales.
*   **Scan QRCode :** La bibliothèque `zxing-android-embedded` est utilisée pour la fonctionnalité de scan.
*   **Gestion d'état et persistance :** Un singleton `CartManager` gère la logique du panier, et Jetpack DataStore assure la persistance locale.
*   **Interface utilisateur :** Construite avec des layouts XML traditionnels et des composants Material Design.

---

Merci d'utiliser l'application PasteauDors !
