package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

import hr.fer.zemris.java.hw11.jnotepadpp.local.AbstractLocalizedAction;
import hr.fer.zemris.java.hw11.jnotepadpp.local.FormLocalizationProvider;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizationProvider;

/**
 * Razred koji predstavlja program {@value #APP_TITLE}. Ovaj program predstavlja
 * jednostavni uređivač teksta, unutar ojeg se može raditi sa više dokumenata
 * odjednom. Program nudi relativno velik broj operacije, ovdje su izlistane
 * samo neke:
 * <ul>
 * <li>Nova datoteka- otvara novu praznu datoteku</li>
 * <li>Otvori- otvara datoteku na disku</li>
 * <li>Spremi/ Spremi kao - sprema datoteku na trenutnu ili specifičnu lokaciju
 * u memoriji</li>
 * <li>Kopiraj, Izreži, Zalijepi</li>
 * <li>Promjena jezika</li>
 * <li>Micanje dupliciranih redaka</li> ...
 * </ul>
 * 
 * Unutar programa za prikaz više prozora korisiti se primjerak razreda
 * {@link JTabbedPane}, a svaki tab je modeliran razredom {@link TabPanel}. Za
 * internacionalizaciju se koristi {@link LocalizationProvider} te primjerak
 * razreda {@link FormLocalizationProvider}.
 * 
 * @see JTabbedPane
 * @see TabPanel
 * @see LocalizationProvider
 * @see FormLocalizationProvider
 * 
 * @author Davor Češljaš
 */
public class JNotepadpp extends JFrame {

	/** Konstanta koja se koristi prilikom serijalizacije objekata */
	private static final long serialVersionUID = 1L;

	/** Konstanta koja predstavlja naziv aplikacije */
	public static final String APP_TITLE = "JNotepad++";

	/**
	 * Konstanta koja predstavlja opciju uzlaznog sortiranja u metodu
	 * {@link #changeLines(short)}
	 */
	public static final short ASCENDING_SORT_OPTION = (short) 0;

	/**
	 * Konstanta koja predstavlja opciju silaznog sortiranja u metodu
	 * {@link #changeLinfes(short)}
	 */
	public static final short DESCENDING_SORT_OPTION = (short) 1;

	/**
	 * Konstanta koja predstavlja opciju micanja duplikata u metodi
	 * {@link #changeLinfes(short)}
	 */
	public static final short UNIQUE_OPTION = (short) 2;

	/**
	 * Članska varijabla koja predstavlja komponentu unutar koje su smješteni
	 * svi prozori ovog programa
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Članska varijabla koja predstavlja {@link List}u svih reprezentanata
	 * prozora u ovom programu
	 */
	private List<TabPanel> tabPanels;

	/**
	 * Člasnka varijabla koja je primjerak razreda
	 * {@link FormLocalizationProvider}, a koja se koristi za lokalizaciju
	 * čitavog programa
	 */
	private FormLocalizationProvider flp = new FormLocalizationProvider(LocalizationProvider.getInstance(), this);

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar ovog
	 * konstruktora stvara se grafičko korisničko sučelje ovog programa. Također
	 * namještaju se svi promatrači na sve promjene potrebne za normalan rad ovog
	 * programa, pozivom privatne pomoćne metode {@link #initGUI()}.
	 */
	public JNotepadpp() {
		tabPanels = new ArrayList<>();

		// mijenjaj poslije u do_nothing
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle(APP_TITLE);
		setSize(1150, 700);
		setLocationRelativeTo(null);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

		initGUI();

		createBlankDocument();
	}

	/**
	 * Metoda koja dohvaća primjerak razreda {@link JTabbedPane} zadužen za
	 * razmještaj prozora unutar ovog programa
	 *
	 * @return primjerak razreda {@link JTabbedPane} zadužen za razmještaj
	 *         prozora unutar ovog programa
	 */
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	/**
	 * Pomoćna metoda zadužena za inicijalizaciju grafičkog korisničkog sučelja
	 * ovog programa. Unutar ove metode i metoda koje ova metoda poziva,
	 * namještaju se svi gumbi, alatne trake, izborničke trake ... Svim tim
	 * komponentama namještaju se pripadni promatrači kako bi program radio kako je
	 * očekivano
	 */
	private void initGUI() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		configureTabbedPane(cp);

		configureMenuBar();

		JToolBar toolBar = new JToolBar();
		cp.add(toolBar, BorderLayout.NORTH);

		configureToolBar(toolBar);
	}

