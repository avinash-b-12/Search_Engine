/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package footballappant;

/**
 *
 * @author Avinash Chandan
 */

public class CrawlerThread implements Runnable {
    Crawler spider;

    public CrawlerThread(Crawler spider) {
        this.spider = spider;
    }

    public void run() {
        spider.crawl();
    }
}