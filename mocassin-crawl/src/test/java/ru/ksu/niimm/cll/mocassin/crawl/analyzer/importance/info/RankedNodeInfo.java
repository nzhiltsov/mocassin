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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance.info;

public class RankedNodeInfo {
	private String docId;
	private String elementId;
	private String labelText;
	private int rankNumber;
	private int elementsCount;
	private float rankValue;

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public int getRankNumber() {
		return rankNumber;
	}

	public void setRankNumber(int rankNumber) {
		this.rankNumber = rankNumber;
	}

	public float getRankValue() {
		return rankValue;
	}

	public void setRankValue(float rankValue) {
		this.rankValue = rankValue;
	}

	public int getElementsCount() {
		return elementsCount;
	}

	public void setElementsCount(int elementsCount) {
		this.elementsCount = elementsCount;
	}

	@Override
	public String toString() {
		return String.format("%s | %s | %s | %d | %d | %f", getDocId(),
				getElementId(), getLabelText(), getRankNumber(),
				getElementsCount(), getRankValue());
	}

}
