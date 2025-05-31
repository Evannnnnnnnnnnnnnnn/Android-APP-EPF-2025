# Guide Utilisateur de PasteauDors

Bienvenue sur PasteauDors, votre nouvelle application de e-commerce mobile ! Ce guide vous aidera à naviguer et à utiliser toutes les fonctionnalités de l'application.

## Table des Matières

1.  [Démarrage de l'Application](#1-démarrage-de-lapplication)
2.  [Page d'Accueil](#2-page-daccueil)
    *   [Barre d'Outils Supérieure](#barre-doutils-supérieure)
    *   [Affichage des Produits](#affichage-des-produits)
    *   [Bouton Panier Flottant](#bouton-panier-flottant)
3.  [Rechercher un Article](#3-rechercher-un-article)
4.  [Filtrer par Catégorie](#4-filtrer-par-catégorie)
5.  [Scanner un QRCode](#5-scanner-un-qrcode)
6.  [Voir la Fiche d'un Article](#6-voir-la-fiche-dun-article)
7.  [Ajouter un Article au Panier](#7-ajouter-un-article-au-panier)
8.  [Consulter et Gérer le Panier](#8-consulter-et-gérer-le-panier)
    *   [Modifier la Quantité](#modifier-la-quantité)
    *   [Supprimer un Article](#supprimer-un-article)
    *   [Vider le Panier](#vider-le-panier)
    *   [Valider la Commande](#valider-la-commande)
9.  [Persistance du Panier](#9-persistance-du-panier)

---

## 1. Démarrage de l'Application

Lancez l'application PasteauDors depuis l'icône sur votre appareil. Vous arriverez directement sur la page d'accueil affichant les produits disponibles.

## 2. Page d'Accueil

La page d'accueil est votre point central pour découvrir et rechercher des articles.

![Exemple de Page d'Accueil](lien_vers_screenshot_page_accueil.png) <!-- Optionnel: ajoute un screenshot -->

### Barre d'Outils Supérieure

En haut de l'écran, vous trouverez la barre d'outils avec les éléments suivants, de gauche à droite :

*   **Icône Catégories (icône filtre/liste)** : Ouvre un menu déroulant pour sélectionner une catégorie de produits spécifique.
*   **Icône Scanner QRCode (icône QR)** : Active l'appareil photo de votre téléphone pour scanner un QRCode associé à un produit.
*   **Titre de l'Application "PasteauDors"** : Affiché au centre de la barre d'outils.
*   **Icône Recherche (loupe)** : Affiche un champ de recherche pour trouver des articles par mots-clés.

### Affichage des Produits

Le corps principal de la page d'accueil affiche une grille de produits. Chaque produit est présenté sous forme de vignette avec :
*   Son image
*   Son titre
*   Son prix

Vous pouvez faire défiler cette liste verticalement pour voir plus d'articles.

### Bouton Panier Flottant

Un bouton rond avec une icône de panier est visible en bas à droite de l'écran. Cliquez sur ce bouton à tout moment pour accéder à votre panier.

## 3. Rechercher un Article

1.  Sur la page d'accueil, appuyez sur l'**icône Recherche (loupe)** dans la barre d'outils.
2.  Un champ de recherche apparaît. Tapez les mots-clés correspondant à l'article que vous recherchez (par exemple, "T-shirt", "montre", "bijoux").
3.  La liste des produits se met à jour en temps réel pour afficher les articles correspondant à votre recherche.
4.  Pour soumettre la recherche, vous pouvez appuyer sur la touche "Entrée" de votre clavier ou sur l'icône de recherche du clavier.
5.  Pour effacer la recherche et revenir à la liste complète (ou filtrée par catégorie), effacez le texte dans le champ de recherche ou fermez le champ de recherche.

## 4. Filtrer par Catégorie

1.  Sur la page d'accueil, appuyez sur l'**icône Catégories (filtre/liste)** dans la barre d'outils.
2.  Un menu déroulant apparaît, listant les différentes catégories de produits disponibles (par exemple, "Électronique", "Bijoux", "Vêtements Homme").
3.  Sélectionnez la catégorie de votre choix.
4.  La liste des produits sur la page d'accueil se mettra à jour pour n'afficher que les articles de la catégorie sélectionnée.
5.  Pour afficher à nouveau toutes les catégories, ouvrez le menu des catégories et sélectionnez "Toutes les catégories".

## 5. Scanner un QRCode

Si un produit physique possède un QRCode fourni par PasteauDors, vous pouvez le scanner pour accéder directement à sa fiche article.

1.  Sur la page d'accueil, appuyez sur l'**icône Scanner QRCode (QR)** dans la barre d'outils.
2.  Si c'est la première fois, l'application vous demandera l'autorisation d'accéder à l'appareil photo. Veuillez accepter pour utiliser cette fonctionnalité.
3.  L'appareil photo de votre téléphone s'active. Pointez-le vers le QRCode du produit.
4.  Une fois le QRCode reconnu, l'application vous redirigera automatiquement vers la fiche détaillée de l'article correspondant.
5.  Si le scan est annulé ou si le QRCode n'est pas valide (ne contient pas un ID de produit reconnu), un message vous en informera.

## 6. Voir la Fiche d'un Article

Depuis la page d'accueil (ou les résultats de recherche/filtre), appuyez sur la vignette d'un produit qui vous intéresse.
Vous serez redirigé vers la page de détail du produit, qui affiche :
*   Une image plus grande du produit
*   Le titre complet
*   Le prix
*   La catégorie
*   La note moyenne et le nombre d'avis
*   Une description détaillée
*   Un bouton "Ajouter au Panier"

Utilisez la flèche "Retour" en haut à gauche de l'écran (ou le bouton retour de votre téléphone) pour revenir à la page précédente.

## 7. Ajouter un Article au Panier

Depuis la fiche détaillée d'un article :

1.  Appuyez sur le bouton **"Ajouter au Panier"**.
2.  Une petite fenêtre (dialogue) s'ouvre, vous demandant de **choisir la quantité** souhaitée pour cet article (par défaut 1). Utilisez les commandes pour ajuster la quantité (généralement entre 1 et 10).
3.  Appuyez sur le bouton **"Ajouter"** dans cette fenêtre.
4.  L'article (avec la quantité choisie) est ajouté à votre panier.
5.  Une notification temporaire (Snackbar) apparaîtra en bas de l'écran confirmant l'ajout, avec un bouton **"VOIR PANIER"** pour accéder directement à votre panier.

## 8. Consulter et Gérer le Panier

Vous pouvez accéder à votre panier à tout moment en appuyant sur le **bouton panier flottant** en bas à droite de la page d'accueil.

La page du panier affiche la liste de tous les articles que vous avez ajoutés, avec pour chacun :
*   Son image
*   Son titre
*   Son prix unitaire
*   La quantité sélectionnée
*   Des boutons pour modifier la quantité ou supprimer l'article

En bas de la page du panier, vous trouverez le **prix total** de votre commande et des boutons d'action.

![Exemple de Page Panier](lien_vers_screenshot_page_panier.png) <!-- Optionnel: ajoute un screenshot -->

### Modifier la Quantité

Pour chaque article dans le panier :
*   Appuyez sur le bouton **"+"** pour augmenter la quantité.
*   Appuyez sur le bouton **"-"** pour diminuer la quantité.
    *   Si la quantité atteint 0 en diminuant, l'article sera automatiquement retiré du panier.

Le prix total du panier se met à jour automatiquement.

### Supprimer un Article

Pour chaque article dans le panier, vous trouverez une **icône poubelle**. Appuyez sur cette icône pour retirer complètement l'article de votre panier.

### Vider le Panier

En bas de la page du panier, un bouton **"Vider le Panier"** vous permet de supprimer tous les articles de votre panier en une seule fois.

### Valider la Commande

Lorsque vous êtes satisfait de votre sélection, appuyez sur le bouton **"Valider la commande"**.
*(Note : La fonctionnalité de paiement et de finalisation de commande n'est pas implémentée dans cette version de l'application.)*

## 9. Persistance du Panier

Votre panier est sauvegardé localement sur votre appareil. Si vous fermez l'application et la rouvrez plus tard, les articles que vous aviez ajoutés à votre panier y seront toujours présents.

---

Merci d'utiliser PasteauDors ! Si vous avez des questions ou rencontrez des problèmes, n'hésitez pas à nous contacter.
