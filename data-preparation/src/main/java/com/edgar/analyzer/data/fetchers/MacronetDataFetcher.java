package com.edgar.analyzer.data.fetchers;

import com.edgar.analyzer.utils.CompanyData;
import com.edgar.analyzer.utils.CompanyRecord;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MacronetDataFetcher {

    private static final String MACRONET_URL = "https://www.macrotrends.net/stocks/industry/<counter>/beverages---soft-drinks";

    public static final int counter = 251;

    public static List<CompanyData> getRoeLinks() throws IOException {
        List<CompanyData> companyDataList = new ArrayList<>();

        for (int roeCounter=1; roeCounter <= counter; roeCounter++) {
            Document doc = Jsoup
                    .connect(MACRONET_URL.replace("<counter>", String.valueOf(roeCounter)))
                    .get();
            Elements links = doc.select("script");
            Iterator<Element> elementIterator = links.iterator();

            while (elementIterator.hasNext()) {
                Element element = elementIterator.next();
                if (!element.html().equals("") && element.html().contains("var data = ")) {
                    String data = element.html()
                            .split("var data = ")[1]
                            .split("var source =")[0].trim();
                    ObjectMapper mapper = new ObjectMapper();
                    List<CompanyData> companyData = mapper.readValue(data, new TypeReference<List<CompanyData>>(){});

                    if (companyData != null) {
                        companyDataList.addAll(companyData);
                    }
                }
            }
        }

        boolean written = true;
        List<CompanyData> newList = new ArrayList<>();
        for (CompanyData companyData: companyDataList) {
            if (!written) {
                newList.add(companyData);
            }
            if (companyData.getTicker().equals("LPSN")) {
                written = false;
            }
        }
        return newList;
    }

    public static List<CompanyRecord> getRoes(CompanyData companyData) throws IOException {
        try {
            List<CompanyRecord> records = new ArrayList<>();

            Document doc = Jsoup.connect(companyData.getLink()).get();
            Elements tables = doc.select("table");
            Iterator<Element> tableIterator = tables.iterator();

            while (tableIterator.hasNext()) {
                boolean found = false;
                Element table = tableIterator.next();

                List<Node> nodes = table.childNodes();
                for (Node node : nodes) {
                    if (node.nodeName().equals("tbody")) {

                        List<Node> trNodes = node.childNodes();
                        for (Node trNode : trNodes) {
                            List<Node> tdNodes = trNode.childNodes();

                            CompanyRecord companyRecord = new CompanyRecord();
                            companyRecord.setCompanyName(companyData.getName());
                            companyRecord.setCompanyCategory(companyData.getCategory());
                            companyRecord.setTicker(companyData.getTicker());

                            int counter = 0;
                            for (Node tdNode : tdNodes) {
                                if ((counter % 2) != 0) {
                                    switch (counter) {
                                        case 1:
                                            companyRecord.setDate(tdNode.childNode(0).toString());
                                            break;
                                        case 3:
                                            companyRecord.setTtmNetIncome(tdNode.childNode(0).toString());
                                            break;
                                        case 5:
                                            companyRecord.setShareholdersEquity(tdNode.childNode(0).toString());
                                            break;
                                        case 7:
                                            try {
                                                companyRecord.setPercentChangeInRoe(Double.valueOf(tdNode.childNode(0).toString()
                                                        .replace("%", "")));
                                            } catch (Exception ex) {
                                                companyRecord.setPercentChangeInRoe(0.0);
                                            }
                                            break;
                                    }
                                }
                                counter++;
                            }
                            records.add(companyRecord);
                        }
                        found = true;
                        break;
                    }
                }
                if (found) {
                    break;
                }
            }

            double currentRoe = records.get(records.size()-1).getPercentChangeInRoe();
            for (int revCounter = records.size()-1; revCounter > 0; revCounter--) {
                records.get(revCounter-1).setPreviousRoe(currentRoe);
                currentRoe = records.get(revCounter-1).getPercentChangeInRoe();
                records.get(revCounter-1).setPercentChangeInRoe(((records.get(revCounter-1).getPercentChangeInRoe()
                    - records.get(revCounter-1).getPreviousRoe()) * 100.0)/ records.get(revCounter-1).getPreviousRoe());
            }
            records.remove(records.size()-1);
            return records;
        } catch (Exception ex) {
            return Arrays.asList();
        }
    }

    public static void main(String[] args) throws Exception {
        List<CompanyData> companyDataList = MacronetDataFetcher.getRoeLinks();

        List<CompanyRecord> records = new ArrayList<>();
        for (CompanyData companyData: companyDataList) {
            records.addAll(MacronetDataFetcher.getRoes(companyData));
        }
        System.out.println(records.size());
    }
}