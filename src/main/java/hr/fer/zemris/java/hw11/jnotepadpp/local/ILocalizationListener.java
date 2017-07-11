package hr.fer.zemris.java.hw11.jnotepadpp.local;

/**
 * Sučelje koje predstavlja apstraktnog promatrača unutar oblikovnog obrasca
 * <a href = "https://en.wikipedia.org/wiki/Observer_pattern">promatrač</a>.
 * Zadaća implementatora ovog sučelja je implementirati što se treba dogoditi
 * kada se promijeni lokalizacija. Tu promjenu potrebno je implementirati kroz
 * jedinu metodu ovog apstraktnog promatrača, a to je
 * {@link #localizationChanged()}. Primjerci ovog razreda mogu se dobiti i
 * korištenjem lambda-izraza budući je potrebno implementirati samo jednu
 * metodu, te se time ovo sučelje može svrstati u funkcijska sučelja
 * 
 * @see ILocalizationProvider
 * 
 * @author Davor Češljaš
 */
public interface ILocalizationListener {

	/**
	 * Metoda koju je dužan pozvati primjerak razreda koji implementira sučelje
	 * {@link ILocalizationProvider} ukoliko se primjerak koji implementira ovo
	 * sučelje registrira na njega. Ovo je također metoda u kojoj je potrebno
	 * implementirati logiku koju e potrebno izvesti ukoliko se dogodi promjena
	 * lokalizacije
	 */
	void localizationChanged();
}
