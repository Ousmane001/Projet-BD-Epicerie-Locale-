package service;

import java.time.LocalDate;

import dao.CommandeDAO;

public class ClotureCommande{
    // fonctions/methodes necessaire a la transaction :
    private CommandeDAO fonctionsCommande = new CommandeDAO();
    public ClotureCommande(String idCommande){

        // on recupere avant tout le mode de recuperation de la commande : 
        String modeRecupCmd = fonctionsCommande.recupModeRecuperation(idCommande);

        // on recupere egalement les mode de payement et les statut actuelle de la commande :
        String modePayement = fonctionsCommande.recupModePayement(idCommande);
        String statutCommande = fonctionsCommande.recupStatutCommande(idCommande);

        // on facture le client : 
        if(!fonctionsCommande.encaisseCommande(idCommande)){
            throw new IllegalStateException("Payement refusé");
        }

        // en fonction du mode de recuperation : 
        if(modeRecupCmd == "Boutique"){
            // implementation de la contrainte textuelle : << pas de statut "En preparation" pour une commande à recuperer en Boutique: 
            if(statutCommande == "En préparation" || statutCommande != "Prête"){
                throw new IllegalStateException("Erreur: Vous ne pouvez pas avoir le statue de la commande " + idCommande + " En preparation avec comme mode de récuperation en Boutique ...");
            }

            
            // pour le moment on se contente de changer le statut de la commande : 
            fonctionsCommande.changeStatutCommande(idCommande, "Récupérée/Livrée");

            // on enregistre la date de recuperation de la commande 
            fonctionsCommande.enregistreDateReceptionCommande(idCommande);
            

            // pas de changement du stock car la commande est deja en statut "prete", donc on a deja retirer  du stock lors de la validation de la cmd
        }else{
            // on change l'etat de la commande 
            fonctionsCommande.changeStatutCommande(idCommande, "En préparation");

            // on recupere les informations de livraison pour calculer les frais de livraison ainsi que la date estimée de livraison
            String idModeRecuperationDomicile = fonctionsCommande.recupIdInfoLivraison(idCommande);
            
            // on effectue les calculs nécessaires pour des fin d'affichages:
            int fraisDeLivraison = fonctionsCommande.calculFraisDeLivraison(idModeRecuperationDomicile);
            LocalDate dateEstimeeDeLivraison = fonctionsCommande.calculDateEstimeeDeLivraison(idModeRecuperationDomicile);

            // la commande n'etant pas encore en statut prete, alors aucun changement de stock
            // ça sera declanché un employe de l'epicerie une fois la commande declarée comme prete !
        }

        
        
        

    }
}