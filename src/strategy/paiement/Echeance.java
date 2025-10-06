// src/main/java/strategy/paiement/Echeance.java
package strategy.paiement;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Echeance(LocalDate date, BigDecimal montant) {}
