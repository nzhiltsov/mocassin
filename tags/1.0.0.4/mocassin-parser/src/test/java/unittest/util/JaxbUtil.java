package unittest.util;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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
			throws JAXBException, IOException {

		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(graphContainer, new FileOutputStream(graphContainer
				.getFileName()));
	}
}
