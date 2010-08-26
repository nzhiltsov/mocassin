package unittest.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.EdgeContext;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.impl.EdgeContextImpl;
import ru.ksu.niimm.cll.mocassin.parser.impl.EdgeImpl;
import ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl;

public class JaxbUtil {
	private static JAXBContext jaxbContext;
	static {
		try {
			jaxbContext = JAXBContext.newInstance(GraphContainer.class,
					EdgeImpl.class, NodeImpl.class, EdgeContextImpl.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private JaxbUtil() {
	}

	public static void marshall(GraphContainer graphContainer)
			throws JAXBException, FileNotFoundException {

		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", true);
		marshaller.marshal(graphContainer, new FileOutputStream(graphContainer
				.getFileName()));
	}
}
