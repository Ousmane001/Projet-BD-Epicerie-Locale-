-- ================================================================
-- DOCUMENTATION TECHNIQUE — Fonctionnalité 2 : Alertes Péremption & Pertes
-- ================================================================
-- Ce fichier décrit toutes les opérations SQL liées :
--   1) à la détection des lots proches de la péremption ;
--   2) à la détection des pertes (casse / vol) ;
--   3) à la fusion de ces informations pour affichage dans l'IHM ;
--   4) à la transaction d’application de réduction (30%).
--
-- Le code métier correspondant se trouve dans :
--   - LotDAO.findLotsPerissables()
--   - PerteDAO.getPertesAvecProduit()
--   - AlertePeremptionService.getAlertes()
--   - AlertePeremptionService.appliquerReduction()
--
-- ================================================================
-- PARTIE A — DÉTECTION DES ALERTES DE PÉREMPTION (lecture seule)
-- ================================================================
-- Cette requête identifie les lots dont la date limite est dans les 7 jours.
-- Aucun verrou n'est nécessaire ici, la lecture est non destructive.

SELECT
    l.idLot,
    s.idProduit,
    s.idProducteur,
    p.nomProduit,
    l.dateLimite,
    (l.dateLimite - TRUNC(SYSDATE)) AS joursRestants
FROM Lot l
JOIN Stock s ON l.idStock = s.idStock
JOIN Produit p ON p.idProduit = s.idProduit AND p.idProducteur = s.idProducteur
WHERE l.dateLimite <= TRUNC(SYSDATE) + 7
  AND l.dateLimite > TRUNC(SYSDATE)
ORDER BY l.dateLimite ASC;

-- Détermination du type de lot (héritage)
SELECT 1 FROM LotPreconditionne WHERE idLot = ?;
SELECT 1 FROM LotVrac            WHERE idLot = ?;

-- Récupération date limite pour affichage
SELECT dateLimite FROM Lot WHERE idLot = ?;


-- ================================================================
-- PARTIE B — DÉTECTION DES PERTES (lecture seule)
-- ================================================================
-- Les pertes (casse / vol) sont enregistrées dans deux tables :
--   Perte(idPerte, datePerte)
--   PerteProduit(idPerte, idProduit, idProducteur)
-- Aucune transaction : simple extraction pour affichage.

SELECT 
    p.idPerte,
    p.datePerte,
    pp.idProduit,
    pp.idProducteur,
    prod.nomProduit
FROM Perte p
JOIN PerteProduit pp ON p.idPerte = pp.idPerte
JOIN Produit prod 
    ON prod.idProduit = pp.idProduit 
   AND prod.idProducteur = pp.idProducteur;

-- Ces enregistrements sont typés "PERTE" dans l'IHM.


-- ================================================================
-- PARTIE C — FUSION DES ALERTES (logique applicative)
-- ================================================================
-- Dans AlertePeremptionService.getAlertes() :
--   - On récupère les lots périssables (type = "PEREMPTION")
--   - On récupère les pertes enregistrées (type = "PERTE")
--   - On renvoie une liste unique d’alertes pour affichage dans Swing.
--
-- Aucun SQL spécifique : c’est une fusion Java.


-- ================================================================
-- PARTIE D — APPLICATION DE LA RÉDUCTION (transaction Oracle)
-- ================================================================
-- Cette partie concerne UNIQUEMENT les alertes de type PEREMPTION.
-- Les pertes ne peuvent pas recevoir de réduction.

-- Mode d’isolation utilisé :
--     SERIALIZABLE
-- Justification :
--     On lit un prix puis on le met à jour → éviter modification concurrente.

-- ================================================================
-- Étape D.1 — Lecture du prix actuel AVEC VERROU
-- ================================================================
SELECT prixVenteClient
FROM Conditionnement
WHERE idProduit = ? AND idProducteur = ?
FOR UPDATE;
-- FOR UPDATE verrouille la ligne jusqu'au COMMIT
-- Empêche :
--   - une double réduction simultanée,
--   - une commande de lire un prix incohérent.

-- ================================================================
-- Étape D.2 — Mise à jour du prix (-30 %)
-- ================================================================
UPDATE Conditionnement
SET prixVenteClient = ROUND(? * 0.7, 2)
WHERE idProduit = ? AND idProducteur = ?;
-- Si aucune ligne modifiée → rollback

-- ================================================================
-- Étape D.3 — Validation
-- ================================================================
-- conn.commit();
-- (ou rollback si erreur)

-- ================================================================
-- NOTES FINALES
-- ================================================================
-- 1. Les pertes NE déclenchent PAS de réduction : affichage uniquement.
-- 2. Seuls les produits en péremption peuvent recevoir une réduction.
-- 3. Le statut affiché dans l'IHM dépend du type et de la réduction appliquée.
-- 4. La transaction est sécurisée contre les accès concurrents grâce à :
--      - SELECT ... FOR UPDATE
--      - isolation SERIALIZABLE
-- 5. L’IHM reconnaît trois statuts :
--      - "En attente"
--      - "Réduction appliquée"
--      - "PERTE détectée"
