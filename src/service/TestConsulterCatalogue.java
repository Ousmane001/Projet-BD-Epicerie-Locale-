package service;

import java.util.List;

import config.DataSourceProvider;
import config.JdbcDriverLoader;
import model.ProduitCatalogue;

/**
 * Classe de test pour vérifier le fonctionnement du service ConsulterCatalogue
 */
public class TestConsulterCatalogue {
    
    public static void main(String[] args) {
        System.out.println("=== TEST DU SERVICE CONSULTER CATALOGUE ===\n");
        
        try {
            // Initialisation de la connexion à la base de données
            new JdbcDriverLoader();
            DataSourceProvider.initConnection();
            
            if (DataSourceProvider.getConnection() == null) {
                System.err.println("Erreur: Impossible d'établir la connexion à la base de données");
                return;
            }
            
            // Création du service
            ConsulterCatalogue service = new ConsulterCatalogue();
            
            // Test 1: Afficher tous les produits
            System.out.println("\n>>> TEST 1: Affichage de tous les produits");
            System.out.println("--------------------------------------------");
            List<ProduitCatalogue> tousLesProduits = service.afficherCatalogue();
            afficherResume(tousLesProduits);
            
            // Test 2: Récupérer les catégories
            System.out.println("\n>>> TEST 2: Liste des catégories disponibles");
            System.out.println("--------------------------------------------");
            List<String> categories = service.getCategories();
            System.out.println("Nombre de catégories: " + categories.size());
            for (String cat : categories) {
                System.out.println("  - " + cat);
            }
            
           
            
            
            
            // Test 5: Affichage détaillé d'un produit (si disponible)
            if (!tousLesProduits.isEmpty()) {
                System.out.println("\n>>> TEST 5: Affichage détaillé du premier produit");
                System.out.println("--------------------------------------------");
                ProduitCatalogue premierProduit = tousLesProduits.get(0);
                service.afficherDetailsProduit(premierProduit);
            }
            
            System.out.println("\n=== TESTS TERMINÉS AVEC SUCCÈS ===");
            
        } catch (Exception e) {
            System.err.println("Erreur lors des tests:");
            e.printStackTrace();
        } finally {
            // Fermeture de la connexion
            DataSourceProvider.closeConnection();
        }
    }
    
    /**
     * Affiche un résumé de la liste de produits
     */
    private static void afficherResume(List<ProduitCatalogue> produits) {
        if (produits.isEmpty()) {
            System.out.println("Aucun produit trouvé");
            return;
        }
        
        System.out.println("Nombre de produits: " + produits.size());
        System.out.println("\nPremiers produits:");
        
        int limite = Math.min(5, produits.size());
        for (int i = 0; i < limite; i++) {
            ProduitCatalogue p = produits.get(i);
            System.out.printf("  %d. %-30s | %-15s | %.2f € | %s\n", 
                i + 1,
                p.getNomProduit(),
                p.getCategorie(),
                p.getPrixVenteClient(),
                p.getTypeConditionnement()
            );
        }
        
        if (produits.size() > 5) {
            System.out.println("  ... et " + (produits.size() - 5) + " autres produits");
        }
    }
}
