package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Razred koji predstavlja naslov koji se prikazuje svakom pojedinom prozoru
 * unutar programa {@link JNotepadpp}. Naslov se sastoji od imena dokumenta,
 * slike predstavljene sa primjerkom razreda {@link ImageIcon}, koja ukazuje na
 * to je li dokument mijenjan ({@link TabInfo#GREEN_DISC} ili
 * {@link TabInfo#RED_DISC}) te gumba za zatvaranje prozora.
 * 
 * @see JTabbedPane
 * @see JNotepadpp
 * @see TabInfo
 * 
 * @author Davor Češljaš
 */
public class TabTitle extends JPanel {

	/** Konstanta koja se koristi prilikom serijalizacije objekata */
	private static final long serialVersionUID = 1L;

	/**
	 * Konstanta koja predstavlja fiksni razmak između svakog pojedinog elementa
	 * naslova (slika, naslov, gumb).
	 */
	static final int FIX_DISTANCE = 5;

	/**
	 * Članska varijabla koja je referenca na prozor predstavljen primjerkom
	 * razreda {@link TabPanel}, čiji je ovo naslov.
	 */
	private TabPanel tabPanel;

	/**
	 * Članska varijabla koja predstavlja putanju do dokumenta u memoriji čiji
	 * je ovo naslov
	 */
	private Path filePath;

	/**
	 * Članska varijabla koja predstavlja referencu na sam program
	 * {@link JNotepadpp}. Ova referenca koristi se kako bi se namještao naslov
	 * prozora i kako bi gasio prozor ukoliko se pritisne gumb za gašenje ili
	 * prikazao prozor ukoliko se pritisne na naslov
	 */
	private JNotepadpp jNotepadpp;

	/**
	 * Članska varijabla koja predstavlja zastavicu je li dokument mijenjan.
	 * Zastavica služi kako se ne bi stalno iznova iscrtavala slika, već da se
	 * ona mijenja samo kada je prvi puta došlo do promijene
	 */
	private boolean currentChangedStatus;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar
	 * konstruktora spremaju se predani parametri u pripadne članske varijable
	 * kako bi se oni mogli koristiti unutar svih metoda koje ovaj razred sadrži
	 * (ne-statičkih naravno). Metoda također namješta grafičko korisničko
	 * sučelje ovog razreda
	 *
	 * @param tabPanel
	 *            referenca na prozor predstavljen primjerkom razreda
	 *            {@link TabPanel}, čiji je ovo naslov.
	 * @param filePath
	 *            putanja do dokumenta u memoriji čiji je ovo naslov
	 * @param jNotepadpp
	 *            referencu na sam program {@link JNotepadpp}.
	 */
	public TabTitle(TabPanel tabPanel, Path filePath, JNotepadpp jNotepadpp) {
		this.tabPanel = tabPanel;
		this.filePath = filePath;
		this.jNotepadpp = jNotepadpp;

		setOpaque(false);
		initGUI();
	}

	/**
	 * Pomoćna metoda koja namješta grafičko korisničko sučelje primjerka ovog
	 * razreda te se prijavljuje na svi promjene za koje je zainteresirana.
	 */
	private void initGUI() {
		JLabel title = new JLabel(filePath.getFileName().toString());
		add(title);
		title.setToolTipText(filePath.toAbsolutePath().toString());
		// referenca na TabInfo, ista za sve listenere!
		TabInfo tabInfo = tabPanel.getTabInfo();

		refreshIcon(tabInfo, title);
		title.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				jNotepadpp.getTabbedPane().setSelectedIndex(tabInfo.getTabIndex());
			}
		});

		JButton closeButton = new JButton("x");
		add(closeButton);
		closeButton.setForeground(Color.RED);
		closeButton.setBorder(null);
		closeButton.setFocusPainted(false);
		closeButton.setContentAreaFilled(false);
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jNotepadpp.closeDocumentAt(tabInfo.getTabIndex());
			}

		});

		calculateSize(title, closeButton);

		tabInfo.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Path newPath = tabInfo.getTabFilePath();
				title.setText(newPath.getFileName().toString());
				title.setToolTipText(newPath.toAbsolutePath().toString());
				// zbog neefikasnosti stalnog renderanja
				if (currentChangedStatus != tabInfo.isChanged()) {
					refreshIcon(tabInfo, title);
				}

				calculateSize(title, closeButton);
			}
		});
	}

	/**
	 * Pomoćna metoda koja se koristi za izračun veličine ovog naslova. Širina i
	 * visina računaju se na temelju širine i visine predanih parametara
	 * <b>label</b> i <b>button</b>. Metoda namješta samo preferirane dimenzije!
	 *
	 * @param label
	 *            labela koja se koristi za izračun širine i visine ove
	 *            komponente
	 * @param button
	 *            gumb koja se koristi za izračun širine i visine ove komponente
	 */
	private void calculateSize(JLabel label, JButton button) {
		Dimension labelDim = label.getPreferredSize();
		Dimension buttonDim = button.getPreferredSize();

		setPreferredSize(new Dimension(labelDim.width + buttonDim.width + FIX_DISTANCE * 3,
				Math.max(buttonDim.height, labelDim.height) + FIX_DISTANCE));

	}

	/**
	 * Pomoćna metoda koja postavlja sliku koja se prikazuje unutar primjerka
	 * razreda {@link JLabel} <b>title</b>. Slika ovisno o informacijama iz
	 * modela koji je primjerak razreda {@link TabInfo}, može biti
	 * {@link TabInfo#GREEN_DISC} ili {@link TabInfo#RED_DISC}. Unutar metoda
	 * namješta se i članska varijabla {@link #currentChangedStatus}
	 *
	 * @param tabInfo
	 *            model iz kojeg se dobiva informacija o tome je li dokument
	 *            mijenjan ili ne.
	 * @param title
	 *            primjerak razreda {@link JLabel} čija se slika namješta
	 */
	private void refreshIcon(TabInfo tabInfo, JLabel title) {
		title.setIcon(tabInfo.isChanged() ? TabInfo.RED_DISC : TabInfo.GREEN_DISC);
		currentChangedStatus = tabInfo.isChanged();
	}

}
