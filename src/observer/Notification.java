package observer;

import java.time.Instant;
import java.util.Map;

public final class Notification {
    private final TypeEvenement type;
    private final String message;
    private final Map<String, String> donnees;
    private final Instant horodatage = Instant.now();

    public Notification(TypeEvenement type, String message, Map<String, String> donnees) {
        this.type = type;
        this.message = message;
        this.donnees = donnees;
    }

    public TypeEvenement getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getDonnees() {
        return donnees;
    }

    public Instant getHorodatage() {
        return horodatage;
    }
}
