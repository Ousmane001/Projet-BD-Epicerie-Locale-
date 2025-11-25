-- ============================================
-- SCRIPT DE PEUPLEMENT DE LA BASE DE DONNÉES
-- ============================================

-- ============================================
-- 1. INSERTION DES TYPES D'ACTIVITÉS
-- ============================================
INSERT INTO TypeActivite (typeActivite) VALUES
('Agriculteur'),
('Éleveur'),
('Apiculteur'),
('Maraîcher'),
('Viticulteur');

-- ============================================
-- 2. INSERTION DES CONTACTS
-- ============================================
INSERT INTO Contact (idContact, nom, prenom, numTel, email) VALUES
-- Contacts Clients
('CT1234567', 'Dupont', 'Marie', '0612345678', 'marie.dupont@email.com'),
('CT2345678', 'Martin', 'Jean', '0623456789', 'jean.martin@email.com'),
('CT3456789', 'Bernard', 'Sophie', '0634567890', 'sophie.bernard@email.com'),
('CT4567890', 'Petit', 'Luc', '0645678901', 'luc.petit@email.com'),
('CT5678901', 'Durand', 'Claire', '0656789012', 'claire.durand@email.com'),
-- Contacts Producteurs
('CT6789012', 'Ferme Bio', 'Pierre', '0467890123', 'contact@fermebio.fr'),
('CT7890123', 'Fromages du Terroir', 'Jacques', '0478901234', 'jacques@fromagesterroir.fr'),
('CT8901234', 'Ruche Dorée', 'Sylvie', '0489012345', 'sylvie@ruchedoree.fr'),
('CT9012345', 'Maraîchage Local', 'Antoine', '0490123456', 'antoine@maraichagelocal.fr'),
('CT0123456', 'Vignoble des Coteaux', 'Michel', '0401234567', 'michel@vignoble-coteaux.fr');

-- ============================================
-- 3. INSERTION DES ADRESSES
-- ============================================
INSERT INTO Adresse (idAdresse, rue, ville, codePostal) VALUES
-- Adresses Clients
('AD1111111', '12 rue de la République', 'Lyon', '69001'),
('AD2222222', '34 avenue des Champs', 'Grenoble', '38000'),
('AD3333333', '56 boulevard Victor Hugo', 'Chambéry', '73000'),
('AD4444444', '78 rue du Commerce', 'Annecy', '74000'),
('AD5555555', '90 place de la Mairie', 'Valence', '26000'),
-- Adresses Producteurs
('AD6666666', '15 chemin des Vignes', 'Saint-Martin-d\'Hères', '38400'),
('AD7777777', '23 route de la Ferme', 'Échirolles', '38130'),
('AD8888888', '45 lieu-dit Les Ruches', 'Meylan', '38240'),
('AD9999999', '67 chemin des Cultures', 'Fontaine', '38600'),
('AD0000000', '89 route des Coteaux', 'Voreppe', '38340');

-- ============================================
-- 4. INSERTION DES CLIENTS
-- ============================================
INSERT INTO Client (idClient, idContact) VALUES
('CL1234567', 'CT1234567'),
('CL2345678', 'CT2345678'),
('CL3456789', 'CT3456789'),
('CL4567890', 'CT4567890'),
('CL5678901', 'CT5678901');

-- ============================================
-- 5. ASSOCIATION CLIENTS-ADRESSES
-- ============================================
INSERT INTO PossedeAdresse (idClient, idAdresse) VALUES
('CL1234567', 'AD1111111'),
('CL2345678', 'AD2222222'),
('CL3456789', 'AD3333333'),
('CL4567890', 'AD4444444'),
('CL5678901', 'AD5555555');

-- ============================================
-- 6. INSERTION DES PRODUCTEURS
-- ============================================
INSERT INTO Producteur (idProducteur, lattitude, longitude, idAdresse, idContact) VALUES
('PR1234567', 45.1885, 5.7245, 'AD6666666', 'CT6789012'),
('PR2345678', 45.1475, 5.7180, 'AD7777777', 'CT7890123'),
('PR3456789', 45.2095, 5.7775, 'AD8888888', 'CT8901234'),
('PR4567890', 45.1935, 5.6880, 'AD9999999', 'CT9012345'),
('PR5678901', 45.2915, 5.6365, 'AD0000000', 'CT0123456');

