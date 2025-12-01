-- ============================================
-- TRANSACTION : Création d'une commande
-- ============================================
-- Ce fichier documente la transaction de création d'une commande
-- qui se fait dans CommandeService.passerCommande()
--
-- Isolation : SERIALIZABLE
-- Justification : On vérifie le stock disponible puis on crée la commande.
--                  Sans cette isolation, le stock pourrait changer entre
--                  la vérification et la création, ce qui pourrait créer
--                  des commandes avec des produits non disponibles.

-- ============================================
-- DÉBUT DE LA TRANSACTION
-- ============================================

-- On sauvegarde le niveau d'isolation actuel
-- oldIsolation = conn.getTransactionIsolation();

-- On passe en isolation SERIALIZABLE
-- conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
-- conn.setAutoCommit(false);

-- ============================================
-- ÉTAPE 1 : Création de la commande
-- ============================================
-- On crée d'abord la commande avec le statut "En préparation"
-- Le stock n'est PAS décrémenté ici, ça se fait au passage à "Prête"

INSERT INTO Commande (idCommande, dateCommande, heureCommande, statutCommande, modePaiement, modeRecuperation, idClient)
VALUES (?, TRUNC(SYSDATE), SYSTIMESTAMP, 'En préparation', ?, ?, ?);
-- Paramètres : idCommande, modePaiement, modeRecuperation, idClient

-- ============================================
-- ÉTAPE 2 : Pour chaque produit de la commande
-- ============================================

-- 2.1) Vérification de la saisonnalité
-- On vérifie si le produit est disponible à la date actuelle
SELECT 1 
FROM ProduitEstDisponible ped 
JOIN Disponibilite d ON ped.idDisponibilite = d.idDisponibilite 
WHERE ped.idProduit = ? 
  AND ped.idProducteur = ? 
  AND ? BETWEEN d.debutDisponibilite AND d.finDisponibilite;
-- Paramètres : idProduit, idProducteur, dateJour
-- Si pas de résultat -> produit hors saison, on annule la transaction

-- 2.2) Vérification du stock disponible
-- On récupère l'idStock pour ce produit/producteur
SELECT idStock 
FROM Stock 
WHERE idProduit = ? AND idProducteur = ?;
-- Paramètres : idProduit, idProducteur

-- On récupère les lots triés par date limite croissante (FEFO)
SELECT l.idLot, l.dateLimite 
FROM Lot l 
WHERE l.idStock = ? 
ORDER BY l.dateLimite ASC;
-- Paramètres : idStock
-- On parcourt les lots pour vérifier qu'il y a assez de stock avec

SELECT quantiteDisponiblePreconditionne 
FROM LotPreconditionne 
WHERE idLot = ? 
FOR UPDATE
--Pour avoir les quantités de préconditionné, et 

SELECT quantiteDisponibleVrac 
FROM LotVrac 
WHERE idLot = ? 
FOR UPDATE
--Pour avoir les quantités de vrac, et

SELECT stockContenant 
FROM Contenant 
WHERE referenceContenant = ? 
FOR UPDATE
--Pour avoir le stock du contenant
--Le FOR UPDATE est essentiel dans le cas où 2 clients commandent le même produit en
--Même temps pour une récuperation en boutique car dans ce cas le stock est directement vidé 

-- (détails dans le code Java)

-- 2.3) Récupération du prix unitaire
SELECT prixVenteClient 
FROM Conditionnement 
WHERE idProduit = ? AND idProducteur = ?;
-- Paramètres : idProduit, idProducteur

-- 2.4) Insertion des lignes de commande
-- a) LigneCommande (ligne générique)
INSERT INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande)
VALUES (?, ?, ?, ?);
-- Paramètres : idLigneCommande, prixUnitaire, sousTotalLigne, idCommande

-- b) LigneCommandeProduit (lien produit-commande)
INSERT INTO LigneCommandeProduit (idLigneCommande, idCommande, idProduit, idProducteur)
VALUES (?, ?, ?, ?);
-- Paramètres : idLigneCommande, idCommande, idProduit, idProducteur

-- c) Selon le type de conditionnement :
--    - Si PRÉCONDITIONNÉ :
INSERT INTO LigneCommandeProduitPreconditionne (idLigneCommande, idCommande, quantiteCommandePreconditionne)
VALUES (?, ?, ?);
-- Paramètres : idLigneCommande, idCommande, quantite (en unités)

--    - Si VRAC :
INSERT INTO LigneCommandeProduitVrac (idLigneCommande, idCommande, quantiteCommandeVrac)
VALUES (?, ?, ?);
-- Paramètres : idLigneCommande, idCommande, quantite (en kg)

-- ============================================
-- ÉTAPE 3 : Si contenants commandés
-- ============================================
-- Pour chaque contenant :
-- a) LigneCommande pour le contenant
INSERT INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande)
VALUES (?, ?, ?, ?);
-- Paramètres : idLigneCommande, prixContenant, sousTotal, idCommande

-- b) LigneCommandeContenant
INSERT INTO LigneCommandeContenant (idLigneCommande, idCommande, referenceContenant, quantiteCommandeContenant)
VALUES (?, ?, ?, ?);
-- Paramètres : idLigneCommande, idCommande, quantite

-- ============================================
-- ÉTAPE 4 : Si livraison à domicile
-- ============================================
-- On crée les informations de livraison
INSERT INTO ModeRecuperationDomicile (
    idModeRecuperationDomicile, 
    paysLivraison, 
    poidsTotalCommande, 
    distanceAdresseBoutique, 
    dateEstimeeLivraison, 
    typePaysLivraison, 
    idCommande, 
    idAdresse
)
VALUES (?, ?, ?, ?, ?, ?, ?, ?);
-- Paramètres : idMode, paysLivraison, poidsTotal, distance, dateEstimee, typePays, idCommande, idAdresse

-- ============================================
-- FIN DE LA TRANSACTION
-- ============================================

-- Si tout s'est bien passé :
-- conn.commit();

-- Si erreur (stock insuffisant, produit hors saison, etc.) :
-- conn.rollback();

-- On restaure le niveau d'isolation d'origine
-- conn.setTransactionIsolation(oldIsolation);

-- ============================================
-- NOTES IMPORTANTES
-- ============================================
-- - Le stock n'est PAS décrémenté lors de la création de la commande
-- - Le décrément se fait au passage au statut "Prête" (voir transaction_cloture.sql)
-- - L'isolation SERIALIZABLE garantit qu'entre la vérification du stock
--   et la création de la commande, aucun autre processus ne peut modifier
--   le stock, évitant ainsi les problèmes de concurrence

