package strategy.paiement;

import java.math.BigDecimal;
import java.util.List;

public record PlanPaiement(List<Echeance> echeances, BigDecimal total) {
}
