package footballappant;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static footballappant.Indexer.DF;
import static footballappant.Indexer.TFIDF;
import static footballappant.Crawler.Data;
/**
 *
 * @author Avinash Chandan
 */
public class QueryProcessor {
    public static LinkedHashMap<String, Double> query(String queryStr, int maxResults) {
//        Scanner scan = new Scanner(System.in);
//        System.out.println("Enter your query or Q for exit: ");
        String query = queryStr;
//
//        if (query.equals("Q"))
//            return null;

//        System.out.println("How many results do you want me to show? The maximum is " + Data.size() + ". Enter: ");
//        int threshold = scan.nextInt();
        int threshold = maxResults;
        String[] filtered = query.split(" ");
        ConcurrentHashMap<String, Integer> TF = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Double> IDF = new ConcurrentHashMap<>();
        ArrayList<Double> TFIDF = new ArrayList<>();

        for (String term : filtered) {
            if (!TF.containsKey(term))
                TF.put(term, 1);
            else
                TF.replace(term, TF.get(term) + 1);

            if (!IDF.containsKey(term))
                if (DF.containsKey(term))
                    IDF.put(term, Math.log(Data.size() / (double) DF.get(term)));
                else
                    IDF.put(term, 0.0);
        }

        for (String term : TF.keySet())
            TFIDF.add(TF.get(term) * IDF.get(term));

        double query_norm;
        double sum_of_terms = 0;

        for (double term : TFIDF)
            sum_of_terms += Math.pow(term, 2);

        query_norm = Math.sqrt(sum_of_terms);
        LinkedHashMap<String, Double> map = calculateCosineSimilarity(TFIDF, query_norm, threshold);
        
        return map;
//        query();
    }

    public static LinkedHashMap<String, Double> calculateCosineSimilarity(ArrayList<Double> QueryVector, double query_norm, int threshold) {
        ConcurrentHashMap<String,Double> Angles = new ConcurrentHashMap<>();

        for (String doc : TFIDF.keySet()) {
            double doc_norm;
            double sum_of_terms = 0;

            for (double term : TFIDF.get(doc))
                sum_of_terms += Math.pow(term, 2);

            doc_norm = Math.sqrt(sum_of_terms);
            Angles.put(doc, (dotProduct(TFIDF.get(doc), QueryVector) / (doc_norm * query_norm)));
        }

        LinkedHashMap<String, Double> sortedMapAsc = sortHashMapByValues(Angles);
        return sortedMapAsc;
//        printMap(sortedMapAsc, threshold);
    }

    public static double dotProduct(ArrayList<Double> A, ArrayList<Double> B) {
        double result = 0.0;
        int size = Math.min(A.size(), B.size());

        for (int i = 0; i < size; i++) {
            result += A.get(i) * B.get(i);
        }
        return result;
    }

    public static LinkedHashMap<String, Double> sortHashMapByValues(ConcurrentHashMap<String, Double> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Double> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, Double> sortedMap =
                new LinkedHashMap<>();

        for (Double val : mapValues) {
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Double comp1 = passedMap.get(key);

                if (comp1.equals(val)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    public static void printMap(LinkedHashMap<String, Double> map, int threshold) {
        int count = 0;
        System.out.println("(Results are sorted based on most relevance)");
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            count++;
            System.out.println(count + ". " + entry.getKey());
            if (count == threshold)
                return;
        }
    }
}
