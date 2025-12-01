-- ============================================
-- SCRIPT DE PEUPLEMENT DE LA BASE DE DONNÉES
-- ============================================

-- ============================================
-- 1. INSERTION DES TYPES D'ACTIVITESA
-- ============================================
INSERT ALL
  INTO TypeActivite (typeActivite) VALUES ('Agriculteur')
  INTO TypeActivite (typeActivite) VALUES ('Éleveur')
  INTO TypeActivite (typeActivite) VALUES ('Apiculteur')
  INTO TypeActivite (typeActivite) VALUES ('Maraîcher')
  INTO TypeActivite (typeActivite) VALUES ('Viticulteur')
SELECT * FROM dual;

-- ============================================
-- 2. INSERTION DES CONTACTS
-- ============================================
INSERT ALL
  INTO Contact (idContact, nom, prenom, numTel, email) 
  VALUES ('CT1234567', 'Dupont', 'Marie', '0612345678', 'marie.dupont@email.com')
  INTO Contact (idContact, nom, prenom, numTel, email) 
  VALUES ('CT2345678', 'Martin', 'Jean', '0623456789', 'jean.martin@email.com')
  INTO Contact (idContact, nom, prenom, numTel, email) 
  VALUES ('CT3456789', 'Bernard', 'Sophie', '0634567890', 'sophie.bernard@email.com')
  INTO Contact (idContact, nom, prenom, numTel, email) 
  VALUES ('CT4567890', 'Petit', 'Luc', '0645678901', 'luc.petit@email.com')
  INTO Contact (idContact, nom, prenom, numTel, email) 
  VALUES ('CT5678901', 'Durand', 'Claire', '0656789012', 'claire.durand@email.com')
  INTO Contact (idContact, nom, prenom, numTel, email) 
  VALUES ('CT6789012', 'Ferme Bio', 'Pierre', '0467890123', 'contact@fermebio.fr')
  INTO Contact (idContact, nom, prenom, numTel, email) 
  VALUES ('CT7890123', 'Fromages du Terroir', 'Jacques', '0478901234', 'jacques@fromagesterroir.fr')
  INTO Contact (idContact, nom, prenom, numTel, email) 
  VALUES ('CT8901234', 'Ruche Dorée', 'Sylvie', '0489012345', 'sylvie@ruchedoree.fr')
  INTO Contact (idContact, nom, prenom, numTel, email) 
  VALUES ('CT9012345', 'Maraîchage Local', 'Antoine', '0490123456', 'antoine@maraichagelocal.fr')
  INTO Contact (idContact, nom, prenom, numTel, email) 
  VALUES ('CT0123456', 'Vignoble des Coteaux', 'Michel', '0401234567', 'michel@vignoble-coteaux.fr')
SELECT * FROM dual;

-- ============================================
-- 3. INSERTION DES ADRESSES
-- ============================================
INSERT ALL 
  INTO Adresse (idAdresse, rue, ville, codePostal) 
  VALUES ('AD1111111', '12 rue de la République', 'Lyon', '69001')
  INTO Adresse (idAdresse, rue, ville, codePostal) 
  VALUES ('AD2222222', '34 avenue des Champs', 'Grenoble', '38000')
  INTO Adresse (idAdresse, rue, ville, codePostal) 
  VALUES ('AD3333333', '56 boulevard Victor Hugo', 'Chambéry', '73000')
  INTO Adresse (idAdresse, rue, ville, codePostal) 
  VALUES ('AD4444444', '78 rue du Commerce', 'Annecy', '74000')
  INTO Adresse (idAdresse, rue, ville, codePostal) 
  VALUES ('AD5555555', '90 place de la Mairie', 'Valence', '26000')
  INTO Adresse (idAdresse, rue, ville, codePostal) 
  VALUES ('AD6666666', '15 chemin des Vignes', 'Saint-Martin-d\Hères', '38400')
  INTO Adresse (idAdresse, rue, ville, codePostal) 
  VALUES ('AD7777777', '23 route de la Ferme', 'Échirolles', '38130')
  INTO Adresse (idAdresse, rue, ville, codePostal) 
  VALUES ('AD8888888', '45 lieu-dit Les Ruches', 'Meylan', '38240')
  INTO Adresse (idAdresse, rue, ville, codePostal) 
  VALUES ('AD9999999', '67 chemin des Cultures', 'Fontaine', '38600')
  INTO Adresse (idAdresse, rue, ville, codePostal) 
  VALUES ('AD0000000', '89 route des Coteaux', 'Voreppe', '38340')
SELECT * FROM dual; 



