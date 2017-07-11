package hr.fer.zemris.java.hw11.jnotepadpp.local;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * Apstraktni razred koji nasljeđuje razred {@link AbstractAction} i koji
 * implementira sučelje {@link ILocalizationListener}. Pozivom metode
 * {@link ILocalizationListener#localizationChanged()}, kada se promijeni
 * lokalizacija, mijenja se tekst akcije i njezin kratki opis. Ovaj razred nudi
 * konstruktor koji prima naziv akcije unutar lokalizacijskih datoteka
 * (prijevoda) te primjerak razreda {@link FormLocalizationProvider} koji je
 * zadužen za dohvat prijevoda
 * {@link #AbstractLocalizedAction(String, FormLocalizationProvider)}. Ono što
 * ovaj razred i dalje ne implementira i zašto je apstraktan je metoda
 * {@link AbstractAction#actionPerformed(java.awt.event.ActionEvent)}.
 * 
 * <p>
 * Napomena: Ovaj razred je nepromijenjiv
 * </p>
 * 
 * @see AbstractAction
 * @see FormLocalizationProvider
 * @see ILocalizationListener
 * 
 * @author Davor Češljaš
 */
public abstract class AbstractLocalizedAction extends AbstractAction implements ILocalizationListener {

	/** Konstanta koja se koristi prilikom serijalizacije objekata */
	private static final long serialVersionUID = 1L;

	/**
	 * Konstanta koja se konkatenira na naziv akcije kako bi se dobio naziv
	 * ključa u lokalizacijskoj datoteci pod kojim je spremljen prijevod za
	 * naziv akcije koji se treba ispisati na pogledu ove akcije
	 */
	public static final String NAME = ".name";

	/**
	 * Konstanta koja se konkatenira na naziv akcije kako bi se dobio naziv
	 * ključa u lokalizacijskoj datoteci pod kojim je spremljen prijevod za
	 * kratki opis akcije koji se treba ispisati kao kratki opis ove akcije
	 */
	public static final String SHORT_DESCRIPTION = ".sd";

	/**
	 * Članska varijabla koja predstavlja naziv akcije unutar lokalizacijske
	 * datoteke
	 */
	private String actionName;

	/**
	 * Članska varijabla koja predstavlja referencu na subjekt koji je
	 * implementiran razredom {@link FormLocalizationProvider} i koji pri tome
	 * još može ponoditi prijevod za pojedini ključ
	 */
	private FormLocalizationProvider flp;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar
	 * konstruktora svi predani parametri spremaju se kako bi im se moglo
	 * pristupiti iz ostalih metoda. Ovdje se ovaj promatrač registrira na
	 * slušanje lokalizacijskih promjena predanom parametru <b>flp</b> te se
	 * također inicijalizira početni tekst naziva akcije i kratkog opisa
	 *
	 * @param actionName
	 *            naziv akcije unutar lokalizacijske datoteke
	 * @param flp
	 *            referenca na subjekt koji je implementiran razredom
	 *            {@link FormLocalizationProvider} i koji pri tome još može
	 *            ponoditi prijevod za pojedini ključ
	 */
	public AbstractLocalizedAction(String actionName, FormLocalizationProvider flp) {
		this.actionName = actionName;
		this.flp = flp;
		flp.addLocalizationListener(this);
		changeData();
	}

	@Override
	public void localizationChanged() {
		changeData();
	}

	/**
	 * Pomoćna metoda koja se poziva u knostruktoru i u metodi
	 * {@link #localizationChanged()} kako bi se promijenio trenutni naziv
	 * akcije i njezin kratki opis
	 */
	private void changeData() {
		putValue(Action.NAME, flp.getString(actionName + NAME));
		putValue(Action.SHORT_DESCRIPTION, flp.getString(actionName + SHORT_DESCRIPTION));
	}
}
