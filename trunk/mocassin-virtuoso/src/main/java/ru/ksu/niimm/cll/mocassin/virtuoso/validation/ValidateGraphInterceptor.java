package ru.ksu.niimm.cll.mocassin.virtuoso.validation;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;

public class ValidateGraphInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object[] args = invocation.getArguments();
		if (args == null) {
			Method method = invocation.getMethod();
			throw new NullPointerException(String.format(
					"invalid call method: %s", method.getName()));
		}
		for (Object arg : args) {
			if (arg instanceof RDFGraph) {
				RDFGraph graph = (RDFGraph) arg;
				ValidationCode validationCode = validate(graph);
				if (validationCode == ValidationCode.CORRECT) {
					return invocation.proceed();
				} else {
					throw new IllegalArgumentException(
							String
									.format(
											"graph parameter is invalid: '%s' cannot be null or empty",
											validationCode));
				}
			}
		}
		throw new IllegalArgumentException("graph parameter is required");
	}

	private ValidationCode validate(RDFGraph graph) {
		if (graph == null)
			return ValidationCode.ERROR_GRAPH;
		if (isEmpty(graph.getUrl()))
			return ValidationCode.ERROR_URL;
		if (isEmpty(graph.getUsername()))
			return ValidationCode.ERROR_USERNAME;
		if (isEmpty(graph.getPassword()))
			return ValidationCode.ERROR_PASSWORD;
		if (isEmpty(graph.getIri()))
			return ValidationCode.ERROR_IRI;
		return ValidationCode.CORRECT;
	}

	private boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	enum ValidationCode {

		CORRECT("correct"), ERROR_GRAPH("graph"), ERROR_URL("URL"), ERROR_USERNAME(
				"username"), ERROR_PASSWORD("password"), ERROR_IRI("IRI");
		private String value;

		private ValidationCode(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

	}
}
