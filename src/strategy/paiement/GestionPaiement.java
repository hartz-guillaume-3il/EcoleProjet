package strategy.paiement;

import java.math.BigDecimal;


public final class GestionPaiement {
    private CalculPaiement strategie;

    public GestionPaiement(CalculPaiement strategie) {
        this.strategie = strategie;
    }

    public void changerStrategie(CalculPaiement strategie) {
        this.strategie = strategie;
    }

    public PlanPaiement genererPlan(BigDecimal prixCours) {
        return strategie.calculer(prixCours);
    }

    public String strategieActive() {
        return strategie.nom();
    }
}
