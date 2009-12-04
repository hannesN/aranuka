package de.topicmapslab.aranuka.codegen.core.code;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.topicmapslab.aranuka.codegen.core.exception.ClassfileWriterException;
import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;

/**
 * 
 * @author Sven Krosse
 *
 */
public abstract class CodeBuffer {

	private StringBuilder builder = new StringBuilder();
	private static final String LINERETURN = "\r\n";
	private static final String CLASSFILEEXTENSION = ".java";
	private final String directory;
	private final String className;

	public CodeBuffer(final String directory, final String className) {
		this.directory = directory;
		this.className = className;
	}

	protected final void appendCodeBlock(final String codeBlock) {
		builder.append(codeBlock + LINERETURN);
	}
	
	protected final void clearBuffer(){
		builder = new StringBuilder();
	}

	public void toClassFile() throws TopicMap2JavaMapperException {
		File file = new File(directory + "/" + className + CLASSFILEEXTENSION);
		try {
			new File(directory).mkdirs();
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(builder.toString());
			writer.flush();
			writer.close();

		} catch (IOException e) {
			throw new ClassfileWriterException("Cannot create class file.", e);
		}
	}

}
