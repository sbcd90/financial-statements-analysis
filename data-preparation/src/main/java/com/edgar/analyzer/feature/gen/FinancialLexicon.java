package com.edgar.analyzer.feature.gen;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class FinancialLexicon {

    public List<String> positiveWords;

    public List<String> negativeWords;

    public List<String> litiguousWords;

    public List<String> constrainingWords;

    public List<String> strongModalWords;

    public List<String> weakModalWords;

    public List<String> uncertaintyWords;

    public FinancialLexicon() throws Exception {
        this.fillPositiveWords();
        this.fillNegativeWords();
        this.fillLitiguousWords();
        this.fillCostrainingWords();
        this.fillStrongModalWords();
        this.fillWeakModalWords();
        this.fillUncertaintyWords();
    }

    public List<String> readWords(String fileName) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is != null) {
            String words = IOUtils.toString(is, Charset.defaultCharset());
            return Arrays.asList(words.toLowerCase().split("\r\n"));
        }
        return Arrays.asList();
    }

    public void fillPositiveWords() throws Exception {
        this.positiveWords = readWords("positive.txt");
    }

    public void fillNegativeWords() throws Exception {
        this.negativeWords = readWords("negative.txt");
    }

    public void fillLitiguousWords() throws Exception {
        this.litiguousWords = readWords("litigious.txt");
    }

    public void fillCostrainingWords() throws Exception {
        this.constrainingWords = readWords("constraining.txt");
    }

    public void fillStrongModalWords() throws Exception {
        this.strongModalWords = readWords("strong_modal.txt");
    }

    public void fillWeakModalWords() throws Exception {
        this.weakModalWords = readWords("weak_modal.txt");
    }

    public void fillUncertaintyWords() throws Exception {
        this.uncertaintyWords = readWords("uncertainty.txt");
    }

    public static void main(String[] args) throws Exception {
        FinancialLexicon lexicon = new FinancialLexicon();
        System.out.println(lexicon.positiveWords.size());
    }
}