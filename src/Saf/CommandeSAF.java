// package interfaceGraphique;

// import java.sql.SQLException;
// import java.time.LocalDate;
// import java.util.Scanner;

// import service.StockService.StockService;
// import config.DataSourceProvider;

// DataSourceProvider DataSourceProvider = new DataSourceProvider();
// Connection conn = DataSourceProvider.getConnection();

// public class CommandeSAF {
//     String idCommande;


//     public CommandeSAF(){
//         this.idCommande = commandeService.generateId("CM");
//     }

//     public String getIdCommande() {
//         return idCommande;
//     }

//     public void passerCommande(){

//         try{

//         Scanner sc = new Scanner(System.in);
//             int totalCommande = 0;
//             int poidsTotalCommande = 0;
//             boolean continuer = true;
//             StockService stockService = new StockService();
//             System.out.println("Mode de récupération (Domicile / Magasin) : ");
//             String ModeRecuperation = sc.nextLine();
//             LocalDate dateEstimeeLivraison = null;
            
            
//             if(ModeRecuperation.equals("Domicile")){
//                 // Calcul des frais de livraison
//                 int fraisDeLivraison = 0; 
//                 System.out.println("Pays de livraison : ");
//                 String pays = sc.nextLine();
//                 System.out.println("Ville de livraison : ");
//                 String ville = sc.nextLine();
//                 System.out.println("Rue de livraison : ");
//                 String rue = sc.nextLine();
//                 System.out.println("Code postal de livraison : ");
//                 String codePostal = sc.nextLine();

//                 RecupDomService recupDomService = new RecupDomService();
//             }


//             while (continuer) {
//                 //le client veut un produit ou un contenant
//                 System.out.println("type achat (Produit/Contenant) ?");
//                 String type = sc.nextLine();
                
//                 // validation du type
//                 if (!type.equals("Produit") && !type.equals("Contenant")) {
//                     System.out.println("Type invalide. Veuillez entrer 'Produit' ou 'Contenant'.");
//                     continue;
//                 }

                
//                 // Si le client veut un contenant
//                 if(type.equals("Contenant")){
//                     System.out.print("Référence du contenant : ");
//                     String referenceContenant = sc.nextLine();
        
//                     System.out.print("Quantité : ");
//                     int quantite = Integer.parseInt(sc.nextLine());

//                     LigneCommandeContenant commandeContenant = new LigneCommandeContenant(idCommande, referenceContenant, quantite, dateEstimeeLivraison);

//                     commandeContenant.executeTransaction();
                    
                   
//                 } 
                
                
//                 // Si le client veut un produit
//                 else {
//                     System.out.print("ID du produit : ");
//                     String idProduit = sc.nextLine();

//                     System.out.print("Type de conditionnement (vrac / preconditionne / contenant) : ");
//                     String typeConditionnement = sc.nextLine();

//                     System.out.print("Quantité (en kg si vrac): ");
//                     int quantite = Integer.parseInt(sc.nextLine());

//                     LigneCommandeProduit commandeProduit = new LigneCommandeProduit(this.idCommande, idProduit, typeConditionnement, quantite);

//                     if(!commandeProduit.stock_suffisant_produit(dateEstimeeLivraison)){
//                         throw new SQLException("Stock insuffisant pour le produit ID: " + idProduit);  
//                     }

//                     // Ici tu ajoutes la ligne dans ta table ou ta liste
//                     System.out.println("→ Produit ajouté : " + idProduit + " | qte=" + quantite + " | cond=" + typeConditionnement);

//                     commandeProduit.enregistrerLigneCommande();
//                     poidsTotalCommande += commandeProduit.getPoids();
//                     totalCommande += commandeProduit.getSousTotal();
//                 }
            
//             System.out.print("Tu veux ajouter un autre produit ? (o/n) : ");
//             String rep = sc.nextLine();
        
//             if (!rep.equalsIgnoreCase("o")) {
//                 continuer = false;
//             }
//         }

        
//         }
        
//     }

// }

package interfaceGraphique;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import service.StockService.StockService;
import config.DataSourceProvider;

public class CommandeSAF {

