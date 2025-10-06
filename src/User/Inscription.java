package User;

import state.Creneau;
import java.time.Instant;

public final class Inscription {
    private final String nomEnfant;
    private final int age;
    private final String cours;
    private final String horaireIso;
    private final Instant horodatage = Instant.now();

    public Inscription(String nomEnfant, int age, Creneau c) {
        this.nomEnfant = nomEnfant;
        this.age = age;
        this.cours = c.getNomCours();
        this.horaireIso = c.getHoraire().toString();
    }

    public String getNomEnfant() { return nomEnfant; }
    public int getAge() { return age; }
    public String getCours() { return cours; }
    public String getHoraireIso() { return horaireIso; }
    public Instant getHorodatage() { return horodatage; }
}
