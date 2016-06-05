package com.inspur.loaddll;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JarDllJava {

	// 动态库解压位置文件属性，用File应该是方便跨平台获取绝对路径以及创建
	static File tempDir = null;
	// 系统平台动态库后缀名
	static String systemType=null;
	// 动态库扩展名
	static String libExtension=null;
	/**
	 * 此处代码来源
	 * https://github.com/bytedeco/javacpp/blob/master/src/main/java/org/bytedeco/javacpp/Loader.java#L373-L384
	 * public static File getTempDir() //392行 我提取出来是为了方便，二是框架有点大，直接用掌握不了
	 * @param tempDir
	 * @return 如果tempDir存在则返回原值，不存在则在临时文件夹下创建与时间相关的问价夹
	 */
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
		//https://github.com/bytedeco/javacpp/blob/master/src/main/java/org/bytedeco/javacpp/Loader.java#L68-L96
		systemType = System.getProperty("os.name");
		String osName=systemType.toLowerCase();
		if(osName.indexOf("win") != -1){
			libExtension=".dll";
		}
		else if(osName.indexOf("mac") != -1){
			libExtension=".dylib";
		}
		else{
			libExtension =".so";
		}
		
	}
	/**
	 * classpath路径获取
	 * @param cls
	 * @return
	 */
	public static String rootPath(Class<?> cls){
		String rootPath=cls.getResource("/").getFile().toString();
		// 特别注意rootPath返回有斜杠，linux和mac下不需要去掉，windows需要去掉
		if ((systemType.toLowerCase().indexOf("win") != -1)) {
			// windows下去掉斜杠
			rootPath = rootPath.substring(1, rootPath.length());
		}
		return rootPath;
	}

	/**
	 * 加载dllpath下的libName库文件，windows libName.dll linux libName.so mac libName.dylib
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
				int len;
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
