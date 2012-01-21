package unittest;

import junit.framework.Assert;

import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.virtuoso.generator.DescribeQueryGenerator;

import com.google.inject.Inject;

public class DescribeQueryGeneratorTest extends AbstractTest {
	private static final String RESOURCE_URI = "http://mathnet.ru/ivm18";
	@Inject
	private DescribeQueryGenerator describeQueryGenerator;

	@Test
	public void testGenerate() {
		String generatedExpression = getDescribeQueryGenerator().generate(
				RESOURCE_URI, getGraph());
		String expectedExpression = String.format("DESCRIBE <%s> FROM NAMED <%s>",
				RESOURCE_URI, getGraph().getIri());
		Assert.assertEquals(expectedExpression, generatedExpression);
	}

	private DescribeQueryGenerator getDescribeQueryGenerator() {
		return describeQueryGenerator;
	}

}
