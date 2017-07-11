package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Razred koji predstavlja konkretan subjekt u oblikovnom obrascu
 * <a href = "https://en.wikipedia.org/wiki/Observer_pattern">promatrač</a>, a
 * koji ujedino implementira metodu {@link #getString(String)} kojom se za
 * predani ključ dobiva prijevod na trenutnom jeziku. Ovaj razred logiku
 * registracije i deregistracije te obavještavanja promatrača ne reimplementira,
 * odnosno implementacija ostaje ista onoj unutar apstraktnog razreda
 * {@link AbstractLocalizationProvider}. Razred također koristi oblikovni
 * obrazac
 * <a href = "https://en.wikipedia.org/wiki/Singleton_pattern">jedinstveni
 * objekt</a> te u duhu njega stvara isključivo jedan primjerak ovog razreda
 * kojem se može pristupiti sa metodom {@link #getInstance()}, a kojeg se može
 * modificirati metodom {@link #setLanguage(String)}
 * 
 * @see AbstractLocalizationProvider
 * 
 * @author Davor Češljaš
 */
public class LocalizationProvider extends AbstractLocalizationProvider {

	/**
	 * Konstanta koja predstavlja jedini primjerak ovog razreda, a u duhu
	 * oblikovnog obrasca
	 * <a href = "https://en.wikipedia.org/wiki/Singleton_pattern">jedinstveni
	 * objekt</a>
	 */
	private static final LocalizationProvider INSTANCE = new LocalizationProvider();

	/** Konstanta koja predstavlja naziv i putanja do lokalizacijskog paketa */
	private static final String BUNDLE_NAME = "hr.fer.zemris.java.hw11.jnotepadpp.local.prijevodi";

	/** Konstanta koja predstavlja defaultni jezik za ovaj razred */
	private static final String DEFAULT_LANGUAGE = "en";

	/** Članska varijabla koja predstavlja trenutni jezik */
	private String language;

	/** Članska varijabla koja predstavlja trenutne lokalizacijske postavke */
	private Locale locale;

	/**
	 * Članska varijabla koja je primjerak razreda {@link ResourceBundle}, a
	 * koja se koristi za dobivanje prijevoda iz lokalizacijskih datoteka sa
	 * trenutnom okalizacijom {@link #locale}
	 */
	private ResourceBundle bundle;

	/**
	 * Privatni konstruktor koji se poziva točno jednom, prilikom "spomena" na
	 * ovaj razred te koja postavlja lokalizaciju na {@value #DEFAULT_LANGUAGE}
	 */
	public LocalizationProvider() {
		setLanguage(DEFAULT_LANGUAGE);
	}

	@Override
	public String getString(String key) {
		return bundle.getString(key);
	}

	/**
	 * Metoda koja dohvaća jezik trenutne lokalizacije
	 *
	 * @return jezik trenutne lokalizacije
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Metoda koja dohvaća primjerak razreda {@link Locale} koji je trenutno
	 * aktivan unutar ovog objekta
	 *
	 * @return primjerak razreda {@link Locale} koji je trenutno aktivan unutar
	 *         ovog objekta
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Metoda koja postavlja trenutni jezika na jezik predan kao parametar
	 * <b>language</b>. Ovime se mijenja lokalizacija i poziva metoda
	 * {@link #fire()} čime se obavještavaju svi registrirani promatrači da je
	 * došlo do promjene lokalizacije
	 *
	 * @param language
	 *            novi jezik ovog razreda/objekta
	 */
	public void setLanguage(String language) {
		this.language = language;
		this.locale = Locale.forLanguageTag(language);
		bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);

		fire();
	}

	/**
	 * Metoda koja dohvaća jedini, statički primjerak ovog razreda ovog razreda
	 *
	 * @return jedini, statički primjerak ovog razreda ovog razreda
	 */
	public static LocalizationProvider getInstance() {
		return INSTANCE;
	}
}
