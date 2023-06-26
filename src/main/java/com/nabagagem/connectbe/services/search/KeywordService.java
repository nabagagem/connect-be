package com.nabagagem.connectbe.services.search;

import lombok.SneakyThrows;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class KeywordService {
    private static final String FIELD_NAME = "sampleName";
    private final Analyzer analyzer = new PortugueseAnalyzer();

    @SneakyThrows
    public Set<String> extractFrom(String text) {
        Set<String> result = new HashSet<>();
        TokenStream tokenStream = analyzer.tokenStream(FIELD_NAME, text);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }

    public Set<String> extractFrom(Object... objects) {
        return extractFrom(Stream.of(objects).map(Object::toString).collect(Collectors.joining(" ")));
    }
}
