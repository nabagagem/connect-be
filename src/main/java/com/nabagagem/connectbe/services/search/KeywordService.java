package com.nabagagem.connectbe.services.search;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class KeywordService {
    private static final String FIELD_NAME = "sampleName";

    @SneakyThrows
    public Set<String> extractFrom(String text) {
        Set<String> result = new HashSet<>();
        try (Analyzer analyzer = new PortugueseAnalyzer()) {
            TokenStream tokenStream = analyzer.tokenStream(FIELD_NAME, text);
            CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String keyword = attr.toString().trim();
                if (!keyword.isEmpty() && !keyword.equals("null")) {
                    result.add(keyword);
                }
            }
            log.info("Keywords extracted from text: {}", result);
            return result;
        }
    }
}
