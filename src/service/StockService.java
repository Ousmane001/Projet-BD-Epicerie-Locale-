package service;

import dao.LotDAO;
import dao.StockDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDate;

public class StockService {
    private final String idStock;
    private final LotDAO lotDAO = new LotDAO();
    private final StockDAO stockDAO = new StockDAO();

    public StockService(String idStock){
        this.idStock = idStock;
    }

    public boolean stockSuffisantProduit(String idProduit, String idProducteur, int quantiteDemande, LocalDate dateEstimeeLivraison, String typeConditionnement, Connection conn) {
        try {
            ResultSet lots = stockDAO.getLotsOrdonnesByIdStock(idStock, conn);
            int quantiteRestante = quantiteDemande;
            while (lots != null && lots.next()) {
                if (quantiteRestante <= 0) break;
                String idLot = lots.getString("idLot");

                String typeLot = lotDAO.getConditionnementByIdLot(idLot, conn);
                if (!typeLot.equalsIgnoreCase(typeConditionnement)) continue;

                LocalDate datePeremption = lotDAO.getDatePeremptionByIdLot(idLot, conn);
                if (dateEstimeeLivraison != null && datePeremption != null && dateEstimeeLivraison.isAfter(datePeremption)) {
                    continue;
                }

                if ("Preconditionne".equalsIgnoreCase(typeConditionnement)) {
                    Integer qteDispo = stockDAO.getQuantitePreconditionneLot(idLot, conn);
                    if (qteDispo == null) continue;
                    int prise = Math.min(qteDispo, quantiteRestante);
                    quantiteRestante -= prise;
                } else if ("Vrac".equalsIgnoreCase(typeConditionnement)) {
                    Double qteDispo = stockDAO.getQuantiteVracLot(idLot, conn);
                    if (qteDispo == null) continue;
                    int prise = (int) Math.min(qteDispo, (double) quantiteRestante);
                    quantiteRestante -= prise;
                }
            }
            return quantiteRestante <= 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
