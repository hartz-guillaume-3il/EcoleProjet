package decorator;

import observer.Notification;

/** Classe abstraite qui enveloppe un service existant. */
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
