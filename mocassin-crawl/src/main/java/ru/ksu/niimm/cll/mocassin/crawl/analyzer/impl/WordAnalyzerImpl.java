package ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl;

import com.google.common.collect.ImmutableSet;
import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.collections.CollectionUtils;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.WordAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.WordFeatureInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordAnalyzerImpl implements WordAnalyzer {
    private static ImmutableSet<String> wordSet;

    static {
        InputStream stream = WordAnalyzerImpl.class.getClassLoader().getResourceAsStream("feature_words.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        ImmutableSet.Builder<String> builder = ImmutableSet.<String>builder();
        try {
            String line = reader.readLine();
            while(line != null && !line.equals("")) {
                builder.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        wordSet = builder.build();
    }

    @Override
    public List<WordFeatureInfo> extractWordFeatures(Graph<StructuralElement, Reference> graph) {
        ArrayList<WordFeatureInfo> result = new ArrayList<WordFeatureInfo>();
        ArrayList<StructuralElement> elements = new ArrayList<StructuralElement>(graph.getVertices());

        for (StructuralElement element: elements) {
            List<String> contents = element.getStemContents();
            List<String> intersection = new ArrayList<String>(CollectionUtils.intersection(contents, wordSet));
            if (intersection.size() > 0) {
                WordFeatureInfo.Builder builder = new WordFeatureInfo.Builder(element);
                for (String stem: intersection) {
                    builder.word(stem);
                }
                result.add(builder.build());
            }
        }

        return result;
    }
}
