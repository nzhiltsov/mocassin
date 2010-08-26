package unittest.util;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GraphContainer {
	@XmlTransient
	private String fileName;
	@XmlElementWrapper(name = "edges")
	@XmlElement(name = "edge")
	private List graph;

	public GraphContainer() {
	}

	public GraphContainer(String fileName, List<Edge<Node, Node>> graph) {
		this.fileName = fileName;
		this.graph = graph;
	}

	public List<Edge<Node, Node>> getGraph() {
		return graph;
	}

	public void setGraph(List<Edge<Node, Node>> graph) {
		this.graph = graph;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
