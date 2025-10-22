package observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class CentreNotifications implements Sujet {
    private static final CentreNotifications INSTANCE = new CentreNotifications();

    public static CentreNotifications getInstance() {
        return INSTANCE;
    }

    private final List<Observateur> observateurs = new CopyOnWriteArrayList<>();

    private CentreNotifications() {
    }

    @Override
    public void abonner(Observateur o) {
        if (o != null) observateurs.add(o);
    }

    @Override
    public void desabonner(Observateur o) {
        observateurs.remove(o);
    }

    @Override
    public void notifierTous(Notification n) {
        for (Observateur o : observateurs) o.mettreAJour(n);
    }
}
