package com.edgar.analyzer.feature.gen;

import com.edgar.analyzer.utils.CompanyRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FeatureGeneration {

    private final FinancialLexicon lexicon;
    private final List<String> allWords;
    private final String ticker;

    public FeatureGeneration(String ticker) throws Exception {
        lexicon = new FinancialLexicon();

        allWords = new ArrayList<>();
        allWords.addAll(lexicon.positiveWords);
        allWords.addAll(lexicon.negativeWords);
        allWords.addAll(lexicon.litiguousWords);
        allWords.addAll(lexicon.constrainingWords);
        allWords.addAll(lexicon.strongModalWords);
        allWords.addAll(lexicon.weakModalWords);
        allWords.addAll(lexicon.uncertaintyWords);

        this.ticker = ticker;

    }

    public CompanyRecord listWordRatios(String secDocument) {
        StringTokenizer tokenizer = new StringTokenizer(secDocument);

        double noOfPositiveWords = 0;
        double noOfNegativeWords = 0;
        double noOfLitiguousWords = 0;
        double noOfConstrainingWords = 0;
        double noOfStrongModalWords = 0;
        double noOfWeakModalWords = 0;
        double noOfUncertaintyWords = 0;

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = token.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");

            if (allWords.contains(token)) {
                if (lexicon.positiveWords.contains(token)) {
                    noOfPositiveWords += 1;
                }
                if (lexicon.negativeWords.contains(token)) {
                    noOfNegativeWords += 1;
                }
                if (lexicon.litiguousWords.contains(token)) {
                    noOfLitiguousWords += 1;
                }
                if (lexicon.constrainingWords.contains(token)) {
                    noOfConstrainingWords += 1;
                }
                if (lexicon.strongModalWords.contains(token)) {
                    noOfStrongModalWords += 1;
                }
                if (lexicon.weakModalWords.contains(token)) {
                    noOfWeakModalWords += 1;
                }
                if (lexicon.uncertaintyWords.contains(token)) {
                    noOfUncertaintyWords += 1;
                }
            }
        }

        double noOfMatchedWords = noOfPositiveWords +
                noOfNegativeWords +
                noOfLitiguousWords +
                noOfConstrainingWords +
                noOfStrongModalWords +
                noOfWeakModalWords +
                noOfUncertaintyWords;

        CompanyRecord record = new CompanyRecord();
        record.setTicker(this.ticker);

        if (noOfMatchedWords > 0) {
            record.setNoOfPositiveWords((noOfPositiveWords * 100.0) / noOfMatchedWords);
            record.setNoOfNegativeWords((noOfNegativeWords * 100.0) / noOfMatchedWords);
            record.setNoOfConstrainingWords((noOfConstrainingWords * 100.0) / noOfMatchedWords);
            record.setNoOfStrongModalWords((noOfStrongModalWords * 100.0) / noOfMatchedWords);
            record.setNoOfWeakModalWords((noOfWeakModalWords * 100.0) / noOfMatchedWords);
            record.setNoOfUncertaintyWords((noOfUncertaintyWords * 100.0) / noOfMatchedWords);
            record.setNoOfLitiguousWords((noOfLitiguousWords * 100.0) / noOfMatchedWords);
        } else {
            record.setNoOfPositiveWords(0.0);
            record.setNoOfNegativeWords(0.0);
            record.setNoOfLitiguousWords(0.0);
            record.setNoOfConstrainingWords(0.0);
            record.setNoOfStrongModalWords(0.0);
            record.setNoOfWeakModalWords(0.0);
            record.setNoOfUncertaintyWords(0.0);
        }
        return record;
    }
}