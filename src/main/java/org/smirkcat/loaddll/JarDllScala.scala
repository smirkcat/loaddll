package org.smirkcat.loaddll

import java.io._
import scala.util.control._
import scala.collection.mutable.ListBuffer


//如果没有scala环境编译，请注释本文件
object JarDllScala {
  //注java代码是参考了javacpp里面源码获取tempDir获取的方法，scala采用static来初始化一个不能改变的变量
  val tempDir: File = {
    val tmpdir = new File(System.getProperty("java.io.tmpdir"))
    val loop = new Breaks
    //临时变量
    var tempDirf: File = null
    loop.breakable {
      for (i <- 0 to 1000) {
        val f = new File(tmpdir, "dll" + System.nanoTime())
        if (f.mkdir()) {
          tempDirf = f
          tempDirf.deleteOnExit()
          loop.break
        }
      }
    }
    tempDirf
  }
  val systemType: String = System.getProperty("os.name")
  val libExtension: String = {
    val osName=systemType.toLowerCase()
    if (osName.indexOf("win") != -1)  ".dll" 
    else if (osName.indexOf("mac") != -1)  ".dylib"
    else ".so"
      }
  def rootPath(cls: Class[_]): String = {
    var rootPath = cls.getResource("/").getFile()
    // 特别注意rootPath返回有斜杠。linux下不需要去掉，windows需要去掉
    if ((systemType.toLowerCase().indexOf("win") != -1)) {
      // windows下去掉斜杠
      rootPath = rootPath.substring(1, rootPath.length())
    }
    rootPath
  }
  def loadlib(libName: String, dllpath: String, cls: Class[_]) {
    val libFullName = dllpath + libName + libExtension
    //java代码有一个获取tempDir的过程
    val filepath = tempDir.getAbsolutePath() + "/" + libName + libExtension
    var extractedLibFile = new File(filepath)
    if (!extractedLibFile.exists()) {
      try {
        val in = cls.getResourceAsStream(libFullName)
        val reader = new BufferedInputStream(in)
        val writer = new FileOutputStream(extractedLibFile)
        //参考下面网址
        //http://wtqq520.iteye.com/blog/1540433
        //http://blog.csdn.net/lyrebing/article/details/20362227
        var buf = ListBuffer[Byte]()
        var b = reader.read()
        while (b != -1) {
          buf.append(b.byteValue)
          b = reader.read()
        }
      } catch {
        case e: IOException => {
          if (!extractedLibFile.exists()) {
            extractedLibFile.delete();
          }
          throw e
        }
      }
    }
    System.load(extractedLibFile.toString());
  }
}