-- ============================================
-- 7. ASSOCIATION PRODUCTEURS-ACTIVITÉS
-- ============================================
INSERT INTO Exerce (idProducteur, typeActivite) VALUES
('PR1234567', 'Agriculteur'),
('PR1234567', 'Maraîcher'),
('PR2345678', 'Éleveur'),
('PR3456789', 'Apiculteur'),
('PR4567890', 'Maraîcher'),
('PR5678901', 'Viticulteur');

-- ============================================
-- 8. INSERTION DES PRODUITS
-- ============================================
INSERT INTO Produit (idProduit, idProducteur, categorie, description, bio, label, nomProduit, allergene, origineGeographique) VALUES
-- Produits Ferme Bio
('PD1111111', 'PR1234567', 'Céréales', 'Farine de blé complet issue de notre ferme', 'Oui', 'AB', 'Farine de Blé Bio', 'Gluten', 'Isère'),
('PD2222222', 'PR1234567', 'Légumes', 'Carottes fraîches cultivées sans pesticides', 'Oui', 'AB', 'Carottes Bio', NULL, 'Isère'),
-- Produits Fromages du Terroir
('PD3333333', 'PR2345678', 'Fromage', 'Fromage de chèvre affiné 4 semaines', 'Non', 'AOP', 'Chèvre Fermier', 'Lait', 'Rhône-Alpes'),
('PD4444444', 'PR2345678', 'Fromage', 'Tomme de vache au lait cru', 'Oui', 'AB', 'Tomme Fermière', 'Lait', 'Rhône-Alpes'),
-- Produits Ruche Dorée
('PD5555555', 'PR3456789', 'Miel', 'Miel de fleurs récoltés en montagne', 'Oui', 'AB', 'Miel de Montagne', NULL, 'Isère'),
('PD6666666', 'PR3456789', 'Miel', 'Miel d\'acacia doux et liquide', 'Oui', 'AB', 'Miel d\'Acacia', NULL, 'Isère'),
-- Produits Maraîchage Local
('PD7777777', 'PR4567890', 'Légumes', 'Tomates anciennes variées', 'Oui', 'AB', 'Tomates Anciennes', NULL, 'Isère'),
('PD8888888', 'PR4567890', 'Légumes', 'Salade mesclun fraîche', 'Oui', 'AB', 'Mesclun Bio', NULL, 'Isère'),
-- Produits Vignoble
('PD9999999', 'PR5678901', 'Boissons', 'Vin rouge AOC Côtes du Rhône', 'Non', 'AOC', 'Vin Rouge 2022', 'Sulfites', 'Rhône'),
('PD0000000', 'PR5678901', 'Boissons', 'Vin blanc sec', 'Oui', 'AB', 'Vin Blanc Bio 2023', 'Sulfites', 'Rhône');

-- ============================================
-- 9. INSERTION DES CONTENANTS
-- ============================================
INSERT INTO Contenant (referenceContenant, typeContenant, capaciteContenant, stockContenant, caractereContenant, prixContenant) VALUES
('CN1234567', 'Bocal en verre réutilisable', 0.5, 50, 'Réutilisable', 2.50),
('CN2345678', 'Bocal en verre réutilisable', 1.0, 30, 'Réutilisable', 3.00),
('CN3456789', 'Sachet kraft', 0.25, 100, 'Non-Réutilisable', 0.20),
('CN4567890', 'Sachet kraft', 0.5, 150, 'Non-Réutilisable', 0.30),
('CN5678901', 'Bouteille en verre', 0.75, 80, 'Réutilisable', 1.50);

-- ============================================
-- 10. INSERTION DES STOCKS
-- ============================================
INSERT INTO Stock (idStock, idProduit, idProducteur) VALUES
('ST1111111', 'PD1111111', 'PR1234567'),
('ST2222222', 'PD2222222', 'PR1234567'),
('ST3333333', 'PD3333333', 'PR2345678'),
('ST4444444', 'PD4444444', 'PR2345678'),
('ST5555555', 'PD5555555', 'PR3456789'),
('ST6666666', 'PD6666666', 'PR3456789'),
('ST7777777', 'PD7777777', 'PR4567890'),
('ST8888888', 'PD8888888', 'PR4567890'),
('ST9999999', 'PD9999999', 'PR5678901'),
('ST0000000', 'PD0000000', 'PR5678901');

