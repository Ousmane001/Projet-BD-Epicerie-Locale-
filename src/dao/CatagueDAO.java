package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.DataSourceProvider;
import model.ProduitCatalogue;

public class CatagueDAO {
    private Connection connection;

    public CatagueDAO(){
        this.connection = DataSourceProvider.getConnection();
    }

    /**
     * Récupère tous les produits disponibles du catalogue avec leurs informations complètes
     * @return Liste des produits du catalogue
     */
    public List<ProduitCatalogue> getTousLesProduits() {
        List<ProduitCatalogue> produits = new ArrayList<>();
        
        String sql = "SELECT DISTINCT " +
                     "    p.idProduit, " +
                     "    p.idProducteur, " +
                     "    p.nomProduit, " +
                     "    p.categorie, " +
                     "    p.description, " +
                     "    p.bio, " +
                     "    p.label, " +
                     "    p.allergene, " +
                     "    p.origineGeographique, " +
                     "    p.delaiDisponibilite, " +
                     "    c.prixVenteClient, " +
                     "    CASE " +
                     "        WHEN cv.idConditionnement IS NOT NULL THEN 'Vrac' " +
                     "        WHEN cp.idConditionnement IS NOT NULL THEN 'Préconditionné' " +
                     "        ELSE 'N/A' " +
                     "    END AS typeConditionnement, " +
                     "    cp.poidsSachet, " +
                     "    d.statutProduit " +
                     "FROM Produit p " +
                     "JOIN Conditionnement c ON p.idProduit = c.idProduit AND p.idProducteur = c.idProducteur " +
                     "LEFT JOIN ConditionnementVrac cv ON c.idConditionnement = cv.idConditionnement " +
                     "LEFT JOIN ConditionnementPreconditionne cp ON c.idConditionnement = cp.idConditionnement " +
                     "LEFT JOIN ProduitEstDisponible ped ON p.idProduit = ped.idProduit AND p.idProducteur = ped.idProducteur " +
                     "LEFT JOIN Disponibilite d ON ped.idDisponibilite = d.idDisponibilite " +
                     "WHERE d.statutProduit = 'Disponible' OR d.statutProduit IS NULL " +
                     "ORDER BY p.categorie, p.nomProduit";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ProduitCatalogue produit = new ProduitCatalogue();
                produit.setIdProduit(rs.getString("idProduit"));
                produit.setIdProducteur(rs.getString("idProducteur"));
                produit.setNomProduit(rs.getString("nomProduit"));
                produit.setCategorie(rs.getString("categorie"));
                produit.setDescription(rs.getString("description"));
                produit.setBio(rs.getString("bio"));
                produit.setLabel(rs.getString("label"));
                produit.setAllergene(rs.getString("allergene"));
                produit.setOrigineGeographique(rs.getString("origineGeographique"));
                
                Integer delai = rs.getInt("delaiDisponibilite");
                produit.setDelaiDisponibilite(rs.wasNull() ? null : delai);
                
                produit.setPrixVenteClient(rs.getFloat("prixVenteClient"));
                produit.setTypeConditionnement(rs.getString("typeConditionnement"));
                
                Float poids = rs.getFloat("poidsSachet");
                produit.setPoidsSachet(rs.wasNull() ? null : poids);
                
                produit.setStatutProduit(rs.getString("statutProduit"));
                
                produits.add(produit);
            }
            
