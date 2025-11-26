package service;

import dao.LotDAO;
import dao.ConditionnementDAO;
import model.AlertePeremption;

import java.sql.SQLException;
import java.util.List;

public class AlertePeremptionService {

    private LotDAO lotDAO = new LotDAO();
    private ConditionnementDAO condDAO = new ConditionnementDAO();

    // 1. Liste des alertes (calculée à la volée)
    public List<AlertePeremption> genererAlertes() throws SQLException {
        return lotDAO.findLotsPerissables();
    }

    // 2. Appliquer la réduction pour ce produit
    public void appliquerReduction(AlertePeremption alerte) throws SQLException {
        condDAO.appliquerReduction(alerte.getIdProduit(), alerte.getIdProducteur());
    }
}
