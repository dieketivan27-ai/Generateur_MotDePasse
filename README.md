# Documentation — Générateur de Mots de Passe (Architecture DevOps)

## 1. Analyse Fonctionnelle
Cet outil permet de générer des mots de passe sécurisés selon des critères précis (longueur, types de caractères). Sa particularité réside dans son système de validation : chaque mot de passe est soumis à un audit externe pour garantir sa solidité réelle.

## 2. Analyse Technique (DevOps)
Le projet utilise une architecture **micro-services** conteneurisée :
*   **Conteneur `password-generator`** : Application CLI Java 21 qui gère la logique de génération et l'interface utilisateur.
*   **Communication** : Le générateur communique avec le validateur via des requêtes **HTTP REST** sur le réseau isolé Docker.
*   **Interopérabilité** : Si le service de validation est indisponible, l'application bascule sur un mode dégradé (audit local) pour assurer la continuité de service.

## 3. Guide d'Installation
1.  Assurez-vous que Docker Desktop est lancé.
2.  Exécutez le script `run.bat` à la racine du projet.
3.  Le script va automatiquement :
    *   Compiler les deux projets Java via des builds multi-stage.
    *   Lancer les deux services dans un réseau Docker commun.
    *   Vous connecter à l'interface de l'application.

---
*Projet 100% Java & Docker — Validation des propriétés DevOps.*
