# 🚗 AutoLoc API - Système de gestion de location de véhicules

## 📖 Description
Application Spring Boot pour la gestion d'une agence de location de véhicules. Architecture multicouche avec Spring Data JPA, Hibernate et MariaDB.

## 🏗️ Architecture

```
┌─────────────────────────────────────┐
│   Controller (REST API)             │  ← Atelier 5 (À venir)
│   @RestController                   │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Service (Logique métier)          │  ← Atelier 4 ✅ TERMINÉ
│   @Service                          │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Repository (Accès données)        │  ← Atelier 3 ✅ TERMINÉ
│   JpaRepository                     │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Domain (Entités JPA)              │  ← Ateliers 1-2 ✅ TERMINÉ
│   @Entity                           │
└─────────────────────────────────────┘
```

## 📦 Modules implémentés

### ✅ Atelier 1-2 : Couche Domain
- 9 entités JPA (Agence, Client, Contrat, Employe, Equipement, Maintenance, Paiement, Reservation, Vehicule)
- 4 énumérations (CategorieVehicule, ModePaiement, RoleEmploye, StatutReservation, StatutVehicule)
- Relations JPA (@OneToMany, @ManyToOne, @ManyToMany, @OneToOne)
- Lombok pour réduire le boilerplate

### ✅ Atelier 3 : Couche Repository
- 9 interfaces Spring Data JPA
- Convention de nommage : I...Repository
- Héritage de JpaRepository<Entity, Long>

### ✅ Atelier 4 : Couche Service + IoC/DI
- 9 services avec interfaces et implémentations
- Injection de dépendances par constructeur
- Gestion des exceptions métier (ResourceNotFoundException)
- Validation des règles métier
- Pattern CRUD complet (sauf Paiement en lecture seule)

## 🛠️ Technologies utilisées

| Technologie | Version | Usage |
|------------|---------|-------|
| Java | 17+ | Langage |
| Spring Boot | 4.1.1 | Framework |
| Spring Data JPA | Inclus | Accès données |
| Hibernate | Inclus | ORM |
| MariaDB | - | Base de données |
| Lombok | Inclus | Réduction boilerplate |
| Maven | Wrapper inclus | Build |

## 🗃️ Modèle de données

### Entités principales
1. **Agence** - Agences de location
2. **Client** - Clients de l'agence
3. **Vehicule** - Véhicules disponibles
4. **Reservation** - Réservations de véhicules
5. **Contrat** - Contrats de location
6. **Paiement** - Paiements des contrats
7. **Employe** - Employés des agences
8. **Equipement** - Équipements des véhicules
9. **Maintenance** - Maintenances des véhicules

### Relations clés
- Agence ↔ Vehicule (1:N)
- Agence ↔ Employe (1:N)
- Client ↔ Reservation (1:N)
- Vehicule ↔ Reservation (1:N)
- Vehicule ↔ Equipement (N:N)
- Reservation ↔ Contrat (1:1)
- Contrat ↔ Paiement (1:N avec cascade)

## ⚙️ Configuration

### Base de données (application.properties)
```properties
spring.datasource.url=jdbc:mariadb://localhost:4307/autoloc_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
```

## 🚀 Démarrage rapide

### Prérequis
- JDK 17 ou supérieur
- MariaDB sur le port 4307
- Maven (wrapper inclus)

### Installation
```bash
# Cloner le projet
git clone <url-du-repo>
cd autoloc-api

# Compiler
.\mvnw.cmd clean compile

# Démarrer l'application
.\mvnw.cmd spring-boot:run
```

### Vérification
Au démarrage, vous devriez voir :
```
Found 9 JPA repository interfaces
```

## 📁 Structure du projet

```
autoloc-api/
├── src/main/java/tn/esprit/autoloc/autoloc/
│   ├── AutolocApiApplication.java      # Point d'entrée
│   ├── domain/                         # Entités JPA (9)
│   ├── repository/                     # Repositories (9)
│   ├── service/                        # Services (18)
│   └── exception/                      # Exceptions métier (1)
├── src/main/resources/
│   └── application.properties          # Configuration
├── docs/
│   ├── service-notes.md               # Notes Atelier 4
│   └── ATELIER4-RESUME.md             # Résumé des ajouts
├── pom.xml                            # Dépendances Maven
└── README.md                          # Ce fichier
```

## 📚 Documentation

- **[service-notes.md](docs/service-notes.md)** - Notes complètes de l'Atelier 4
  - Modes d'injection comparés
  - Liste des services et dépendances
  - Messages d'erreur du conteneur
  - Anomalies SonarQube
  - Questions de compréhension

- **[ATELIER4-RESUME.md](docs/ATELIER4-RESUME.md)** - Résumé des différences ajoutées
  - Structure complète
  - Fichiers créés
  - Règles de gestion
  - Bonnes pratiques

## 🎯 Services disponibles

| Service | Opérations | Spécificités |
|---------|-----------|-------------|
| AgenceService | CRUD complet | Validation nom/ville |
| ClientService | CRUD complet | Validation nom/email |
| ContratService | CRUD complet | Validation montant ≥ 0 |
| EmployeService | CRUD complet | Validation nom/prénom/rôle |
| EquipementService | CRUD complet | Validation libellé |
| MaintenanceService | CRUD complet | Validation dates cohérentes |
| PaiementService | **Lecture seule** | Pas de création/modification |
| ReservationService | CRUD complet | Validation dates cohérentes |
| VehiculeService | CRUD complet | Validation tarif ≥ 0 |

## 🧪 Tests

Les tests seront ajoutés dans les ateliers suivants.
Pour l'instant, la vérification se fait par :
1. Compilation réussie
2. Démarrage sans erreur
3. Détection des 9 repositories

## 🔜 Prochaines étapes

### Atelier 5 - API REST
- Création des contrôleurs REST
- DTOs (Data Transfer Objects)
- Endpoints HTTP (GET, POST, PUT, DELETE)
- Tests avec Postman

## 👨‍💻 Auteur
Projet pédagogique - Architecture des Systèmes d'Information (ASI 26-27)

## 📄 Licence
Projet éducatif - ESPRIT
