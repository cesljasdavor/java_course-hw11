package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;

import hr.fer.zemris.java.hw11.jnotepadpp.local.FormLocalizationProvider;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizedLabel;

/**
 * Razred koji nasljeđuje razred {@link JPanel}. Razred se koristi kao statusna
 * traka unutar prozora koji je oblikovan razredom {@link TabPanel}. Svaki
 * primjerak ovog razreda sadrži dva primjerka razreda {@link LocalizedLabel} i
 * jedan primjerak razreda {@link Clock}. Unutar primjeraka razreda
 * {@link LocalizedLabel} ispisuje se duljina teksta te trenutni redak, stupac i
 * selekcija.
 * 
 * @see JPanel
 * @see Clock
 * @see LocalizedLabel
 * 
 * @author Davor Češljaš
 */
public class StatusBar extends JPanel {

	/** Konstanta koja se koristi prilikom serijalizacije objekata */
	private static final long serialVersionUID = 1L;

	/**
	 * Članska varijabla koja predstavlja labelu unutar koje se ispisuje duljina
	 * teksta
	 */
	private LocalizedLabel lengthLabel;

	/**
	 * Članska varijabla koja predstavlja labelu unutar koje se ispisuju podaci
	 * o trenutnoj poziciji znaka za umetanje (redak i stupac) te koliko je
	 * znakova označeno
	 */
	private LocalizedLabel infoLabel;

	/**
	 * Članska varijabla koja predstavlja labelu unutar koje je zapisan trenutni
	 * datum i vrijeme
	 */
	private Clock clock;

	/**
	 * Člasnka varijabla koja je primjerak razreda {@link JTextArea}, a ovdje se
	 * koristi za pračenje promjena i vađenje teksta koju ona trenutno ispisuje
	 */
	private JTextArea textArea;

	/**
	 * Člasnka varijabla koja je primjerak razreda
	 * {@link FormLocalizationProvider}, a koja se koristi za lokalizaciju
	 * trenutnih naziva unutar labela {@link #infoLabel} i {@link #lengthLabel}
	 */
	private FormLocalizationProvider flp;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Konstruktor sve
	 * parametre sprema u odgovarajuće članske varijable te ih koristi kroz svoj
	 * rad.
	 *
	 * @param flp
	 *            primjerak razreda {@link FormLocalizationProvider}, a koja se
	 *            koristi za lokalizaciju trenutnih naziva unutar labela
	 *            {@link #infoLabel} i {@link #lengthLabel}
	 * @param textArea
	 *            primjerak razreda {@link JTextArea}, a ovdje se koristi za
	 *            pračenje promjena i vađenje teksta koju ona trenutno ispisuje
	 * @param clock
	 *            varijabla koja predstavlja labelu unutar koje je zapisan
	 *            trenutni datum i vrijeme
	 */
	public StatusBar(FormLocalizationProvider flp, JTextArea textArea, Clock clock) {
		this.textArea = textArea;
		this.clock = clock;
		this.flp = flp;

		setLayout(new GridLayout(1, 3));

		initGUI();
	}

	/**
	 * Pomoćna metoda koja inicijalizira grafičko korisničko sučelje primjerka
	 * ovog razreda. Unutar ove metode inicijaliziraju se {@link #infoLabel} i
	 * {@link #lengthLabel}. Te svi potrebni promatrači na događaje koji bi mogli
	 * uzrokovati promjenu ispisa unutar labela
	 */
	private void initGUI() {
		Border labelBorder = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY);
		lengthLabel = new LocalizedLabel() {

			private static final long serialVersionUID = 1L;

			@Override
			public void localizationChanged() {
				setLengthLabelData();
			}
		};
		flp.addLocalizationListener(lengthLabel);

		setUpStatusLabel(lengthLabel, labelBorder);
		setLengthLabelData();

		infoLabel = new LocalizedLabel() {

			private static final long serialVersionUID = 1L;

			@Override
			public void localizationChanged() {
				setInfoLabelData();
			}
		};
		flp.addLocalizationListener(infoLabel);

		setUpStatusLabel(infoLabel, labelBorder);
		setInfoLabelData();

		// clock je namješten u statičkom bloku aplikacije
		clock.setHorizontalAlignment(SwingConstants.RIGHT);
		add(clock);

		setupTextAreaListeners();
	}

	/**
	 * Pomoćna metoda koja postavlja sve parametre predanog primjerka razreda
	 * {@link JLabel} koji su odgovorni za izgled same labele, ali ne i ono što
	 * se unutar nje ispisuje
	 *
	 * @param label
	 *            primjerak razreda {@link JLabel} koji se postavlja
	 * @param border
	 *            primjerak razreda {@link Border} koji se koristi za
	 *            postavljanje rubova labele
	 */
	private void setUpStatusLabel(JLabel label, Border border) {
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setBorder(border);

		add(label);
	}

	/**
	 * Pomoćna metoda koja postavlja promatrače na sve promjene teksta i pozicije
	 * znaka za umetanje nad članskom varijablom {@link #textArea}
	 */
	private void setupTextAreaListeners() {
		textArea.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				setInfoLabelData();
			}
		});

		textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				setLengthLabelData();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				setLengthLabelData();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
	}

	/**
	 * Pomoćna metoda koja postavlja privatnu člasnku varijablu
	 * {@link #infoLabel}. Ova metoda odgovorna je za ispis unutar same labele
	 */
	private void setInfoLabelData() {
		Caret caret = textArea.getCaret();

		int dotPosition = caret.getDot();
		int line = 0;
		int column = 0;
		try {
			// linije i stupci u sublime textu počinju od 1,1 pa sa i ja tako
			// uzeo
			line = textArea.getLineOfOffset(dotPosition);
			column = dotPosition - textArea.getLineStartOffset(line) + 1;
			line++;
		} catch (BadLocationException ignorable) {
		}

		int selection = Math.abs(dotPosition - caret.getMark());

		infoLabel.setText(String.format("%s %s %s", flp.getString("statusBar.line") + line,
				flp.getString("statusBar.column") + column, flp.getString("statusBar.selection") + selection));
	}

	/**
	 * Pomoćna metoda koja postavlja privatnu člasnku varijablu
	 * {@link #lengthLabel}. Ova metoda odgovorna je za ispis unutar same labele
	 */
	private void setLengthLabelData() {
		lengthLabel.setText(flp.getString("statusBar.length") + textArea.getText().length());
	}
}
