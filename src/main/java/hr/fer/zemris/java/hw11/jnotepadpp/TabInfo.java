package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Razred koji predstavlja model primjerka razreda {@link TabPanel}. Razred
 * sadrži člasnke varijable koje spremaju stanje dokumenta (datoteke) koji se
 * koristi unutar {@link TabPanel}. Razred prati je li dokument spremljen u
 * memoriji, je li mijenjan, koja mu je putanja te gdje se nalazi (u kojem
 * prozoru) unutar programa {@link JNotepadpp} (njegovog {@link JTabbedPane}a).
 * Razred je također subjekt u oblikovnom obrascu
 * <a href = "https://en.wikipedia.org/wiki/Observer_pattern">promatrač</a> na
 * kojeg se moguće registrirati za pračenje promjena.
 * 
 * <p>
 * Napomena: unutar dokumentacije riječ dokument predstavlja datoteku i sam
 * njezin sadržaj (ovisno o kontekstu)
 * </p>
 * 
 * @see TabPanel
 * @see JNotepadpp
 * @see JTabbedPane
 * 
 * @author Davor Češljaš
 */
public class TabInfo {

	/**
	 * Konstanta koja predstavlja sliku zelene disketu. Slika predstavlja da je
	 * dokument spremljen u memoriju i da nije mijenjan
	 */
	public static final ImageIcon GREEN_DISC;

	/**
	 * Konstanta koja predstavlja sliku crevenu disketu. Slika predstavlja da
	 * dokument nije spremljen u memoriju i/ili da je mijenjan.
	 */
	public static final ImageIcon RED_DISC;

	/**
	 * Konstanta koja predstavlja defaultnu veličinu slika koje su spremljene
	 * unutar konstantama {@link #RED_DISC} i {@link #GREEN_DISC}
	 */
	private static final int IMAGE_SIZE = 15;

	static {
		GREEN_DISC = loadIcon("icons/green-disc.png");
		RED_DISC = loadIcon("icons/red-disc.png");
	}

	/**
	 * Članska varijabla koja odgovara poziciji prozora unutar programa
	 * {@link JNotepadpp} unutar koje se prikazuje ovaj dokument.
	 */
	private int tabIndex;

	/** Članska varijabla koja odgovara putanji do dokumenta. */
	private Path tabFilePath;

	/** Članska varijabla koja ukazuje na to je li dokument mijenjan. */
	private boolean changed;

	/**
	 * Članska varijabla koja ukazuje na to je li dokument spremljen u memoriji
	 */
	private boolean inMemory;

	/**
	 * Članska varijabla koja predstavlja {@link List} svih promatrača na događaje
	 * promjena putanje, i statusa samog dokumenta ({@link #inMemory} i
	 * {@link #changed})
	 */
	private List<ChangeListener> listeners;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar
	 * konstrukotra svi parametri se spremaju unutar pripadnih članskih
	 * varijabli te se koriste kroz metode ovog razreda.
	 *
	 * @param tabIndex
	 *            pozicija prozora unutar programa {@link JNotepadpp} unutar
	 *            koje se prikazuje ovaj dokument.
	 * @param tabFilePath
	 *            putanja do dokumenta.
	 * @param changed
	 *            varijabla koja ukazuje na to je li dokument mijenjan
	 * @param inMemory
	 *            varijabla koja ukazuje na to je li dokument spremljen u
	 *            memoriji
	 */
	public TabInfo(int tabIndex, Path tabFilePath, boolean changed, boolean inMemory) {
		this.tabIndex = tabIndex;
		this.tabFilePath = tabFilePath;
		this.changed = changed;
		this.inMemory = inMemory;

		listeners = new ArrayList<>();
	}

	/**
	 * Metoda koja dohvaća putanju do dokumenta na disku.
	 *
	 * @return putanju do dokumenta na disku.
	 */
	public Path getTabFilePath() {
		return tabFilePath;
	}

	/**
	 * Metoda koja postavlja putanju do dokumenta na disku na predani parametar
	 * <b>tabFilePath</b> i o tome obavještava sve registrirane promatrače
	 *
	 * @param tabFilePath
	 *            nova putanja do dokumenta na disku
	 */
	public void setTabFilePath(Path tabFilePath) {
		this.tabFilePath = tabFilePath;
		fireStateChanged();
	}

	/**
	 * Metoda koja dohvaća zastavicu koja ukazuje na to je li dokument mijenjan
	 *
	 * @return zastavicu koja ukazuje na to je li dokument mijenjan
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * Metoda koja postavlja zastavicu koja ukazuje na to je li dokument
	 * mijenjan. Metoda također obavještava sve prijavljene promatrače da je došlo
	 * do promjene u ovom modelu
	 *
	 * @param changed
	 *            nova vrijednost zastavice koja ukazuje na to je li dokument
	 *            mijenjan
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
		fireStateChanged();
	}

	/**
	 * Metoda koja dohvaća zastavicu koja ukazuje na to je li dokument spremljen
	 * u memoriju
	 *
	 * @return zastavicu koja ukazuje na to je li dokument spremljen u memoriju
	 */
	public boolean isInMemory() {
		return inMemory;
	}

