-- SCRIPT SIMPLIFIÉ - INSERTION PRODUITS SEULEMENT
-- Ce script insère uniquement les données minimales pour avoir des produits dans le catalogue

-- 1. Types d'activité
INSERT INTO TypeActivite (typeActivite) VALUES ('Agriculteur');
INSERT INTO TypeActivite (typeActivite) VALUES ('Maraîcher');
INSERT INTO TypeActivite (typeActivite) VALUES ('Éleveur');
INSERT INTO TypeActivite (typeActivite) VALUES ('Apiculteur');
INSERT INTO TypeActivite (typeActivite) VALUES ('Viticulteur');

-- 2. Contacts
INSERT INTO Contact VALUES ('CT6789012', 'Ferme Bio', 'Pierre', '0467890123', 'contact@fermebio.fr');
INSERT INTO Contact VALUES ('CT7890123', 'Fromages du Terroir', 'Jacques', '0478901234', 'jacques@fromagesterroir.fr');
INSERT INTO Contact VALUES ('CT8901234', 'Ruche Dorée', 'Sylvie', '0489012345', 'sylvie@ruchedoree.fr');
INSERT INTO Contact VALUES ('CT9012345', 'Maraîchage Local', 'Antoine', '0490123456', 'antoine@maraichagelocal.fr');
INSERT INTO Contact VALUES ('CT0123456', 'Vignoble des Coteaux', 'Michel', '0401234567', 'michel@vignoble-coteaux.fr');

-- 3. Adresses
INSERT INTO Adresse VALUES ('AD6666666', '45 chemin des Vignes', 'Bernin', '38190');
INSERT INTO Adresse VALUES ('AD7777777', '78 route de la Ferme', 'Saint-Ismier', '38330');
INSERT INTO Adresse VALUES ('AD8888888', '23 rue du Rucher', 'Meylan', '38240');
INSERT INTO Adresse VALUES ('AD9999999', '56 avenue des Maraîchers', 'Grenoble', '38100');
INSERT INTO Adresse VALUES ('AD0000000', '12 chemin des Coteaux', 'Voreppe', '38340');

-- 4. Producteurs
INSERT INTO Producteur VALUES ('PR1234567', 45.1885, 5.7245, 'AD6666666', 'CT6789012');
INSERT INTO Producteur VALUES ('PR2345678', 45.2505, 5.7755, 'AD7777777', 'CT7890123');
INSERT INTO Producteur VALUES ('PR3456789', 45.2095, 5.7775, 'AD8888888', 'CT8901234');
INSERT INTO Producteur VALUES ('PR4567890', 45.1935, 5.6880, 'AD9999999', 'CT9012345');
INSERT INTO Producteur VALUES ('PR5678901', 45.2915, 5.6365, 'AD0000000', 'CT0123456');

-- 5. Activités des producteurs
INSERT INTO Exerce VALUES ('PR1234567', 'Agriculteur');
INSERT INTO Exerce VALUES ('PR1234567', 'Maraîcher');
INSERT INTO Exerce VALUES ('PR2345678', 'Éleveur');
INSERT INTO Exerce VALUES ('PR3456789', 'Apiculteur');
INSERT INTO Exerce VALUES ('PR4567890', 'Maraîcher');
INSERT INTO Exerce VALUES ('PR5678901', 'Viticulteur');

-- 6. PRODUITS (10 produits)
INSERT INTO Produit VALUES ('PD1111111', 'PR1234567', 'Céréales', 'Farine de blé complet issue de notre ferme', 'Oui', 'AB', 'Farine de Blé Bio', 'Gluten', 'Isère', NULL);
INSERT INTO Produit VALUES ('PD2222222', 'PR1234567', 'Légumes', 'Carottes fraîches cultivées sans pesticides', 'Oui', 'AB', 'Carottes Bio', NULL, 'Isère', NULL);
INSERT INTO Produit VALUES ('PD3333333', 'PR2345678', 'Fromage', 'Fromage de chèvre affiné 4 semaines', 'Non', 'AOP', 'Chèvre Fermier', 'Lait', 'Rhône-Alpes', NULL);
INSERT INTO Produit VALUES ('PD4444444', 'PR2345678', 'Fromage', 'Tomme de vache au lait cru', 'Oui', 'AB', 'Tomme Fermière', 'Lait', 'Rhône-Alpes', NULL);
INSERT INTO Produit VALUES ('PD5555555', 'PR3456789', 'Miel', 'Miel de fleurs récoltés en montagne', 'Oui', 'AB', 'Miel de Montagne', NULL, 'Isère', NULL);
INSERT INTO Produit VALUES ('PD6666666', 'PR3456789', 'Miel', 'Miel d''acacia doux et liquide', 'Oui', 'AB', 'Miel d''Acacia', NULL, 'Isère', NULL);
INSERT INTO Produit VALUES ('PD7777777', 'PR4567890', 'Légumes', 'Tomates anciennes variées', 'Oui', 'AB', 'Tomates Anciennes', NULL, 'Isère', NULL);
INSERT INTO Produit VALUES ('PD8888888', 'PR4567890', 'Légumes', 'Salade mesclun fraîche', 'Oui', 'AB', 'Mesclun Bio', NULL, 'Isère', NULL);
INSERT INTO Produit VALUES ('PD9999999', 'PR5678901', 'Boissons', 'Vin rouge AOC Côtes du Rhône', 'Non', 'AOC', 'Vin Rouge 2022', 'Sulfites', 'Rhône', NULL);
INSERT INTO Produit VALUES ('PD0000000', 'PR5678901', 'Boissons', 'Vin blanc sec', 'Oui', 'AB', 'Vin Blanc Bio 2023', 'Sulfites', 'Rhône', NULL);

