package ru.ksu.niimm.cll.mocassin.parser.pdf;

@SuppressWarnings("serial")
public class PdflatexCompilationException extends Exception {

	public PdflatexCompilationException() {
	
	}

	public PdflatexCompilationException(String message) {
		super(message);
	
	}

	public PdflatexCompilationException(Throwable cause) {
		super(cause);
	
	}

	public PdflatexCompilationException(String message, Throwable cause) {
		super(message, cause);
	
	}

}
