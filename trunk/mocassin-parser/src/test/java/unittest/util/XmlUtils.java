package unittest.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;

public class XmlUtils {

	private static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";

	private XmlUtils() {
	}

	public static void save(GraphContainer graphContainer) throws IOException {
		String rootDir = graphContainer.getRootDir();
		String filename = graphContainer.getFileName();

		File rootFile = new File(rootDir);
		if (!rootFile.canRead()) {
			if (!rootFile.mkdir()) {
				throw new RuntimeException(String.format(
						"couldn't create root folder:%s", rootFile.toString()));
			}
		}

		File dirPerDocument = new File(String
				.format("%s/%s", rootDir, filename));
		if (!dirPerDocument.mkdir())
			throw new RuntimeException(String.format(
					"couldn't create folder per document:%s", dirPerDocument
							.toString()));

		for (Edge<Node, Node> edge : graphContainer.getGraph()) {

			String fromStr = edge.getFrom().getName();
			String fromIdStr = edge.getFrom().getId();
			String toStr = edge.getTo().getName();
			String toIdStr = edge.getTo().getId();
			String aroundTextStr = edge.getContext().getAroundText();
			String refIdStr = edge.getContext().getRefId();
			StringBuffer sb = new StringBuffer();
			sb.append(HEADER);
			sb
					.append(String
							.format(
									"<aroundText from=\"%s\" fromId=\"%s\" to=\"%s\" toId=\"%s\" refid=\"%s\" filename=\"%s\">%s</aroundText>",
									fromStr, fromIdStr, toStr, toIdStr,
									refIdStr, filename, aroundTextStr));
			FileWriter fileWriter = new FileWriter(String.format("%s/%s",
					dirPerDocument.getAbsolutePath(), refIdStr));
			fileWriter.write(sb.toString());
			fileWriter.flush();
			fileWriter.close();
		}

	}
}
