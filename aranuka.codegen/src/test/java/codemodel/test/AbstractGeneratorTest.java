/**
 * 
 */
package codemodel.test;

import java.io.File;

/**
 * @author niederhausen
 *
 */
public abstract class AbstractGeneratorTest {
	protected File getDir() {
		File dir = deleteTestDir();

		dir.mkdirs();
		return dir;

	}

	protected File deleteTestDir() {
	    File dir = new File("tmp/test_result");
		if (dir.exists()) {
			delete(dir);
		}
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