	/**
	 * Pomoćna metoda koja stvara i namješta člansku varijablu
	 * {@link #tabbedPane}
	 *
	 * @param cp
	 *            primjerak razreda {@link Container} dobiven pozivom
	 *            {@link JFrame#getContentPane()}
	 */
	private void configureTabbedPane(Container cp) {
		tabbedPane = new JTabbedPane();
		cp.add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				TabInfo tabInfo = getTabInfo(tabbedPane.getSelectedIndex());
				if (tabInfo == null) {
					return;
				}

				setNewTitle(tabInfo);
			}
		});
	}

	/**
	 * Pomoćna metoda koja namješta izborničku traku. Izbornička traka
	 * konfigurira se pozivima {@link #configureFilesMenu(JMenuBar)},
	 * {@link #configureToolsMenu(JMenuBar)} te
	 * {@link #configureLanguagesMenu(JMenuBar)}
	 */
	private void configureMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
	
		configureFilesMenu(menuBar);
	
		configureToolsMenu(menuBar);
	
		configureLanguagesMenu(menuBar);
	}

	/**
	 * Pomoćna metoda koja namješta izbornik "Datoteka".
	 *
	 * @param menuBar
	 *            primjerak razreda {@link JMenuBar} kojem se dodaje izbornik
	 *            "datoteka"
	 */
	private void configureFilesMenu(JMenuBar menuBar) {
		JMenu fileMenu = new JMenu(flp.getString("file"));
		menuBar.add(fileMenu);
	
		flp.addLocalizationListener(() -> fileMenu.setText(flp.getString("file")));
	
		fileMenu.add(new JMenuItem(createNewDocument));
		fileMenu.add(new JMenuItem(openDocument));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(saveDocument));
		fileMenu.add(new JMenuItem(saveAsDocument));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(copyAction));
		fileMenu.add(new JMenuItem(cutAction));
		fileMenu.add(new JMenuItem(pasteAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(statisticsInfo));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(closeCurrentTab));
		fileMenu.add(new JMenuItem(exitApplication));
	
	}

	/**
	 * Pomoćna metoda koja namješta izbornik "Jezici".
	 *
	 * @param menuBar
	 *            primjerak razreda {@link JMenuBar} kojem se dodaje izbornik
	 *            "Jezici"
	 */
	private void configureLanguagesMenu(JMenuBar menuBar) {
		JMenu languagesMenu = new JMenu(flp.getString("languages"));
		menuBar.add(languagesMenu);
	
		flp.addLocalizationListener(() -> languagesMenu.setText(flp.getString("languages")));
	
		languagesMenu.add(new JMenuItem(croatianLanguageAction));
		languagesMenu.add(new JMenuItem(englishLanguageAction));
		languagesMenu.add(new JMenuItem(germanLanguageAction));
	}

	/**
	 * Pomoćna metoda koja namješta izbornik "Alati".
	 *
	 * @param menuBar
	 *            primjerak razreda {@link JMenuBar} kojem se dodaje izbornik
	 *            "Alati"
	 */
	private void configureToolsMenu(JMenuBar menuBar) {
		JMenu toolsMenu = new JMenu(flp.getString("tools"));
		menuBar.add(toolsMenu);
	
		JMenu changeCaseMenu = new JMenu(flp.getString("changeCase"));
		toolsMenu.add(changeCaseMenu);
	
		JMenuItem toUppercase = new JMenuItem(toUppercaseAction);
		changeCaseMenu.add(toUppercase);
		JMenuItem toLowercase = new JMenuItem(toLowercaseAction);
		changeCaseMenu.add(toLowercase);
		JMenuItem invertCase = new JMenuItem(invertCaseAction);
		changeCaseMenu.add(invertCase);
	
		JMenu sortMenu = new JMenu(flp.getString("sort"));
		toolsMenu.add(sortMenu);
	
		JMenuItem ascending = new JMenuItem(ascendingSortAction);
		sortMenu.add(ascending);
		JMenuItem descending = new JMenuItem(descendingSortAction);
		sortMenu.add(descending);
	
		JMenuItem unique = new JMenuItem(uniqueAction);
		toolsMenu.add(unique);
	
		flp.addLocalizationListener(() -> {
			toolsMenu.setText(flp.getString("tools"));
			changeCaseMenu.setText(flp.getString("changeCase"));
			sortMenu.setText(flp.getString("sort"));
		});
	
		tabbedPane.addChangeListener(new ChangeListener() {
	
			@Override
			public void stateChanged(ChangeEvent e) {
				setToolsEnabled(false);
	
				JTextArea textArea = getCurrentTextArea();
				if (textArea == null) {
					return;
				}
	
				textArea.addCaretListener(new CaretListener() {
	
					@Override
					public void caretUpdate(CaretEvent e) {
						Caret caret = textArea.getCaret();
	
						if (caret.getDot() != caret.getMark()) {
							setToolsEnabled(true);
							return;
						}
	
						setToolsEnabled(false);
					}
				});
			}
	
			private void setToolsEnabled(boolean b) {
				invertCase.setEnabled(b);
				toLowercase.setEnabled(b);
				toUppercase.setEnabled(b);
				ascending.setEnabled(b);
				descending.setEnabled(b);
				unique.setEnabled(b);
			}
		});
	}

	/**
	 * Pomoćna metoda koja namješta alatnu traku programa.
	 *
	 * @param toolBar
	 *            primjerak razreda {@link JToolBar} koji predstavlja alatnu
	 *            traku koju je potrebno namjestiti
	 */
	private void configureToolBar(JToolBar toolBar) {
		toolBar.add(new JButton(createNewDocument));
		toolBar.add(new JButton(openDocument));
		toolBar.addSeparator();
		toolBar.add(new JButton(saveDocument));
		toolBar.add(new JButton(saveAsDocument));
		toolBar.addSeparator();
		toolBar.add(new JButton(copyAction));
		toolBar.add(new JButton(cutAction));
		toolBar.add(new JButton(pasteAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(statisticsInfo));
		toolBar.addSeparator();
		toolBar.add(new JButton(closeCurrentTab));
		toolBar.add(new JButton(exitApplication));
	}

	/**
	 * Pomoćna metoda (koja se koristi unutar paketa) za namještanje naziva
	 * prozora. Što se ispisuje kao naziv prozora ovisi o tome koji je prozor u
	 * trenutno aktivan
	 *
	 * @param tabInfo
	 *            primjerka razreda {@link TabInfo} iz kojeg se dohvaća naziv
	 *            trenutno aktivne datoteke
	 */
	void setNewTitle(TabInfo tabInfo) {
		JNotepadpp.this.setTitle(tabInfo.getTabFilePath().toAbsolutePath().toString() + " - " + APP_TITLE);
	}

	/**
	 * Pomoćna metoda koja iz članske varijable {@link #tabPanels} dohvaća model
	 * koji je primjerak razreda {@link TabInfo}, a koji se nalazi spremljen
	 * unutar primjerka razreda {@link TabPanel} na lokaciji <b>index</b>
	 *
	 * @param index
	 *            pozicija na kojem se nalazi traženi primjerak razreda
	 *            {@link TabPanel} (time i njegov {@link TabInfo})
	 * @return pripradni model za primjerak razreda {@link TabPanel} na poziciji
	 *         <b>index</b> ili null ukoliko ne postoji pozicija <b>index</b>
	 *         unutar {@link #tabPanels}
	 */
	private TabInfo getTabInfo(int index) {
		if (index < 0 || index > tabPanels.size() - 1) {
			return null;
		}
		return tabPanels.get(index).getTabInfo();
	}

	/**
	 * Pomoćna metoda koja dohvaća primjerak razreda {@link JTextArea} spremljen
	 * unutar trenutnog prozora (onoga na poziciji
	 * {@link JTabbedPane#getSelectedIndex()})
	 *
	 * @return rimjerak razreda {@link JTextArea} spremljen unutar trenutnog
	 *         prozora (onoga na poziciji
	 *         {@link JTabbedPane#getSelectedIndex()})
	 */
	private JTextArea getCurrentTextArea() {
		int index = tabbedPane.getSelectedIndex();
		if (index < 0 || index > tabPanels.size() - 1) {
			return null;
		}
		return tabPanels.get(index).getTextArea();
	}

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za novog prozora
	 * sa defaultnim imenom "neimenovano"
	 */
	private Action createNewDocument = new AbstractLocalizedAction("createNewDocument", flp) {

		private static final long serialVersionUID = 1L;
		{
			flp.addLocalizationListener(this);

			// ovo nije lokalizirano
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			createBlankDocument();
		}

	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za otvaranje
	 * postojeće datoteke na disku unutar novog prozora.
	 */
	private Action openDocument = new AbstractLocalizedAction("openDocument", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(flp.getString("openFileDialogTitle"));
			if (fc.showOpenDialog(JNotepadpp.this) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			Path filePath = fc.getSelectedFile().toPath();

			addNewTab(filePath, true, false);
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za spremanje
	 * dokumenta na njegovu trenutnu lokaciju na disku ili na specifičnu
	 * lokaciju ukoliko dokument nije na disku
	 */
	private Action saveDocument = new AbstractLocalizedAction("saveDocument", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			save(tabbedPane.getSelectedIndex());
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za spremanje
	 * dokumenta na specifičnu lokaciju na disku
	 */
	private Action saveAsDocument = new AbstractLocalizedAction("saveAsDocument", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift S"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			saveAs();
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za kopiranje
	 * označenog dijela teksta u međuspremnik
	 */
	private Action copyAction = new AbstractLocalizedAction("copy", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			performAction(DefaultEditorKit.copyAction, e);
		}

	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za izrezivanje
	 * označenog dijela teksta u međuspremnik
	 */
	private Action cutAction = new AbstractLocalizedAction("cut", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			performAction(DefaultEditorKit.cutAction, e);
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za ljepljenje
	 * teksta iz međuspremnika u dokument od lokacije pokazivača
	 */
	private Action pasteAction = new AbstractLocalizedAction("paste", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			performAction(DefaultEditorKit.pasteAction, e);
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za zatvaranje
	 * trenutno aktivnog prozora. Prilikom zatvaranja, a ukoliko je došlo do
	 * promjena na dokumentu, korisnika se pita želi li spremiti promjene
	 */
	private Action closeCurrentTab = new AbstractLocalizedAction("closeCurrentTab", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control W"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			closeDocumentAt(tabbedPane.getSelectedIndex());
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za ispisivanje
	 * statističkih informacija u novom dialogu. Informacije sadrže broj
	 * znakova, broj nepraznih znakova te broj linija trenutnog dokumenta
	 */
	private Action statisticsInfo = new AbstractLocalizedAction("statisticsInfo", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control B"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_B);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea textArea = getCurrentTextArea();
			if (textArea == null) {
				return;
			}

			String text = textArea.getText();

			int length = text.length();

			String nonEmptyString = new String(text);
			nonEmptyString = nonEmptyString.replaceAll("\\s", "");
			int numberOfNonEmpty = nonEmptyString.length();

			int numberOfLines = textArea.getLineCount();

			JOptionPane.showMessageDialog(
					JNotepadpp.this,
					String.format(flp.getString("statisticsInfo.message"), length, numberOfNonEmpty, numberOfLines),
					flp.getString("statisticsInfo.title"), 
					JOptionPane.INFORMATION_MESSAGE);
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za zatvaranje
	 * aplikacije. Za svaki dokument na kojem su nastale promjene, a koje nisu
	 * spremljene, aplikacija će pitati da li korisnik želi spremiti promjene
	 */
	private Action exitApplication = new AbstractLocalizedAction("exit", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift X"));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			exit();
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za postavljanje
	 * jezika aplikacije na hrvatski
	 */
	private Action croatianLanguageAction = new AbstractLocalizedAction("croatianLanguage", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			LocalizationProvider.getInstance().setLanguage("hr");
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za postavljanje
	 * jezika aplikacije na engleski
	 */
	private Action englishLanguageAction = new AbstractLocalizedAction("englishLanguage", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			LocalizationProvider.getInstance().setLanguage("en");
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za postavljanje
	 * jezika aplikacije na njemački
	 */
	private Action germanLanguageAction = new AbstractLocalizedAction("germanLanguage", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			LocalizationProvider.getInstance().setLanguage("de");
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za promjenu
	 * označenog dijela teksta u velika slova
	 */
	private Action toUppercaseAction = new AbstractLocalizedAction("toUppercase", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
		}

		private UnaryOperator<String> textChanger = String::toUpperCase;

		@Override
		public void actionPerformed(ActionEvent e) {
			changeText(textChanger);
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za promjenu
	 * označenog dijela teksta u mala slova
	 */
	private Action toLowercaseAction = new AbstractLocalizedAction("toLowercase", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
		}

		private UnaryOperator<String> textChanger = String::toLowerCase;

		@Override
		public void actionPerformed(ActionEvent e) {
			changeText(textChanger);
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za invertiranje
	 * veličine slova označenog dijela teksta
	 */
	private Action invertCaseAction = new AbstractLocalizedAction("invertCase", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
		}

		private UnaryOperator<String> textChanger = str -> {
			StringBuilder sb = new StringBuilder(str.length());
			for (char c : str.toCharArray()) {
				if (Character.isUpperCase(c)) {
					sb.append(Character.toLowerCase(c));
				} else {
					sb.append(Character.toUpperCase(c));
				}
			}
			return sb.toString();
		};

		@Override
		public void actionPerformed(ActionEvent e) {
			changeText(textChanger);
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za uzlazno
	 * sortiranje označenih redaka. Redak je označen ako je barem barem jedan
	 * znak ili pokazivač teksta u tom redku
	 */
	private Action ascendingSortAction = new AbstractLocalizedAction("ascSort", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			changeLines(ASCENDING_SORT_OPTION);
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za silazno
	 * sortiranje označenih redaka. Redak je označen ako je barem barem jedan
	 * znak ili pokazivač teksta u tom redku
	 */
	private Action descendingSortAction = new AbstractLocalizedAction("descSort", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			changeLines(DESCENDING_SORT_OPTION);
		}
	};

	/**
	 * Privatni primjerak razreda koji nasljeđuje
	 * {@link AbstractLocalizedAction}. Ova akcija koristi se za micanje
	 * dupliciranih redaka unutra označenih redaka. Redak je označen ako je
	 * barem barem jedan znak ili pokazivač teksta u tom redku
	 */
	private Action uniqueAction = new AbstractLocalizedAction("unique", flp) {

		private static final long serialVersionUID = 1L;

		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			changeLines(UNIQUE_OPTION);
		}
	};

	/**
	 * Pomoćna metoda koja stvara novi prazni dokument koji nije nigdje
	 * spremljen u memoriji
	 */
	private void createBlankDocument() {
		Path filePath = Paths.get(flp.getString("defaultFileName"));
		addNewTab(filePath, false, true);
	}

	/**
	 * Pomoćna metoda koja stvara novi prozor preko argumenata koji su mu
	 * predani.
	 *
	 * @param filePath
	 *            putanja do datoteke u memoriji iz koje treba učitati dokument
	 * @param inMemory
	 *            zastavica koja ukazuje na to je li dokument spremljena u
	 *            memoriju
	 * @param changed
	 *            zastavica koja ukazuje na to je li dokument mijenjan
	 */
	private void addNewTab(Path filePath, boolean inMemory, boolean changed) {
		int index = tabbedPane.getTabCount();
	
		TabPanel tabPanel = TabPanel.createNewTabPanel(this, flp, filePath, inMemory, changed, index);
		if (tabPanel == null) {
			return;
		}
	
		tabPanels.add(tabPanel);
	
		tabbedPane.insertTab(null, null, tabPanel, null, index);
		tabbedPane.setTabComponentAt(index, tabPanel.getTabTitle());
	
		// postavi da se taj sada vidi
		tabbedPane.setSelectedIndex(index);
	}

	/**
	 * Pomoćna metoda koja se koristi za spremanje dokumenta čija je pozicija
	 * primjerka razreda {@link TabPanel} u {@link List}i {@link #tabPanels}
	 * <b>index</b>. Ukoliko ovaj dokument postoji u memoriji piše se na njegovu
	 * lokaciju direktnim pozivom metode {@link #writeToFile(Path)}, a inače se
	 * poziva {@link #saveAs()} metoda
	 *
	 * @param index
	 *            pozicija unutar {@link List} {@link #tabPanels} s koje se
	 *            dohvaća dokument
	 */
	private void save(int index) {
		TabInfo tabInfo = getTabInfo(index);
		// jer metode writeToFile i saveAs rade s tim indexom
		tabbedPane.setSelectedIndex(index);
		if (tabInfo.isInMemory()) {
			writeToFile(tabInfo.getTabFilePath());
		} else {
			saveAs();
		}
	}

	/**
	 * Pomoćna metoda koja se koristi za stvaranje datoteke ukoliko ona ne
	 * postoji te pisanje sadržaja dokumenta trenutnog prozora u tu datoteku
	 *
	 * @param filePath
	 *            putanja na kojoj se treba opcionalno stvoriti datoteka i u nju
	 *            spremiti sadržaj dokumenta trenutnog prozora
	 */
	private void writeToFile(Path filePath) {
		try {
			String toWrite = getCurrentTextArea().getText();
			Files.write(filePath, toWrite.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
					JNotepadpp.this,
					String.format(flp.getString("saveAsError.message"),filePath.toAbsolutePath()),
					flp.getString("saveAsError.title"), 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// promjena moguća tek kada je sve izvedene korektno
		TabInfo info = getTabInfo(tabbedPane.getSelectedIndex());
		info.setTabFilePath(filePath);
		info.setChanged(false);
		info.setInMemory(true);
	}

	/**
	 * Pomoćna metoda koja korisnika pita za lokaciju i naziv pod kojim se
	 * trenutni dokument treba spremiti te nakon toga sa tom putanjom poziva
	 * metodu {@link #writeToFile(Path)} s kojom stvara datoteku i u nju upisuje
	 * sadržaj dokumenta
	 */
	private void saveAs() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(flp.getString("saveAsDialogTitle"));
	
		if (fc.showSaveDialog(JNotepadpp.this) != JFileChooser.APPROVE_OPTION) {
			JOptionPane.showMessageDialog(
					JNotepadpp.this, 
					flp.getString("saveAsInfo.message"),
					flp.getString("saveAsInfo.title"), 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	
		Path filePath = fc.getSelectedFile().toPath();
		if (Files.isRegularFile(filePath)) {
			// putanja predstavlja datoteku na disku
			if (JOptionPane.showConfirmDialog(JNotepadpp.this, flp.getString("overwrite.message"),
					flp.getString("overwrite.title"), JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
				return;
			}
		}
	
		writeToFile(filePath);
	}

	/**
	 * Pomoćna metoda koja iz mape akcije koja se dobije pozivom
	 * {@link JTextArea#getActionMap()} poziva akciju pod ključem
	 * <b>actionKey</b> i nad njom zove
	 * {@link Action#actionPerformed(ActionEvent)}
	 *
	 * @param actionKey
	 *            ključ pod kojim se nalazi akcija koja se treba izvesti
	 * @param e
	 *            primjerak razreda {@link ActionEvent} koji predstavlja događaj
	 *            koji se dogodio
	 */
	private void performAction(String actionKey, ActionEvent e) {
		JTextArea textArea = getCurrentTextArea();
		if (textArea == null) {
			return;
		}
	
		textArea.getActionMap().get(actionKey).actionPerformed(e);
	}

	/**
	 * Pomoćna metoda koja zatvara dokument na poziciji <b>index</b> unutar
	 * članske varijable {@link #tabbedPane}. Metoda također uklanja primjerak
	 * razreda {@link TabPanel} koji je predstavljao prozor na toj poziciji iz
	 * {@link List}e {@link #tabPanels}. Potom se poziva metoda
	 * {@link #changeTabInfoIndexes()}
	 *
	 * @param index
	 *            pozicija s koje se unutar {@link #tabbedPane}, odnosno
	 *            {@link #tabPanels} uklanjaju elementi
	 */
	void closeDocumentAt(int index) {
		TabInfo tabInfo = getTabInfo(index);
		if (tabInfo.isChanged() && !userWantsToClose(tabInfo)) {
			return;
		}
		tabbedPane.remove(index);
	
		tabPanels.remove(index);
		// nakon skidanja mijenjaj indekse
		changeTabInfoIndexes();
	
		if (tabbedPane.getTabCount() == 0) {
			createBlankDocument();
		}
	}

	/**
	 * Metoda koja ponovo izračunava pozicije prozora po uklanjanju prozora u
	 * metodi {@link #closeDocumentAt(int)}.Pozicije se ažuriraju u svakom od
	 * primjeraka razreda {@link TabInfo} koje sadrže prozori modelirani
	 * razredima {@link TabPanel} spremljeni unutar {@link #tabPanels}
	 */
	private void changeTabInfoIndexes() {
		int index = 0;
	
		for (TabPanel tabPanel : tabPanels) {
			tabPanel.getTabInfo().setTabIndex(index++);
		}
	}

	/**
	 * Pomoćna metoda koja od korisnika, preko dialoga, dohvaća informaciju želi
	 * li zatvoriti prozor s modelom <b>tabInfo</b>. Te ukoliko želi, želi li
	 * pri tom spremiti promjene na trenutnu lokaciju u memoriji (ukoliko ona
	 * postoji)
	 *
	 * @param tabInfo
	 *            model prozora koji se gasi
	 * @return <code>true</code> ako je korisnik stisnuo "Yes" ili "No",
	 *         <code>false</code> inače
	 */
	private boolean userWantsToClose(TabInfo tabInfo) {
		int status = JOptionPane.showConfirmDialog(
				this,
				String.format(flp.getString("userWantsToClose.message"), tabInfo.getTabFilePath().getFileName()),
				flp.getString("userWantsToClose.title"), 
				JOptionPane.YES_NO_CANCEL_OPTION);
	
		if (status == JOptionPane.CANCEL_OPTION) {
			return false;
		}
	
		if (status == JOptionPane.YES_OPTION) {
			save(tabInfo.getTabIndex());
		}
		return true;
	}

	/**
	 * Pomoćna metoda koja zatvara aplikaciju, ispitujući pritom korisnika želi
	 * li ugasiti svaki od pojedinih ažuriranih i nespremljenih dokumenata.
	 */
	private void exit() {
		for (int i = 0, len = tabbedPane.getTabCount(); i < len; i++) {
			TabInfo tabInfo = getTabInfo(i);
			if (tabInfo.isChanged()) {
				int status = JOptionPane.showConfirmDialog(
						JNotepadpp.this,
						String.format(flp.getString("exitApplication.message"), tabInfo.getTabFilePath().getFileName()),
						flp.getString("exitApplication.title"), 
						JOptionPane.YES_NO_CANCEL_OPTION);
	
				if (status == JOptionPane.CANCEL_OPTION) {
					return;
				}
	
				if (status == JOptionPane.YES_OPTION) {
					save(i);
				}
			}
		}
		// JFrame će se ovime ugasiti time će se maknuti JTabbedPane, a time i
		// sve ostalo
		dispose();
	}

	/**
	 * Pomoćna metoda koja mijenja označeni tekst koristeći predanu strategiju
	 * koja je primjerak razreda koji implementira sučelje {@link UnaryOperator}
	 * <b>textChanger</b>
	 *
	 * @param textChanger
	 *            strategija koja je primjerak razreda koji implementira sučelje
	 *            {@link UnaryOperator} <b>textChanger</b>, a kojom se mijenja
	 *            označeni tekst
	 * 
	 */
	private void changeText(UnaryOperator<String> textChanger) {
		JTextArea textArea = getCurrentTextArea();
		if (textArea == null) {
			return;
		}
	
		Caret caret = textArea.getCaret();
		int dotPosition = caret.getDot();
		int markPosition = caret.getMark();
	
		int offset = Math.min(dotPosition, markPosition);
		int length = Math.abs(dotPosition - markPosition);
	
		Document document = textArea.getDocument();
	
		try {
			String toChange = document.getText(offset, length);
			document.remove(offset, length);
			document.insertString(offset, textChanger.apply(toChange), null);
		} catch (BadLocationException ignorable) {
		}
	}

	/**
	 * Pomoćna metoda koja se koristi za promijenu linija ovisno o parametru
	 * <b>changeOption</b>, koji može biti {@value #ASCENDING_SORT_OPTION},
	 * {@value #DESCENDING_SORT_OPTION} ili {@value #UNIQUE_OPTION}
	 *
	 * @param changeOption
	 *            parametar o kojem ovisi kako će se označene linije
	 *            promijeniti. Može biti {@value #ASCENDING_SORT_OPTION},
	 *            {@value #DESCENDING_SORT_OPTION} ili {@value #UNIQUE_OPTION}
	 */
	private void changeLines(short changeOption) {
		JTextArea textArea = getCurrentTextArea();
		if (textArea == null) {
			return;
		}

		Caret caret = textArea.getCaret();
		int dotPosition = caret.getDot();
		int markPosition = caret.getMark();

		try {
			int dotLine = textArea.getLineOfOffset(dotPosition);
			int markLine = textArea.getLineOfOffset(markPosition);
			int from = textArea.getLineStartOffset(Math.min(dotLine, markLine));
			int to = textArea.getLineEndOffset(Math.max(dotLine, markLine));
			Document document = textArea.getDocument();
			int len = to - from;
			String text = document.getText(from, len);

			String changed;
			if (changeOption == UNIQUE_OPTION) {
				changed = uniqueLines(text);
			} else {
				changed = sortLines(changeOption == ASCENDING_SORT_OPTION, text);
			}

			document.remove(from, len);
			document.insertString(from, changed, null);
		} catch (BadLocationException e) {
			return;
		}
	}

	/**
	 * Pomoćna metoda koja iz označenih linija predstavljenih primjerkom razreda
	 * {@link String} <b>text</b> miče duplikate
	 * 
	 * @param text
	 *            označene linije iz kojih se miču duplikati
	 * @return primjerak razreda {@link String} koji predstavlja označene linije
	 *         iz kojih su maknuti duplikati
	 */
	private String uniqueLines(String text) {
		// ovo će maknuti duplikate
		Set<String> toSort = new LinkedHashSet<>(Arrays.asList(text.split("\n")));

		return toSort.stream().collect(Collectors.joining("\n", "", "\n"));
	}

	/**
	 * Pomoćna metoda koja označene linije predstavljenih primjerkom razreda
	 * {@link String} <b>text</b> sortira u redoslijedu ovisno o
	 * <b>ascending</b> parametru
	 * 
	 * @param ascending
	 *            ukoliko je parametar <code>true</code> metoda sortira uzlazno,
	 *            inače metoda sortira slizano
	 * @param text
	 *            označene linije iz koje se sortiraju
	 * @return primjerak razreda {@link String} koji predstavlja označene linije
	 *         sortirane ovisno o parametru <b>ascending</b>
	 */
	private String sortLines(boolean ascending, String text) {
		List<String> toSort = new ArrayList<>(Arrays.asList(text.split("\n")));

		Collator collator = Collator.getInstance(LocalizationProvider.getInstance().getLocale());
		Comparator<String> comparator = (word1, word2) -> collator.compare(word1, word2);

		toSort.sort(ascending ? comparator : comparator.reversed());

		return toSort.stream().collect(Collectors.joining("\n", "", "\n"));
	}

	/**
	 * Metoda od koje započinje izvođenje programa.
	 *
	 * @param args
	 *            argumenti naredbenog redka. Ovdje se ne koriste
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new JNotepadpp().setVisible(true));
	}

}
