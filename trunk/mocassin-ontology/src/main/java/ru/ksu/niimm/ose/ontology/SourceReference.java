package ru.ksu.niimm.ose.ontology;

public class SourceReference {
	/**
	 * source file full name
	 * e.g. file:/collections/example.tex
	 */
	private String fileName;
	/**
	 * number of line in source file
	 */
	private int line;
	/*
	 * number of column in source file
	 */
	private int column;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + line;
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
		SourceReference other = (SourceReference) obj;
		if (column != other.column)
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (line != other.line)
			return false;
		return true;
	}

}
