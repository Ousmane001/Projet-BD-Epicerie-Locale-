package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.DataSourceProvider;
import dao.LotDAO;
import dao.ConditionnementDAO;
import model.AlertePeremption;

public class AlertePeremptionService {

    private LotDAO lotDAO = new LotDAO();
    private ConditionnementDAO condDAO = new ConditionnementDAO();

    // 1. Liste des alertes (calculée à la volée)
    public List<AlertePeremption> genererAlertes() throws SQLException {
        return lotDAO.findLotsPerissables();
    }

    // 2. Appliquer la réduction pour ce produit
    public void appliquerReduction(AlertePeremption alerte) throws SQLException {
        Connection conn = DataSourceProvider.getConnection();
        int oldIsolation = Connection.TRANSACTION_READ_COMMITTED;
        
        try {
            oldIsolation = conn.getTransactionIsolation();
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            
            condDAO.appliquerReduction(alerte.getIdProduit(), alerte.getIdProducteur());
        } finally {
            try {
                conn.setTransactionIsolation(oldIsolation);
            } catch (SQLException ignore) {}
        }
    }
}
