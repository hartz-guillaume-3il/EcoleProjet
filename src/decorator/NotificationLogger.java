package decorator;

import observer.Notification;

import java.time.Instant;

/**
 * Ajoute un log horodaté avant chaque envoi.
 */
public final class NotificationLogger extends NotificationDecorator {
    public NotificationLogger(NotificationService service) {
        super(service);
    }

    @Override
    public void envoyer(Notification notification) {
        System.out.println("[LOG] " + Instant.now() + " -> " + notification.getType());
        super.envoyer(notification);
    }
}
