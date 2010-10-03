package unittest.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class PrepareDocumentUtil {

	private PrepareDocumentUtil() {
	}

	/**
	 * copy the only XML documents with non-empty text content
	 */
	public static void copyNonEmptyDocuments(File fromDir, File toDir)
			throws Exception {
		for (File file : fromDir.listFiles()) {
			if (file.isDirectory()) {
				File newDir = new File(toDir + "/" + file.getName());
				if (!newDir.mkdir())
					throw new RuntimeException(String.format(
							"couldn't create directory: %s", newDir.toString()));
				copyNonEmptyDocuments(file, newDir);
			} else {
				boolean isEmptyDocument = isEmpty(file);
				if (!isEmptyDocument) {
					InputStream in = new FileInputStream(file);
					FileOutputStream out = new FileOutputStream(toDir + "/"
							+ file.getName(), true);
					try {
						IOUtils.copy(in, out);
					} catch (Exception e) {
						IOUtils.closeQuietly(in);
						IOUtils.closeQuietly(out);
					}
				}
			}
		}
	}

	private static boolean isEmpty(File file)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(true);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		InputStream inputStream = new FileInputStream(file);
		Document doc = documentBuilder.parse(inputStream);
		return isEmptyContent(doc);
	}

	private static boolean isEmptyContent(Node node) {
		if (node.getChildNodes().getLength() == 0) {
			boolean isEmpty = node.getTextContent() == null ? true : node
					.getTextContent().replaceAll("[ \t\r\n]", "").equals("");
			return isEmpty;
		}
		
		boolean isEmptyChild = true;
		for (int i = 0; i < node.getChildNodes().getLength() && isEmptyChild; i++) {
			Node child = node.getChildNodes().item(i);
			isEmptyChild = isEmptyChild && isEmptyContent(child);
		}
		return isEmptyChild;
	}
}
