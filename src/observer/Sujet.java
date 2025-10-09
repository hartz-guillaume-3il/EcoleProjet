package observer;

public interface Sujet {
    void abonner(Observateur o);

    void desabonner(Observateur o);

    void notifierTous(Notification notification);
}
