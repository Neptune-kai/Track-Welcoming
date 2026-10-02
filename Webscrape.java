import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
 
/**
 * Starter MileSplit Texas results scraper.
 *
 * Setup: add Jsoup to your project.
 *   Maven:  org.jsoup:jsoup:1.17.2
 *   or download the jar from https://jsoup.org/download and add it to your classpath.
 *
 * Open the target page in your browser first and confirm it has a normal
 * <table> of results. Selectors below are untested against the live site.
 * Please respect MileSplit's Terms of Service and robots.txt.
 */
public class Webscrape {
 
    private String url;
    private int delayMillis;
    private int maxPages;
    private List<String[]> rows;
    private String[] headers;
 
    public Webscrape(String url) {
        this(url, 5000, 5);
    }
 
    public Webscrape(String url, int delayMillis, int maxPages) {
        this.url = url;
        this.delayMillis = delayMillis;
        this.maxPages = maxPages;
        this.rows = new ArrayList<>();
        this.headers = new String[0];
    }
 
    /** Fetches each page, parses the first results table, and stores the rows. */
    public void scrape() throws IOException, InterruptedException {
        for (int page = 1; page <= maxPages; page++) {
            String pageUrl = (page == 1) ? url : url + "&page=" + page;
 
            Document doc = Jsoup.connect(pageUrl)
                    .userAgent("Mozilla/5.0 (personal research scraper)")
                    .timeout(30000)
                    .get();
 
            Element table = doc.selectFirst("table");
            if (table == null) {
                System.out.println("No table found on page " + page + ", stopping.");
                break;
            }
 
            if (headers.length == 0) {
                Elements ths = table.select("th");
                headers = new String[ths.size()];
                for (int i = 0; i < ths.size(); i++) {
                    headers[i] = ths.get(i).text();
                }
            }
 
            int added = 0;
            for (Element tr : table.select("tr")) {
                Elements tds = tr.select("td");
                if (tds.isEmpty()) continue;
                String[] row = new String[tds.size()];
                for (int i = 0; i < tds.size(); i++) {
                    row[i] = tds.get(i).text();
                }
                rows.add(row);
                added++;
            }
            System.out.println("Page " + page + ": " + added + " rows");
            if (added == 0) break;
 
            Thread.sleep(delayMillis); // be polite
        }
    }
 
    /** Writes the scraped data to a CSV file. */
    public void writeCsv(String path) throws IOException {
        try (PrintWriter out = new PrintWriter(path, "UTF-8")) {
            out.println(toCsvLine(headers));
            for (String[] row : rows) {
                out.println(toCsvLine(row));
            }
        }
        System.out.println("Wrote " + rows.size() + " rows to " + path);
    }
 
    private String toCsvLine(String[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(',');
            sb.append('"').append(values[i].replace("\"", "\"\"")).append('"');
        }
        return sb.toString();
    }
 
    public List<String[]> getRows() {
        return rows;
    }
 
    public String[] getHeaders() {
        return headers;
    }
 
    public static void main(String[] args) {
        // Paste a Texas rankings/results URL from your browser here.
        String url = "https://tx.milesplit.com/rankings/leaders/high-school-boys/outdoor-track-and-field?year=2026&event=100m";
 
        Webscrape scraper = new Webscrape(url);
        try {
            scraper.scrape();
            scraper.writeCsv("milesplit_tx_results.csv");
        } catch (IOException e) {
            System.out.println("Request failed (the site may be blocking bots): " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}