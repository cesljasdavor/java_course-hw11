package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * Razred koji nasljeđuje {@link JLabel}. Razred predstavlja trenutni sat i
 * datum. Format sata i datuma zadan je kao argument {@link SimpleDateFormat}u
 * te je oblika <b>yyyy/MM/dd HH:mm:ss</b>. Razred također implementira sučelje
 * {@link ActionListener} kako bi slušao na promjenu vremena svake sekunde.
 * Unutar razreda inicijalizira se primjerak razreda {@link Timer} koji okida
 * metodu {@link #actionPerformed(ActionEvent)} ovog razreda. Kako bi sat stao s
 * radom potrebno je pozvati metodu {@link #interrupt()} nad primjerkom ovog
 * razreda
 * 
 * @see JLabel
 * @see ActionListener
 * @see Timer
 * 
 * @author Davor Češljaš
 */
public class Clock extends JLabel implements ActionListener {

	/** Konstanta koja se koristi prilikom serijalizacije objekata */
	private static final long serialVersionUID = 1L;

	/** Konstanta koja predstavlja 1 sekudnu izraženu u milisekundama */
	private static final int SECOND = 1000;

	/**
	 * Konstanta koja je primjerak razreda {@link SimpleDateFormat} i koja se
	 * koristi za formatiranje datuma i vremena
	 */
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/**
	 * Članska varijabla koja je primjerak razreda {@link Timer} i koja svake
	 * sekunde okida metodu {@link #actionPerformed(ActionEvent)}
	 */
	private Timer timer;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Unutar ovo
	 * konstruktora postavlja se trenutno vrijeme i inicijalizira se člasnka
	 * varijabla {@link #timer} koja se koristi za dojavu da je prošla jedna
	 * sekunda
	 */
	public Clock() {
		changeDate();
		timer = new Timer(SECOND, this);
		timer.start();
	}

	/**
	 * Metoda koja prekida privatni primjerak razreda {@link Timer} kako bi on
	 * odjavio sve svoje promatrače i stao okidati metode
	 * {@link #actionPerformed(ActionEvent)} svojih promatrača
	 */
	public void interrupt() {
		timer.stop();
	}

	/**
	 * Pomoćna metoda koja se koristi za ažuriranje vremena (teksta) unutar ove
	 * labele
	 */
	private void changeDate() {
		setText(dateFormat.format(new Date()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		changeDate();
	}
}
