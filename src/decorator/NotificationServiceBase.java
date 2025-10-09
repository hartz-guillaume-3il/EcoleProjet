package decorator;

import observer.Notification;

/**
 * Service de notification de base (console).
 */
public class NotificationServiceBase implements NotificationService {
    @Override
    public void envoyer(Notification notification) {
        System.out.println("[BASE] " + notification.getType() + " - " + notification.getMessage());
    }
}
