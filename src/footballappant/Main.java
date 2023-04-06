/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package footballappant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import static footballappant.Indexer.TF;
import static footballappant.Indexer.DF;
import static footballappant.Indexer.IDF;
import static footballappant.Indexer.TFIDF;
import static footballappant.Crawler.Data;
/**
 *
 * @author Avinash Chandan
 */
public class Main {
    int pagesCrawled;
    int pagesIndexed;
    int totalTerms;
    int totalDocuments;
     public void main(String baseURL, int noOfPages, int noOfThreads)throws InterruptedException{
//    public static void main(String args[])throws InterruptedException {
        LinkedList<String> Links = new LinkedList<>();
        ArrayList<Thread> CrawlerThreads = new ArrayList<>();
        ArrayList<Thread> IndexerThreads = new ArrayList<>();
//        Scanner scan = new Scanner(System.in);
//        System.out.println("Enter the following arguments, in the respective order, separated by spaces: [Initial URL, Number of Pages to Crawl, Threads to Run].");
//        System.out.print("NOTE: The Initial URL should be a base URI, not a subdomain.\n\nSet: ");
//        String[] input = scan.nextLine().split(" ");
//        String starting_url = input[0];

        String starting_url = baseURL;
        Links.offer(starting_url);

        int threshold = noOfPages;
        int threads = noOfThreads;

//        int threshold = Integer.parseInt(input[1]);
//        int threads = Integer.parseInt(input[2]);

        for (int i=0; i < threads; i++) {
            Crawler spider = new Crawler(Links, threshold, true);
            CrawlerThread crawler_thread = new CrawlerThread(spider);

            Indexer bookkeeper = new Indexer();
            Indexer labourer = new Indexer();

            IndexerThread indexer_index_thread = new IndexerThread(bookkeeper, "index");
            IndexerThread indexer_idf_thread = new IndexerThread(labourer, "idf");
            IndexerThread indexer_vectorize_thread = new IndexerThread(labourer, "vectorize");

            Thread strand = new Thread(crawler_thread);
            Thread string = new Thread(indexer_index_thread);
            Thread fibre = new Thread(indexer_idf_thread);
            Thread tress = new Thread(indexer_vectorize_thread);

            CrawlerThreads.add(strand);
            IndexerThreads.add(string);
            IndexerThreads.add(fibre);
            IndexerThreads.add(tress);

            strand.start();
        }

        for (Thread strand : CrawlerThreads) {
            strand.join();
        }

        for (int i = 0; i < IndexerThreads.size(); i+=3) {
            IndexerThreads.get(i).start();
        }

        for (int i = 0; i < IndexerThreads.size(); i+=3) {
            IndexerThreads.get(i).join();
        }

        for (int i = 1; i < IndexerThreads.size(); i+=3) {
            IndexerThreads.get(i).start();
        }

        for (int i = 1; i < IndexerThreads.size(); i+=3) {
            IndexerThreads.get(i).join();
        }

        for (int i = 2; i < IndexerThreads.size(); i+=3) {
            IndexerThreads.get(i).start();
        }

        for (int i = 2; i < IndexerThreads.size(); i+=3) {
            IndexerThreads.get(i).join();
        }
        
        pagesCrawled = Data.size();
        pagesIndexed = TF.size();
        totalTerms = DF.size();
        totalDocuments = Data.size();

        System.out.println("\nPages Crawled: " + Data.size());
        System.out.println("Pages Indexed: " + TF.size());
        System.out.println("TF : "+TF);
        System.out.println("Total Terms: " + DF.size() + " | IDFs Calculated: " + IDF.size());
        System.out.println("Total Documents: " + Data.size() + " | Total Vectors Produced: " + TFIDF.size() + "\n");

//        QueryProcessor.query();

        System.out.println("\nThank you for your time :)");
    }
}
