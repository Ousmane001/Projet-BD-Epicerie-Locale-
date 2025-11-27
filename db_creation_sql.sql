-- ============================================
-- CRÉATION DE LA BASE DE DONNÉES - SYSTÈME DE VENTE EN CIRCUIT COURT
-- ============================================

-- DROP DES TABLES 

BEGIN EXECUTE IMMEDIATE 'DROP TABLE PerteProduitVrac CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE PerteProduitPreconditionne CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE PerteProduit CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE PerteContenant CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Perte CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE LigneCommandeProduitVrac CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE LigneCommandeProduitPreconditionne CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE LigneCommandeProduit CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE LigneCommandeContenant CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE LigneCommande CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE ModeRecuperationDomicile CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Commande CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE PeutEtreUtiliseAvec CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE ConditionnementVrac CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE ConditionnementPreconditionne CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Conditionnement CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE ProduitEstDisponible CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Disponibilite CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE LotPreconditionne CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE LotVrac CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Lot CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Stock CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Produit CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Exerce CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE TypeActivite CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Producteur CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE PossedeAdresse CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Client CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Contenant CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Adresse CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Contact CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;
/




-- Table Contact

CREATE TABLE Contact (
    idContact VARCHAR(10) PRIMARY KEY,  -- Format: CT0000000
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    numTel VARCHAR(20),
    email VARCHAR(150)
);

-- Table Client

CREATE TABLE Client (
    idClient VARCHAR(10) PRIMARY KEY,  -- Format: CL0000000
    idContact VARCHAR(10) NOT NULL UNIQUE,
    FOREIGN KEY (idContact) REFERENCES Contact(idContact) ON DELETE CASCADE
);

-- Table Adresse

CREATE TABLE Adresse (
    idAdresse VARCHAR(10) PRIMARY KEY,  -- Format: AD0000000
    rue VARCHAR(255) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    codePostal VARCHAR(10) NOT NULL
);

-- Table PossedeAdresse (Association Client-Adresse)

CREATE TABLE PossedeAdresse (
    idClient VARCHAR(10),
    idAdresse VARCHAR(10),
    PRIMARY KEY (idClient, idAdresse),
    FOREIGN KEY (idClient) REFERENCES Client(idClient) ON DELETE CASCADE,
    FOREIGN KEY (idAdresse) REFERENCES Adresse(idAdresse) ON DELETE CASCADE
);

-- Table Producteur

CREATE TABLE Producteur (
    idProducteur VARCHAR(10) PRIMARY KEY,  -- Format: PR0000000
    lattitude FLOAT,
    longitude FLOAT,
    idAdresse VARCHAR(10) NOT NULL,
    idContact VARCHAR(10) NOT NULL UNIQUE,
    FOREIGN KEY (idAdresse) REFERENCES Adresse(idAdresse),
    FOREIGN KEY (idContact) REFERENCES Contact(idContact) ON DELETE CASCADE
);

-- Table TypeActivite 

CREATE TABLE TypeActivite (
    typeActivite VARCHAR(50) PRIMARY KEY  -- Ex: "Agriculteur", "Eleveur"
);

-- Table Exerce (Association Producteur-TypeActivite)

CREATE TABLE Exerce (
    idProducteur VARCHAR(10),
    typeActivite VARCHAR(50),
    PRIMARY KEY (idProducteur, typeActivite),
    FOREIGN KEY (idProducteur) REFERENCES Producteur(idProducteur) ON DELETE CASCADE,
    FOREIGN KEY (typeActivite) REFERENCES TypeActivite(typeActivite) ON DELETE CASCADE
);

-- Table Produit

CREATE TABLE Produit (
    idProduit VARCHAR(10),  -- Format: PD0000000
    idProducteur VARCHAR(10),
    categorie VARCHAR(50) NOT NULL,  -- Ex: "Fromage", "Boissons", "Céréales"
    description CLOB,
    bio VARCHAR(50),
    label VARCHAR(100),
    nomProduit VARCHAR(150) NOT NULL,
    allergene CLOB,
    origineGeographique VARCHAR(150),
    delaiDisponibilite INTEGER, 
    PRIMARY KEY (idProduit, idProducteur),
    FOREIGN KEY (idProducteur) REFERENCES Producteur(idProducteur) ON DELETE CASCADE
);

-- Table Stock

