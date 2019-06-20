package com.edgar.analyzer.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyData implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("ticker")
    private String ticker;

    @JsonProperty("zacks_x_ind_desc")
    private String category;

    @JsonProperty("comp_name")
    private String name;

    @JsonProperty("link")
    private String link;

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    public String getLink() {
        return link.split("href='")[1]
                .split("'>")[0]
                .replace("stock-price-history", "roe");
    }
}