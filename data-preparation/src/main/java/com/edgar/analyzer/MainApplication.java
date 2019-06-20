package com.edgar.analyzer;

import com.edgar.analyzer.feature.gen.DataMerger;

public class MainApplication {

    public static void main(String[] args) throws Exception {
        DataMerger dataMerger = new DataMerger("test6.csv");
        dataMerger.mergeData();
        dataMerger.closeFile();
    }
}