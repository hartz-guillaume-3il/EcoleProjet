package decorator;

import observer.Notification;

/**
 * Composant de base pour l’envoi de notifications.
 */
public interface NotificationService {
    void envoyer(Notification notification);
}
