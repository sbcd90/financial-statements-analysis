package com.edgar.analyzer.feature.gen;

import com.edgar.analyzer.data.fetchers.MacronetDataFetcher;
import com.edgar.analyzer.data.fetchers.SecEdgarDataFetcher;
import com.edgar.analyzer.utils.CompanyData;
import com.edgar.analyzer.utils.CompanyRecord;
import org.apache.commons.lang3.tuple.Triple;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataMerger {
    private BufferedWriter bufferedWriter;

    public DataMerger(String filename) throws Exception {
        this.initializeFileWriter(filename);
    }

    public void initializeFileWriter(String filename) throws Exception {
        File file = new File(filename);
        FileOutputStream fos = new FileOutputStream(file);
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos));

        bufferedWriter.write("Ticker,CompanyName,CompanyCategory,Date,TtmNetIncome,ShareholdersEquity,PreviousRoe,PercentChangeInRoe" +
                ",PercentOfPositiveWords,PercentOfNegativeWords,PercentOfLitiguousWords,PercentOfConstrainingWords" +
                ",PercentOfStrongModalWords,PercentOfWeakModalWords,PercentOfUncertaintyWords");
        bufferedWriter.newLine();
    }

    public void mergeData() throws Exception {
        List<CompanyData> companyDataList = MacronetDataFetcher.getRoeLinks();

        for (CompanyData companyData: companyDataList) {
            List<CompanyRecord> macronetRecords = MacronetDataFetcher.getRoes(companyData);

            SecEdgarDataFetcher secEdgarDataFetcher = new SecEdgarDataFetcher(companyData.getTicker(),
                    "20190101");
            List<Triple<String, String, CompanyRecord>> secEdgarRecords = secEdgarDataFetcher.listAllSecDocs();

            boolean[] done = new boolean[macronetRecords.size()];


            for (Triple<String, String, CompanyRecord> secEdgarRecord: secEdgarRecords) {

                int count = 0;
                for (CompanyRecord macronetRecord: macronetRecords) {
                    if (!done[count]) {
                        if (macronetRecord.getDate().split("-")[0]
                                .equals(secEdgarRecord.getMiddle())) {
                            macronetRecord.setNoOfPositiveWords(secEdgarRecord.getRight().getNoOfPositiveWords());
                            macronetRecord.setNoOfNegativeWords(secEdgarRecord.getRight().getNoOfNegativeWords());
                            macronetRecord.setNoOfLitiguousWords(secEdgarRecord.getRight().getNoOfLitiguousWords());
                            macronetRecord.setNoOfConstrainingWords(secEdgarRecord.getRight().getNoOfConstrainingWords());
                            macronetRecord.setNoOfStrongModalWords(secEdgarRecord.getRight().getNoOfStrongModalWords());
                            macronetRecord.setNoOfWeakModalWords(secEdgarRecord.getRight().getNoOfWeakModalWords());
                            macronetRecord.setNoOfUncertaintyWords(secEdgarRecord.getRight().getNoOfUncertaintyWords());
                            done[count] = true;
                        }
                    }
                    count++;
                }
            }

            List<CompanyRecord> finalRecords = new ArrayList<>();
            for (int idx = 0; idx < done.length; idx++) {
                if (done[idx]) {
                    finalRecords.add(macronetRecords.get(idx));
                }
            }
            writeSingleCompanyToFile(finalRecords);
        }
    }

    public void writeSingleCompanyToFile(List<CompanyRecord> companyRecords) throws IOException {
        for (CompanyRecord companyRecord: companyRecords) {
            bufferedWriter.write(companyRecord.getTicker() + "," +
                    companyRecord.getCompanyName() + "," +
                    companyRecord.getCompanyCategory() + "," +
                    companyRecord.getDate() + "," +
                    companyRecord.getTtmNetIncome() + "," +
                    companyRecord.getShareholdersEquity() + "," +
                    companyRecord.getPreviousRoe() + "," +
                    companyRecord.getPercentChangeInRoe() + "," +
                    companyRecord.getNoOfPositiveWords() + "," +
                    companyRecord.getNoOfNegativeWords() + "," +
                    companyRecord.getNoOfLitiguousWords() + "," +
                    companyRecord.getNoOfConstrainingWords() + "," +
                    companyRecord.getNoOfStrongModalWords() + "," +
                    companyRecord.getNoOfWeakModalWords() + "," +
                    companyRecord.getNoOfUncertaintyWords());
            bufferedWriter.newLine();
        }
    }

    public void closeFile() throws IOException {
        bufferedWriter.close();
    }
}