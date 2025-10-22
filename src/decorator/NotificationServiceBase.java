package decorator;

import observer.Notification;

public class NotificationServiceBase implements NotificationService {
    @Override
    public void envoyer(Notification notification) {
        System.out.println("[BASE] " + notification.getType() + " - " + notification.getMessage());
    }
}
