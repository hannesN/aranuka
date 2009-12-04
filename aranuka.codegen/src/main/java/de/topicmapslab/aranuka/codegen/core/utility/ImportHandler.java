package de.topicmapslab.aranuka.codegen.core.utility;

import java.util.HashSet;
import java.util.Set;

import annoTM.core.annotations.Topic;
import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;
import de.topicmapslab.aranuka.codegen.properties.PropertyConstants;

/**
 * 
 * @author Sven Krosse
 *
 */
public class ImportHandler {

	private static final Set<Object> imports = new HashSet<Object>();

	static {
		imports.add(Topic.class.getPackage());
		imports.add("gnu.trove");
		imports.add(ImportHandler.class.getPackage());
		imports.add(TopicMap2JavaMapperException.class.getPackage());
		imports.add(PropertyConstants.class.getPackage());
		imports.add(Package.getPackage("java.util"));
		imports.add(Package.getPackage("java.net"));

	}

	public static final String generateImports() {
		StringBuilder builder = new StringBuilder();

		for (Object p : imports) {
			if (p instanceof Package) {
				builder.append(JavaKeywords.IMPORT + " "
						+ ((Package) p).getName() + ".*;\r\n");
			} else if (p instanceof String) {
				builder.append(JavaKeywords.IMPORT + " " + p + ".*;\r\n");
			}

		}

		return builder.toString();
	}

}
