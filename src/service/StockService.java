package service;

import dao.ContenantDAO;
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

    public ArrayList<String> stockSuffisantProduit(
        String idProduit,
        String idProducteur,
        double quantiteDemande,
        LocalDate dateEstimeeLivraison,
        String typeConditionnement,
        Connection conn
) {
    ArrayList<String> lotsPris = new ArrayList<>();

    try {
        ResultSet lots = stockDAO.getLotsOrdonnesByIdStock(idStock, conn);

        boolean isVrac = "Vrac".equalsIgnoreCase(typeConditionnement);
        double qteRestanteVracKg = isVrac ? quantiteDemande : 0.0;
        int qteRestantePre = isVrac ? 0 : (int) quantiteDemande;

        double totalPrisVrac = 0.0;
        int totalPrisPre = 0;

        while (lots != null && lots.next()) {
            if (isVrac && qteRestanteVracKg <= 0) break;
            if (!isVrac && qteRestantePre <= 0) break;

            String idLot = lots.getString("idLot");

            String typeLot = lotDAO.getConditionnementByIdLot(idLot, conn);
            if (typeLot == null || !typeLot.equalsIgnoreCase(typeConditionnement)) continue;

            LocalDate datePeremption = lotDAO.getDatePeremptionByIdLot(idLot, conn);
            if (dateEstimeeLivraison != null && datePeremption != null &&
                dateEstimeeLivraison.isAfter(datePeremption)) {
                continue;
            }

            if (isVrac) {
                Double dispo = stockDAO.getQuantiteVracLot(idLot, conn);
                if (dispo == null) continue;

                double prise = Math.min(dispo, qteRestanteVracKg);
                if (prise > 0) {
                    lotsPris.add(idLot);
                    totalPrisVrac += prise;
                }
                qteRestanteVracKg -= prise;

            } else {
                Integer dispo = stockDAO.getQuantitePreconditionneLot(idLot, conn);
                if (dispo == null) continue;

                int prise = Math.min(dispo, qteRestantePre);
                if (prise > 0) {
                    lotsPris.add(idLot);
                    totalPrisPre += prise;
                }
                qteRestantePre -= prise;
            }

        }

        // Vérification finale : est-ce qu’on a TOUT ?
        boolean suffisant = isVrac
                ? totalPrisVrac >= quantiteDemande
                : totalPrisPre >= (int) quantiteDemande;

        if (!suffisant) {
            // Stock dans les choux, on renvoie une liste VIDE
            return new ArrayList<>();
        }

        return lotsPris;

    } catch (Exception e) {
        // En cas de crash, on renvoie vide, pas des conneries
        return new ArrayList<>();
    }
}

public boolean stocksuffisantContenant(String refContenat, double quantiteDemande, Connection conn){
    try{
        ContenantDAO contenantDAO = new ContenantDAO();
        boolean suffisant = contenantDAO.stocksuffisantContenant(refContenat, quantiteDemande, conn);
        if(suffisant){
            return true;
        }
        return false;
    } catch(Exception e){
        return false;
    }
}
}