-- ============================================
-- 4. INSERTION DES CLIENTS
-- ============================================
INSERT ALL 
  INTO Client (idClient, idContact) VALUES ('CL1234567', 'CT1234567')
  INTO Client (idClient, idContact) VALUES ('CL2345678', 'CT2345678')
  INTO Client (idClient, idContact) VALUES ('CL3456789', 'CT3456789')
  INTO Client (idClient, idContact) VALUES ('CL4567890', 'CT4567890')
  INTO Client (idClient, idContact) VALUES ('CL5678901', 'CT5678901')
SELECT * FROM dual; 

-- ============================================
-- 5. ASSOCIATION CLIENTS-ADRESSES
-- ============================================
INSERT ALL
  INTO PossedeAdresse (idClient, idAdresse) VALUES ('CL1234567', 'AD1111111')
  INTO PossedeAdresse (idClient, idAdresse) VALUES ('CL2345678', 'AD2222222')
  INTO PossedeAdresse (idClient, idAdresse) VALUES ('CL3456789', 'AD3333333')
  INTO PossedeAdresse (idClient, idAdresse) VALUES ('CL4567890', 'AD4444444')
  INTO PossedeAdresse (idClient, idAdresse) VALUES ('CL5678901', 'AD5555555')
SELECT * FROM dual; 

-- ============================================
-- 6. INSERTION DES PRODUCTEURS
-- ============================================
INSERT ALL 
  INTO Producteur (idProducteur, lattitude, longitude, idAdresse, idContact) 
  VALUES ('PR1234567', 45.1885, 5.7245, 'AD6666666', 'CT6789012')
  INTO Producteur (idProducteur, lattitude, longitude, idAdresse, idContact) 
  VALUES ('PR2345678', 45.1475, 5.7180, 'AD7777777', 'CT7890123')
  INTO Producteur (idProducteur, lattitude, longitude, idAdresse, idContact) 
  VALUES ('PR3456789', 45.2095, 5.7775, 'AD8888888', 'CT8901234')
  INTO Producteur (idProducteur, lattitude, longitude, idAdresse, idContact) 
  VALUES ('PR4567890', 45.1935, 5.6880, 'AD9999999', 'CT9012345')
  INTO Producteur (idProducteur, lattitude, longitude, idAdresse, idContact) 
  VALUES ('PR5678901', 45.2915, 5.6365, 'AD0000000', 'CT0123456')
SELECT * FROM dual;

-- ============================================
-- 7. ASSOCIATION PRODUCTEURS-ACTIVITÉS
-- ============================================
INSERT ALL 
  INTO Exerce (idProducteur, typeActivite) VALUES ('PR1234567', 'Agriculteur')
  INTO Exerce (idProducteur, typeActivite) VALUES ('PR1234567', 'Maraîcher')
  INTO Exerce (idProducteur, typeActivite) VALUES ('PR2345678', 'Éleveur')
  INTO Exerce (idProducteur, typeActivite) VALUES ('PR3456789', 'Apiculteur')
  INTO Exerce (idProducteur, typeActivite) VALUES ('PR4567890', 'Maraîcher')
  INTO Exerce (idProducteur, typeActivite) VALUES ('PR5678901', 'Viticulteur')
SELECT * FROM dual;