CREATE TABLE Stock (
    idStock VARCHAR(10) PRIMARY KEY,  -- Format: ST0000000
    idProduit VARCHAR(10) NOT NULL,
    idProducteur VARCHAR(10) NOT NULL,
    FOREIGN KEY (idProduit, idProducteur) REFERENCES Produit(idProduit, idProducteur) ON DELETE CASCADE
);

-- Table Lot

CREATE TABLE Lot (
    idLot VARCHAR(10) PRIMARY KEY,  -- Format: LT0000000
    dateReception DATE NOT NULL,
    dateLimite DATE NOT NULL,
    typeDateLimite VARCHAR(10) NOT NULL CHECK (typeDateLimite IN ('DLC', 'DLUO')),
    idStock VARCHAR(10) NOT NULL,
    FOREIGN KEY (idStock) REFERENCES Stock(idStock) ON DELETE CASCADE
);

-- Table LotVrac

CREATE TABLE LotVrac (
    idLot VARCHAR(10) PRIMARY KEY,
    quantiteDisponibleVrac FLOAT NOT NULL CHECK (quantiteDisponibleVrac > 0),
    FOREIGN KEY (idLot) REFERENCES Lot(idLot) ON DELETE CASCADE
);

-- Table LotPreconditionne

CREATE TABLE LotPreconditionne (
    idLot VARCHAR(10) PRIMARY KEY,
    quantiteDisponiblePreconditionne INTEGER NOT NULL CHECK (quantiteDisponiblePreconditionne > 0),
    FOREIGN KEY (idLot) REFERENCES Lot(idLot) ON DELETE CASCADE
);

-- Table Disponibilite

CREATE TABLE Disponibilite (
    idDisponibilite VARCHAR(10) PRIMARY KEY,  -- Format: DS0000000
    debutDisponibilite DATE NOT NULL,
    finDisponibilite DATE NOT NULL,
    statutProduit VARCHAR(20) NOT NULL CHECK (statutProduit IN ('Disponible', 'Pas disponible')),
    CHECK (finDisponibilite >= debutDisponibilite)
);

-- Table ProduitEstDisponible (Association)

CREATE TABLE ProduitEstDisponible (
    idProduit VARCHAR(10),
    idProducteur VARCHAR(10),
    idDisponibilite VARCHAR(10),
    PRIMARY KEY (idProduit, idProducteur, idDisponibilite),
    FOREIGN KEY (idProduit, idProducteur) REFERENCES Produit(idProduit, idProducteur) ON DELETE CASCADE,
    FOREIGN KEY (idDisponibilite) REFERENCES Disponibilite(idDisponibilite) ON DELETE CASCADE
);

-- Table Conditionnement 

CREATE TABLE Conditionnement (
    idConditionnement VARCHAR(10) PRIMARY KEY,  -- Format: CD0000000
    prixAchatProducteur FLOAT NOT NULL CHECK (prixAchatProducteur >= 0),
    prixVenteClient FLOAT NOT NULL CHECK (prixVenteClient >= 0),
    idProduit VARCHAR(10) NOT NULL,
    idProducteur VARCHAR(10) NOT NULL,
    FOREIGN KEY (idProduit, idProducteur) REFERENCES Produit(idProduit, idProducteur) ON DELETE CASCADE
);

-- Table ConditionnementPreconditionne

CREATE TABLE ConditionnementPreconditionne (
    idConditionnement VARCHAR(10) PRIMARY KEY,
    poidsSachet FLOAT NOT NULL CHECK (poidsSachet > 0),
    FOREIGN KEY (idConditionnement) REFERENCES Conditionnement(idConditionnement) ON DELETE CASCADE
);

-- Table ConditionnementVrac

CREATE TABLE ConditionnementVrac (
    idConditionnement VARCHAR(10) PRIMARY KEY,
    FOREIGN KEY (idConditionnement) REFERENCES Conditionnement(idConditionnement) ON DELETE CASCADE
);

-- Table Contenant

CREATE TABLE Contenant (
    referenceContenant VARCHAR(10) PRIMARY KEY,  -- Format: CN0000000
    typeContenant VARCHAR(100) NOT NULL,  -- Ex: "Bocaux en verre réutilisable", "sachet kraft"
    capaciteContenant FLOAT NOT NULL CHECK (capaciteContenant > 0),
    stockContenant INTEGER NOT NULL CHECK (stockContenant >= 0),
    caractereContenant VARCHAR(30) NOT NULL CHECK (caractereContenant IN ('Réutilisable', 'Non-Réutilisable')),
    prixContenant FLOAT NOT NULL CHECK (prixContenant >= 0)
);

