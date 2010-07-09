package unittest;

import org.junit.Assert;
import org.junit.Test;

import ru.ksu.niimm.ose.ontology.SourceReference;
import ru.ksu.niimm.ose.ontology.impl.util.SrcRefUtil;

public class SourceRefUtilTest {
	@Test
	public void testParse() {
		String srcRefValue = "at main.tex; line 38 col 33";
		String resourceUri = "file:/home/nzhiltsov/projects/thirdparty/stex2/example/paper/main.omdoc";
		SourceReference srcRef = SrcRefUtil.parse(resourceUri, srcRefValue);
		Assert
				.assertEquals(srcRef.getFileName(),
						"file:/home/nzhiltsov/projects/thirdparty/stex2/example/paper/main.tex");
		Assert.assertEquals(srcRef.getLine(), 38);
		Assert.assertEquals(srcRef.getColumn(), 33);

		srcRefValue = "at another.tex; line 128 col 100";
		resourceUri = "file:/home/nzhiltsov/projects/thirdparty/stex2/example/paper/another.omdoc";
		srcRef = SrcRefUtil.parse(resourceUri, srcRefValue);
		Assert
				.assertEquals(srcRef.getFileName(),
						"file:/home/nzhiltsov/projects/thirdparty/stex2/example/paper/another.tex");
		Assert.assertEquals(srcRef.getLine(), 128);
		Assert.assertEquals(srcRef.getColumn(), 100);
	}
}
