package decorator;

import observer.Notification;

/**
 * Simule un envoi d’e-mail en plus du log.
 */
public final class NotificationEmail extends NotificationDecorator {
    private final String adresseExpediteur = "noreply@cours-soutien.fr";

    public NotificationEmail(NotificationService service) {
        super(service);
    }

    @Override
    public void envoyer(Notification notification) {
        super.envoyer(notification);
        System.out.println("[EMAIL] Envoi depuis " + adresseExpediteur + " : " + notification.getMessage());
    }
}
