-- ============================================
-- TRANSACTION : Application de réduction sur produits proches de péremption
-- ============================================
-- Ce fichier documente la transaction d'application de réduction
-- qui se fait dans AlertePeremptionService.appliquerReduction()
--
-- Isolation : SERIALIZABLE
-- Justification : On lit le prix actuel puis on le modifie. Il faut garantir
--                  que le prix lu reste cohérent jusqu'à la mise à jour
--                  (lecture puis modification).

-- ============================================
-- ÉTAPE 1 : Génération des alertes (lecture seule)
-- ============================================
-- Cette étape ne modifie rien, elle liste juste les lots proches de péremption
-- Pas de transaction nécessaire ici, juste des SELECT

-- Récupérer les lots qui expirent dans les 7 prochains jours
SELECT 
    l.idLot,
    l.idStock,
    l.dateLimite,
    (l.dateLimite - TRUNC(SYSDATE)) AS joursRestants,
    s.idProduit,
    s.idProducteur
FROM Lot l
JOIN Stock s ON l.idStock = s.idStock
WHERE l.dateLimite <= TRUNC(SYSDATE) + 7
  AND l.dateLimite > TRUNC(SYSDATE)
ORDER BY l.dateLimite ASC;
-- On récupère les lots avec leur produit/producteur associé

-- Pour chaque lot, déterminer le type (vrac ou préconditionné) et la quantité
-- Pour PRÉCONDITIONNÉ :
SELECT quantiteDisponiblePreconditionne
FROM LotPreconditionne
WHERE idLot = ?;
-- Paramètres : idLot

-- Pour VRAC :
SELECT quantiteDisponibleVrac
FROM LotVrac
WHERE idLot = ?;
-- Paramètres : idLot

-- Récupérer le prix actuel du produit
SELECT prixVenteClient
FROM Conditionnement
WHERE idProduit = ? AND idProducteur = ?;
-- Paramètres : idProduit, idProducteur

-- ============================================
-- ÉTAPE 2 : Application de la réduction (TRANSACTION)
-- ============================================
-- Cette transaction applique une réduction de 30% (prix * 0.7)
-- sur le prix de vente d'un produit

-- DÉBUT TRANSACTION
-- oldIsolation = conn.getTransactionIsolation();
-- conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
-- conn.setAutoCommit(false);

-- ÉTAPE 2.1 : Lecture du prix actuel avec verrouillage
-- On utilise FOR UPDATE pour verrouiller la ligne et éviter
-- qu'une autre transaction modifie le prix en même temps
SELECT prixVenteClient
FROM Conditionnement
WHERE idProduit = ? AND idProducteur = ?
FOR UPDATE;
-- Paramètres : idProduit, idProducteur
-- FOR UPDATE : verrouille la ligne jusqu'à la fin de la transaction
-- Si pas de résultat -> erreur, le conditionnement n'existe pas

-- ÉTAPE 2.2 : Mise à jour du prix avec réduction de 30%
-- On applique la réduction : nouveau_prix = ancien_prix * 0.7
-- On arrondit à 2 décimales
UPDATE Conditionnement
SET prixVenteClient = ROUND(? * 0.7, 2)
WHERE idProduit = ? AND idProducteur = ?;
-- Paramètres : prixActuel (lu à l'étape précédente), idProduit, idProducteur
-- Si aucune ligne modifiée -> erreur, rollback

-- FIN TRANSACTION
-- conn.commit();  (si tout OK)
-- conn.rollback(); (si erreur)
-- conn.setTransactionIsolation(oldIsolation);

-- ============================================
-- NOTES IMPORTANTES
-- ============================================
-- - La réduction est de 30% (nouveau prix = ancien prix * 0.7)
-- - L'isolation SERIALIZABLE garantit qu'entre la lecture et la mise à jour
--   du prix, aucune autre transaction ne peut modifier le prix
-- - Le FOR UPDATE dans le SELECT verrouille la ligne pour éviter les
--   problèmes de concurrence (deux réductions simultanées sur le même produit)
-- - Cette transaction est appelée depuis l'interface graphique quand
--   l'utilisateur clique sur "Appliquer réduction" pour un produit en alerte

