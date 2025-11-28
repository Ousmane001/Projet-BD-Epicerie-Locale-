import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

LotDAO lotDAO = new LotDAO();
StockDAO stockDAO = new StockDAO();

public class StockService{
    String idStock;

    public StockService(String idStock){
        this.idStock = idStock;
    }

    public boolean stock_suffisant_produit(String idProduit, String idProducteur, int quantiteDemande, LocalDate dateEstimmeLivraison, String typeConditionnement, Connection conn){
        
        ResultSet lots = stockDAO.getLotsOrdonnesByIdStock(idStock, conn);
        int quantiteRestante = quantiteDemande;
        while(lots.next()){
            if(quantiteRestante <= 0){
                break;
            }
            String idLot = lots.getString("idLot");
            String conditionnementLot = lotDAO.getConditionnementByIdLot(idLot, conn);
            if(!conditionnementLot.equals(typeConditionnement)){
                continue   ;
            }

            
                //verifier que la date d'estimme de livraison est avant la date de peremption du lotdatePeremptionLot
            LocalDate datePeremptionLot = lotDAO.getDatePeremptionByIdLot(idLot,conn);
            if(dateEstimmeLivraison != null){
                
                if(dateEstimmeLivraison.compareTo(datePeremptionLot) > 0){
                        continue;
                }
            }
            
                

            
            int quantiteLot = lotDAO.getQuantite(idLot, conn);
            int quantitePrise = Math.min(quantiteLot, quantiteDemande);
            if(quantitePrise > 0){
                quantiteRestante-=quantitePrise;
            
            
        }

    }
    if(quantiteDemande == 0){
        return true;
    }
    return false; 

}
}