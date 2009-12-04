package de.topicmapslab.aranuka.codegen.core.code.templates;

import java.util.Scanner;

/**
 * 
 * @author Sven Krosse
 *
 */
public class Templates {

	public static final String TEMPLATE_CLASSHEAD;
	public static final String TEMPLATE_CLASSFOOT;
	public static final String TEMPLATE_CACHE;
	public static final String TEMPLATE_CONSTRUCTOR;
	public static final String TEMPLATE_COMMENT;
	public static final String TEMPLATE_PACKAGE;
	public static final String TEMPLATE_IMPORTS;
	public static final String TEMPLATE_FIELDDEFINITION;
	public static final String TEMPLATE_METHODDEFINITION_ADDER;
	public static final String TEMPLATE_METHODDEFINITION_SETTER;
	public static final String TEMPLATE_METHODDEFINITION_GETTER;
	public static final String TEMPLATE_LAZY_METHODDEFINITION_ADDER;
	public static final String TEMPLATE_LAZY_METHODDEFINITION_SETTER;
	public static final String TEMPLATE_LAZY_METHODDEFINITION_GETTER;
	public static final String TEMPLATE_CACHED_METHODDEFINITION_ADDER;
	public static final String TEMPLATE_CACHED_METHODDEFINITION_SETTER;
	public static final String TEMPLATE_CACHED_METHODDEFINITION_GETTER;

	static {
		TEMPLATE_CLASSHEAD = fromFile("template-classhead.txt");
		TEMPLATE_CLASSFOOT = fromFile("template-classfoot.txt");
		TEMPLATE_CACHE = fromFile("template-cache.txt");
		TEMPLATE_CONSTRUCTOR = fromFile("template-constructor.txt");
		TEMPLATE_COMMENT = fromFile("template-comment.txt");
		TEMPLATE_PACKAGE = fromFile("template-package.txt");
		TEMPLATE_IMPORTS = fromFile("template-imports.txt");
		TEMPLATE_FIELDDEFINITION = fromFile("template-fielddefinition.txt");
		TEMPLATE_METHODDEFINITION_ADDER = fromFile("template-methoddefinition-adder.txt");
		TEMPLATE_METHODDEFINITION_SETTER = fromFile("template-methoddefinition-setter.txt");
		TEMPLATE_METHODDEFINITION_GETTER = fromFile("template-methoddefinition-getter.txt");
		TEMPLATE_LAZY_METHODDEFINITION_ADDER = fromFile("template-lazy-methoddefinition-adder.txt");
		TEMPLATE_LAZY_METHODDEFINITION_SETTER = fromFile("template-lazy-methoddefinition-setter.txt");
		TEMPLATE_LAZY_METHODDEFINITION_GETTER = fromFile("template-lazy-methoddefinition-getter.txt");
		TEMPLATE_CACHED_METHODDEFINITION_ADDER = fromFile("template-cached-methoddefinition-adder.txt");
		TEMPLATE_CACHED_METHODDEFINITION_SETTER = fromFile("template-cached-methoddefinition-setter.txt");
		TEMPLATE_CACHED_METHODDEFINITION_GETTER = fromFile("template-cached-methoddefinition-getter.txt");
	}

	private static final String fromFile(final String filename) {
		StringBuilder builder = new StringBuilder();

		Scanner scanner = new Scanner(Templates.class
				.getResourceAsStream(filename));
		while (scanner.hasNextLine()) {
			builder.append(scanner.nextLine() + "\r\n");
		}
		scanner.close();
		return builder.toString();
	}

	private Templates() {
	}

}
