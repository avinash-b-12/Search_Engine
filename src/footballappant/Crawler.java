package footballappant;

/**
 *
 * @author Avinash Chandan
 */

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static footballappant.Indexer.IndexFrontier;

public class Crawler {
    static LinkedList<String> Frontier;
    static ConcurrentHashMap<String, String[]> Data;
    static HashSet<String> Visited;
    static int pages_crawled = 0;

    int threshold;
    Padlock lock;

    public Crawler(LinkedList<String> Frontier, int threshold, boolean store_data) {
        if (!store_data) {
            Crawler.Frontier = Frontier;
            Data = new ConcurrentHashMap<>();
            Visited = new HashSet<>();
        }

        if (Crawler.Frontier == null) {
            Crawler.Frontier = Frontier;
        }

        if (Data == null)
            Data = new ConcurrentHashMap<>();

        if (Visited == null)
            Visited = new HashSet<>();

        this.threshold = threshold;
        lock = new Padlock();
    }

    public void crawl() {
        try {
            do {
                lock.lock();
                String link = Frontier.poll();
                if (link == null) {
                    break;
                }
                pages_crawled++;
                lock.unlock();

                Document doc = Jsoup.connect(link).get();

                String[] content = doc.text().split("\\W+");
                Data.put(link, content);
                IndexFrontier.offer(link);

                lock.lock();
                Visited.add(link);
                extractLinks(doc);
                lock.unlock();
            } while (!Frontier.isEmpty() && pages_crawled < threshold);

        } catch (HttpStatusException e) {
            System.out.println("HTTP error fetching URL.");

        } catch (IOException e) {
            System.out.println("Error! Couldn't access webpage.");
            e.printStackTrace();

        } catch (NullPointerException e) {
            System.out.println("Error! Invalid starting point URL.");
            e.printStackTrace();

        } catch (InterruptedException e) {
            System.out.println("Error! CrawlerThreads conflicted.");
        }
    }

    public void extractLinks(Document doc) {
        Elements Anchors = doc.select("a");

        for (Element anchor : Anchors) {
            String sublink = anchor.attr("abs:href");
            if (Data.size() < threshold && !isEmpty(sublink) && !Visited.contains(sublink) && !Frontier.contains(sublink))
                Frontier.offer(sublink);
        }
    }

    public static boolean isEmpty(String string) {
        return (string == null || string.length() == 0);
    }
}
