package com.backbase.movie.service.domain;

import com.opencsv.bean.CsvBindByName;

public class MovieInfo {

    @CsvBindByName(column = "Year")
    private String year;
    @CsvBindByName(column = "Category")
    private String category;
    @CsvBindByName(column = "Nominee")
    private String nominee;
    @CsvBindByName(column = "Additional Info")
    private String additionalInfo;
    @CsvBindByName(column = "Won?")
    private String won;

    public String getYear() {
        return year;
    }

    public String getCategory() {
        return category;
    }

    public String getNominee() {
        return nominee;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public String getWon() {
        return won;
    }
}
