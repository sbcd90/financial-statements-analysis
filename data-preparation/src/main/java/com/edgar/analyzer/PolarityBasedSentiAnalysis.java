package com.edgar.analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PolarityBasedSentiAnalysis {

    private static final String POSITIVE_WORDS_TEXT = "positive-words.txt";
    private static final String NEGATIVE_WORDS_TEXT = "negative-words.txt";
    private static final String NEGATIVE_MASTER_DICT_TEXT = "negative-master-dictionary.txt";
    private static final String POSITIVE_MASTER_DICT_TEXT = "positive-master-dictionary.txt";
    private static final String UNCERTAINTY_MASTER_DICT_TEXT = "uncertainty-master-dictionary.txt";
    private static final String LITIGIOUS_MASTER_DICT_TEXT = "litigious-master-dictionary.txt";
    private static final String CONSTRAINING_MASTER_DICT_TEXT = "constraining-master-dictionary.txt";
    private static final String SUPERFLUOUS_MASTER_DICT_TEXT = "superfluous-master-dictionary.txt";
    private static final String INTERESTING_MASTER_DICT_TEXT = "interesting-master-dictionary.txt";
    private static final String MODAL_MASTER_DICT_TEXT = "modal-master-dictionary.txt";

    private List<String> positiveWordsList;
    private List<String> negativeWordsList;
    private List<String> positiveMasterDictList;
    private List<String> negativeMasterDictList;
    private List<String> uncertaintyMasterDictList;
    private List<String> litigiousMasterDictList;
    private List<String> constrainingMasterDictList;
    private List<String> superfluousMasterDictList;
    private List<String> interestingMasterDictList;
    private List<String> modalMasterDictList;

    private List<Object> rowList;

    private int netcnt2 = 0;
    private int netcnt = 0;
    private int counti = 1;
    private int qa = 0;
    private int qb = 0;
    private int qc = 0;
    private int qa2 = 0;
    private int qb2 = 0;
    private int qc2 = 0;
    private int unc = 0;
    private int lit = 0;
    private int con = 0;
    private int sup = 0;
    private int inte = 0;
    private int mod = 0;

    public List<Object> getResults() {
        return this.rowList;
    }

    private List<String> readWords(String filename) throws IOException {
        BufferedReader br = null;
        StringBuilder value = new StringBuilder();
        try {
            URL url = getClass().getClassLoader().getResource(filename);
            br = new BufferedReader(new FileReader(url.getFile()));

            String line;
            while ((line = br.readLine()) != null) {
                value.append(line).append(",");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
        if (value.toString().length() > 1) {
            return Arrays.asList(value.toString()
                    .substring(0, value.length() - 1)
                    .split(","));
        }
        return Collections.emptyList();
    }

    private List<String> readPositiveWords() throws IOException {
        return this.readWords(POSITIVE_WORDS_TEXT);
    }

    private List<String> readNegativeWords() throws IOException {
        return this.readWords(NEGATIVE_WORDS_TEXT);
    }

    private List<String> readPositiveWordsInMasterDict() throws IOException {
        return this.readWords(POSITIVE_MASTER_DICT_TEXT);
    }

    private List<String> readNegativeWordsInMasterDict() throws IOException {
        return this.readWords(NEGATIVE_MASTER_DICT_TEXT);
    }

    private List<String> readUncertaintyWordsInMasterDict() throws IOException {
        return this.readWords(UNCERTAINTY_MASTER_DICT_TEXT);
    }

    private List<String> readLitiguousWordsInMasterDict() throws IOException {
        return this.readWords(LITIGIOUS_MASTER_DICT_TEXT);
    }

    private List<String> readConstrainingWordsInMasterDict() throws IOException {
        return this.readWords(CONSTRAINING_MASTER_DICT_TEXT);
    }

    private List<String> readSuperFluousWordsInMasterDict() throws IOException {
        return this.readWords(SUPERFLUOUS_MASTER_DICT_TEXT);
    }

    private List<String> readInterestingWordsInMasterDict() throws IOException {
        return this.readWords(INTERESTING_MASTER_DICT_TEXT);
    }

    private List<String> readModalWordsInMasterDict() throws IOException {
        return this.readWords(MODAL_MASTER_DICT_TEXT);
    }

    private List<String> getWordsFromTextString(String text) {
        return Arrays.asList(text.split(" "));
    }

    protected void calculateScores(String text) {
        List<String> words = this.getWordsFromTextString(text);

        for (String posWord: positiveWordsList) {
            if (words.contains(posWord)) {
                qa += 1;
            }
        }

        for (String negWord: negativeWordsList) {
            if (words.contains(negWord)) {
                qb += 1;
            }
        }

        qc = qa - qb;

        String sentiment = null;
        if (qc > 0) {
            sentiment = "POSITIVE";
        } else if (qc == 0) {
            sentiment = "NEUTRAL";
        } else {
            sentiment = "NEGATIVE";
        }

        for (String posWord: positiveMasterDictList) {
            if (words.contains(posWord)) {
                qa2 += 1;
            }
        }

        for (String negWord: negativeMasterDictList) {
            if (words.contains(negWord)) {
                qb2 += 1;
            }
        }

        qc2 = qa2 - qb2;

        String sentiment2 = null;
        if (qc2 > 0) {
            sentiment2 = "POSITIVE";
        } else if (qc2 == 0) {
            sentiment2 = "NEUTRAL";
        } else {
            sentiment2 = "NEGATIVE";
        }

        for (String uncertainWord: uncertaintyMasterDictList) {
            if (words.contains(uncertainWord)) {
                unc += 1;
            }
        }

        for (String litiguousWord: litigiousMasterDictList) {
            if (words.contains(litiguousWord)) {
                lit += 1;
            }
        }

        for (String constrainingWord: constrainingMasterDictList) {
            if (words.contains(constrainingWord)) {
                con += 1;
            }
        }

        for (String superfluousWord: superfluousMasterDictList) {
            if (words.contains(superfluousWord)) {
                sup += 1;
            }
        }

        for (String interestingWord: interestingMasterDictList) {
            if (words.contains(interestingWord)) {
                inte += 1;
            }
        }

        for (String modalWord: modalMasterDictList) {
            if (words.contains(modalWord)) {
                mod += 1;
            }
        }

        rowList = new ArrayList<>();
        rowList.add(qa);
        rowList.add(qb);
        rowList.add(qc);
        rowList.add(sentiment);
        rowList.add(qa2);
        rowList.add(qb2);
        rowList.add(qc2);
        rowList.add(sentiment2);
        rowList.add(unc);
        rowList.add(lit);
        rowList.add(con);
        rowList.add(sup);
        rowList.add(inte);
        rowList.add(mod);
    }

    protected void readDictionaries() throws IOException {
        positiveWordsList = this.readPositiveWords();
        negativeWordsList = this.readNegativeWords();
        positiveMasterDictList = this.readPositiveWordsInMasterDict();
        negativeMasterDictList = this.readNegativeWordsInMasterDict();
        uncertaintyMasterDictList = this.readUncertaintyWordsInMasterDict();
        litigiousMasterDictList = this.readLitiguousWordsInMasterDict();
        constrainingMasterDictList = this.readConstrainingWordsInMasterDict();
        superfluousMasterDictList = this.readSuperFluousWordsInMasterDict();
        interestingMasterDictList = this.readInterestingWordsInMasterDict();
        modalMasterDictList = this.readModalWordsInMasterDict();
    }

    public static void main(String[] args) throws Exception {
        PolarityBasedSentiAnalysis polarityBasedSentiAnalysis = new PolarityBasedSentiAnalysis();
        polarityBasedSentiAnalysis.readDictionaries();

        polarityBasedSentiAnalysis.calculateScores("hello world");
        System.out.println(polarityBasedSentiAnalysis.rowList.get(3));
    }
}