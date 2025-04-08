# Ticketer - Système de Gestion de Tickets pour Helpdesk

## Description
Ticketer est une application web de gestion de tickets de support (helpdesk) développée avec Spring Boot. Cette plateforme complète permet aux étudiants de soumettre des demandes d'assistance à différents secteurs administratifs (Finance, EAD, Secrétariat) et aux agents de ces secteurs de traiter efficacement ces demandes. Le système suit l'intégralité du cycle de vie d'un ticket, de sa création jusqu'à sa résolution.

Ce projet implémente une architecture MVC robuste avec Spring Security pour l'authentification et l'autorisation, Spring Data JPA pour la persistance des données, et Thymeleaf avec Bootstrap pour les interfaces utilisateur.

## Fonctionnalités principales

### Pour les étudiants
- Inscription et authentification via un système de comptes sécurisés
- Tableau de bord personnalisé affichant tous les tickets de l'étudiant
- Création de tickets pour différents secteurs administratifs
- Filtrage des tickets par statut (ouverts, en cours, finalisés)
- Suivi détaillé de l'évolution des tickets
- Communication bidirectionnelle avec les agents via un système de messagerie intégré
- Interface utilisateur intuitive et responsive

### Pour les agents
- Authentification sécurisée avec séparation des rôles
- Tableau de bord spécialisé affichant les tickets pertinents pour leur secteur
- Filtrage avancé des tickets par statut 
- Prise en charge (assignation) des tickets ouverts
- Traitement des demandes avec historique complet
- Communication efficace avec les étudiants
- Finalisation et clôture des tickets résolus

### Fonctionnalités système
- Gestion complète du cycle de vie des tickets
- Système de rôles et privilèges pour contrôler les accès
- Traçabilité des actions avec horodatage
- Affichage visuel du statut des tickets (codes couleur)
- Protection des données sensibles
- Architecture modulaire et extensible

## Technologies utilisées
- **Backend**
  - Java 8+
  - Spring Boot 2.x (framework principal)
  - Spring MVC (architecture web)
  - Spring Security (authentification et autorisation)
  - Spring Data JPA (accès aux données)
  - Hibernate (ORM pour la persistance)
  - BCrypt (hachage des mots de passe)

- **Frontend**
  - Thymeleaf (moteur de templates)
  - HTML5 / CSS3
  - Bootstrap 5 (framework CSS responsive)
  - JavaScript (interactions côté client)

- **Base de données**
  - MySQL/H2 Database (stockage relationnel)
  - JPA/Hibernate (mapping objet-relationnel)

- **Outils de build et déploiement**
  - Maven (gestion des dépendances et build)
  - Git (contrôle de version)
  - Spring Boot Embedded Server (déploiement simplifié)

## Structure du projet

### Modèle (Model)
- `User` : Classe abstraite parent implémentant UserDetails de Spring Security
  - Attributs: id, username, password, email, enabled
  - Relations: roles (ManyToMany), messages (OneToMany)
  - Méthodes: getAuthorities(), isAccountNonExpired(), etc.

