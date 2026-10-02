import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Window that shows the top 8 boys finishers from the 2026 UIL State Meet.
 * Pick an event and class from the drop-downs.
 *
 * Run THIS class (it has the main method).
 */
public class ResultsWindow extends JFrame {

    private static final String[] EVENTS = {
        "100 meter Dash", "200 meter Dash", "400 meter Dash",
        "800 meter Run", "1600 meter Run", "3200 meter Run",
        "110 meter Hurdles", "300 meter Hurdles"
    };
    private static final String[] DIVISIONS = {"1A", "2A", "3A", "4A", "5A", "6A"};
    private static final String[] COLUMNS = {"Place", "Name", "Grade", "School", "Time", "Wind"};

    private final Webscrape scraper = new Webscrape();
    private final JComboBox<String> eventBox = new JComboBox<>(EVENTS);
    private final JComboBox<String> divisionBox = new JComboBox<>(DIVISIONS);
    private final JLabel status = new JLabel("Loading results...");
    private final DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };

    public ResultsWindow() {
        super("2026 UIL State Meet - Boys Top 8");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(750, 400);
        setLayout(new BorderLayout(8, 8));

        // top bar with the two drop-downs
        JPanel top = new JPanel();
        top.add(new JLabel("Event:"));
        top.add(eventBox);
        top.add(new JLabel("Class:"));
        top.add(divisionBox);
        divisionBox.setSelectedItem("6A");
        add(top, BorderLayout.NORTH);

        // results table
        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // status line
        status.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 8));
        add(status, BorderLayout.SOUTH);

        eventBox.addActionListener(e -> refresh());
        divisionBox.addActionListener(e -> refresh());

        loadData();
    }

    /** Downloads the page on a background thread so the window doesn't freeze. */
    private void loadData() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                scraper.load();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    refresh();
                } catch (Exception ex) {
                    Throwable t = (ex.getCause() != null) ? ex.getCause() : ex;
                    status.setText("Could not load results: " + t.getMessage());
                }
            }
        }.execute();
    }

    /** Fills the table with the top 8 for the selected event and class. */
    private void refresh() {
        if (!scraper.isLoaded()) {
            return;
        }
        String event = (String) eventBox.getSelectedItem();
        String division = (String) divisionBox.getSelectedItem();

        List<String[]> rows = scraper.getTop(event, division, 8);
        model.setRowCount(0);
        for (String[] r : rows) {
            model.addRow(r);
        }
        if (rows.isEmpty()) {
            status.setText("No results found for " + event + ", " + division + ".");
        } else {
            status.setText("Showing top " + rows.size() + ": Boys " + event + ", " + division);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResultsWindow().setVisible(true));
    }
}
