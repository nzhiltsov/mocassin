package ru.ksu.niimm.cll.mocassin.nlp.util;

public interface NlpModulePropertiesLoader {
	String get(String key);
	
	int getWindowTokenSize();
	
	boolean useStemming();
}
