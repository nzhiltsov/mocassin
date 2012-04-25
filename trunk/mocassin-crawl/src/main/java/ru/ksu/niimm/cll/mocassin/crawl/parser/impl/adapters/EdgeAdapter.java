package ru.ksu.niimm.cll.mocassin.crawl.parser.impl.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import ru.ksu.niimm.cll.mocassin.crawl.parser.impl.EdgeImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Edge;

public class EdgeAdapter extends XmlAdapter<EdgeImpl, Edge> {

	@Override
	public EdgeImpl marshal(Edge v) throws Exception {
		return (EdgeImpl) v;
	}

	@Override
	public Edge unmarshal(EdgeImpl v) throws Exception {
		return v;
	}

}
