
package org.apache.camel.example.management;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.language.XPath;

/**
 * @version 
 */
public class StockService {

    private List<String> symbols;
    private Map<String, Integer> stat = new ConcurrentHashMap<String, Integer>();

    public String transform(@XPath("/stock/symbol/text()") String symbol, @XPath("/stock/value/text()") String value) {
        Integer hits = stat.get(symbol);
        if (hits == null) {
            hits = 1;
        } else {
            hits++;
        }
        stat.put(symbol, hits);

        return symbol + "@" + hits;
    }

    public String getHits() {
        return stat.toString();
    }

    public String createRandomStocks() {
        Random ran = new Random();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<stocks>\n");
        for (int i = 0; i < 100; i++) {
            int winner = ran.nextInt(symbols.size());
            String symbol = symbols.get(winner);
            int value = ran.nextInt(1000);
            xml.append("<stock>");
            xml.append("<symbol>").append(symbol).append("</symbol>");
            xml.append("<value>").append(value).append("</value>");
            xml.append("</stock>\n");
        }
        xml.append("</stocks>");

        return xml.toString();
    }

    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
    }
}