            System.out.println("Récupération de " + produits.size() + " produits du catalogue");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits du catalogue:");
            e.printStackTrace();
        }
        
        return produits;
    }

    /**
     * Récupère les produits filtrés par catégorie
     * @param categorie La catégorie à filtrer
     * @return Liste des produits de la catégorie
     */
    public List<ProduitCatalogue> getProduitsParCategorie(String categorie) {
        List<ProduitCatalogue> produits = new ArrayList<>();
        
        String sql = "SELECT DISTINCT " +
                     "    p.idProduit, " +
                     "    p.idProducteur, " +
                     "    p.nomProduit, " +
                     "    p.categorie, " +
                     "    p.description, " +
                     "    p.bio, " +
                     "    p.label, " +
                     "    p.allergene, " +
                     "    p.origineGeographique, " +
                     "    p.delaiDisponibilite, " +
                     "    c.prixVenteClient, " +
                     "    CASE " +
                     "        WHEN cv.idConditionnement IS NOT NULL THEN 'Vrac' " +
                     "        WHEN cp.idConditionnement IS NOT NULL THEN 'Préconditionné' " +
                     "        ELSE 'N/A' " +
                     "    END AS typeConditionnement, " +
                     "    cp.poidsSachet, " +
                     "    d.statutProduit " +
                     "FROM Produit p " +
                     "JOIN Conditionnement c ON p.idProduit = c.idProduit AND p.idProducteur = c.idProducteur " +
                     "LEFT JOIN ConditionnementVrac cv ON c.idConditionnement = cv.idConditionnement " +
                     "LEFT JOIN ConditionnementPreconditionne cp ON c.idConditionnement = cp.idConditionnement " +
                     "LEFT JOIN ProduitEstDisponible ped ON p.idProduit = ped.idProduit AND p.idProducteur = ped.idProducteur " +
                     "LEFT JOIN Disponibilite d ON ped.idDisponibilite = d.idDisponibilite " +
                     "WHERE p.categorie = ? AND (d.statutProduit = 'Disponible' OR d.statutProduit IS NULL) " +
                     "ORDER BY p.nomProduit";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, categorie);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProduitCatalogue produit = new ProduitCatalogue();
                    produit.setIdProduit(rs.getString("idProduit"));
                    produit.setIdProducteur(rs.getString("idProducteur"));
                    produit.setNomProduit(rs.getString("nomProduit"));
                    produit.setCategorie(rs.getString("categorie"));
                    produit.setDescription(rs.getString("description"));
                    produit.setBio(rs.getString("bio"));
                    produit.setLabel(rs.getString("label"));
                    produit.setAllergene(rs.getString("allergene"));
                    produit.setOrigineGeographique(rs.getString("origineGeographique"));
                    
                    Integer delai = rs.getInt("delaiDisponibilite");
                    produit.setDelaiDisponibilite(rs.wasNull() ? null : delai);
                    
                    produit.setPrixVenteClient(rs.getFloat("prixVenteClient"));
                    produit.setTypeConditionnement(rs.getString("typeConditionnement"));
                    
                    Float poids = rs.getFloat("poidsSachet");
                    produit.setPoidsSachet(rs.wasNull() ? null : poids);
                    
                    produit.setStatutProduit(rs.getString("statutProduit"));
                    
                    produits.add(produit);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits par catégorie:");
            e.printStackTrace();
        }
        
        return produits;
    }

    /**
     * Récupère toutes les catégories distinctes de produits
     * @return Liste des catégories
     */
    public List<String> getToutesLesCategories() {
        List<String> categories = new ArrayList<>();
        
        String sql = "SELECT DISTINCT categorie FROM Produit ORDER BY categorie";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                categories.add(rs.getString("categorie"));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des catégories:");
            e.printStackTrace();
        }
        
        return categories;
    }

    /**
     * Recherche des produits par nom (recherche partielle insensible à la casse)
     * @param motCle Le mot-clé à rechercher dans le nom du produit
     * @return Liste des produits correspondants
     */
    public List<ProduitCatalogue> rechercherProduits(String motCle) {
        List<ProduitCatalogue> produits = new ArrayList<>();
        
        String sql = "SELECT DISTINCT " +
                     "    p.idProduit, " +
                     "    p.idProducteur, " +
                     "    p.nomProduit, " +
                     "    p.categorie, " +
                     "    p.description, " +
                     "    p.bio, " +
                     "    p.label, " +
                     "    p.allergene, " +
                     "    p.origineGeographique, " +
                     "    p.delaiDisponibilite, " +
                     "    c.prixVenteClient, " +
                     "    CASE " +
                     "        WHEN cv.idConditionnement IS NOT NULL THEN 'Vrac' " +
                     "        WHEN cp.idConditionnement IS NOT NULL THEN 'Préconditionné' " +
                     "        ELSE 'N/A' " +
                     "    END AS typeConditionnement, " +
                     "    cp.poidsSachet, " +
                     "    d.statutProduit " +
                     "FROM Produit p " +
                     "JOIN Conditionnement c ON p.idProduit = c.idProduit AND p.idProducteur = c.idProducteur " +
                     "LEFT JOIN ConditionnementVrac cv ON c.idConditionnement = cv.idConditionnement " +
                     "LEFT JOIN ConditionnementPreconditionne cp ON c.idConditionnement = cp.idConditionnement " +
                     "LEFT JOIN ProduitEstDisponible ped ON p.idProduit = ped.idProduit AND p.idProducteur = ped.idProducteur " +
                     "LEFT JOIN Disponibilite d ON ped.idDisponibilite = d.idDisponibilite " +
                     "WHERE UPPER(p.nomProduit) LIKE UPPER(?) AND (d.statutProduit = 'Disponible' OR d.statutProduit IS NULL) " +
                     "ORDER BY p.categorie, p.nomProduit";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + motCle + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProduitCatalogue produit = new ProduitCatalogue();
                    produit.setIdProduit(rs.getString("idProduit"));
                    produit.setIdProducteur(rs.getString("idProducteur"));
                    produit.setNomProduit(rs.getString("nomProduit"));
                    produit.setCategorie(rs.getString("categorie"));
                    produit.setDescription(rs.getString("description"));
                    produit.setBio(rs.getString("bio"));
                    produit.setLabel(rs.getString("label"));
                    produit.setAllergene(rs.getString("allergene"));
                    produit.setOrigineGeographique(rs.getString("origineGeographique"));
                    
                    Integer delai = rs.getInt("delaiDisponibilite");
                    produit.setDelaiDisponibilite(rs.wasNull() ? null : delai);
                    
                    produit.setPrixVenteClient(rs.getFloat("prixVenteClient"));
                    produit.setTypeConditionnement(rs.getString("typeConditionnement"));
                    
                    Float poids = rs.getFloat("poidsSachet");
                    produit.setPoidsSachet(rs.wasNull() ? null : poids);
                    
                    produit.setStatutProduit(rs.getString("statutProduit"));
                    
                    produits.add(produit);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de produits:");
            e.printStackTrace();
        }
        
        return produits;
    }

    /**
     * Récupère un produit spécifique par son ID
     * @param idProduit L'identifiant du produit
     * @param idProducteur L'identifiant du producteur
     * @return Le produit ou null si non trouvé
     */
    public ProduitCatalogue getProduitParId(String idProduit, String idProducteur) {
        String sql = "SELECT DISTINCT " +
                     "    p.idProduit, " +
                     "    p.idProducteur, " +
                     "    p.nomProduit, " +
                     "    p.categorie, " +
                     "    p.description, " +
                     "    p.bio, " +
                     "    p.label, " +
                     "    p.allergene, " +
                     "    p.origineGeographique, " +
                     "    p.delaiDisponibilite, " +
                     "    c.prixVenteClient, " +
                     "    CASE " +
                     "        WHEN cv.idConditionnement IS NOT NULL THEN 'Vrac' " +
                     "        WHEN cp.idConditionnement IS NOT NULL THEN 'Préconditionné' " +
                     "        ELSE 'N/A' " +
                     "    END AS typeConditionnement, " +
                     "    cp.poidsSachet, " +
                     "    d.statutProduit " +
                     "FROM Produit p " +
                     "JOIN Conditionnement c ON p.idProduit = c.idProduit AND p.idProducteur = c.idProducteur " +
                     "LEFT JOIN ConditionnementVrac cv ON c.idConditionnement = cv.idConditionnement " +
                     "LEFT JOIN ConditionnementPreconditionne cp ON c.idConditionnement = cp.idConditionnement " +
                     "LEFT JOIN ProduitEstDisponible ped ON p.idProduit = ped.idProduit AND p.idProducteur = ped.idProducteur " +
                     "LEFT JOIN Disponibilite d ON ped.idDisponibilite = d.idDisponibilite " +
                     "WHERE p.idProduit = ? AND p.idProducteur = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idProduit);
            stmt.setString(2, idProducteur);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ProduitCatalogue produit = new ProduitCatalogue();
                    produit.setIdProduit(rs.getString("idProduit"));
                    produit.setIdProducteur(rs.getString("idProducteur"));
                    produit.setNomProduit(rs.getString("nomProduit"));
                    produit.setCategorie(rs.getString("categorie"));
                    produit.setDescription(rs.getString("description"));
                    produit.setBio(rs.getString("bio"));
                    produit.setLabel(rs.getString("label"));
                    produit.setAllergene(rs.getString("allergene"));
                    produit.setOrigineGeographique(rs.getString("origineGeographique"));
                    
                    Integer delai = rs.getInt("delaiDisponibilite");
                    produit.setDelaiDisponibilite(rs.wasNull() ? null : delai);
                    
                    produit.setPrixVenteClient(rs.getFloat("prixVenteClient"));
                    produit.setTypeConditionnement(rs.getString("typeConditionnement"));
                    
                    Float poids = rs.getFloat("poidsSachet");
                    produit.setPoidsSachet(rs.wasNull() ? null : poids);
                    
                    produit.setStatutProduit(rs.getString("statutProduit"));
                    
                    return produit;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du produit:");
            e.printStackTrace();
        }
        
        return null;
    }
}
