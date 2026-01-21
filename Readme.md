# Projet Base de Données - Épicerie Locale

Projet de gestion d'une épicerie locale développé dans le cadre du cours de bases de données.

## Description

Application Java avec interface graphique permettant la gestion complète d'une épicerie : catalogue produits, commandes clients, stock, alertes de péremption et livraisons.

## Prérequis

- Java 8 ou supérieur
- Oracle Database
- Driver JDBC Oracle (ojdbc6.jar inclus dans `lib/`)

## Installation

1. Créer la base de données :
```bash
sqlplus user/password@database < db_creation_sql.sql
sqlplus user/password@database < db_population_sql.sql
```

2. Configurer la connexion dans `src/config/DataSourceProvider.java`

## Compilation et exécution

```bash
make clean    # Nettoyer les fichiers compilés
make          # Compiler et lancer l'application
```

## Structure du projet

- `src/` : Code source Java (DAO, services, interface graphique)
- `lib/` : Bibliothèques externes (driver JDBC)
- `squelette_sql/` : Scripts SQL pour les transactions
- `rapport/` : Documentation du projet et slides de la soutenance

## Fonctionnalités

- Consultation du catalogue produits
- Gestion des commandes clients
- Suivi du stock et des lots
- Alertes de péremption
- Clôture et livraison des commandes

## Auteurs

DIAKITE Alpha OUSMANE
ARDAN FATIMA-AZZAHRA
MENGOSSO ADRIEN
NAMATY YASSER
OUDRHIRI IDRISSI SAFWANE.
