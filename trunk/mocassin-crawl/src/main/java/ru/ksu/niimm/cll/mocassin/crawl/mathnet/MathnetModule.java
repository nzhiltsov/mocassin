package ru.ksu.niimm.cll.mocassin.crawl.mathnet;

import ru.ksu.niimm.cll.mocassin.crawl.ArXMLivAdapter;
import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class MathnetModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ArXMLivAdapter.class).to(MathnetAdapter.class);
		bindListener(Matchers.any(), new Slf4jTypeListener());
	}
}
