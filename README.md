
# 🎓 Projet de Gestion d’Inscriptions à des Cours de Soutien

## 👥 Auteurs
- **Hartz Guillaume**
- **Pouch Clément**

---

## 📘 Aperçu
Ce projet a pour objectif de développer une application Java modulaire permettant la **gestion complète des inscriptions d’enfants à des cours de soutien**.  
L’application prend en compte la **capacité des salles**, la **gestion des paiements** et la **communication avec les parents**.

---

## 🧩 Fonctionnalités principales

| Domaine | Fonctionnalités |
|----------|----------------|
| 👤 Utilisateurs | Gestion des comptes (parents, gestionnaires) |
| 🔐 Authentification | Connexion par email et mot de passe |
| 🧒 Inscriptions | Attribution des enfants à des créneaux disponibles |
| 🕒 Créneaux | Gestion des états (disponible, complet) |
| 💳 Paiements | Paiement en une fois ou jusqu’à 6 versements |
| 📢 Notifications | Alertes automatiques des échéances de paiement |
| 📊 Suivi | Tableau de bord des montants et mouvements financiers |

---

## 🏗️ Architecture du projet

src/

├── decorator/       
├── facade/          
├── factory/         
├── main/            
├── observer/        
├── state/           
├── stockage/        
├── strategy/        
├── ui/              
└── User/            

---

## ⚙️ Principes de Conception Orientée Objet

| Concept | Description |
|----------|-------------|
| **Agrégation** | Un parent peut posséder plusieurs enfants inscrits. |
| **Cohésion** | Chaque classe a une responsabilité unique. |
| **Héritage** | Les rôles *Parent* et *Gestionnaire* héritent de *Utilisateur*. |
| **Encapsulation** | Les informations sensibles sont protégées par des accesseurs. |
| **Polymorphisme** | Les actions varient selon le type d’utilisateur. |

---

## 🧠 Patrons de Conception Utilisés

| Patron | Rôle |
|---------|------|
| **Factory** | Instanciation des modes de paiement et utilisateurs |
| **Observer** | Notification automatique des parents |
| **State** | Gestion dynamique des états de créneaux |
| **strategy** | Calcul flexible des paiements |
| **Singleton** | Configuration globale unique |
| **Facade** | Simplification de l’accès aux modules |

---

## 💻 Exécution

### Prérequis
- **Java 17+**
- IDE compatible (IntelliJ, Eclipse, VS Code)
- **Maven** (optionnel pour la gestion de dépendances)

### Lancer le projet
# Compilation
javac -d bin src/main/*.java

# Exécution
java -cp bin main.Main
---

## 📚 Documentation

La documentation technique comprend :

* Description des classes et packages
* Diagrammes UML (architecture, séquence, classes)
* Guide d’installation et d’utilisation

---

## 🧾 Licence

Projet académique réalisé dans le cadre d’un enseignement en ingénierie informatique.
Tous droits réservés © 2025 — *Hartz Guillaume & Pouch Clément*


---
