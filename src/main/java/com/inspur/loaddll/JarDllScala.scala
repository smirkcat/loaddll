package com.inspur.loaddll

import java.io._
import scala.util.control._

//如果没有scala环境编译，请注释本文件
object JarDllScala {
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
  var libExtension: String = if (systemType.toLowerCase().indexOf("win") != -1) { ".dll" } else ".so"
  def rootPath(cls:Class[_]): String = {
    var rootPath = cls.getResource("/").getFile()
    // 特别注意rootPath返回有斜杠。linux下不需要去掉，windows需要去掉
    if ((systemType.toLowerCase().indexOf("win") != -1)) {
      // windows下去掉斜杠
      rootPath = rootPath.substring(1, rootPath.length())
    }
    rootPath
  }
  def loadlib(libName:String, dllpath:String, cls:Class[_]){
    
  }
}

