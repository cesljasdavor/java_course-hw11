package hr.fer.zemris.java.hw11.jnotepadpp.local;

import javax.swing.JFrame;

/**
 * Razreda predstavlja dekorator sučelja {@link ILocalizationProvider}. Ovaj
 * razred nudi dvije dodatne metode kojima se upravlja konekcijom
 * {@link #connect()} i {@link #disconnect()}. Kada korisnik pozove metodu
 * {@link #connect()} stavar se novi primjerak razreda
 * {@link ILocalizationListener} koji se registrira dekoriranom primjerku
 * razreda koji implementira sučelje {@link ILocalizationProvider}. Kada se
 * pozove {@link #disconnect()} veza se prekida. Ovaj razred rješava problem
 * unutar kojeg se prilikom gašenja komponente, komponenta ne pokupi od strane
 * sakupljača smeća jer primjerak razreda {@link ILocalizationProvider} još drži
 * referencu na taj objekt (time se npr. prozor ne da ugaisti iako smo pozvali
 * njegovu metodu {@link JFrame#dispose()})
 * 
 * @see ILocalizationListener
 * @see ILocalizationProvider
 * 
 * @author Davor Češljaš
 */
public class LocalizationProviderBridge extends AbstractLocalizationProvider {

	/**
	 * Članska varijabla koja predstavlja zastavicu koja ukazuje na to je li
	 * komponenta povezana ili nije
	 */
	private boolean connected;

	/**
	 * Članska varijabla koja predstavlja primjerak razreda
	 * {@link ILocalizationListener} koji se registrira nad članskom varijablom
	 * {@link #parent}
	 * 
	 * @see ILocalizationListener
	 * 
	 * @author Davor Češljaš
	 */
	private final ILocalizationListener listener;

	/**
	 * Članska varijabla koja predstavlja dekorirani objekt, kojem se delegiraju
	 * svi poslovi koje obavlja jedan primjerak razreda koji implementira
	 * sučelje {@link ILocalizationProvider}, ali se pri tome ostavlja mjesto za
	 * raskidanje veze
	 */
	private ILocalizationProvider parent;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar ovog
	 * razreda pamti se dekorirani objekt predstavljen sučeljem
	 * {@link ILocalizationProvider} <b>parent</b> kojem se delegiraju svi
	 * poslovi tog sučelja
	 *
	 * @param parent
	 *            dekorirani objekt, kojem se delegiraju svi poslovi koje
	 *            obavlja jedan primjerak razreda koji implementira sučelje
	 *            {@link ILocalizationProvider}
	 */
	public LocalizationProviderBridge(ILocalizationProvider parent) {
		this.parent = parent;
		this.listener = () -> fire();
	}

	@Override
	public String getString(String key) {
		return parent.getString(key);
	}

	/**
	 * Metoda koja registrira pripadnu člansku varijablu na predanim dekoriranim
	 * objektom. 
	 */
	public void connect() {
		parent.addLocalizationListener(listener);
		connected = true;
	}

	/**
	 * Metoda koja deregistrira pripadnu člansku varijablu nad predanim dekoriranim
	 * objektom.
	 */
	public void disconnect() {
		parent.removeLocalizationListener(listener);
		connected = false;
	}

	/**
	 * Metoda koja dohvaća vraća je li veza uspostavljena ili nije
	 *
	 * @return <code>true</code> ako je veza uspostavljena, <code>false</code>
	 *         inače
	 */
	public boolean isConnected() {
		return connected;
	}
}
