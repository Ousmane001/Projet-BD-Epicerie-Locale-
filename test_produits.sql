SELECT COUNT(*) as nb_produits FROM Produit;
SELECT p.idProduit, p.nomProduit, p.categorie 
FROM Produit p 
WHERE ROWNUM <= 5;
