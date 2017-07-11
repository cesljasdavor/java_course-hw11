package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import hr.fer.zemris.java.hw11.jnotepadpp.local.FormLocalizationProvider;

/**
 * Razred nasljeđuje razred {@link JPanel}. Primjerci ovog razreda predsavljaju
 * jedan prozor koji se dodaje {@link JTabbedPane}u unutar programa
 * {@link JNotepadpp}. Ovaj razred sadrži:
 * <ul>
 * <li>Naslov, oblikovan razredom {@link TabTitle}</li>
 * <li>Komponentu za uređivanje teksta, oblikovnu razredom
 * {@link JTextArea}</li>
 * <li>Statusnu traku, oblikovanu razredom {@link StatusBar}</li>
 * <li>Model, oblikovan razredom {@link TabInfo}</li>
 * </ul>
 * 
 * Razred sadrži privatni konstruktor koji poziva metoda statička metoda
 * tvornica
 * {@link #createNewTabPanel(JNotepadpp, FormLocalizationProvider, Path, boolean, boolean, int)}.
 * Primjerci ovog razreda ne mogu se direktno izraditi zbog mogućnosti pogreške
 * prilikom učitavanja teksta iz dokumenta, što bi moglo dovesti do
 * nekonzistentnosti prozora (podaci se nisu mogli učitati iz datoteke)
 * 
 * @see TabTitle
 * @see JTextArea
 * @see StatusBar
 * @see TabInfo
 * @see JNotepadpp
 * 
 * @author Davor Češljaš
 * 
 */
public class TabPanel extends JPanel {

	/** Konstanta koja se koristi prilikom serijalizacije objekata */
	private static final long serialVersionUID = 1L;

	/**
	 * Članska varijabla koja predstavlja referencu na sam program
	 * {@link JNotepadpp}. Ova referenca koristi se kako bi se izgradio čitav
	 * sustav jednog prozora.
	 */
	private JNotepadpp jNotepadpp;

	/**
	 * Članska varijabla koja predsavlja komponentu za uređivanje teksta koja se
	 * prikazuje unutar prozora.
	 */
	private JTextArea textArea;

	/**
	 * Članska varijabla koja predstavlja model podataka za jedan prozor unutar
	 * programa {@link JNotepadpp}. Što sve model sadrži napisano je u
	 * dokumentaciji razreda {@link TabInfo}
	 * 
	 * @see TabInfo
	 */
	private TabInfo tabInfo;

	/**
	 * Članska varijabla oblikovana razredom {@link TabTitle} koja predstavlja
	 * naslov jednog prozora u programu {@link JNotepadpp}
	 */
	private TabTitle tabTitle;

	/**
	 * Članska varijabla koja predstavlja statusnu traku. Za sadržaj trake
	 * pogledati dokumentaciju razreda {@link StatusBar}
	 * 
	 * @see StatusBar
	 */
	private StatusBar statusBar;

	/**
	 * Privatni konstruktor koji se koristi za inicijalizaciju ovog razreda.
	 * Konstruktor je privatan zbog mogućnosti pogreške prilikom učitavanja
	 * dokumenta, a što vodi ka nekonzistentnosti programa. Za stvaranje
	 * primjeraka ovog razreda savjetuje se korištenje statičke metode tvornice
	 * {@link #createNewTabPanel(JNotepadpp, FormLocalizationProvider, Path, boolean, boolean, int)}
	 *
	 * @param jNotepadpp
	 *            referencu na sam program {@link JNotepadpp}
	 * @param flp
	 *            primjerak razreda {@link FormLocalizationProvider} koji se
	 *            koristi unutar programa {@link JNotepadpp} za
	 *            internacionalizaciju
	 * @param filePath
	 *            putanje do dokumenta koji je potrebno prikazati i moći
	 *            uređivati u prozoru
	 * @param inMemory
	 *            zastavica koja ukazuje na to je li datoteka u memoriji
	 * @param changed
	 *            zastavica koja ukazuje na to je li datoteka mijenjana
	 * @param index
	 *            pozicija prozora unutar {@link JNotepadpp} programa
	 * @throws IOException
	 *             Ukoliko nije moguće učitati daoteku, jer ona ne postoji ili
	 *             korisnik nema dozvolu čitanja i pisanja u tu datoteku
	 */
	private TabPanel(JNotepadpp jNotepadpp, FormLocalizationProvider flp, Path filePath, boolean inMemory,
			boolean changed, int index) throws IOException {
		this.jNotepadpp = jNotepadpp;

		setLayout(new BorderLayout());

		initGUI(flp, filePath, inMemory, changed, index);
	}

	/**
	 * Pomoćna metoda koja namješta grafičko korisničko sučelje ove komponente.
	 * Metoda također na temelju predanih parametara stvara primjerke razreda
	 * {@link JTextArea}, {@link TabInfo}, {@link TabTitle} te
	 * {@link StatusBar}, koji se koriste unutar prozora
	 *
	 * @param flp
	 *            primjerak razreda {@link FormLocalizationProvider} koji se
	 *            koristi unutar programa {@link JNotepadpp} za
	 *            internacionalizaciju
	 * @param filePath
	 *            putanje do dokumenta koji je potrebno prikazati i moći
	 *            uređivati u prozoru
	 * @param inMemory
	 *            zastavica koja ukazuje na to je li datoteka u memoriji
	 * @param changed
	 *            zastavica koja ukazuje na to je li datoteka mijenjana
	 * @param index
	 *            pozicija prozora unutar {@link JNotepadpp} programa
	 * @throws IOException
	 *             Ukoliko nije moguće učitati daoteku, jer ona ne postoji ili
	 *             korisnik nema dozvolu čitanja i pisanja u tu datoteku
	 */
	private void initGUI(FormLocalizationProvider flp, Path filePath, boolean inMemory, boolean changed, int index)
			throws IOException {
		textArea = new JTextArea();

		add(new JScrollPane(textArea), BorderLayout.CENTER);

		setUpStatusBar(flp);
		add(statusBar, BorderLayout.SOUTH);

		if (inMemory) {
			loadText(filePath);
		}

		tabInfo = new TabInfo(index, filePath, changed, inMemory);
		tabInfo.addChangeListener(e -> jNotepadpp.setNewTitle(tabInfo));
		// namjesti vezu između tabInfo i pripadne textAreae
		setupTextChangeListener();

		tabTitle = new TabTitle(this, filePath, jNotepadpp);
	}

