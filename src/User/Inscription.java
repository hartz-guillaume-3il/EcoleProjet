package User;

import java.time.Instant;

public final class Inscription {
    private final String nomEnfant;
    private final int age;
    private final String cours;
    private final String horaireIso;
    private final Instant horodatage;

    // créer depuis un créneau
    public Inscription(String nomEnfant, int age, state.Creneau c) {
        this(nomEnfant, age, c.getNomCours(), c.getHoraire().toString(), Instant.now());
    }
    // rechargement depuis fichier
    public Inscription(String nomEnfant, int age, String cours, String horaireIso, Instant ts) {
        this.nomEnfant = nomEnfant; this.age = age; this.cours = cours; this.horaireIso = horaireIso; this.horodatage = ts;
    }

    public String getNomEnfant(){ return nomEnfant; }
    public int getAge(){ return age; }
    public String getCours(){ return cours; }
    public String getHoraireIso(){ return horaireIso; }
    public Instant getHorodatage(){ return horodatage; }
}
