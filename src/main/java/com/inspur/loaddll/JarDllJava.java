package com.inspur.loaddll;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JarDllJava {

	// 动态库解压位置
	static File tempDir = null;
	// 系统平台名称
	static String systemType=null;
	// 动态库扩展名
	static String libExtension=null;

	public static File getTempDir(File tempDir) {
		if (tempDir == null) {
			File tmpdir = new File(System.getProperty("java.io.tmpdir"));
			File f = null;
			for (int i = 0; i < 1000; i++) {
				f = new File(tmpdir, "dll" + System.nanoTime());
				if (f.mkdir()) {
					tempDir = f;
					tempDir.deleteOnExit();
					break;
				}
			}
		}
		return tempDir;
	}
	
	static{
		systemType = System.getProperty("os.name");
		libExtension = (systemType.toLowerCase().indexOf("win") != -1) ? ".dll" : ".so";
	}
	/**
	 * classpath路径获取
	 * @param cls
	 * @return
	 */
	public static String rootPath(Class<?> cls){
		String rootPath=cls.getResource("/").getFile().toString();
		// 特别注意getAppPath返回有斜杠。linux下不需要去掉，windows需要去掉
		if ((systemType.toLowerCase().indexOf("win") != -1)) {
			// windows下去掉斜杠
			rootPath = rootPath.substring(1, rootPath.length());
		}
		return rootPath;
	}

	/**
	 * 加载dllpath下的libName库文件，windows libName.dll linux libName.so
	 * 会把动态库问价解压到jar包同级目录下
	 * 
	 * @param libName
	 *            库文件名没有后缀
	 * @param dllpath
	 *            库文件所在文件夹 /dll/ 相对于jar包里根目录
	 * @param cls
	 *            动态库所在包的类
	 * @throws IOException
	 *             抛出异常
	 */
	public static void loadLib(String libName, String dllpath, Class<?> cls) throws IOException {

		String libFullName = dllpath + libName + libExtension;
		tempDir = getTempDir(tempDir);
		String filepath = tempDir.getAbsolutePath() + "/" + libName + libExtension;
		File extractedLibFile = new File(filepath);
		if (!extractedLibFile.exists()) {
			try (InputStream in = cls.getResourceAsStream(libFullName);
					BufferedInputStream reader = new BufferedInputStream(in);
					FileOutputStream writer = new FileOutputStream(extractedLibFile)) {

				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, len);
				}
			} catch (IOException e) {
				if (!extractedLibFile.exists()) {
					extractedLibFile.delete();
				}
				throw e;
			}
		}
		System.load(extractedLibFile.toString());
	}
}
