/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
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
    /**
     * this is computed as a product of the both elements' neighborhood
     * cardinalities
     */
    private final int preferentialAttachmentScore;
    /**
     * First element's PageRank, which is computed by considering edge weights and alpha
     * (jump probability) is equal to .2
     */
    private final float fromPR;
    /**
     * Second element's PageRank, which is computed by considering edge weights and alpha
     * (jump probability) is equal to .2
     */
    private final float toPR;

    public static class Builder {
	private final StructuralElement from;
	private final StructuralElement to;
	private float neighborJaccardCoefficient;
	private int preferentialAttachmentScore;
	private float fromPR;
	private float toPR;

	public Builder(StructuralElement from, StructuralElement to) {
	    checkNotNull(from);
	    checkNotNull(to);
	    this.from = from;
	    this.to = to;
	}

	public Builder neigborJaccard(float neighborJaccardCoefficient) {
	    checkState(neighborJaccardCoefficient >= 0
		    && neighborJaccardCoefficient <= 1);
	    this.neighborJaccardCoefficient = neighborJaccardCoefficient;
	    return this;
	}

	public Builder preferentialAttachmentScore(
		int preferentialAttachmentScore) {
	    checkState(preferentialAttachmentScore >= 1);
	    this.preferentialAttachmentScore = preferentialAttachmentScore;
	    return this;
	}

	public Builder fromPR(float fromPR) {
	    checkState(fromPR >= 0 && fromPR <= 1);
	    this.fromPR = fromPR;
	    return this;
	}

	public Builder toPR(float toPR) {
	    checkState(toPR >= 0 && toPR <= 1);
	    this.toPR = toPR;
	    return this;
	}

	public RelationFeatureInfo build() {
	    return new RelationFeatureInfo(this);
	}
    }

    private RelationFeatureInfo(Builder builder) {
	this.from = builder.from;
	this.to = builder.to;
	this.neighborJaccardCoefficient = builder.neighborJaccardCoefficient;
	this.preferentialAttachmentScore = builder.preferentialAttachmentScore;
	this.fromPR = builder.fromPR;
	this.toPR = builder.toPR;
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

    public int getPreferentialAttachmentScore() {
	return preferentialAttachmentScore;
    }

    public float getFromPR() {
	return fromPR;
    }

    public float getToPR() {
	return toPR;
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
