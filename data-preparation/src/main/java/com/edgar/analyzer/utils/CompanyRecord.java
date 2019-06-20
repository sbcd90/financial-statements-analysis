package com.edgar.analyzer.utils;

import java.io.Serializable;

public class CompanyRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ticker;

    private String companyName;

    private String companyCategory;

    private String date;

    private String ttmNetIncome;

    private String shareholdersEquity;

    private Double previousRoe;

    private Double percentChangeInRoe;

    private Double noOfPositiveWords;

    private Double noOfNegativeWords;

    private Double noOfLitiguousWords;

    private Double noOfConstrainingWords;

    private Double noOfStrongModalWords;

    private Double noOfWeakModalWords;

    private Double noOfUncertaintyWords;

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getTicker() {
        return ticker;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyCategory(String companyCategory) {
        this.companyCategory = companyCategory;
    }

    public String getCompanyCategory() {
        return companyCategory;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setTtmNetIncome(String ttmNetIncome) {
        this.ttmNetIncome = ttmNetIncome;
    }

    public String getTtmNetIncome() {
        return ttmNetIncome;
    }

    public void setShareholdersEquity(String shareholdersEquity) {
        this.shareholdersEquity = shareholdersEquity;
    }

    public String getShareholdersEquity() {
        return shareholdersEquity;
    }

    public void setPreviousRoe(Double previousRoe) {
        this.previousRoe = previousRoe;
    }

    public Double getPreviousRoe() {
        return previousRoe;
    }

    public void setPercentChangeInRoe(Double percentChangeInRoe) {
        this.percentChangeInRoe = percentChangeInRoe;
    }

    public Double getPercentChangeInRoe() {
        return percentChangeInRoe;
    }

    public void setNoOfPositiveWords(Double noOfPositiveWords) {
        this.noOfPositiveWords = noOfPositiveWords;
    }

    public Double getNoOfPositiveWords() {
        return noOfPositiveWords;
    }

    public void setNoOfNegativeWords(Double noOfNegativeWords) {
        this.noOfNegativeWords = noOfNegativeWords;
    }

    public Double getNoOfNegativeWords() {
        return noOfNegativeWords;
    }

    public void setNoOfLitiguousWords(Double noOfLitiguousWords) {
        this.noOfLitiguousWords = noOfLitiguousWords;
    }

    public Double getNoOfLitiguousWords() {
        return noOfLitiguousWords;
    }

    public void setNoOfConstrainingWords(Double noOfConstrainingWords) {
        this.noOfConstrainingWords = noOfConstrainingWords;
    }

    public Double getNoOfConstrainingWords() {
        return noOfConstrainingWords;
    }

    public void setNoOfStrongModalWords(Double noOfStrongModalWords) {
        this.noOfStrongModalWords = noOfStrongModalWords;
    }

    public Double getNoOfStrongModalWords() {
        return noOfStrongModalWords;
    }

    public void setNoOfWeakModalWords(Double noOfWeakModalWords) {
        this.noOfWeakModalWords = noOfWeakModalWords;
    }

    public Double getNoOfWeakModalWords() {
        return noOfWeakModalWords;
    }

    public void setNoOfUncertaintyWords(Double noOfUncertaintyWords) {
        this.noOfUncertaintyWords = noOfUncertaintyWords;
    }

    public Double getNoOfUncertaintyWords() {
        return noOfUncertaintyWords;
    }
}