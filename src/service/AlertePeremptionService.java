package service;

import dao.LotDAO;
import dao.ConditionnementDAO;
import dao.PerteDAO;
import model.AlertePeremption;

import java.sql.SQLException;
import java.util.List;

public class AlertePeremptionService {

    private final LotDAO lotDAO = new LotDAO();
    private final ConditionnementDAO condDAO = new ConditionnementDAO();
    private final PerteDAO perteDAO = new PerteDAO();

    /**
     * Retourne les alertes de péremption + pertes
     */
    public List<AlertePeremption> getAlertes() throws SQLException {

        List<AlertePeremption> peremption = lotDAO.findLotsPerissables();
        peremption.forEach(a -> a.setTypeAlerte("PEREMPTION"));

        List<AlertePeremption> pertes = perteDAO.getPertesAvecProduit();

        peremption.addAll(pertes);

        return peremption;
    }

    /**
     * Applique une réduction de 30% sur un produit
     */
    public void appliquerReduction(AlertePeremption a) throws SQLException {
        condDAO.appliquerReduction(a.getIdProduit(), a.getIdProducteur());
    }



}