-- ============================================
-- 8. INSERTION DES PRODUITS
-- ============================================
INSERT ALL 
  INTO Produit (idProduit, idProducteur, categorie, description, bio, label, nomProduit, allergene, origineGeographique) 
  VALUES ('PD1111111', 'PR1234567', 'Céréales', 'Farine de blé complet issue de notre ferme', 'Oui', 'AB', 'Farine de Blé Bio', 'Gluten', 'Isère')
  INTO Produit (idProduit, idProducteur, categorie, description, bio, label, nomProduit, allergene, origineGeographique) 
  VALUES ('PD2222222', 'PR1234567', 'Légumes', 'Carottes fraîches cultivées sans pesticides', 'Oui', 'AB', 'Carottes Bio', NULL, 'Isère')
  INTO Produit (idProduit, idProducteur, categorie, description, bio, label, nomProduit, allergene, origineGeographique) 
  VALUES ('PD3333333', 'PR2345678', 'Fromage', 'Fromage de chèvre affiné 4 semaines', 'Non', 'AOP', 'Chèvre Fermier', 'Lait', 'Rhône-Alpes')
  INTO Produit (idProduit, idProducteur, categorie, description, bio, label, nomProduit, allergene, origineGeographique) 
  VALUES ('PD4444444', 'PR2345678', 'Fromage', 'Tomme de vache au lait cru', 'Oui', 'AB', 'Tomme Fermière', 'Lait', 'Rhône-Alpes')
  INTO Produit (idProduit, idProducteur, categorie, description, bio, label, nomProduit, allergene, origineGeographique) 
  VALUES ('PD5555555', 'PR3456789', 'Miel', 'Miel de fleurs récoltés en montagne', 'Oui', 'AB', 'Miel de Montagne', NULL, 'Isère')
  INTO Produit (idProduit, idProducteur, categorie, description, bio, label, nomProduit, allergene, origineGeographique) 
  VALUES ('PD6666666', 'PR3456789', 'Miel', 'Miel d\acacia doux et liquide', 'Oui', 'AB', 'Miel d\Acacia', NULL, 'Isère')
  INTO Produit (idProduit, idProducteur, categorie, description, bio, label, nomProduit, allergene, origineGeographique) 
  VALUES ('PD7777777', 'PR4567890', 'Légumes', 'Tomates anciennes variées', 'Oui', 'AB', 'Tomates Anciennes', NULL, 'Isère')
  INTO Produit (idProduit, idProducteur, categorie, description, bio, label, nomProduit, allergene, origineGeographique) 
  VALUES ('PD8888888', 'PR4567890', 'Légumes', 'Salade mesclun fraîche', 'Oui', 'AB', 'Mesclun Bio', NULL, 'Isère')
  INTO Produit (idProduit, idProducteur, categorie, description, bio, label, nomProduit, allergene, origineGeographique) 
  VALUES ('PD9999999', 'PR5678901', 'Boissons', 'Vin rouge AOC Côtes du Rhône', 'Non', 'AOC', 'Vin Rouge 2022', 'Sulfites', 'Rhône')
  INTO Produit (idProduit, idProducteur, categorie, description, bio, label, nomProduit, allergene, origineGeographique) 
  VALUES ('PD0000000', 'PR5678901', 'Boissons', 'Vin blanc sec', 'Oui', 'AB', 'Vin Blanc Bio 2023', 'Sulfites', 'Rhône')
SELECT * FROM dual;


-- ============================================
-- 9. INSERTION DES CONTENANTS
-- ============================================
INSERT ALL 
  INTO Contenant (referenceContenant, typeContenant, capaciteContenant, stockContenant, caractereContenant, prixContenant) 
  VALUES ('CN1234567', 'Bocal en verre réutilisable', 0.5, 50, 'Réutilisable', 2.50)
  INTO Contenant (referenceContenant, typeContenant, capaciteContenant, stockContenant, caractereContenant, prixContenant) 
  VALUES ('CN2345678', 'Bocal en verre réutilisable', 1.0, 30, 'Réutilisable', 3.00)
  INTO Contenant (referenceContenant, typeContenant, capaciteContenant, stockContenant, caractereContenant, prixContenant) 
  VALUES ('CN3456789', 'Sachet kraft', 0.25, 100, 'Non-Réutilisable', 0.20)
  INTO Contenant (referenceContenant, typeContenant, capaciteContenant, stockContenant, caractereContenant, prixContenant) 
  VALUES ('CN4567890', 'Sachet kraft', 0.5, 150, 'Non-Réutilisable', 0.30)
  INTO Contenant (referenceContenant, typeContenant, capaciteContenant, stockContenant, caractereContenant, prixContenant) 
  VALUES ('CN5678901', 'Bouteille en verre', 0.75, 80, 'Réutilisable', 1.50)
SELECT * FROM dual;


-- ============================================
-- 10. INSERTION DES STOCKS
-- ============================================
INSERT ALL
  INTO Stock (idStock, idProduit, idProducteur) VALUES ('ST1111111', 'PD1111111', 'PR1234567')
  INTO Stock (idStock, idProduit, idProducteur) VALUES ('ST2222222', 'PD2222222', 'PR1234567')
  INTO Stock (idStock, idProduit, idProducteur) VALUES ('ST3333333', 'PD3333333', 'PR2345678')
  INTO Stock (idStock, idProduit, idProducteur) VALUES ('ST4444444', 'PD4444444', 'PR2345678')
  INTO Stock (idStock, idProduit, idProducteur) VALUES ('ST5555555', 'PD5555555', 'PR3456789')
  INTO Stock (idStock, idProduit, idProducteur) VALUES ('ST6666666', 'PD6666666', 'PR3456789')
  INTO Stock (idStock, idProduit, idProducteur) VALUES ('ST7777777', 'PD7777777', 'PR4567890')
  INTO Stock (idStock, idProduit, idProducteur) VALUES ('ST8888888', 'PD8888888', 'PR4567890')
  INTO Stock (idStock, idProduit, idProducteur) VALUES ('ST9999999', 'PD9999999', 'PR5678901')
  INTO Stock (idStock, idProduit, idProducteur) VALUES ('ST0000000', 'PD0000000', 'PR5678901')
