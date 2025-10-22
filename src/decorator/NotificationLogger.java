package decorator;

import observer.Notification;

import java.time.Instant;

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
