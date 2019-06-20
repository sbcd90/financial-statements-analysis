package com.edgar.analyzer.data.fetchers;

import com.edgar.analyzer.data.cleansers.CAAParser;
import com.edgar.analyzer.feature.gen.FeatureGeneration;
import com.edgar.analyzer.utils.CompanyData;
import com.edgar.analyzer.utils.CompanyRecord;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SecEdgarDataFetcher {

    private static final String SEC_EDGAR_URL = "http://www.sec.gov/cgi-bin/browse-edgar" +
            "?action=getcompany" +
            "&owner=exclude" +
            "&output=xml" +
            "&CIK=<ticker>" +
            "&type=<filing-type>" +
            "&dateb=<prior_to_date>" +
            "&count=<count>";

    private final String ticker;
    private final String priorTo;
    private int count;
    private final FeatureGeneration featureGeneration;


    public SecEdgarDataFetcher(String ticker, String priorTo) throws Exception {
        this.ticker = ticker;
        this.priorTo = priorTo;
        this.featureGeneration = new FeatureGeneration(this.ticker);
    }

    public String callCAA(String secDocument, String filingType) throws Exception {
        CAAParser caaParser = new CAAParser(secDocument, true);
        return caaParser.applyAlgorithm(filingType);
    }

    public List<Triple<String, String, CompanyRecord>> browseSec(String filingType, int count) throws Exception {
        try {
            Document doc = Jsoup
                    .connect(SEC_EDGAR_URL.replace("<ticker>", this.ticker)
                            .replace("<filing-type>", filingType)
                            .replace("<prior_to_date>", this.priorTo)
                            .replace("<count>", String.valueOf(this.count)))
                    .get();
            Iterator<Element> linkIterator = doc.select("filinghref").iterator();

            List<Pair<String, String>> linkUrls = new ArrayList<>();
            while (linkIterator.hasNext()) {
                Element link = linkIterator.next();
                String htmLink = link.childNode(0).toString().trim();
                String newLink = htmLink.substring(0, htmLink.lastIndexOf("-")) + ".txt";
                String docName = newLink.substring(newLink.lastIndexOf("/") + 1);
                linkUrls.add(Pair.of(newLink, docName));
            }

            List<Triple<String, String, CompanyRecord>> yearSecDocsList = new ArrayList<>();
            for (Pair<String, String> linkUrl : linkUrls) {
                String year = linkUrl.getRight().split("-")[1];

                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(linkUrl.getLeft());
                String secDocument = target.request(MediaType.APPLICATION_XML)
                        .get(String.class);

                secDocument = this.callCAA(secDocument, filingType);

                CompanyRecord record = featureGeneration.listWordRatios(secDocument);
                yearSecDocsList.add(Triple.of(this.ticker, "20" + year, record));
            }
            return yearSecDocsList;
        } catch (Exception ex) {
            return Arrays.asList();
        }
    }

    public List<Triple<String, String, CompanyRecord>> listAllSecDocs() throws Exception {
        List<Triple<String, String, CompanyRecord>> yearSecDocsList = new ArrayList<>();
        yearSecDocsList.addAll(this.browseSec("10-Q", 45));
        yearSecDocsList.addAll(this.browseSec("10-K", 15));
        return yearSecDocsList;
    }

    public static void main(String[] args) throws Exception {
        List<CompanyData> companyDataList = MacronetDataFetcher.getRoeLinks();

        for (CompanyData companyData: companyDataList) {
            SecEdgarDataFetcher secEdgarDataFetcher = new SecEdgarDataFetcher(companyData.getTicker(),
                    "20190101");
            System.out.println(secEdgarDataFetcher.ticker + " - " + secEdgarDataFetcher.listAllSecDocs().size());
        }
    }
}