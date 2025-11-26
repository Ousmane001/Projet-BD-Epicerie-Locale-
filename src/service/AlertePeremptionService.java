package service;

import dao.AlertePeremptionDAO;
import dao.ConditionnementDAO;
import dao.LotDAO;
import model.AlertePeremption;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.DataSourceProvider;

public class AlertePeremptionService {

    private LotDAO lotDAO = new LotDAO();
    private AlertePeremptionDAO alerteDAO = new AlertePeremptionDAO();
    private ConditionnementDAO condDAO = new ConditionnementDAO();

    
     // 1. Cherche les lots périssables
     // 2. Crée des alertes en BDD (statut = 'proposee')
    
    public void genererAlertes() throws SQLException {
        Connection conn = DataSourceProvider.getConnection();
        conn.setAutoCommit(false);
        try {
            List<AlertePeremption> alertes = lotDAO.findLotsPerissables();
            for (AlertePeremption a : alertes) {
                alerteDAO.insererAlerte(a);
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    
    // Applique la réduction pour les alertes validées (simple version : toutes les alertes périssables)
    
    public void appliquerReductions() throws SQLException {
        Connection conn = DataSourceProvider.getConnection();
        conn.setAutoCommit(false);
        try {
            List<AlertePeremption> alertes = lotDAO.findLotsPerissables();
            for (AlertePeremption a : alertes) {
                condDAO.appliquerReductionPourProduit(a.getIdProduit(), a.getIdProducteur());
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
