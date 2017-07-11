package hr.fer.zemris.java.hw11.jnotepadpp.local;

/**
 * Sučelje koje se koristi za registraciju na promjene jezika unutar aplikacije.
 * Implementator ovog sučelja dužan je implementirati metode:
 * <ul>
 * <li>{@link #addLocalizationListener(ILocalizationListener)} - registracija
 * zainteresiranih promatrača</li>
 * <li>{@link #removeLocalizationListener(ILocalizationListener)} -
 * deregistracija zainteresiranih promatrača</li>
 * <li>{@link #getString(String)} - dohvat prijevoda za pojedini ključ</li>
 * </ul>
 * 
 * Ovo sučelje predstavlja apstraktnog subjekta unutar oblikovnog obrasca
 * <a href = "https://en.wikipedia.org/wiki/Observer_pattern">promatrač</a>
 * 
 * @see ILocalizationListener
 * 
 * @author Davor Češljaš
 */
public interface ILocalizationProvider {

	/**
	 * Metoda koja registrira promatrača koji je primjerak razreda
	 * {@link ILocalizationListener} <b>l</b>. Ovim činom metoda se obvezuje tog
	 * promatrača obavijestiti za svaku promjenu u lokalizaciji
	 *
	 * @param l
	 *            primjerak razreda {@link ILocalizationListener} koji se
	 *            registrira na promatranje promjena u lokalizaciji
	 */
	void addLocalizationListener(ILocalizationListener l);

	/**
	 * Metoda koja uklanja primjerak razreda {@link ILocalizationListener}
	 * <b>l</b> iz svojih promatrača ukoliko on je on registriran na promatranje
	 * ovog subjekta
	 *
	 * @param l
	 *            primjerak razreda {@link ILocalizationListener} koji se
	 *            uklanja iz promatrača
	 */
	void removeLocalizationListener(ILocalizationListener l);

	/**
	 * Metoda koja dohvaća prijevod za predani ključ koji je primjerak razreda
	 * {@link String}
	 *
	 * @param key
	 *            ključ kojem se traži prijevod
	 * @return prijevod za predani ključ <b>key</b>
	 */
	String getString(String key);
}
