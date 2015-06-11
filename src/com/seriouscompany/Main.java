package com.seriouscompany;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Main
{

    public static void main(String[] args)
    {
        // Load json
        HashMap<String, WordArticleHolder> inMemoryData = extractDataToMemory();

        // Load words from text file
        ArrayList<String> knownWords = getKnownWords();


        ArrayList<WordArticleHolder> selectedWordInformation = getSelectedWordsFromMemory(inMemoryData, knownWords);

        HashMap<String, IntegerWrapper> accumulateWordUseCount = accumulateCounts(selectedWordInformation);

        // Move articles to an ArrayList
        Set<Map.Entry<String,IntegerWrapper>> articleSet = accumulateWordUseCount.entrySet();

        ArrayList<ArticleExampleInformation> articleData = new ArrayList<>();
        for(Map.Entry<String,IntegerWrapper> article : articleSet)
        {
            articleData.add(new ArticleExampleInformation(article.getKey(), article.getValue().number));
        }

        // Sort and print our results
        Collections.sort(articleData);
        for(ArticleExampleInformation article : articleData)
        {
            System.out.print(article.getArticleName());
            System.out.println(" : "+article.getUseAmount());
        }

    }

    public static HashMap<String, IntegerWrapper> accumulateCounts(ArrayList<WordArticleHolder> selectedWordInformation)
    {
        HashMap<String, IntegerWrapper> accumulator = new HashMap<String, IntegerWrapper>();

        // for each word...
        for(WordArticleHolder word : selectedWordInformation)
        {
            // and every article referenced in...
            for(ArticleExampleInformation articleInfo : word.getWorduseageInstances())
            {

                // if it already exists just add the number of references to it
                if(accumulator.containsKey(articleInfo.getArticleName()))
                {
                    accumulator.get(articleInfo.getArticleName()).number = accumulator.get(articleInfo.getArticleName()).number + articleInfo.getUseAmount();
                }
                else // we need to add it
                {
                    IntegerWrapper wrappedInteger = new IntegerWrapper(articleInfo.getUseAmount());
                    accumulator.put(articleInfo.getArticleName(), wrappedInteger);
                }
            }
        }

        return accumulator;
    }

    public static ArrayList<WordArticleHolder> getSelectedWordsFromMemory(HashMap<String, WordArticleHolder> inMemoryData, ArrayList<String> knownWords)
    {
        ArrayList<WordArticleHolder> selectedReferences = new ArrayList<WordArticleHolder>();
        // Iterate over every selected word and get a reference if possible
        for(String knownWord : knownWords)
        {
            WordArticleHolder wordInformation = inMemoryData.get(knownWord);
            if(wordInformation == null) // Could not be found in the in memory structure
            {
                System.out.println(knownWord + " not found");
            }
            else
            {
                selectedReferences.add(wordInformation);
            }
        }

        return selectedReferences;
    }

    public static ArrayList<String> getKnownWords()
    {
        ArrayList<String> inputWordsFromFile = new ArrayList<String>();

        BufferedReader inputWordReader;
        try
        {
            inputWordReader = new BufferedReader((new InputStreamReader(new FileInputStream("data/words.txt"), "UTF-8")));
            String line;
            while((line = inputWordReader.readLine()) != null) {
                if(!inputWordsFromFile.contains(line)) // Todo Nightmare for complexity fix this
                {
                    inputWordsFromFile.add(line);
                }
                else
                {
                    System.out.println("Known word input set contains multiple instances of" + line);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("EXCEPTION: IOException in getKnownWords");
            e.printStackTrace();
            return inputWordsFromFile;
        }

        return inputWordsFromFile;
    }

    /*
        Do not try to edit this method.
     */
    public static HashMap<String, WordArticleHolder> extractDataToMemory()
    {
        HashMap<String, WordArticleHolder> dataFromJson = new HashMap<String, WordArticleHolder>();

        JsonReader file;
        try
        {
            file = new JsonReader(new InputStreamReader(new FileInputStream("data/output.json"), "UTF-8"));
        }
        catch (Exception e)
        {
            System.out.println("EXCEPTION: IOException in extractDataToMemory");
            e.printStackTrace();
            return dataFromJson;
        }
        JsonParser jsonParser = new JsonParser();
        JsonObject result = jsonParser.parse(file).getAsJsonObject().getAsJsonObject("words");
        Set<Map.Entry<String,JsonElement>> allWordsEntrySet = result.entrySet();

        // Iterate over every word in the output.json file
        for(Map.Entry<String,JsonElement> word : allWordsEntrySet)
        {

            JsonObject readings = result.getAsJsonObject(word.getKey()).getAsJsonObject("readings");
            Set<Map.Entry<String,JsonElement>> readingSet = readings.entrySet();

            // Prepare a word object here
            WordArticleHolder currentWord = new WordArticleHolder(word.getKey());

            // Iterate over every reading in each word
            for(Map.Entry<String,JsonElement> reading : readingSet)
            {
                JsonObject readingJson = readings.getAsJsonObject(reading.getKey());
                JsonObject examples = readingJson.getAsJsonObject("examples");
                Set<Map.Entry<String,JsonElement>> examplesSet = examples.entrySet();

                // Iterate over all instances of words being used in articles
                for(Map.Entry<String,JsonElement> useExample : examplesSet)
                {
                    JsonArray useLocations = (JsonArray)useExample.getValue();
                    currentWord.addArticleUsageAmountPair(useExample.getKey(), useLocations.size());
                } // end of use instances

            } // end of readings

            dataFromJson.put(word.getKey(), currentWord);

        } // end of word

        return dataFromJson;
    }

}
