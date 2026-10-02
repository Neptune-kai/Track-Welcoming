import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Downloads the raw (plain-text) results for the 2026 UIL State Track & Field
 * Championships from Texas MileSplit and pulls out the top finishers for an event.
 *
 * Requires the Jsoup library on your classpath.
 * Please respect MileSplit's Terms of Service.
 */
public class Webscrape {

    public static final String RESULTS_URL =
        "https://tx.milesplit.com/meets/746342-uil-state-hs-track-and-field-championships-2026/results/1304569/raw";

    private String url;
    private String[] lines;

    public Webscrape() {
        this(RESULTS_URL);
    }

    public Webscrape(String url) {
        this.url = url;
        this.lines = new String[0];
    }

    /** Downloads the page and stores its text, one entry per line. */
    public void load() throws IOException {
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (personal school project)")
                .timeout(30000)
                .get();

        // The raw results sit in a preformatted block; fall back to the whole page.
        Element pre = doc.selectFirst("pre");
        String text = (pre != null) ? pre.wholeText() : doc.body().wholeText();
        lines = text.split("\\r?\\n");
    }

    public boolean isLoaded() {
        return lines.length > 0;
    }

    /**
     * Returns up to 'count' boys finishers for one event and class.
     * Each row is: {place, name, grade, school, time, wind}.
     *
     * @param event    e.g. "100 meter Dash", "800 meter Run", "110 meter Hurdles"
     * @param division e.g. "6A"
     */
    public List<String[]> getTop(String event, String division, int count) {
        String header = "Boys " + event + " Finals " + division;
        List<String[]> out = new ArrayList<>();

        // find the section header
        int i = 0;
        while (i < lines.length && !lines[i].trim().equals(header)) {
            i++;
        }
        i += 2; // skip the header line and the column-names line

        // read rows until the next section (rows start with a place number)
        while (i < lines.length && out.size() < count) {
            String line = lines[i].trim();
            if (line.isEmpty() || !Character.isDigit(line.charAt(0))) {
                break;
            }
            // columns: PLACE, NAME, GRADE, GENDER, TEAM, MARK, HEAT, WIND
            String[] c = line.split("\t", -1);
            if (c.length >= 6) {
                String wind = (c.length > 7) ? c[7] : "";
                out.add(new String[]{c[0], c[1], c[2], c[4], c[5], wind});
            }
            i++;
        }
        return out;
    }
}