    private String idCommande;
    private ArrayList<LigneCommandeProduit> lignesCommandeProduits = new ArrayList<>();
    private ArrayList<CommandeContenant> lignesCommandeContenants = new ArrayList<>();

    public CommandeSAF() {
        this.idCommande = CommandeService.generateId("CM"); // je suppose que ta classe CommandeService existe
    }

    public String getIdCommande() {
        return idCommande;
    }

    public void passerCommande() {

        Scanner sc = new Scanner(System.in);

        int totalCommande = 0;
        int poidsTotalCommande = 0;

        StockService stockService = new StockService();
        LocalDate dateEstimeeLivraison = null;

        try (Connection conn = new DataSourceProvider().getConnection()) {
            conn.setAutoCommit(false); // transaction globale

            System.out.println("Mode de récupération (Domicile / Magasin) : ");
            String modeRecuperation = sc.nextLine().trim();

            if (modeRecuperation.equalsIgnoreCase("Domicile")) {
                System.out.println("Pays de livraison : ");
                String pays = sc.nextLine();
                System.out.println("Ville de livraison : ");
                String ville = sc.nextLine();
                System.out.println("Rue de livraison : ");
                String rue = sc.nextLine();
                System.out.println("Code postal de livraison : ");
                String codePostal = sc.nextLine();


                CommandeDom commandeDom = new CommandeDom(pays, ville, rue, codePostal);
                dateEstimeeLivraison = commandeDom.calculerDateLivraison(); // tu dois implémenter cette méthode
            }

            boolean continuer = true;
            while (continuer) {
                System.out.println("Type achat (Produit/Contenant) ?");
                String type = sc.nextLine().trim();

                if (!type.equalsIgnoreCase("Produit") && !type.equalsIgnoreCase("Contenant")) {
                    System.out.println("Type invalide. Veuillez entrer 'Produit' ou 'Contenant'.");
                    continue;
                }

                if (type.equalsIgnoreCase("Contenant")) {
                    System.out.print("Référence du contenant : ");
                    String referenceContenant = sc.nextLine().trim();

                    System.out.print("Quantité : ");
                    int quantite = Integer.parseInt(sc.nextLine());

                    CommandeContenant commandeContenant = new CommandeContenant(idCommande, referenceContenant, quantite);

                    lignesCommandeContenants.add(commandeContenant);
                    
                    if(!commandeContenant.est_dispo()){
                        throw new SQLException("Stock insuffisant pour le contenant Réf: " + referenceContenant);
                    }



                    System.out.println("→ Contenant ajouté : " + referenceContenant + " | qte=" + quantite);

                } 
                
                else { // Produit
                    System.out.print("ID du produit : ");
                    String idProduit = sc.nextLine().trim();

                    System.out.print("Type de conditionnement (vrac / preconditionne / contenant) : ");
                    String typeConditionnement = sc.nextLine().trim();

                    System.out.print("Quantité (en kg si vrac): ");
                    int quantite = Integer.parseInt(sc.nextLine());

                    LigneCommandeProduit commandeProduit = new LigneCommandeProduit(idCommande, idProduit, typeConditionnement, quantite, dateEstimeeLivraison);
                    lignesCommandeProduits.add(commandeProduit);
                    //commandeProduit.executeTransaction();   

                    //commandeProduit.enregistrerLigneCommande();
                    poidsTotalCommande += commandeProduit.getPoids();
                    totalCommande += commandeProduit.getSousTotal();

                    System.out.println("→ Produit ajouté : " + idProduit + " | qte=" + quantite + " | cond=" + typeConditionnement);
                }

                System.out.print("Tu veux ajouter un autre produit ? (o/n) : ");
                String rep = sc.nextLine().trim();
                if (!rep.equalsIgnoreCase("o")) {
                    continuer = false;
                }
            }

            conn.commit(); // tout est ok, on commit
            System.out.println("Commande passée avec succès ! Total : " + totalCommande + ", poids total : " + poidsTotalCommande);

        } catch (Exception e) {
            System.out.println("Erreur lors de la commande, rollback effectué.");
            // rollback automatique si connexion fermée après exception avec try-with-resources
        } finally {
            sc.close();
        }
    }
}