	/**
	 * Pomoćna metoda koja namješta člansku varijablu {@link #statusBar}
	 *
	 * @param flp
	 *            primjerak razreda {@link FormLocalizationProvider} koji se
	 *            koristi unutar programa {@link JNotepadpp} za
	 *            internacionalizaciju
	 */
	private void setUpStatusBar(FormLocalizationProvider flp) {
		Clock clock = new Clock();
		jNotepadpp.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				clock.interrupt();
			}
		});

		this.statusBar = new StatusBar(flp, textArea, clock);
	}

	/**
	 * Pomoćna metoda koja učitava tekst iz dokumenta u memoriji i prikazuje ga
	 * unutar članske varijable {@link #textArea}
	 *
	 * @param filePath
	 *            putanje do dokumenta koji je potrebno prikazati i moći
	 *            uređivati u prozoru
	 * @throws IOException
	 *             Ukoliko nije moguće učitati daoteku, jer ona ne postoji ili
	 *             korisnik nema dozvolu čitanja i pisanja u tu datoteku
	 */
	private void loadText(Path filePath) throws IOException {
		textArea.setText(new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8));
	}

	/**
	 * Pomoćna metoda koja registrira i namješta sve promatrače na promjene unutar
	 * članske varijable {@link #textArea}
	 */
	private void setupTextChangeListener() {
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			private boolean currentChangedStatus;

			@Override
			public void removeUpdate(DocumentEvent e) {
				notifyTabInfo();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				notifyTabInfo();
			}

			private void notifyTabInfo() {
				if (!currentChangedStatus) {
					tabInfo.setChanged(true);
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
	}

	/**
	 * Metoda koja dohvaća primjerak razreda {@link JTextArea} spremljen unutar
	 * ovog primjerka razreda {@link TabPanel}
	 *
	 * @return primjerak razreda {@link JTextArea} spremljen unutar ovog
	 *         primjerka razreda {@link TabPanel}
	 */
	public JTextArea getTextArea() {
		return textArea;
	}

	/**
	 * Metoda koja dohvaća model podataka ovog razred predstavljen primjerkom
	 * razreda {@link TabInfo}
	 *
	 * @return model podataka ovog razred predstavljen primjerkom razreda
	 *         {@link TabInfo}
	 */
	public TabInfo getTabInfo() {
		return tabInfo;
	}

	/**
	 * Metoda koja dohvaća naslov ovog prozora koji je modeliran razredom
	 * {@link TabTitle}
	 *
	 * @return naslov ovog prozora koji je modeliran razredom {@link TabTitle}
	 */
	public TabTitle getTabTitle() {
		return tabTitle;
	}

	/**
	 * Statička metoda tvornica primjeraka razreda {@link TabPanel}. Metoda
	 * stvara primjerak razreda na temelju svih predanih parametar, pozivajući
	 * privatni konstruktor
	 * {@link #TabPanel(JNotepadpp, FormLocalizationProvider, Path, boolean, boolean, int)}.
	 * Ukoliko taj konstruktor ne baci {@link IOException} stvoreni primjerak
	 * razreda se vraća kroz povratnu vrijednost. U suprotnom vraća se <code>null</code>
	 * 
	 * @param jNotepadpp
	 *            referencu na sam program {@link JNotepadpp}
	 * @param flp
	 *            primjerak razreda {@link FormLocalizationProvider} koji se
	 *            koristi unutar programa {@link JNotepadpp} za
	 *            internacionalizaciju
	 * @param filePath
	 *            putanje do dokumenta koji je potrebno prikazati i moći
	 *            uređivati u prozoru
	 * @param inMemory
	 *            zastavica koja ukazuje na to je li datoteka u memoriji
	 * @param changed
	 *            zastavica koja ukazuje na to je li datoteka mijenjana
	 * @param index
	 *            pozicija prozora unutar {@link JNotepadpp} programa
	 * @return novi primjerak razreda {@link TabPanel} ukoliko je moguće
	 *         otvoriti dokument predstavljen sa putanjom <b>filePath</b> za
	 *         čitanje i pisanje. Ukoliko to nije moguće metoda vraća
	 *         <code>null</code>
	 */
	public static TabPanel createNewTabPanel(JNotepadpp jNotepadpp, FormLocalizationProvider flp, Path filePath,
			boolean inMemory, boolean changed, int index) {
		try {
			return new TabPanel(jNotepadpp, flp, filePath, inMemory, changed, index);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(
					jNotepadpp,
					String.format(flp.getString("openFileError.message"), 
					filePath.toAbsolutePath().toString()),
					flp.getString("openFileError.title"), 
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
}
