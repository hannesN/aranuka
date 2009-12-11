package de.topicmapslab.aranuka.codegen.core.code;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.hunsicker.jalopy.Jalopy;
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
		String filename = directory + "/" + className + CLASSFILEEXTENSION;
		File file = new File(filename);
		try {
			new File(directory).mkdirs();
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
						
			// formatting code
			Jalopy jalopy = new Jalopy();
			jalopy.setInput(builder.toString(), filename);
			StringBuffer b = new StringBuffer();
			jalopy.setOutput(b);
			jalopy.format();
			writer = new FileWriter(file);
			writer.write(b.toString());
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			throw new ClassfileWriterException("Cannot create class file.", e);
		}
	}

}