-- Table PeutEtreUtiliseAvec (Association ConditionnementVrac-Contenant) 

CREATE TABLE PeutEtreUtiliseAvec (
    idConditionnement VARCHAR(10),
    referenceContenant VARCHAR(10),
    PRIMARY KEY (idConditionnement, referenceContenant),
    FOREIGN KEY (idConditionnement) REFERENCES ConditionnementVrac(idConditionnement) ON DELETE CASCADE,
    FOREIGN KEY (referenceContenant) REFERENCES Contenant(referenceContenant) ON DELETE CASCADE
);

-- Table Commande 

CREATE TABLE Commande (
    idCommande VARCHAR(10) PRIMARY KEY,  -- Format: CM0000000
    dateCommande DATE NOT NULL,
    dateRecuperation DATE, 
    datePaiement DATE, 
    heureCommande TIMESTAMP NOT NULL,
    statutCommande VARCHAR(30) NOT NULL CHECK (statutCommande IN ('En préparation', 'Prête', 'En livraison', 'Annulée', 'Récupérée/Livrée')),
    modePaiement VARCHAR(20) NOT NULL CHECK (modePaiement IN ('En ligne', 'En Boutique')),
    modeRecuperation VARCHAR(20) NOT NULL CHECK (modeRecuperation IN ('Boutique', 'Domicile')),
    idClient VARCHAR(10) NOT NULL,
    FOREIGN KEY (idClient) REFERENCES Client(idClient) ON DELETE CASCADE
);

-- Table ModeRecuperationDomicile

CREATE TABLE ModeRecuperationDomicile (
    idModeRecuperationDomicile VARCHAR(10) PRIMARY KEY,  -- Format: MR0000000
    paysLivraison VARCHAR(100) NOT NULL,
    poidsTotalCommande FLOAT NOT NULL CHECK (poidsTotalCommande > 0),
    distanceAdresseBoutique FLOAT NOT NULL CHECK (distanceAdresseBoutique >= 0),
    dateEstimeeLivraison DATE,
    typePaysLivraison VARCHAR(30) NOT NULL CHECK (typePaysLivraison IN ('France Métropolitaine', 'DOM-TOM', 'International')),
    idCommande VARCHAR(10) NOT NULL UNIQUE,
    idAdresse VARCHAR(10) NOT NULL,
    FOREIGN KEY (idCommande) REFERENCES Commande(idCommande) ON DELETE CASCADE,
    FOREIGN KEY (idAdresse) REFERENCES Adresse(idAdresse)
);

-- Table LigneCommande

CREATE TABLE LigneCommande (
    idLigneCommande VARCHAR(10),  -- Format: LC0000000
    prixUnitaire FLOAT NOT NULL CHECK (prixUnitaire >= 0),
    sousTotalLigne FLOAT NOT NULL CHECK (sousTotalLigne >= 0),
    idCommande VARCHAR(10) NOT NULL,
    PRIMARY KEY (idLigneCommande, idCommande), 
    FOREIGN KEY (idCommande) REFERENCES Commande(idCommande) ON DELETE CASCADE
);

-- Table LigneCommandeContenant

CREATE TABLE LigneCommandeContenant (
    idLigneCommande VARCHAR(10), 
    idCommande VARCHAR(10), 
    quantiteCommandeContenant INTEGER NOT NULL CHECK (quantiteCommandeContenant > 0),
    PRIMARY KEY (idLigneCommande, Commande),
    FOREIGN KEY (idLigneCommande) REFERENCES LigneCommande(idLigneCommande) ON DELETE CASCADE
);

-- Table LigneCommandeProduit

CREATE TABLE LigneCommandeProduit (
    idLigneCommande VARCHAR(10),
    idCommande VARCHAR(10),
    idProduit VARCHAR(10) NOT NULL,
    idProducteur VARCHAR(10) NOT NULL,
    PRIMARY KEY (idLigneCommande, Commande),
    FOREIGN KEY (idLigneCommande) REFERENCES LigneCommande(idLigneCommande) ON DELETE CASCADE,
    FOREIGN KEY (idProduit, idProducteur) REFERENCES Produit(idProduit, idProducteur)
);

-- Table LigneCommandeProduitVrac

