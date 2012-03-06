package ru.ksu.niimm.cll.mocassin.parser.impl.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import ru.ksu.niimm.cll.mocassin.parser.impl.EdgeContextImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.EdgeContext;

public class EdgeContextAdapter extends
		XmlAdapter<EdgeContextImpl, EdgeContext> {

	@Override
	public EdgeContextImpl marshal(EdgeContext v) throws Exception {
		return (EdgeContextImpl) v;
	}

	@Override
	public EdgeContext unmarshal(EdgeContextImpl v) throws Exception {
		return v;
	}

}
