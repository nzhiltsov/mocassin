package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The class captures candidate relation features in the context of the document
 * graph
 *
 * @author Nikita Zhiltsov
 * @author Azat Khasanshin
 */
public class WordFeatureInfo {
    private final StructuralElement element;

    private final List<String> words;

    public static class Builder {
        private final StructuralElement element;
        private final List<String> words;

        public Builder(StructuralElement element) {
            checkNotNull(element);
            this.element = element;
            words = new ArrayList<String>();
        }

        public Builder word(String word) {
            this.words.add(word);
            return this;
        }

        public WordFeatureInfo build() {
            return new WordFeatureInfo(this);
        }
    }

    private WordFeatureInfo(Builder builder) {
        this.element = builder.element;
        this.words = builder.words;
    }

    public StructuralElement getElement() {
        return this.element;
    }

    public List<String> getWords() {
        return this.words;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((element == null) ? 0 : element.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WordFeatureInfo other = (WordFeatureInfo) obj;
        if (element == null) {
            if (other.element != null)
                return false;
        } else if (!element.equals(other.element))
            return false;
        return this.words == other.getWords();
    }

    @Override
    public String toString() {
        return element.getId() + " " + words;
    }
}
