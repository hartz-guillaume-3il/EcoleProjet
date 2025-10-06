package strategy.paiement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class PaiementUneFois implements CalculPaiement {
    @Override
    public PlanPaiement calculer(BigDecimal prixCours) {
        return new PlanPaiement(List.of(new Echeance(LocalDate.now().plusDays(0), prixCours)), prixCours);
    }
    @Override public String nom() { return "Paiement en une fois"; }
}