SELECT * FROM dual;


-- ============================================
-- 11. INSERTION DES LOTS
-- ============================================
INSERT ALL
  INTO Lot (idLot, dateReception, dateLimite, typeDateLimite, idStock) 
  VALUES ('LT1111111', TO_DATE(CURRENT_DATE - 3), TO_DATE(CURRENT_DATE + 2), 'DLUO', 'ST1111111')
  INTO Lot (idLot, dateReception, dateLimite, typeDateLimite, idStock) 
  VALUES ('LT2222222', TO_DATE('2025-11-15', 'YYYY-MM-DD'), TO_DATE('2025-11-25', 'YYYY-MM-DD'), 'DLC', 'ST2222222')
  INTO Lot (idLot, dateReception, dateLimite, typeDateLimite, idStock) 
  VALUES ('LT3333333', TO_DATE('2025-11-10', 'YYYY-MM-DD'), TO_DATE('2025-12-10', 'YYYY-MM-DD'), 'DLC', 'ST3333333')
  INTO Lot (idLot, dateReception, dateLimite, typeDateLimite, idStock) 
  VALUES ('LT4444444', TO_DATE('2025-11-08', 'YYYY-MM-DD'), TO_DATE('2026-01-08', 'YYYY-MM-DD'), 'DLC', 'ST4444444')
  INTO Lot (idLot, dateReception, dateLimite, typeDateLimite, idStock) 
  VALUES ('LT5555555', TO_DATE('2025-10-20', 'YYYY-MM-DD'), TO_DATE('2027-10-20', 'YYYY-MM-DD'), 'DLUO', 'ST5555555')
  INTO Lot (idLot, dateReception, dateLimite, typeDateLimite, idStock) 
  VALUES ('LT7777777', TO_DATE('2025-11-17', 'YYYY-MM-DD'), TO_DATE('2025-11-22', 'YYYY-MM-DD'), 'DLC', 'ST7777777')
  INTO Lot (idLot, dateReception, dateLimite, typeDateLimite, idStock) 
  VALUES ('LT8888888', TO_DATE('2025-11-18', 'YYYY-MM-DD'), TO_DATE('2025-11-21', 'YYYY-MM-DD'), 'DLC', 'ST8888888')
  INTO Lot (idLot, dateReception, dateLimite, typeDateLimite, idStock) 
  VALUES ('LT9999999', TO_DATE('2025-09-01', 'YYYY-MM-DD'), TO_DATE('2035-09-01', 'YYYY-MM-DD'), 'DLUO', 'ST9999999')
  INTO Lot (idLot, dateReception, dateLimite, typeDateLimite, idStock) 
  VALUES ('LT0000000', TO_DATE('2025-09-15', 'YYYY-MM-DD'), TO_DATE('2035-09-15', 'YYYY-MM-DD'), 'DLUO', 'ST0000000')
SELECT * FROM dual;



-- ============================================
-- 12. INSERTION DES LOTS VRAC ET PRÉCONDITIONNÉ
-- ============================================
-- Lots Vrac
INSERT ALL
  INTO LotVrac (idLot, quantiteDisponibleVrac) VALUES ('LT1111111', 50.5)
  INTO LotVrac (idLot, quantiteDisponibleVrac) VALUES ('LT5555555', 25.0)
  INTO LotVrac (idLot, quantiteDisponibleVrac) VALUES ('LT0000000', 18.5)
SELECT * FROM dual;

-- Lots Préconditionné
INSERT ALL
  INTO LotPreconditionne (idLot, quantiteDisponiblePreconditionne) VALUES ('LT2222222', 100)
  INTO LotPreconditionne (idLot, quantiteDisponiblePreconditionne) VALUES ('LT3333333', 45)
  INTO LotPreconditionne (idLot, quantiteDisponiblePreconditionne) VALUES ('LT4444444', 30)
  INTO LotPreconditionne (idLot, quantiteDisponiblePreconditionne) VALUES ('LT7777777', 80)
  INTO LotPreconditionne (idLot, quantiteDisponiblePreconditionne) VALUES ('LT8888888', 60)
  INTO LotPreconditionne (idLot, quantiteDisponiblePreconditionne) VALUES ('LT9999999', 120)
  INTO LotPreconditionne (idLot, quantiteDisponiblePreconditionne) VALUES ('LT0000000', 95)
SELECT * FROM dual;

