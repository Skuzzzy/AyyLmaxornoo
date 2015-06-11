package com.seriouscompany;

import java.util.ArrayList;

/**
 * Created by Daniel on 6/10/2015.
 */
public class WordArticleHolder {

    private String word;
    private ArrayList<ArticleExampleInformation> worduseageInstances;

    public WordArticleHolder(String word)
    {
        this.word = word;
        this.worduseageInstances = new ArrayList<ArticleExampleInformation>();
    }

    public void addArticleUsageAmountPair(String article, int instances)
    {
        worduseageInstances.add(new ArticleExampleInformation(article, instances));
    }

    public ArrayList<ArticleExampleInformation> getWorduseageInstances()
    {
        return this.worduseageInstances;
    }

    public String getWord()
    {
        return this.word;
    }


}
