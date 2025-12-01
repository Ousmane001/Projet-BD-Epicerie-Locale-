

-- TRANSACTION : Préparation et clôture d'une commande

-- Ce fichier documente les transactions de préparation et clôture
-- qui se font dans ClotureCommande.preparerCommande() et 
-- ClotureCommande.cloturerCommande()
--
-- Isolation : SERIALIZABLE
-- Justification : La sortie de stock fait plusieurs lectures puis modifications.
--                  Il faut garantir la cohérence pour éviter les problèmes
--                  de concurrence (deux commandes qui prennent le même stock).


-- TRANSACTION 1 : Préparer une commande (passage à "Prête")

-- Cette transaction concerne uniquement les commandes DOMICILE
-- Elle sort le stock 

-- DÉBUT TRANSACTION
-- oldIsolation = conn.getTransactionIsolation();
-- conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
-- conn.setAutoCommit(false);

-- ÉTAPE 1 : Vérifications préalables
-- On récupère les infos de la commande
SELECT c.idCommande, c.statutCommande, c.modeRecuperation, c.modePaiement, c.datePaiement
FROM Commande c
WHERE c.idCommande = ?;
-- Paramètres : idCommande
-- Vérifications :
--   - La commande doit exister
--   - modeRecuperation = 'Domicile'
--   - statutCommande = 'En préparation'
--   - modePaiement = 'En ligne'

-- ÉTAPE 2 : Paiement si pas déjà fait
-- (simulation de paiement en ligne, pas de vraie requête SQL)
-- Si datePaiement IS NULL :
--   - Simulation du paiement (dans le code Java)
--   - Enregistrement de la date de paiement :
UPDATE Commande
SET datePaiement = TRUNC(SYSDATE)
WHERE idCommande = ?;
-- Paramètres : idCommande

-- ÉTAPE 3 : Sortie de stock (stratégie FEFO) fist expired first out
-- Pour chaque ligne de commande produit :

-- 3.1) Récupérer les lignes produit de la commande
SELECT lcp.idLigneCommande, lcp.idProduit, lcp.idProducteur
FROM LigneCommandeProduit lcp
WHERE lcp.idCommande = ?;
-- Paramètres : idCommande

-- 3.2) Pour chaque ligne, déterminer le type (vrac ou préconditionné)
-- a) Vérifier si c'est préconditionné
SELECT quantiteCommandePreconditionne
FROM LigneCommandeProduitPreconditionne
WHERE idLigneCommande = ? AND idCommande = ?;
-- Paramètres : idLigneCommande, idCommande

-- b) Sinon, vérifier si c'est vrac
SELECT quantiteCommandeVrac
FROM LigneCommandeProduitVrac
WHERE idLigneCommande = ? AND idCommande = ?;
-- Paramètres : idLigneCommande, idCommande

-- 3.3) Récupérer l'idStock
SELECT idStock
FROM Stock
WHERE idProduit = ? AND idProducteur = ?;
-- Paramètres : idProduit, idProducteur

-- 3.4) Récupérer les lots triés par date limite croissante (FEFO)
SELECT l.idLot, l.dateLimite
FROM Lot l
WHERE l.idStock = ?
ORDER BY l.dateLimite ASC
FOR UPDATE;
-- Paramètres : idStock
-- FOR UPDATE : verrouille les lignes pour éviter les modifications concurrentes

-- 3.5) Pour chaque lot (dans l'ordre FEFO) :
--     - Vérifier que le lot n'est pas périmé
--     - Récupérer la quantité disponible avec verrouillage
--     Pour PRÉCONDITIONNÉ :
SELECT quantiteDisponiblePreconditionne
FROM LotPreconditionne
WHERE idLot = ?
FOR UPDATE;
-- Paramètres : idLot

--     Pour VRAC :
SELECT quantiteDisponibleVrac
FROM LotVrac
WHERE idLot = ?
FOR UPDATE;
-- Paramètres : idLot

-- 3.6) Décrémenter le stock
--     Pour PRÉCONDITIONNÉ :
UPDATE LotPreconditionne
SET quantiteDisponiblePreconditionne = quantiteDisponiblePreconditionne - ?
WHERE idLot = ?;
-- Paramètres : quantite, idLot

--     Pour VRAC :
UPDATE LotVrac
SET quantiteDisponibleVrac = quantiteDisponibleVrac - ?
WHERE idLot = ?;
-- Paramètres : quantite, idLot

-- On continue jusqu'à avoir décrémenté toute la quantité demandée
-- Si on n'a pas assez de stock -> erreur, rollback

-- ÉTAPE 4 : Changer le statut à "Prête"
UPDATE Commande
SET statutCommande = 'Prête'
WHERE idCommande = ?;
-- Paramètres : idCommande

-- FIN TRANSACTION
-- conn.commit();  (si tout OK)
-- conn.rollback(); (si erreur)
-- conn.setTransactionIsolation(oldIsolation);


-- TRANSACTION 2 : Clôturer une commande

-- Cette transaction enregistre la récupération/livraison

-- DÉBUT TRANSACTION
-- oldIsolation = conn.getTransactionIsolation();
-- conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
-- conn.setAutoCommit(false);

-- ÉTAPE 1 : Vérifications
SELECT c.idCommande, c.statutCommande, c.modeRecuperation, c.modePaiement, c.datePaiement, c.dateRecuperation
FROM Commande c
WHERE c.idCommande = ?;
-- Paramètres : idCommande
-- Vérifications :
--   - La commande ne doit pas être déjà clôturée (dateRecuperation IS NULL)
--   - statutCommande = 'Prête'
--   - Si Domicile : modePaiement = 'En ligne'

-- ÉTAPE 2 : Paiement boutique si nécessaire
-- Si modeRecuperation = 'Boutique' ET modePaiement = 'En Boutique' ET datePaiement IS NULL :
--   - Simulation du paiement (dans le code Java)
--   - Enregistrement de la date de paiement :
UPDATE Commande
SET datePaiement = TRUNC(SYSDATE)
WHERE idCommande = ?;
-- Paramètres : idCommande

-- ÉTAPE 3 : Sortie de stock pour BOUTIQUE (si pas déjà fait)
-- Si modeRecuperation = 'Boutique' :
--   Même processus que dans la transaction 1, étape 3
--   (sortie de stock FEFO)

-- ÉTAPE 4 : Enregistrer la date de récupération
UPDATE Commande
SET dateRecuperation = TRUNC(SYSDATE)
WHERE idCommande = ?;
-- Paramètres : idCommande

-- ÉTAPE 5 : Changer le statut à "Récupérée/Livrée"
UPDATE Commande
SET statutCommande = 'Récupérée/Livrée'
WHERE idCommande = ?;
-- Paramètres : idCommande

-- FIN TRANSACTION
-- conn.commit();  (si tout OK)
-- conn.rollback(); (si erreur)
-- conn.setTransactionIsolation(oldIsolation);


-- NOTES IMPORTANTES
-- - La stratégie FEFO (First-Expired, First-Out) garantit qu'on utilise
--   d'abord les lots les plus proches de la péremption
-- - L'isolation SERIALIZABLE évite que deux transactions modifient
--   le même stock en même temps