-- ============================================
-- 13. INSERTION DES CONDITIONNEMENTS
-- ============================================
INSERT ALL
  INTO Conditionnement (idConditionnement, prixAchatProducteur, prixVenteClient, idProduit, idProducteur) 
    VALUES ('CD1111111', 3.50, 5.00, 'PD1111111', 'PR1234567')
  INTO Conditionnement (idConditionnement, prixAchatProducteur, prixVenteClient, idProduit, idProducteur) 
    VALUES ('CD2222222', 2.00, 3.50, 'PD2222222', 'PR1234567')
  INTO Conditionnement (idConditionnement, prixAchatProducteur, prixVenteClient, idProduit, idProducteur) 
    VALUES ('CD3333333', 8.00, 12.00, 'PD3333333', 'PR2345678')
  INTO Conditionnement (idConditionnement, prixAchatProducteur, prixVenteClient, idProduit, idProducteur) 
    VALUES ('CD4444444', 10.00, 15.00, 'PD4444444', 'PR2345678')
  INTO Conditionnement (idConditionnement, prixAchatProducteur, prixVenteClient, idProduit, idProducteur) 
    VALUES ('CD5555555', 12.00, 18.00, 'PD5555555', 'PR3456789')
  INTO Conditionnement (idConditionnement, prixAchatProducteur, prixVenteClient, idProduit, idProducteur) 
    VALUES ('CD6666666', 10.00, 15.50, 'PD6666666', 'PR3456789')
  INTO Conditionnement (idConditionnement, prixAchatProducteur, prixVenteClient, idProduit, idProducteur) 
    VALUES ('CD7777777', 3.50, 5.50, 'PD7777777', 'PR4567890')
  INTO Conditionnement (idConditionnement, prixAchatProducteur, prixVenteClient, idProduit, idProducteur) 
    VALUES ('CD8888888', 2.50, 4.00, 'PD8888888', 'PR4567890')
  INTO Conditionnement (idConditionnement, prixAchatProducteur, prixVenteClient, idProduit, idProducteur) 
    VALUES ('CD9999999', 8.00, 12.50, 'PD9999999', 'PR5678901')
  INTO Conditionnement (idConditionnement, prixAchatProducteur, prixVenteClient, idProduit, idProducteur) 
    VALUES ('CD0000000', 9.00, 14.00, 'PD0000000', 'PR5678901')
SELECT * FROM dual;

-- Conditionnements Vrac
INSERT ALL
  INTO ConditionnementVrac (idConditionnement) VALUES ('CD1111111')
  INTO ConditionnementVrac (idConditionnement) VALUES ('CD5555555')
  INTO ConditionnementVrac (idConditionnement) VALUES ('CD6666666')
SELECT * FROM dual;
select * from produit; 
-- Conditionnements Préconditionné
INSERT ALL
  INTO ConditionnementPreconditionne (idConditionnement, poidsSachet) VALUES ('CD2222222', 1.0)
  INTO ConditionnementPreconditionne (idConditionnement, poidsSachet) VALUES ('CD3333333', 0.25)
  INTO ConditionnementPreconditionne (idConditionnement, poidsSachet) VALUES ('CD4444444', 0.5)
  INTO ConditionnementPreconditionne (idConditionnement, poidsSachet) VALUES ('CD7777777', 0.5)
  INTO ConditionnementPreconditionne (idConditionnement, poidsSachet) VALUES ('CD8888888', 0.2)
  INTO ConditionnementPreconditionne (idConditionnement, poidsSachet) VALUES ('CD9999999', 0.75)
  INTO ConditionnementPreconditionne (idConditionnement, poidsSachet) VALUES ('CD0000000', 0.75)
SELECT * FROM dual;

-- ============================================
-- 14. ASSOCIATION CONDITIONNEMENT VRAC - CONTENANT
-- ============================================
INSERT ALL
  INTO PeutEtreUtiliseAvec (idConditionnement, referenceContenant) VALUES ('CD1111111', 'CN3456789')
  INTO PeutEtreUtiliseAvec (idConditionnement, referenceContenant) VALUES ('CD1111111', 'CN4567890')
  INTO PeutEtreUtiliseAvec (idConditionnement, referenceContenant) VALUES ('CD5555555', 'CN1234567')
  INTO PeutEtreUtiliseAvec (idConditionnement, referenceContenant) VALUES ('CD5555555', 'CN2345678')
  INTO PeutEtreUtiliseAvec (idConditionnement, referenceContenant) VALUES ('CD6666666', 'CN1234567')
  INTO PeutEtreUtiliseAvec (idConditionnement, referenceContenant) VALUES ('CD6666666', 'CN2345678')
SELECT * FROM dual;

