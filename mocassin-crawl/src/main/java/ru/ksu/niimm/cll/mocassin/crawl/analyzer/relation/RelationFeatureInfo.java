package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;

public class RelationFeatureInfo {
	private final StructuralElement from;
	private final StructuralElement to;
	/**
	 * this is computed as Jaccard coefficient for neighbors of a given pair of
	 * structural elements
	 */
	private final float neighborJaccardCoefficient;

	public RelationFeatureInfo(StructuralElement from, StructuralElement to,
			float neighborJaccardCoefficient) {
		checkNotNull(from);
		checkNotNull(to);
		checkState(neighborJaccardCoefficient >= 0
				&& neighborJaccardCoefficient <= 1);
		this.from = from;
		this.to = to;
		this.neighborJaccardCoefficient = neighborJaccardCoefficient;
	}

	public StructuralElement getFrom() {
		return from;
	}

	public StructuralElement getTo() {
		return to;
	}

	public float getNeighborJaccardCoefficient() {
		return neighborJaccardCoefficient;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelationFeatureInfo other = (RelationFeatureInfo) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

}