	/**
	 * Metoda koja postavlja zastavicu koja ukazuje na to je li dokument
	 * spremljen u memoriju. Metoda također obavještava sve prijavljene promatrače
	 * da je došlo do promjene u ovom modelu
	 *
	 * @param changed
	 *            nova vrijednost zastavice koja ukazuje na to je li dokument
	 *            spremljen u memoriju
	 */
	public void setInMemory(boolean inMemory) {
		this.inMemory = inMemory;
		fireStateChanged();
	}

	/**
	 * Metoda koja dohvaća trenutnu poziciju prozora unutar programa
	 * {@link JNotepadpp}
	 *
	 * @return trenutnu poziciju prozora unutar programa {@link JNotepadpp}
	 */
	public int getTabIndex() {
		return tabIndex;
	}

	/**
	 * Metoda koja postavlja trenutnu poziciju prozora unutar programa
	 * {@link JNotepadpp}
	 *
	 * @param tabIndex
	 *            nova poziciju prozora unutar programa {@link JNotepadpp}
	 */
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	/**
	 * Metoda koja registrira promatrače na promjene unutar primjerka ovog razreda.
	 * Promatrači će biti obavješteni ukoliko bilo tko nad modelom pozove metode:
	 * <ul>
	 * <li>{@link #setChanged(boolean)}</li>
	 * <li>{@link #setInMemory(boolean)}</li>
	 * <li>{@link #setTabFilePath(Path)}</li>
	 * </ul>
	 *
	 * @param l
	 *            Primjerak razreda {@link ChangeListener} čija će se metoda
	 *            {@link ChangeListener#stateChanged(ChangeEvent)} pozvati kada
	 *            se dogodi gore opisan događaj
	 */
	public void addChangeListener(ChangeListener l) {
		listeners = new ArrayList<>(listeners);
		listeners.add(l);
	}

	/**
	 * Metoda koja odjavljuje registriranog promatrača ,ako on postoji, u
	 * {@link List}i svih promatrača na promjene u ovom model
	 *
	 * @param l
	 *            Primjerak razreda {@link ChangeListener} koji se odjavljuje od
	 *            ovog modela
	 */
	public void removeChangeListener(ChangeListener l) {
		listeners = new ArrayList<>(listeners);
		listeners.remove(l);
	}

	/**
	 * Pomoćna metoda koja se koristi za obaviještavanje svih registriranih
	 * promatrača da se dogodila promjena.
	 */
	private void fireStateChanged() {
		ChangeEvent event = new ChangeEvent(this);
		for (ChangeListener l : listeners) {
			l.stateChanged(event);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tabFilePath == null) ? 0 : tabFilePath.hashCode());
		result = prime * result + tabIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TabInfo other = (TabInfo) obj;
		if (tabFilePath == null) {
			if (other.tabFilePath != null)
				return false;
		} else if (!tabFilePath.equals(other.tabFilePath))
			return false;
		if (tabIndex != other.tabIndex)
			return false;
		return true;
	}

	/**
	 * Pomoćna statička metoda koja se koristi za učitavanje konstanti
	 * {@link #GREEN_DISC} i {@link #RED_DISC}. Ova metoda biti će pozvana samo
	 * jednom (prilikom prvog spomena na razred {@link TabInfo})
	 *
	 * @param iconPath
	 *            relativna putanja do slike u memoriji
	 * @return {@link #GREEN_DISC} ili {@link #RED_DISC}
	 */
	private static ImageIcon loadIcon(String iconPath) {
		return scaleImage(new ImageIcon(TabInfo.class.getResource(iconPath)), IMAGE_SIZE, IMAGE_SIZE);
	}

	/**
	 * Pomoćna statička metoda koja se koristi za skaliranje slike. Metoda prima
	 * preferiranu visinu i širinu <b>prefWidth</b> i <b>prefHeight</b>, te
	 * primjerak razreda {@link ImageIcon} koji je potrebno skalirati
	 *
	 * @param icon
	 *            primjerak razreda {@link ImageIcon} koji je potrebno skalirati
	 * @param prefWidth
	 *            preferirana širina slike
	 * @param prefHeight
	 *            preferirana visina slike
	 * @return novi primjerak razreda {@link ImageIcon} koji predstavlja
	 *         <b>icon</b> koji je skaliran na <b>prefWidth</b> i
	 *         <b>prefHeight</b>
	 */
	private static ImageIcon scaleImage(ImageIcon icon, int prefWidth, int prefHeight) {
		BufferedImage bim = new BufferedImage(prefWidth, prefHeight, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = bim.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage(icon.getImage(), 0, 0, prefWidth, prefHeight, null);
		g.dispose();
		return new ImageIcon(bim);
	}
}
