package strategy.paiement;

import java.math.BigDecimal;

public interface CalculPaiement {
    PlanPaiement calculer(BigDecimal prixCours);

    String nom();
}
