package com.seriouscompany;

/**
 * Created by Daniel on 6/10/2015.
 */
public class ArticleExampleInformation implements Comparable{
    private String articleName;
    private int useAmount;

    public ArticleExampleInformation(String articleName, int useAmount)
    {
        this.articleName = articleName;
        this.useAmount = useAmount;
    }

    public String getArticleName()
    {
        return this.articleName;
    }

    public int getUseAmount()
    {
        return this.useAmount;
    }

    public int compareTo(Object o)
    {
        return -(((ArticleExampleInformation)o).getUseAmount() - this.useAmount);
    }
}
