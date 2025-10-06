package strategy.paiement;

import java.math.BigDecimal;

/** Stratégie de calcul des échéanciers. */
public interface CalculPaiement {
    PlanPaiement calculer(BigDecimal prixCours);
    String nom();
}