-- ============================================
-- 15. INSERTION DES DISPONIBILITÉS
-- ============================================
INSERT ALL
  INTO Disponibilite (idDisponibilite, debutDisponibilite, finDisponibilite, statutProduit) 
    VALUES ('DS1111111', TO_DATE('2025-11-01', 'YYYY-MM-DD'), TO_DATE('2025-12-31', 'YYYY-MM-DD'), 'Disponible')
  INTO Disponibilite (idDisponibilite, debutDisponibilite, finDisponibilite, statutProduit) 
    VALUES ('DS2222222', TO_DATE('2025-11-15', 'YYYY-MM-DD'), TO_DATE('2025-11-30', 'YYYY-MM-DD'), 'Disponible')
  INTO Disponibilite (idDisponibilite, debutDisponibilite, finDisponibilite, statutProduit) 
    VALUES ('DS3333333', TO_DATE('2025-11-01', 'YYYY-MM-DD'), TO_DATE('2026-02-28', 'YYYY-MM-DD'), 'Disponible')
  INTO Disponibilite (idDisponibilite, debutDisponibilite, finDisponibilite, statutProduit) 
    VALUES ('DS4444444', TO_DATE('2025-10-01', 'YYYY-MM-DD'), TO_DATE('2025-12-31', 'YYYY-MM-DD'), 'Disponible')
  INTO Disponibilite (idDisponibilite, debutDisponibilite, finDisponibilite, statutProduit) 
    VALUES ('DS5555555', TO_DATE('2025-01-01', 'YYYY-MM-DD'), TO_DATE('2025-12-31', 'YYYY-MM-DD'), 'Disponible')
SELECT * FROM dual;

-- ============================================
-- 16. ASSOCIATION PRODUIT-DISPONIBILITÉ
-- ============================================
INSERT ALL
  INTO ProduitEstDisponible (idProduit, idProducteur, idDisponibilite) VALUES ('PD1111111', 'PR1234567', 'DS1111111')
  INTO ProduitEstDisponible (idProduit, idProducteur, idDisponibilite) VALUES ('PD2222222', 'PR1234567', 'DS2222222')
  INTO ProduitEstDisponible (idProduit, idProducteur, idDisponibilite) VALUES ('PD3333333', 'PR2345678', 'DS3333333')
  INTO ProduitEstDisponible (idProduit, idProducteur, idDisponibilite) VALUES ('PD4444444', 'PR2345678', 'DS3333333')
  INTO ProduitEstDisponible (idProduit, idProducteur, idDisponibilite) VALUES ('PD5555555', 'PR3456789', 'DS5555555')
  INTO ProduitEstDisponible (idProduit, idProducteur, idDisponibilite) VALUES ('PD6666666', 'PR3456789', 'DS5555555')
  INTO ProduitEstDisponible (idProduit, idProducteur, idDisponibilite) VALUES ('PD7777777', 'PR4567890', 'DS2222222')
  INTO ProduitEstDisponible (idProduit, idProducteur, idDisponibilite) VALUES ('PD8888888', 'PR4567890', 'DS2222222')
  INTO ProduitEstDisponible (idProduit, idProducteur, idDisponibilite) VALUES ('PD9999999', 'PR5678901', 'DS4444444')
  INTO ProduitEstDisponible (idProduit, idProducteur, idDisponibilite) VALUES ('PD0000000', 'PR5678901', 'DS4444444')
SELECT * FROM dual;

