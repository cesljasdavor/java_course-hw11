package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Razred koji nasljeđuje razred {@link LocalizationProviderBridge}. Uz to ovaj
 * razred nudi konstruktor
 * {@link #FormLocalizationProvider(ILocalizationProvider, JFrame)} unutar kojeg
 * se registrira primjerak apstraktnog razreda {@link WindowAdapter} na predani
 * primjerak razreda {@link JFrame}. Implementacija je ovakva kako bi se
 * prilikom paljenja ,odnosno gašenja prozora mogle pozvati metode
 * {@link LocalizationProviderBridge#connect()}, odnosno
 * {@link LocalizationProviderBridge#disconnect()} i time se ujedino
 * registrirati na promjene lokalizacije, ali i omogućiti otpuštanje svih
 * nativnih resurasa prilikom gašenja prozora
 * 
 * @see LocalizationProviderBridge
 * @see JFrame
 * @see WindowAdapter
 * 
 * @author Davor Češljaš
 */
public class FormLocalizationProvider extends LocalizationProviderBridge {

	/**
	 * Konstruktor koji incijalizira primjerak ovog razreda. Unutar ovog
	 * konstruktora registrira se primjerak apstraktnog razreda
	 * {@link WindowAdapter} na predani primjerak razreda {@link JFrame}
	 * <b>frame</b>. Implementacija je ovakva kako bi se prilikom paljenja
	 * ,odnosno gašenja prozora mogle pozvati metode
	 * {@link LocalizationProviderBridge#connect()}, odnosno
	 * {@link LocalizationProviderBridge#disconnect()} i time se ujedino
	 * registrirati na promjene lokalizacije, ali i omogućiti otpuštanje svih
	 * nativnih resurasa prilikom gašenja prozora
	 *
	 * @param parent
	 *            argument koji se predaje nadređenom konstruktoru
	 *            {@link LocalizationProviderBridge#LocalizationProviderBridge(ILocalizationProvider)}
	 * @param frame
	 *            primjerak razreda {@link JFrame} na koji se registrira
	 *            primjerak apstraktnog razreda {@link WindowAdapter}
	 */
	public FormLocalizationProvider(ILocalizationProvider parent, JFrame frame) {
		super(parent);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				connect();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				disconnect();
			}
		});
	}
}
