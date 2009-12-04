package de.topicmapslab.aranuka.codegen.core.lazy;

import de.topicmapslab.aranuka.codegen.core.AranukaCodeGenTestCase;
import de.topicmapslab.aranuka.codegen.core.TopicMap2JavaMapper;
import de.topicmapslab.aranuka.codegen.core.code.POJOTypes;
import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;

/**
 * 
 * @author Sven Krosse
 *
 */
public class LazyPojoGeneratorTest extends AranukaCodeGenTestCase {

	public void testClassFileGeneration() throws TopicMap2JavaMapperException {
		final String packageName = getClass().getPackage().getName()
				+ ".generated";
		TopicMap2JavaMapper mapper = new TopicMap2JavaMapper(getTopicMapSystem(), getTopicMap(),
						packageName, getDirectoryPrefix()
								+ packageName.replaceAll("\\.", "/"));
		mapper.run(POJOTypes.LAZY_POJO);
	}

}