-- ============================================
-- 17. INSERTION DES COMMANDES
-- ============================================
INSERT ALL
  INTO Commande (idCommande, dateCommande, heureCommande, statutCommande, modePaiement, modeRecuperation, idClient) 
    VALUES ('CM1111111', TO_DATE('2025-11-15', 'YYYY-MM-DD'), TO_TIMESTAMP('2025-11-15 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'Récupérée/Livrée', 'En ligne', 'Boutique', 'CL1234567')
  INTO Commande (idCommande, dateCommande, heureCommande, statutCommande, modePaiement, modeRecuperation, idClient) 
    VALUES ('CM2222222', TO_DATE('2025-11-16', 'YYYY-MM-DD'), TO_TIMESTAMP('2025-11-16 14:15:00', 'YYYY-MM-DD HH24:MI:SS'), 'En livraison', 'En ligne', 'Domicile', 'CL2345678')
  INTO Commande (idCommande, dateCommande, heureCommande, statutCommande, modePaiement, modeRecuperation, idClient) 
    VALUES ('CM3333333', TO_DATE('2025-11-17', 'YYYY-MM-DD'), TO_TIMESTAMP('2025-11-17 09:45:00', 'YYYY-MM-DD HH24:MI:SS'), 'Prête', 'En Boutique', 'Boutique', 'CL3456789')
  INTO Commande (idCommande, dateCommande, heureCommande, statutCommande, modePaiement, modeRecuperation, idClient) 
    VALUES ('CM4444444', TO_DATE('2025-11-18', 'YYYY-MM-DD'), TO_TIMESTAMP('2025-11-18 11:20:00', 'YYYY-MM-DD HH24:MI:SS'), 'En préparation', 'En ligne', 'Domicile', 'CL4567890')
  INTO Commande (idCommande, dateCommande, heureCommande, statutCommande, modePaiement, modeRecuperation, idClient) 
    VALUES ('CM5555555', TO_DATE('2025-11-18', 'YYYY-MM-DD'), TO_TIMESTAMP('2025-11-18 16:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'En préparation', 'En ligne', 'Boutique', 'CL5678901')
SELECT * FROM dual;

-- ============================================
-- 18. INSERTION MODE RÉCUPÉRATION DOMICILE
-- ============================================
INSERT ALL
  INTO ModeRecuperationDomicile (idModeRecuperationDomicile, paysLivraison, poidsTotalCommande, distanceAdresseBoutique, dateEstimeeLivraison, typePaysLivraison, idCommande, idAdresse) 
    VALUES ('MR1111111', 'France', 5.5, 8.5, TO_DATE('2025-11-18', 'YYYY-MM-DD'), 'France Métropolitaine', 'CM2222222', 'AD2222222')
  INTO ModeRecuperationDomicile (idModeRecuperationDomicile, paysLivraison, poidsTotalCommande, distanceAdresseBoutique, dateEstimeeLivraison, typePaysLivraison, idCommande, idAdresse) 
    VALUES ('MR2222222', 'France', 3.2, 12.3, TO_DATE('2025-11-20', 'YYYY-MM-DD'), 'France Métropolitaine', 'CM4444444', 'AD4444444')
SELECT * FROM dual;


-- ============================================
-- 19. INSERTION DES LIGNES DE COMMANDE
-- ============================================
INSERT ALL
  INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande) 
    VALUES ('LC1111111', 5.00, 10.00, 'CM1111111')
  INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande) 
    VALUES ('LC2222222', 12.00, 24.00, 'CM1111111')
  INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande) 
    VALUES ('LC3333333', 3.50, 7.00, 'CM2222222')
  INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande) 
    VALUES ('LC4444444', 15.50, 31.00, 'CM2222222')
  INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande) 
    VALUES ('LC5555555', 5.50, 11.00, 'CM3333333')
  INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande) 
    VALUES ('LC6666666', 4.00, 8.00, 'CM3333333')
  INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande) 
    VALUES ('LC7777777', 18.00, 36.00, 'CM4444444')
  INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande) 
    VALUES ('LC8888888', 12.50, 25.00, 'CM4444444')
  INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande) 
    VALUES ('LC9999999', 14.00, 28.00, 'CM5555555')
  INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande) 
    VALUES ('LC0000000', 2.50, 5.00, 'CM5555555')
SELECT * FROM dual;

-- ============================================
-- 20. INSERTION LIGNES COMMANDE PRODUIT
-- ============================================
INSERT ALL
  INTO LigneCommandeProduit (idLigneCommande, idCommande, idProduit, idProducteur) 
    VALUES ('LC1111111', 'CM1111111', 'PD1111111', 'PR1234567')
  INTO LigneCommandeProduit (idLigneCommande, idCommande, idProduit, idProducteur) 
    VALUES ('LC2222222', 'CM1111111', 'PD3333333', 'PR2345678')
  INTO LigneCommandeProduit (idLigneCommande, idCommande, idProduit, idProducteur) 
    VALUES ('LC3333333', 'CM2222222', 'PD2222222', 'PR1234567')
  INTO LigneCommandeProduit (idLigneCommande,idCommande, idProduit, idProducteur) 
    VALUES ('LC4444444', 'CM2222222', 'PD6666666', 'PR3456789')
  INTO LigneCommandeProduit (idLigneCommande, idCommande, idProduit, idProducteur) 
    VALUES ('LC5555555', 'CM3333333', 'PD7777777', 'PR4567890')
  INTO LigneCommandeProduit (idLigneCommande,idCommande, idProduit, idProducteur) 
    VALUES ('LC6666666', 'CM3333333', 'PD8888888', 'PR4567890')
  INTO LigneCommandeProduit (idLigneCommande,idCommande, idProduit, idProducteur) 
    VALUES ('LC7777777', 'CM4444444', 'PD5555555', 'PR3456789')
  INTO LigneCommandeProduit (idLigneCommande,idCommande, idProduit, idProducteur) 
    VALUES ('LC8888888','CM4444444', 'PD9999999', 'PR5678901')
  INTO LigneCommandeProduit (idLigneCommande, idCommande, idProduit, idProducteur) 
    VALUES ('LC9999999','CM5555555', 'PD0000000', 'PR5678901')
