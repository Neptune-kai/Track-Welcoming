import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Game extends JPanel implements Runnable, KeyListener, MouseListener {

	private static final String[] EVENTS = {
		"100 meter Dash", "200 meter Dash", "400 meter Dash",
		"800 meter Run", "1600 meter Run", "3200 meter Run",
		"110 meter Hurdles", "300 meter Hurdles"
	};
	private static final String[] DIVISIONS = {"1A", "2A", "3A", "4A", "5A", "6A"};

	private BufferedImage back;
	private Webscrape scraper = new Webscrape();
	private volatile List<String[]> rows = new ArrayList<String[]>();
	private volatile String status = "Loading results...";
	private int eventIndex = 0;      // starts on the 100m
	private int divisionIndex = 5;   // starts on 6A

	public Game() {
		new Thread(this).start();
		this.addKeyListener(this);
		this.addMouseListener(this);
		back = null;

		loadData();
	}

	// Downloads the results on a separate thread so the window doesn't freeze.
	private void loadData() {
		new Thread(() -> {
			try {
				scraper.load();
				updateRows();
			} catch (Exception e) {
				status = "Could not load results: " + e.getMessage();
			}
		}).start();
	}

	// Grabs the top 8 for the current event and class.
	private void updateRows() {
		rows = scraper.getTop(EVENTS[eventIndex], DIVISIONS[divisionIndex], 8);
		if (rows.isEmpty()) {
			status = "No results found for that event and class.";
		} else {
			status = "";
		}
	}

	public void run() {
		try {
			while (true) {
				Thread.sleep(5);
				repaint();
			}
		} catch (Exception e) {
		}
	}

	public void paint(Graphics g) {
		Graphics2D twoDgraph = (Graphics2D) g;

		if (back == null) {
			back = (BufferedImage) (createImage(getWidth(), getHeight()));
		}

		Graphics g2d = back.createGraphics();

		g2d.clearRect(0, 0, getSize().width, getSize().height);

		// START CODING GRAPHICS HERE

		// black background
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, getSize().width, getSize().height);

		// title
		g2d.setColor(Color.CYAN);
		g2d.setFont(new Font("SansSerif", Font.BOLD, 40));
		g2d.drawString("2026 UIL State Meet - Boys " + EVENTS[eventIndex]
				+ " (" + DIVISIONS[divisionIndex] + ")", 60, 80);

		// column headings
		g2d.setColor(new Color(139, 53, 153));
		g2d.setFont(new Font("SansSerif", Font.BOLD, 28));
		g2d.drawString("Place", 60, 150);
		g2d.drawString("Name", 160, 150);
		g2d.drawString("Grade", 560, 150);
		g2d.drawString("School", 680, 150);
		g2d.drawString("Time", 1100, 150);
		g2d.drawString("Wind", 1250, 150);

		// the results
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("SansSerif", Font.PLAIN, 28));
		int y = 200;
		for (String[] r : rows) {
			g2d.drawString(r[0], 60, y);    // place
			g2d.drawString(r[1], 160, y);   // name
			g2d.drawString(r[2], 560, y);   // grade
			g2d.drawString(r[3], 680, y);   // school
			g2d.drawString(r[4], 1100, y);  // time
			g2d.drawString(r[5], 1250, y);  // wind
			y += 50;
		}

		// status message and controls
		g2d.setColor(Color.YELLOW);
		g2d.setFont(new Font("SansSerif", Font.PLAIN, 24));
		if (!status.isEmpty()) {
			g2d.drawString(status, 60, 650);
		}
		g2d.setColor(Color.GRAY);
		g2d.drawString("Left/Right arrows: change event     Up/Down arrows: change class", 60, 700);

		// This line tells the program to draw everything above.
		twoDgraph.drawImage(back, 0, 0, null);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
		if (k == KeyEvent.VK_RIGHT) {
			eventIndex = (eventIndex + 1) % EVENTS.length;
		} else if (k == KeyEvent.VK_LEFT) {
			eventIndex = (eventIndex + EVENTS.length - 1) % EVENTS.length;
		} else if (k == KeyEvent.VK_UP) {
			divisionIndex = (divisionIndex + 1) % DIVISIONS.length;
		} else if (k == KeyEvent.VK_DOWN) {
			divisionIndex = (divisionIndex + DIVISIONS.length - 1) % DIVISIONS.length;
		} else {
			return;
		}
		if (scraper.isLoaded()) {
			updateRows();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}