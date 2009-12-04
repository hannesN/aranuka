package de.topicmapslab.aranuka.codegen.core.factory;

import gnu.trove.THashMap;

import java.lang.reflect.Constructor;
import java.util.Map;

import de.topicmapslab.aranuka.codegen.core.code.AnnotationSupportingCodeBuffer;
import de.topicmapslab.aranuka.codegen.core.code.CachedCodeBuffer;
import de.topicmapslab.aranuka.codegen.core.code.CodeBuffer;
import de.topicmapslab.aranuka.codegen.core.code.LazyCodeBuffer;
import de.topicmapslab.aranuka.codegen.core.code.POJOTypes;
import de.topicmapslab.aranuka.codegen.core.definition.TopicAnnotationDefinition;
import de.topicmapslab.aranuka.codegen.core.exception.InitializationException;
import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;

/**
 * 
 * @author Sven Krosse
 *
 */
public class CodeBufferFactory {

	private final Map<POJOTypes, Class<? extends CodeBuffer>> registeredBufferClasses = new THashMap<POJOTypes, Class<? extends CodeBuffer>>();

	private static CodeBufferFactory factory;

	private CodeBufferFactory() {
		registeredBufferClasses.put(POJOTypes.SIMPLE_POJO,
				AnnotationSupportingCodeBuffer.class);
		registeredBufferClasses.put(POJOTypes.LAZY_POJO, LazyCodeBuffer.class);
		registeredBufferClasses.put(POJOTypes.CACHED_POJO,
				CachedCodeBuffer.class);
	}

	public static CodeBufferFactory getFactory() {
		if (factory == null) {
			factory = new CodeBufferFactory();
		}
		return factory;
	}

	public CodeBuffer newCodeBuffer(POJOTypes pojoType, String directory,
			String packageName,
			TopicAnnotationDefinition topicAnnotationDefinition)
			throws TopicMap2JavaMapperException {
		if (!registeredBufferClasses.containsKey(pojoType)) {
			throw new InitializationException(
					"No code-buffer-class registered for given POJO-type");
		}
		try {
			Class<? extends CodeBuffer> clazz = registeredBufferClasses
					.get(pojoType);
			Constructor<? extends CodeBuffer> constructor = clazz
					.getConstructor(String.class, String.class,
							TopicAnnotationDefinition.class);
			return constructor.newInstance(directory, packageName,
					topicAnnotationDefinition);
		} catch (Exception e) {
			throw new InitializationException(
					"Cannot initialize new code-buffer.");
		}
	}

}