- `Student` (étend User)
  - Attributs spécifiques: registration (numéro d'inscription)
  - Relations: tickets créés (OneToMany)

- `Agent` (étend User)
  - Attributs spécifiques: sector (FINANCIAL, EAD, SECRETARY)
  - Relations: tickets assignés (OneToMany)

- `Ticket` : Représente une demande d'assistance
  - Attributs: id, subject, status, creationDate, closingDate, sector
  - Relations: student (ManyToOne), agent (ManyToOne), messages (OneToMany)

- `Message` : Communication entre utilisateurs
  - Attributs: id, text, sendDate
  - Relations: author (ManyToOne), ticket (ManyToOne)

- `Role` et `Privilege` : Pour la gestion RBAC des droits d'accès
  - Relations complexes: User-Role (ManyToMany), Role-Privilege (ManyToMany)

- `TicketForm` : DTO pour la création de tickets
  - Utilisé pour valider les entrées du formulaire

- Enums : 
  - `Status` (OPEN, IN PROCESSING, FINALIZED)
  - `Sector` (FINANCIAL, EAD, SECRETARY)

### Contrôleur (Controller)
- `HomeController` : Gestion de la page d'accueil
  - Mappings: / (page d'accueil)

- `StudentController` : Gestion du flux étudiant
  - Mappings: /student/login, /student/form_student, /student/new, /student/dashboard, /student/dashboard/{status}
  - Fonctionnalités: inscription, connexion, tableau de bord, filtrage par statut

- `AgentController` : Gestion du flux agent
  - Mappings: /agent/login, /agent/dashboard, /agent/dashboard/{status}
  - Fonctionnalités: connexion, tableau de bord, filtrage par statut et secteur

- `TicketController` : Gestion du cycle de vie des tickets
  - Mappings: /ticket/{id}, /ticket/{id}/send, /ticket/{id}/close, /ticket/{id}/take, /ticket/form_ticket, /ticket/save_ticket
  - Fonctionnalités: création, visualisation, envoi de messages, prise en charge, clôture

### Vues (Views)
- Templates Thymeleaf avec fragments réutilisables
  - `base.html` : Fragments pour en-tête, navigation, etc.
  - `home.html` : Page d'accueil principale
  - `login.html` et `login_agent.html` : Pages de connexion
  - `form_student.html` : Inscription étudiant
  - `form_ticket.html` : Création de ticket
  - `dashboard_student.html` et `dashboard_agent.html` : Tableaux de bord
  - `ticket.html` et `ticket_agent.html` : Détail des tickets

### Repositories (Spring Data JPA)
- `UserRepository` : Recherche par email
- `StudentRepository` : Opérations spécifiques aux étudiants
- `AgentRepository` : Opérations spécifiques aux agents
- `TicketRepository` : Recherche complexe de tickets (par statut, secteur, agent)
- `MensagemRepository` : Gestion des messages
- `RoleRepository` et `PrivilegeRepository` : Gestion des rôles et privilèges

### Services
- `AuthenticationService` : Implémentation de UserDetailsService pour l'authentification

### Configuration
- `WebSecurityConfig` : Configuration avancée de Spring Security
  - Trois configurations distinctes (StudentSecurityConfig, AgentSecurityConfig, SecurityConfig)
  - Configuration des encodeurs de mot de passe
  - Définition des règles d'accès aux URLs

- `DataLoader` : Initialisation des données au démarrage
  - Création des rôles et privilèges
  - Création des comptes agents par défaut

- `TicketerApplication` : Point d'entrée principal de l'application Spring Boot

## Configuration et déploiement

### Prérequis
- JDK 8 ou supérieur
- Maven 3.x
- MySQL ou autre base de données relationnelle (configurable)
- Git (pour cloner le dépôt)

### Installation
1. Cloner le dépôt
```bash
git clone https://github.com/Abdelghany32/PROJETJAVAhelpdesk.git
cd PROJETJAVAhelpdesk
```

2. Configurer la base de données dans `src/main/resources/application.properties`
```properties
# Configuration de la base de données
spring.datasource.url=jdbc:mysql://localhost:3306/ticketer?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=password

# Configuration JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Configuration Thymeleaf
spring.thymeleaf.cache=false
```

3. Compiler l'application
```bash
mvn clean install
```

4. Lancer l'application
```bash
mvn spring-boot:run
```

5. Accéder à l'application
```
http://localhost:8080
```

### Déploiement en production

Pour un déploiement en production, suivez ces étapes supplémentaires :

1. Créer un fichier WAR déployable
```bash
mvn clean package -P prod
```

2. Configuration pour environnement de production dans `application-prod.properties`
```properties
# Configuration production
spring.datasource.url=jdbc:mysql://[your-production-db-host]:3306/ticketer
spring.datasource.username=[production-username]
spring.datasource.password=[production-password]
spring.jpa.hibernate.ddl-auto=validate

# Logging
logging.level.org.springframework=ERROR
logging.level.com.brub.ticketer=INFO
```

3. Déployer le fichier WAR généré sur un serveur d'application comme Tomcat ou utiliser le JAR avec:
```bash
java -jar -Dspring.profiles.active=prod target/ticketer-0.0.1-SNAPSHOT.jar
```

## Comptes préconfigurés pour test

### Agents
- **Secteur EAD**
  - Email: ead@ra.com
  - Mot de passe: 12345

- **Secteur FINANCEIRO**
  - Email: tesouraria@ra.com
  - Mot de passe: 12345

- **Secteur SECRETARIA**
  - Email: secretaria@ra.com
  - Mot de passe: 12345

### Étudiants
Les comptes étudiants peuvent être créés via le formulaire d'inscription.

## Cycle de vie d'un ticket
1. Un étudiant crée un ticket pour un secteur spécifique (statut ABERTO)
2. Un agent du secteur prend en charge le ticket (statut EM_ANDAMENTO)
3. Communication via messages entre l'étudiant et l'agent
4. L'agent finalise le ticket une fois résolu (statut FINALIZADO)

## Structure de l'interface utilisateur
- **Page d'accueil** : Choix entre connexion Étudiant ou Agent
- **Tableaux de bord** : Visualisation et filtrage des tickets
- **Détail du ticket** : Historique des messages et actions possibles

## Développement

### Extension du projet
- Ajout de nouveaux secteurs dans l'enum `Sector`
- Création de nouveaux rôles et privilèges dans `DataLoader`
- Extension des fonctionnalités via les contrôleurs existants

### Contribuer
1. Fork le projet
2. Créer une branche pour votre fonctionnalité (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Commiter vos changements (`git commit -m 'Ajouter une nouvelle fonctionnalité'`)
4. Pousser vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Ouvrir une Pull Request

## Caractéristiques avancées

### Validation des données
- Validation des entrées utilisateur avec Bean Validation (`@NotBlank`, etc.)
- Gestion des erreurs de validation côté client et serveur
- Feedback visuel pour les erreurs de formulaire

### Sécurité renforcée
- Protection contre les injections SQL avec JPA/Hibernate
- Chiffrement des mots de passe avec BCrypt
- Sessions gérées par Spring Security
- Principe du moindre privilège appliqué aux rôles

### Structure de packages organisée
```
com.brub.ticketer
├── controller        # Contrôleurs MVC
├── model             # Entités et modèles de données
├── repository        # Interfaces d'accès aux données
├── service           # Services métier 
└── config            # Configuration Spring
```

### Couverture des tests
- Tests unitaires pour les services critiques
- Tests d'intégration pour les flux principaux
- Tests de repositories avec H2 en mémoire

## Roadmap
Fonctionnalités planifiées pour les versions futures