-- ============================================
-- 11. INSERTION DES LOTS
-- ============================================
INSERT INTO Lot (idLot, dateReception, dateLimite, typeDateLimite, idStock) VALUES
('LT1111111', '2025-11-01', '2026-05-01', 'DLUO', 'ST1111111'),
('LT2222222', '2025-11-15', '2025-11-25', 'DLC', 'ST2222222'),
('LT3333333', '2025-11-10', '2025-12-10', 'DLC', 'ST3333333'),
('LT4444444', '2025-11-08', '2026-01-08', 'DLC', 'ST4444444'),
('LT5555555', '2025-10-20', '2027-10-20', 'DLUO', 'ST5555555'),
('LT6666666', '2025-10-15', '2027-10-15', 'DLUO', 'ST6666666'),
('LT7777777', '2025-11-17', '2025-11-22', 'DLC', 'ST7777777'),
('LT8888888', '2025-11-18', '2025-11-21', 'DLC', 'ST8888888'),
('LT9999999', '2025-09-01', '2035-09-01', 'DLUO', 'ST9999999'),
('LT0000000', '2025-09-15', '2035-09-15', 'DLUO', 'ST0000000');

-- ============================================
-- 12. INSERTION DES LOTS VRAC ET PRÉCONDITIONNÉ
-- ============================================
-- Lots Vrac
INSERT INTO LotVrac (idLot, quantiteDisponibleVrac) VALUES
('LT1111111', 50.5),
('LT5555555', 25.0),
('LT6666666', 18.5);

-- Lots Préconditionné
INSERT INTO LotPreconditionne (idLot, quantiteDisponiblePreconditionne) VALUES
('LT2222222', 100),
('LT3333333', 45),
('LT4444444', 30),
('LT7777777', 80),
('LT8888888', 60),
('LT9999999', 120),
('LT0000000', 95);

-- ============================================
-- 13. INSERTION DES CONDITIONNEMENTS
-- ============================================
INSERT INTO Conditionnement (idConditionnement, prixAchatProducteur, prixVenteClient, idProduit, idProducteur) VALUES
('CD1111111', 3.50, 5.00, 'PD1111111', 'PR1234567'),
('CD2222222', 2.00, 3.50, 'PD2222222', 'PR1234567'),
('CD3333333', 8.00, 12.00, 'PD3333333', 'PR2345678'),
('CD4444444', 10.00, 15.00, 'PD4444444', 'PR2345678'),
('CD5555555', 12.00, 18.00, 'PD5555555', 'PR3456789'),
('CD6666666', 10.00, 15.50, 'PD6666666', 'PR3456789'),
('CD7777777', 3.50, 5.50, 'PD7777777', 'PR4567890'),
('CD8888888', 2.50, 4.00, 'PD8888888', 'PR4567890'),
('CD9999999', 8.00, 12.50, 'PD9999999', 'PR5678901'),
('CD0000000', 9.00, 14.00, 'PD0000000', 'PR5678901');

-- Conditionnements Vrac
INSERT INTO ConditionnementVrac (idConditionnement) VALUES
('CD1111111'),
('CD5555555'),
('CD6666666');

-- Conditionnements Préconditionné
INSERT INTO ConditionnementPreconditionne (idConditionnement, poidsSachet) VALUES
('CD2222222', 1.0),
('CD3333333', 0.25),
('CD4444444', 0.5),
('CD7777777', 0.5),
('CD8888888', 0.2),
('CD9999999', 0.75),
('CD0000000', 0.75);

-- ============================================
-- 14. ASSOCIATION CONDITIONNEMENT VRAC - CONTENANT
-- ============================================
INSERT INTO PeutEtreUtiliseAvec (idConditionnement, referenceContenant) VALUES
('CD1111111', 'CN3456789'),
('CD1111111', 'CN4567890'),
('CD5555555', 'CN1234567'),
('CD5555555', 'CN2345678'),
('CD6666666', 'CN1234567'),
('CD6666666', 'CN2345678');

