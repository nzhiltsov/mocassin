package unittest.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;

public class XmlUtils {

	private static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<graphContainer>\n";

	private XmlUtils() {
	}

	public static void save(GraphContainer graphContainer) throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append(HEADER);
		for (Edge<Node, Node> edge : graphContainer.getGraph()) {
			String fromStr = edge.getFrom().getName();
			String toStr = edge.getTo().getName();
			String aroundTextStr = edge.getContext().getAroundText();
			String refIdStr = edge.getContext().getRefId();
			sb
					.append(String
							.format(
									"<aroundText from=\"%s\" to=\"%s\" refid=\"%s\">%s</aroundText>",
									fromStr, toStr, refIdStr, aroundTextStr));
		}
		sb.append("\n</graphContainer>");
		FileWriter fileWriter = new FileWriter(graphContainer.getFileName());
		try {
			fileWriter.write(sb.toString());
			fileWriter.flush();
		} finally {
			fileWriter.close();
		}
	}

}
