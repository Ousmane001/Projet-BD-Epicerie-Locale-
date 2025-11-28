package dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;

public class CommandeDAO {
    
    public CommandeDAO(){
        // constructeur bidon ....
    }

    
    public String recupModeRecuperation(String idCommande){
        return "";
    }

    public String recupModePayement(String idCommande){
        return "";
    }

    public String recupStatutCommande(String idCommande){
        return "";
    }

    public void changeStatutCommande(String idCommande, String nouveau_statut){
        return;
    }
    
    public void enregistreDateReceptionCommande(String idCommande){
        // enregistrement du timestamps comme date de reception de la commande par le client
    }

    public String recupIdInfoLivraison(String idCommande){
        return "";
    }

    public int calculFraisDeLivraison(String idModeDeRecuperationDomicile){
        /*0 à 50 km        → +0 €
50 à 300 km      → +1 €
300 à 2000 km    → +2 €
> 2000 km        → +3 €


2_4_5. et par kilo -> 0.8  1.2  1.5 */
        return 0;
    }

    public LocalDate calculDateEstimeeDeLivraison(String idModeDeRecuperationDomicile){

/*| Zone          | Délai (jours) |
| ------------- | ------------- |
| France        | 2 jours       |
| DOM–TOM       | 5 jours       |
| International | 7 jours       |


| Distance    | + jours |
| ----------- | ------- |
| 0–50 km     | 0       |
| 50–300 km   | 1       |
| 300–2000 km | 2       |
| > 2000 km   | 3       |
*/

        return LocalDate.now();
    }


    public ResultSet getLignesCommandeByIdCommande(String idCommande, java.sql.Connection conn){

        String sql = "SELECT * FROM LigneCommande WHERE idCommande = ?";

        try{
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idCommande);
            ResultSet rs = pstmt.executeQuery();
            return rs;
        } catch (java.sql.SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public Date getDateEstimmeLivraisonByIdCommande(String idCommande, Connection conn){
        String sql = "SELECT dateEstimmeLivraison FROM ModeRecuperationDomicile WHERE idCommande = ?";

        try{
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idCommande);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getDate("dateEstimmeLivraison");
            }
        } catch (java.sql.SQLException e){
            e.printStackTrace();
}
    }
}
