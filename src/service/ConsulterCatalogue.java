package service;

import java.util.List;

import dao.CatagueDAO;
import model.ProduitCatalogue;

/**
 * Service de consultation du catalogue de produits
 * Gère la logique métier pour l'affichage et le filtrage des produits
 */
public class ConsulterCatalogue {
    
    private CatagueDAO catalogueDAO;
    
    public ConsulterCatalogue(){
        this.catalogueDAO = new CatagueDAO();
    }
    
    /**
     * Récupère tous les produits disponibles du catalogue
     * @return Liste des produits disponibles
     */
    public List<ProduitCatalogue> afficherCatalogue() {
        System.out.println("=== Consultation du catalogue ===");
        List<ProduitCatalogue> produits = catalogueDAO.getTousLesProduits();
        
        if (produits.isEmpty()) {
            System.out.println("Aucun produit disponible dans le catalogue.");
        } else {
            System.out.println("Nombre de produits disponibles: " + produits.size());
        }
        
        return produits;
    }
    
    /**
     * Filtre les produits par catégorie
     * @param categorie La catégorie à filtrer
     * @return Liste des produits de la catégorie
     */
    public List<ProduitCatalogue> filtrerParCategorie(String categorie) {
        System.out.println("=== Filtrage par catégorie: " + categorie + " ===");
        
        if (categorie == null || categorie.trim().isEmpty()) {
            System.err.println("Erreur: Catégorie invalide");
            return afficherCatalogue(); // Retourne tous les produits par défaut
        }
        
        List<ProduitCatalogue> produits = catalogueDAO.getProduitsParCategorie(categorie);
        System.out.println("Nombre de produits dans la catégorie '" + categorie + "': " + produits.size());
        
        return produits;
    }
    
    /**
     * Recherche des produits par mot-clé dans le nom
     * @param motCle Le mot-clé à rechercher
     * @return Liste des produits correspondants
     */
    public List<ProduitCatalogue> rechercherProduits(String motCle) {
        System.out.println("=== Recherche de produits: " + motCle + " ===");
        
        if (motCle == null || motCle.trim().isEmpty()) {
            System.err.println("Erreur: Mot-clé de recherche vide");
            return afficherCatalogue(); // Retourne tous les produits par défaut
        }
        
        List<ProduitCatalogue> produits = catalogueDAO.rechercherProduits(motCle);
        System.out.println("Nombre de produits trouvés: " + produits.size());
        
        return produits;
    }
    
    /**
     * Récupère toutes les catégories disponibles
     * @return Liste des catégories
     */
    public List<String> getCategories() {
        return catalogueDAO.getToutesLesCategories();
    }
    
    /**
     * Récupère les détails d'un produit spécifique
     * @param idProduit L'identifiant du produit
     * @param idProducteur L'identifiant du producteur
     * @return Le produit ou null si non trouvé
     */
    public ProduitCatalogue getDetailsProduit(String idProduit, String idProducteur) {
        System.out.println("=== Consultation détails produit: " + idProduit + " ===");
        
        if (idProduit == null || idProducteur == null) {
            System.err.println("Erreur: Identifiants invalides");
            return null;
        }
        
        ProduitCatalogue produit = catalogueDAO.getProduitParId(idProduit, idProducteur);
        
        if (produit == null) {
            System.out.println("Produit non trouvé");
        } else {
            System.out.println("Produit trouvé: " + produit.getNomProduit());
        }
        
        return produit;
    }
    
    /**
     * Affiche un résumé formaté d'un produit
     * @param produit Le produit à afficher
     */
    public void afficherDetailsProduit(ProduitCatalogue produit) {
        if (produit == null) {
            System.out.println("Aucun produit à afficher");
            return;
        }
        
        System.out.println("\n========================================");
        System.out.println("Nom: " + produit.getNomProduit());
        System.out.println("Catégorie: " + produit.getCategorie());
        System.out.println("Prix: " + produit.getPrixVenteClient() + " €");
        System.out.println("Type: " + produit.getTypeConditionnement());
        
        if (produit.getPoidsSachet() != null) {
            System.out.println("Poids: " + produit.getPoidsSachet() + " kg");
        }
        
        if (produit.getBio() != null) {
            System.out.println("Bio: " + produit.getBio());
        }
        
        if (produit.getLabel() != null) {
            System.out.println("Label: " + produit.getLabel());
        }
        
        if (produit.getOrigineGeographique() != null) {
            System.out.println("Origine: " + produit.getOrigineGeographique());
        }
        
        // if (produit.getDescription() != null && !produit.getDescription().isEmpty()) {
        //     System.out.println("Description: " + produit.getDescription());
        // }
        
        // if (produit.getAllergene() != null && !produit.getAllergene().isEmpty()) {
        //     System.out.println("Allergènes: " + produit.getAllergene());
        // }
        
        if (produit.getDelaiDisponibilite() != null && produit.getDelaiDisponibilite() > 0) {
            System.out.println("Délai de disponibilité: " + produit.getDelaiDisponibilite() + " jours");
        }
        
        System.out.println("Statut: " + produit.getStatutProduit());
        System.out.println("========================================\n");
    }
}