SELECT * FROM dual;

-- Lignes Commande Produit Vrac
INSERT ALL
  INTO LigneCommandeProduitVrac (idLigneCommande, idCommande,  quantiteCommandeVrac) 
    VALUES ('LC1111111', 'CM1111111', 2.0)
  INTO LigneCommandeProduitVrac (idLigneCommande, idCommande, quantiteCommandeVrac) 
    VALUES ('LC7777777', 'CM4444444', 2.0)
SELECT * FROM dual;

-- Lignes Commande Produit Préconditionné
INSERT ALL
  INTO LigneCommandeProduitPreconditionne (idLigneCommande, idCommande, quantiteCommandePreconditionne) 
    VALUES ('LC2222222','CM1111111', 2)
  INTO LigneCommandeProduitPreconditionne (idLigneCommande, idCommande, quantiteCommandePreconditionne) 
    VALUES ('LC3333333', 'CM2222222', 2)
  INTO LigneCommandeProduitPreconditionne (idLigneCommande, idCommande, quantiteCommandePreconditionne) 
    VALUES ('LC4444444', 'CM2222222', 2)
  INTO LigneCommandeProduitPreconditionne (idLigneCommande, idCommande, quantiteCommandePreconditionne) 
    VALUES ('LC5555555', 'CM3333333', 2)
  INTO LigneCommandeProduitPreconditionne (idLigneCommande, idCommande, quantiteCommandePreconditionne) 
    VALUES ('LC6666666', 'CM3333333',  2)
  INTO LigneCommandeProduitPreconditionne (idLigneCommande, idCommande, quantiteCommandePreconditionne) 
    VALUES ('LC8888888', 'CM4444444', 2)
  INTO LigneCommandeProduitPreconditionne (idLigneCommande, idCommande, quantiteCommandePreconditionne) 
    VALUES ('LC9999999', 'CM5555555', 2)
SELECT * FROM dual;

-- ============================================
-- 21. INSERTION LIGNE COMMANDE CONTENANT
-- ============================================
INSERT INTO LigneCommandeContenant (idLigneCommande, idCommande, referenceContenant, quantiteCommandeContenant) 
  VALUES ('LC0000000', 'CM5555555', 'CN1234567', 2);
  
-- ============================================
-- 22. INSERTION DES PERTES
-- ============================================
INSERT ALL
  INTO Perte (idPerte, datePerte, naturePerte) 
    VALUES ('PT1111111', TO_DATE('2025-11-10', 'YYYY-MM-DD'), 'Casse')
  INTO Perte (idPerte, datePerte, naturePerte) 
    VALUES ('PT2222222', TO_DATE('2025-11-12', 'YYYY-MM-DD'), 'Vol')
  INTO Perte (idPerte, datePerte, naturePerte) 
    VALUES ('PT3333333', TO_DATE('2025-11-14', 'YYYY-MM-DD'), 'Casse')
  INTO Perte (idPerte, datePerte, naturePerte) 
    VALUES ('PT4444444', TO_DATE('2025-11-16', 'YYYY-MM-DD'), 'Casse')
SELECT * FROM dual;

-- ============================================
-- 23. INSERTION PERTES PRODUITS
-- ============================================
INSERT ALL
  INTO PerteProduit (idPerte, idProduit, idProducteur) 
    VALUES ('PT1111111', 'PD3333333', 'PR2345678')
  INTO PerteProduit (idPerte, idProduit, idProducteur) 
    VALUES ('PT2222222', 'PD5555555', 'PR3456789')
  INTO PerteProduit (idPerte, idProduit, idProducteur) 
    VALUES ('PT3333333', 'PD7777777', 'PR4567890')
SELECT * FROM dual;

-- Pertes Produit Préconditionné
INSERT INTO PerteProduitPreconditionne (idPerte, quantitePerduePreconditionne) 
  VALUES ('PT1111111', 3);
INSERT INTO PerteProduitPreconditionne (idPerte, quantitePerduePreconditionne) 
  VALUES ('PT3333333', 5);

-- Pertes Produit Vrac
INSERT INTO PerteProduitVrac (idPerte, quantitePerdueVrac) 
  VALUES ('PT2222222', 1.5);

-- ============================================
-- 24. INSERTION PERTES CONTENANTS
-- ============================================
INSERT INTO PerteContenant (idPerte, quantitePerdueContenant, referenceContenant) 
  VALUES ('PT4444444', 4, 'CN1234567');


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

commit;