-- ============================================
-- 15. INSERTION DES DISPONIBILITÉS
-- ============================================
INSERT INTO Disponibilite (idDisponibilite, debutDisponibilite, finDisponibilite, statutProduit) VALUES
('DS1111111', '2025-11-01', '2025-12-31', 'Disponible'),
('DS2222222', '2025-11-15', '2025-11-30', 'Disponible'),
('DS3333333', '2025-11-01', '2026-02-28', 'Disponible'),
('DS4444444', '2025-10-01', '2025-12-31', 'Disponible'),
('DS5555555', '2025-01-01', '2025-12-31', 'Disponible');

-- ============================================
-- 16. ASSOCIATION PRODUIT-DISPONIBILITÉ
-- ============================================
INSERT INTO ProduitEstDisponible (idProduit, idProducteur, idDisponibilite) VALUES
('PD1111111', 'PR1234567', 'DS1111111'),
('PD2222222', 'PR1234567', 'DS2222222'),
('PD3333333', 'PR2345678', 'DS3333333'),
('PD4444444', 'PR2345678', 'DS3333333'),
('PD5555555', 'PR3456789', 'DS5555555'),
('PD6666666', 'PR3456789', 'DS5555555'),
('PD7777777', 'PR4567890', 'DS2222222'),
('PD8888888', 'PR4567890', 'DS2222222'),
('PD9999999', 'PR5678901', 'DS4444444'),
('PD0000000', 'PR5678901', 'DS4444444');

-- ============================================
-- 17. INSERTION DES COMMANDES
-- ============================================
INSERT INTO Commande (idCommande, dateCommande, heureCommande, statutCommande, modePaiement, modeRecuperation, idClient) VALUES
('CM1111111', '2025-11-15', '10:30:00', 'Récupérée/Livrée', 'En ligne', 'Boutique', 'CL1234567'),
('CM2222222', '2025-11-16', '14:15:00', 'En livraison', 'En ligne', 'Domicile', 'CL2345678'),
('CM3333333', '2025-11-17', '09:45:00', 'Prête', 'En Boutique', 'Boutique', 'CL3456789'),
('CM4444444', '2025-11-18', '11:20:00', 'En préparation', 'En ligne', 'Domicile', 'CL4567890'),
('CM5555555', '2025-11-18', '16:00:00', 'En préparation', 'En ligne', 'Boutique', 'CL5678901');

-- ============================================
-- 18. INSERTION MODE RÉCUPÉRATION DOMICILE
-- ============================================
INSERT INTO ModeRecuperationDomicile (idModeRecuperationDomicile, paysLivraison, poidsTotalCommande, distanceAdresseBoutique, dateEstimeeLivraison, typePaysLivraison, idCommande, idAdresse) VALUES
('MR1111111', 'France', 5.5, 8.5, '2025-11-18', 'France Métropolitaine', 'CM2222222', 'AD2222222'),
('MR2222222', 'France', 3.2, 12.3, '2025-11-20', 'France Métropolitaine', 'CM4444444', 'AD4444444');

-- ============================================
-- 19. INSERTION DES LIGNES DE COMMANDE
-- ============================================
INSERT INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande) VALUES
-- Commande CM1111111
('LC1111111', 5.00, 10.00, 'CM1111111'),
('LC2222222', 12.00, 24.00, 'CM1111111'),
-- Commande CM2222222
('LC3333333', 3.50, 7.00, 'CM2222222'),
('LC4444444', 15.50, 31.00, 'CM2222222'),
-- Commande CM3333333
('LC5555555', 5.50, 11.00, 'CM3333333'),
('LC6666666', 4.00, 8.00, 'CM3333333'),
-- Commande CM4444444
('LC7777777', 18.00, 36.00, 'CM4444444'),
('LC8888888', 12.50, 25.00, 'CM4444444'),
-- Commande CM5555555
('LC9999999', 14.00, 28.00, 'CM5555555'),
('LC0000000', 2.50, 5.00, 'CM5555555');

