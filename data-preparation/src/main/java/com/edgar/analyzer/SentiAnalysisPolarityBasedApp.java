package com.edgar.analyzer;

import java.util.List;

public class SentiAnalysisPolarityBasedApp {

    public static void main(String[] args) throws Exception {
        CAAParser parser = new CAAParser("alphabet_data.txt");
        String text = parser.applyAlgorithm();

        PolarityBasedSentiAnalysis polarityBasedSentiAnalysis = new PolarityBasedSentiAnalysis();
        polarityBasedSentiAnalysis.readDictionaries();

        polarityBasedSentiAnalysis.calculateScores(text);
        List<Object> rows = polarityBasedSentiAnalysis.getResults();
        System.out.println("BL_P - " + rows.get(0));
        System.out.println("BL_N - " + rows.get(1));
        System.out.println("BL_NET - " + rows.get(2));
        System.out.println("BL_SENTIMENT - " + rows.get(3));
        System.out.println("M_P - " + rows.get(4));
        System.out.println("M_N - " + rows.get(5));
        System.out.println("M_NET - " + rows.get(6));
        System.out.println("M_SENTIMENT - " + rows.get(7));
        System.out.println("M_UNCERTAINTY - " + rows.get(8));
        System.out.println("M_LITIGIOUS - " + rows.get(9));
        System.out.println("M_CONSTRAINING - " + rows.get(10));
        System.out.println("M_SUPERFLUOUS - " + rows.get(11));
        System.out.println("M_INTERESTING - " + rows.get(12));
        System.out.println("M_MODAL - " + rows.get(13));
    }
}