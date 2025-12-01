package service;

import dao.LotDAO;
import dao.StockDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

public class StockService {
    private final String idStock;
    private final LotDAO lotDAO = new LotDAO();
    private final StockDAO stockDAO = new StockDAO();

    public StockService(String idStock){
        this.idStock = idStock;
    }

    public ArrayList<String> stockSuffisantProduit(String idProduit, String idProducteur, double quantiteDemande, LocalDate dateEstimeeLivraison, String typeConditionnement, Connection conn) {
        ArrayList<String> lots_pris = new ArrayList<>();
        try {
            ResultSet lots = stockDAO.getLotsOrdonnesByIdStock(idStock, conn);
            // ArrayList<String> lots_pris = new ArrayList<>();
            // Pour Preconditionne: quantiteDemande représente un nombre d'unités (int)
            // Pour Vrac: quantiteDemande est fourni par l'UI comme un entier (grammes) => convertir en kilos
            boolean isVrac = "Vrac".equalsIgnoreCase(typeConditionnement);
            double quantiteRestanteVracKg = 0.0;
            int quantiteRestantePre = 0;
            if (isVrac) {
                quantiteRestanteVracKg = quantiteDemande; // convertir gram -> kg
            } else {
                quantiteRestantePre = (int) quantiteDemande;
            }
            while (lots != null && lots.next()) {
                // Arrêter si la demande a été satisfaite
                if (isVrac) {
                    if (quantiteRestanteVracKg <= 0.0) break;
                } else {
                    if (quantiteRestantePre <= 0) break;
                }
                String idLot = lots.getString("idLot");

                String typeLot = lotDAO.getConditionnementByIdLot(idLot, conn);
                if (typeLot == null || !typeLot.equalsIgnoreCase(typeConditionnement)) continue;

                LocalDate datePeremption = lotDAO.getDatePeremptionByIdLot(idLot, conn);
                if (dateEstimeeLivraison != null && datePeremption != null && dateEstimeeLivraison.isAfter(datePeremption)) {
                    continue;
                }

                if ("Preconditionne".equalsIgnoreCase(typeConditionnement)) {
                    Integer qteDispo = stockDAO.getQuantitePreconditionneLot(idLot, conn);
                    if (qteDispo == null) continue;
                    int prise = Math.min(qteDispo, quantiteRestantePre);
                    if(prise>0){
                        lots_pris.add(idLot);
                    }
                    quantiteRestantePre -= prise;
                } else if ("Vrac".equalsIgnoreCase(typeConditionnement)) {
                    Double qteDispo = stockDAO.getQuantiteVracLot(idLot, conn);
                    if (qteDispo == null) continue;
                    // qteDispo est en kilos (Double). quantiteRestanteVracKg est en kilos aussi.
                    double priseKg = Math.min(qteDispo, quantiteRestanteVracKg);
                    if(priseKg>0.0){
                        lots_pris.add(idLot);
                    }
                    quantiteRestanteVracKg -= priseKg;
                }
            }
            return lots_pris;
        } catch (Exception e) {
            //return false;
            return lots_pris;
        }
    }
}
