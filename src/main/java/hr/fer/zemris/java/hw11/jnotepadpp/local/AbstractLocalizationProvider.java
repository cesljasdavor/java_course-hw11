package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.ArrayList;
import java.util.List;

/**
 * Apstraktni razred koji implementira sučelje {@link ILocalizationProvider} i
 * time postaje subjekt u oblikovnom obrascu
 * <a href = "https://en.wikipedia.org/wiki/Observer_pattern">promatrač</a>.
 * Ovaj razred implementira sve akcije koje jedan subjekt mora imati, a to su
 * {@link #addLocalizationListener(ILocalizationListener)} i
 * {@link #removeLocalizationListener(ILocalizationListener)}. Ovaj razred
 * također implementira metodu {@link #fire()} preko koje se obavještavaju svi
 * promatrači ovog subjekta. Ono što ovaj razred ne implementira je metoda
 * {@link #getString(String)} jer ovaj razred "ne zna" prevoditi.
 * 
 * @see ILocalizationListener
 * @see ILocalizationProvider
 * 
 * @author Davor Češljaš
 */
public abstract class AbstractLocalizationProvider implements ILocalizationProvider {

	/**
	 * Članska varijabla koja predstavlja {@link List}u svih prijavljenih
	 * promatrača na ovaj subjekt
	 */
	List<ILocalizationListener> listeners = new ArrayList<>();

	@Override
	public void addLocalizationListener(ILocalizationListener l) {
		listeners = new ArrayList<>(listeners);
		listeners.add(l);
	}

	@Override
	public void removeLocalizationListener(ILocalizationListener l) {
		listeners = new ArrayList<>(listeners);
		listeners.remove(l);
	}

	/**
	 * Metoda koja se koristi za obavještavanje promatrača da je došlo do
	 * promijene u lokalizaciji.
	 */
	public void fire() {
		for (ILocalizationListener l : listeners) {
			l.localizationChanged();
		}
	}
}
