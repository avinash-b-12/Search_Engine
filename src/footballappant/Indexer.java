/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package footballappant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import static footballappant.Crawler.*;
/**
 *
 * @author Avinash Chandan
 */
public class Indexer {
    static ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> TF;
    static ConcurrentHashMap<String, Integer> DF;
    static ConcurrentHashMap<String, Double> IDF;
    static ConcurrentHashMap<String, ArrayList<Double>> TFIDF;
    static LinkedList<String> IndexFrontier;
    Padlock lock;

    public Indexer() {
        if (DF == null) {
            DF = new ConcurrentHashMap<>();
        }

        if (TFIDF == null) {
            TFIDF = new ConcurrentHashMap<>();
        }

        if (IDF == null) {
            IDF = new ConcurrentHashMap<>();
        }

        if (TF == null) {
            TF = new ConcurrentHashMap<>();
        }

        if (IndexFrontier == null) {
            IndexFrontier = new LinkedList<>();
        }

        lock = new Padlock();
    }

    public void index() {
        try {
            do {
                HashSet<String> Terms = new HashSet<>();
                int occurrences;

                lock.lock();
                String link = IndexFrontier.poll();
                lock.unlock();

                if (isEmpty(link))
                    return;

                ConcurrentHashMap<String, Integer> Tokens = new ConcurrentHashMap<>();

                lock.lock();
                String[] content = Data.get(link);
                lock.unlock();

                for (String token : content) {
                    if (Tokens.containsKey(token)) {
                        occurrences = Tokens.get(token);
                        Tokens.replace(token, ++occurrences);
                    } else {
                        Tokens.put(token, 1);
                    }
                    Terms.add(token);
                }

                lock.lock();
                for (String term : Terms) {
                    if (!DF.containsKey(term))
                        DF.put(term, 1);
                    else {
                        int appearances = DF.get(term);
                        DF.replace(term, ++appearances);
                    }
                }

                TF.put(link, Tokens);
                lock.unlock();
            } while (!IndexFrontier.isEmpty());
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error! IndexerThreads conflicted.");
        }
    }

    public void calculateIDF() throws InterruptedException {
        double idf;
        lock.lock();
        for (String term : DF.keySet()) {
            idf = Math.log(Data.size() / (double) DF.get(term));
            IDF.put(term, idf);
        }
        lock.unlock();
    }

    public void vectorize() throws InterruptedException {
        lock.lock();
        for (String doc : TF.keySet()) {
            ArrayList<Double> tf_idf = new ArrayList<>();
            for (String term : TF.get(doc).keySet()) {
                tf_idf.add(TF.get(doc).get(term) * IDF.get(term));
            }
            TFIDF.put(doc, tf_idf);
        }
        lock.unlock();
    }
}
