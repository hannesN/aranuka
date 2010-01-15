/**
 * 
 */
package codemodel.test;

import java.io.File;

/**
 * @author niederhausen
 * 
 */
public class AbstractGenerationTest {

	protected File getDir() {
		File dir = new File("tmp/test_result");
		if (dir.exists()) {
			System.out.println("delete dir");
			delete(dir);
		}

		dir.mkdirs();
		System.out.println("created dir");
		return dir;

	}

	private void delete(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				delete(f);
			}
		}
		file.delete();
	}
}
