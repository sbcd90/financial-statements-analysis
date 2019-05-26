package com.edgar.analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class SentimentOf10k {

    private Map<String, Double> dictionary;
    private String pathToFile;
    private String pathToNearestWordsFile;

    public SentimentOf10k(String pathToFile, String pathToNearestWordsFile) {
        this.pathToFile = getClass().getClassLoader().getResource(pathToFile).getPath();
        this.pathToNearestWordsFile = getClass().getClassLoader().getResource(pathToNearestWordsFile).getPath();
        this.dictionary = new HashMap<>();
    }

    public void extract() throws Exception {
        BufferedReader csv = null;
        Map<String, Map<Integer, Double>> tempDictionary = new HashMap<>();
        try {
            csv = new BufferedReader(new FileReader(this.pathToFile));
            int lineNumber = 0;

            String line;
            while ((line = csv.readLine()) != null) {
                lineNumber++;

                if (!line.trim().startsWith("#")) {
                    String[] data = line.split("\t");
                    String wordTypeMarker = data[0];

                    if (data.length != 6) {
                        throw new IllegalArgumentException("Incorrect tabulation format in file, line: " + lineNumber);
                    }

                    double synsetScore = Double.parseDouble(data[2]) - Double.parseDouble(data[3]);

                    String[] synTermsSplit = data[4].split(" ");

                    for (String synTermSplit: synTermsSplit) {
                        String[] synTermAndRank = synTermSplit.split("#");
                        String synTerm = synTermAndRank[0] + "#" + wordTypeMarker;

                        int synTermRank = Integer.parseInt(synTermAndRank[1]);

                        if (!tempDictionary.containsKey(synTerm)) {
                            tempDictionary.put(synTerm, new HashMap<>());
                        }
                        tempDictionary.get(synTerm).put(synTermRank, synsetScore);
                    }
                }
            }

            for (Map.Entry<String, Map<Integer, Double>> entry: tempDictionary.entrySet()) {
                String word = entry.getKey();
                Map<Integer, Double> synSetScoreMap = entry.getValue();

                double score = 0.0;
                double sum = 0.0;
                for (Map.Entry<Integer, Double> setScore: synSetScoreMap.entrySet()) {
                    score += setScore.getValue() / (double) setScore.getKey();
                    sum += 1.0 / (double) setScore.getKey();
                }
                if (sum > 0.0) {
                    score /= sum;
                }
                dictionary.put(word, score);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (csv != null) {
                csv.close();
            }
        }
    }

    public Double getWeight(String word, String pos) {
        return this.dictionary.get(word + "#" + pos);
    }

    public Map<String, Double> extractNearbyWords() throws Exception {
        Map<String, Double> finalWordList = new HashMap<>();
        BufferedReader rd = null;
        try {
            String line = null;
            rd = new BufferedReader(new FileReader(pathToNearestWordsFile));
            while ((line = rd.readLine()) != null) {
                Map<String, List<String>> words = getNearbyWordsForWords(line);

                for (Map.Entry<String, List<String>> word: words.entrySet()) {
                    Double weight = getWeight(word.getKey(), "a");
                    if (weight == null) {
                        weight = getWeight(word.getKey(), "n");
                    }

                    if (weight == null) {
                        weight = 0.0;
                    }

                    for (String wordItem: word.getValue()) {
                        Double wordItemWeight = getWeight(wordItem, "a");
                        if (wordItemWeight == null) {
                            wordItemWeight = getWeight(wordItem, "n");
                        }

                        if (wordItemWeight != null) {
                            weight += wordItemWeight;
                        }
                    }
                    finalWordList.put(word.getKey(), weight);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rd != null) {
                rd.close();
            }
        }
        return finalWordList;
    }

    public Map<String, List<String>> getNearbyWordsForWords(String line) {
        String[] wordsUncleaned = line.split(" ");

        String wordKey = wordsUncleaned[2].replace(":", "");

        List<String> wordValues = new ArrayList<>();
        for (int idx = 3; idx < wordsUncleaned.length; idx++) {
            String wordValue = wordsUncleaned[idx].replace(",", "");
            wordValues.add(wordValue);
        }
        return Collections.singletonMap(wordKey, wordValues);
    }

    public static void main(String[] args) throws Exception {

        System.out.println("=============================================");
        System.out.println("twitter10k_2013");
        System.out.println("=============================================");
        SentimentOf10k wordNetDemo = new SentimentOf10k("SentiWordNet.txt", "twitter10k_2013_output.txt");
        wordNetDemo.extract();
        Map<String, Double> finalWords = wordNetDemo.extractNearbyWords();
        for (Map.Entry<String, Double> finalWord: finalWords.entrySet()) {
            System.out.println(finalWord.getKey() + " : " + finalWord.getValue());
        }

        System.out.println("=============================================");
        System.out.println("twitter10k_2014");
        System.out.println("=============================================");
        wordNetDemo = new SentimentOf10k("SentiWordNet.txt", "twitter10k_2014_output.txt");
        wordNetDemo.extract();
        finalWords = wordNetDemo.extractNearbyWords();
        for (Map.Entry<String, Double> finalWord: finalWords.entrySet()) {
            System.out.println(finalWord.getKey() + " : " + finalWord.getValue());
        }

        System.out.println("=============================================");
        System.out.println("twitter10k_2015");
        System.out.println("=============================================");
        wordNetDemo = new SentimentOf10k("SentiWordNet.txt", "twitter10k_2015_output.txt");
        wordNetDemo.extract();
        finalWords = wordNetDemo.extractNearbyWords();
        for (Map.Entry<String, Double> finalWord: finalWords.entrySet()) {
            System.out.println(finalWord.getKey() + " : " + finalWord.getValue());
        }
//        System.out.println(wordNetDemo.getWeight("decline", "n"));
    }
}