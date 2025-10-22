package decorator;

import observer.Notification;

public abstract class NotificationDecorator implements NotificationService {
    protected final NotificationService service;

    protected NotificationDecorator(NotificationService service) {
        this.service = service;
    }

    @Override
    public void envoyer(Notification notification) {
        service.envoyer(notification);
    }
}