CREATE TABLE LigneCommandeProduitVrac (
    idLigneCommande VARCHAR(10),
    idCommande VARCHAR(10), 
    quantiteCommandeVrac FLOAT NOT NULL CHECK (quantiteCommandeVrac > 0),
    PRIMARY KEY (idLigneCommande, Commande),
    FOREIGN KEY (idLigneCommande) REFERENCES LigneCommandeProduit(idLigneCommande) ON DELETE CASCADE
);

-- Table LigneCommandeProduitPreconditionne

CREATE TABLE LigneCommandeProduitPreconditionne (
    idLigneCommande VARCHAR(10),
    idCommande VARCHAR(10),
    quantiteCommandePreconditionne INTEGER NOT NULL CHECK (quantiteCommandePreconditionne > 0),
    PRIMARY KEY (idLigneCommande, idCommande), 
    FOREIGN KEY (idLigneCommande) REFERENCES LigneCommandeProduit(idLigneCommande) ON DELETE CASCADE
);

-- Table Perte

CREATE TABLE Perte (
    idPerte VARCHAR(10) PRIMARY KEY,  -- Format: PT0000000
    datePerte DATE NOT NULL,
    naturePerte VARCHAR(20) NOT NULL CHECK (naturePerte IN ('Vol', 'Casse'))
);

-- Table PerteProduit

CREATE TABLE PerteProduit (
    idPerte VARCHAR(10) PRIMARY KEY,
    idProduit VARCHAR(10) NOT NULL,
    idProducteur VARCHAR(10) NOT NULL,
    FOREIGN KEY (idPerte) REFERENCES Perte(idPerte) ON DELETE CASCADE,
    FOREIGN KEY (idProduit, idProducteur) REFERENCES Produit(idProduit, idProducteur)
);

-- Table PerteProduitPreconditionne

CREATE TABLE PerteProduitPreconditionne (
    idPerte VARCHAR(10) PRIMARY KEY,
    quantitePerduePreconditionne INTEGER NOT NULL CHECK (quantitePerduePreconditionne > 0),
    FOREIGN KEY (idPerte) REFERENCES PerteProduit(idPerte) ON DELETE CASCADE
);

-- Table PerteProduitVrac

CREATE TABLE PerteProduitVrac (
    idPerte VARCHAR(10) PRIMARY KEY,
    quantitePerdueVrac FLOAT NOT NULL CHECK (quantitePerdueVrac > 0),
    FOREIGN KEY (idPerte) REFERENCES PerteProduit(idPerte) ON DELETE CASCADE
);

-- Table PerteContenant

CREATE TABLE PerteContenant (
    idPerte VARCHAR(10) PRIMARY KEY,
    quantitePerdueContenant INTEGER NOT NULL CHECK (quantitePerdueContenant > 0),
    referenceContenant VARCHAR(10) NOT NULL,
    FOREIGN KEY (idPerte) REFERENCES Perte(idPerte) ON DELETE CASCADE,
    FOREIGN KEY (referenceContenant) REFERENCES Contenant(referenceContenant)
);

-- ============================================
-- INDEX POUR OPTIMISER LES PERFORMANCES
-- ============================================

CREATE INDEX idx_commande_client ON Commande(idClient);
CREATE INDEX idx_commande_date ON Commande(dateCommande);
CREATE INDEX idx_commande_statut ON Commande(statutCommande);
CREATE INDEX idx_lignecommande_commande ON LigneCommande(idCommande, idLigneCommande);
CREATE INDEX idx_produit_producteur ON Produit(idProducteur);
CREATE INDEX idx_produit_categorie ON Produit(categorie);
CREATE INDEX idx_stock_produit ON Stock(idProduit, idProducteur);
CREATE INDEX idx_lot_stock ON Lot(idStock);
CREATE INDEX idx_lot_datelimite ON Lot(dateLimite);

-- ============================================
-- NOTES SUR LES IDENTIFIANTS
-- ============================================
-- Format des ID (Préfixe + 7 chiffres aléatoires) :
-- CL0000000 : Client
-- CT0000000 : Contact
-- AD0000000 : Adresse
-- PR0000000 : Producteur
-- PD0000000 : Produit
-- ST0000000 : Stock
-- LT0000000 : Lot
-- DS0000000 : Disponibilite
-- CD0000000 : Conditionnement
-- CN0000000 : Contenant
-- CM0000000 : Commande
-- MR0000000 : ModeRecuperationDomicile
-- LC0000000 : LigneCommande
-- PT0000000 : Perte

-- Les identifiants doivent être générés de manière unique
-- dans notre application avant l'insertion
