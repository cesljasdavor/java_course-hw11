package hr.fer.zemris.java.hw11.jnotepadpp.local;

import javax.swing.JLabel;

/**
 * Apstraktan razred koji nasljeđuje razred {@link JLabel} te implementira
 * sučelje {@link ILocalizationListener}. Cilj ovog razreda je da se prilikom
 * promjene lokalizacije odradi dio koda koji će promijeniti tekst same labele
 * (taj dio koda biti će napisan upravo u metodi
 * {@link ILocalizationListener#localizationChanged()}).
 * 
 * @see ILocalizationListener
 * 
 * @author Davor Češljaš
 */
public abstract class LocalizedLabel extends JLabel implements ILocalizationListener {

	/** Konstanta koja se koristi prilikom serijalizacije objekata */
	private static final long serialVersionUID = 1L;

}