-- 7. Conditionnements (prix pour les produits)
INSERT INTO Conditionnement VALUES ('CD1111111', 'PD1111111', 'PR1234567', 5.50);
INSERT INTO Conditionnement VALUES ('CD2222222', 'PD2222222', 'PR1234567', 3.20);
INSERT INTO Conditionnement VALUES ('CD3333333', 'PD3333333', 'PR2345678', 12.90);
INSERT INTO Conditionnement VALUES ('CD4444444', 'PD4444444', 'PR2345678', 15.50);
INSERT INTO Conditionnement VALUES ('CD5555555', 'PD5555555', 'PR3456789', 8.50);
INSERT INTO Conditionnement VALUES ('CD6666666', 'PD6666666', 'PR3456789', 9.20);
INSERT INTO Conditionnement VALUES ('CD7777777', 'PD7777777', 'PR4567890', 4.80);
INSERT INTO Conditionnement VALUES ('CD8888888', 'PD8888888', 'PR4567890', 3.50);
INSERT INTO Conditionnement VALUES ('CD9999999', 'PD9999999', 'PR5678901', 14.90);
INSERT INTO Conditionnement VALUES ('CD0000000', 'PD0000000', 'PR5678901', 16.50);

-- 8. Types de conditionnement
INSERT INTO ConditionnementPreconditionne VALUES ('CD1111111', 1.0);  -- Farine 1kg
INSERT INTO ConditionnementVrac VALUES ('CD2222222');  -- Carottes en vrac
INSERT INTO ConditionnementPreconditionne VALUES ('CD3333333', 0.25);  -- Fromage chèvre 250g
INSERT INTO ConditionnementPreconditionne VALUES ('CD4444444', 0.40);  -- Tomme 400g
INSERT INTO ConditionnementPreconditionne VALUES ('CD5555555', 0.50);  -- Miel 500g
INSERT INTO ConditionnementPreconditionne VALUES ('CD6666666', 0.50);  -- Miel acacia 500g
INSERT INTO ConditionnementVrac VALUES ('CD7777777');  -- Tomates en vrac
INSERT INTO ConditionnementVrac VALUES ('CD8888888');  -- Mesclun en vrac
INSERT INTO ConditionnementPreconditionne VALUES ('CD9999999', 0.75);  -- Vin rouge 75cl
INSERT INTO ConditionnementPreconditionne VALUES ('CD0000000', 0.75);  -- Vin blanc 75cl

-- 9. Disponibilités
INSERT INTO Disponibilite VALUES ('DS1111111', 'Permanent', NULL);
INSERT INTO Disponibilite VALUES ('DS2222222', 'Saisonnier', NULL);

-- 10. Produits disponibles
INSERT INTO ProduitEstDisponible VALUES ('PD1111111', 'PR1234567', 'DS1111111');
INSERT INTO ProduitEstDisponible VALUES ('PD2222222', 'PR1234567', 'DS2222222');
INSERT INTO ProduitEstDisponible VALUES ('PD3333333', 'PR2345678', 'DS1111111');
INSERT INTO ProduitEstDisponible VALUES ('PD4444444', 'PR2345678', 'DS1111111');
INSERT INTO ProduitEstDisponible VALUES ('PD5555555', 'PR3456789', 'DS1111111');
INSERT INTO ProduitEstDisponible VALUES ('PD6666666', 'PR3456789', 'DS1111111');
INSERT INTO ProduitEstDisponible VALUES ('PD7777777', 'PR4567890', 'DS2222222');
INSERT INTO ProduitEstDisponible VALUES ('PD8888888', 'PR4567890', 'DS2222222');
INSERT INTO ProduitEstDisponible VALUES ('PD9999999', 'PR5678901', 'DS1111111');
INSERT INTO ProduitEstDisponible VALUES ('PD0000000', 'PR5678901', 'DS1111111');

COMMIT;