-- ============================================
-- 20. INSERTION LIGNES COMMANDE PRODUIT
-- ============================================
INSERT INTO LigneCommandeProduit (idLigneCommande, idProduit, idProducteur) VALUES
('LC1111111', 'PD1111111', 'PR1234567'),
('LC2222222', 'PD3333333', 'PR2345678'),
('LC3333333', 'PD2222222', 'PR1234567'),
('LC4444444', 'PD6666666', 'PR3456789'),
('LC5555555', 'PD7777777', 'PR4567890'),
('LC6666666', 'PD8888888', 'PR4567890'),
('LC7777777', 'PD5555555', 'PR3456789'),
('LC8888888', 'PD9999999', 'PR5678901'),
('LC9999999', 'PD0000000', 'PR5678901');

-- Lignes Commande Produit Vrac
INSERT INTO LigneCommandeProduitVrac (idLigneCommande, quantiteCommandeVrac) VALUES
('LC1111111', 2.0),
('LC7777777', 2.0);

-- Lignes Commande Produit Préconditionné
INSERT INTO LigneCommandeProduitPreconditionne (idLigneCommande, quantiteCommandePreconditionne) VALUES
('LC2222222', 2),
('LC3333333', 2),
('LC4444444', 2),
('LC5555555', 2),
('LC6666666', 2),
('LC8888888', 2),
('LC9999999', 2);

-- ============================================
-- 21. INSERTION LIGNE COMMANDE CONTENANT
-- ============================================
INSERT INTO LigneCommandeContenant (idLigneCommande, quantiteCommandeContenant) VALUES
('LC0000000', 2);

-- ============================================
-- 22. INSERTION DES PERTES
-- ============================================
INSERT INTO Perte (idPerte, datePerte, naturePerte) VALUES
('PT1111111', '2025-11-10', 'Casse'),
('PT2222222', '2025-11-12', 'Vol'),
('PT3333333', '2025-11-14', 'Casse'),
('PT4444444', '2025-11-16', 'Casse');

-- ============================================
-- 23. INSERTION PERTES PRODUITS
-- ============================================
INSERT INTO PerteProduit (idPerte, idProduit, idProducteur) VALUES
('PT1111111', 'PD3333333', 'PR2345678'),
('PT2222222', 'PD5555555', 'PR3456789'),
('PT3333333', 'PD7777777', 'PR4567890');

-- Pertes Produit Préconditionné
INSERT INTO PerteProduitPreconditionne (idPerte, quantitePerduePreconditionne) VALUES
('PT1111111', 3),
('PT3333333', 5);

-- Pertes Produit Vrac
INSERT INTO PerteProduitVrac (idPerte, quantitePerdueVrac) VALUES
('PT2222222', 1.5);

-- ============================================
-- 24. INSERTION PERTES CONTENANTS
-- ============================================
INSERT INTO PerteContenant (idPerte, quantitePerdueContenant, referenceContenant) VALUES
('PT4444444', 4, 'CN1234567');

-- ============================================
-- FIN DU SCRIPT DE PEUPLEMENT
-- ============================================

-- Vérification du nombre d'enregistrements insérés
SELECT 'TypeActivite' AS Table_Name, COUNT(*) AS Nombre FROM TypeActivite
UNION ALL SELECT 'Contact', COUNT(*) FROM Contact
UNION ALL SELECT 'Client', COUNT(*) FROM Client
UNION ALL SELECT 'Adresse', COUNT(*) FROM Adresse
UNION ALL SELECT 'Producteur', COUNT(*) FROM Producteur
UNION ALL SELECT 'Produit', COUNT(*) FROM Produit
UNION ALL SELECT 'Stock', COUNT(*) FROM Stock
UNION ALL SELECT 'Lot', COUNT(*) FROM Lot
UNION ALL SELECT 'Conditionnement', COUNT(*) FROM Conditionnement
UNION ALL SELECT 'Contenant', COUNT(*) FROM Contenant
UNION ALL SELECT 'Commande', COUNT(*) FROM Commande
UNION ALL SELECT 'LigneCommande', COUNT(*) FROM LigneCommande
UNION ALL SELECT 'Perte', COUNT(*) FROM Perte;