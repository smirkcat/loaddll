作者[@秀才遇到猫][1]
### 目录结构遵循[maven标准目录结构][2]
* 此项目是用于统一加载linux和windows动态库，运用了java和scala两种语言实现,java方式已经运用到web展示，scala是研究spark预处理图像所想的思路,还未实践
* jni具体方法参考另一个[scalacpptest][5]，其中动态库解压目录参考项目[javacpp][6]，代码详见<https://github.com/bytedeco/javacpp/blob/master/src/main/java/org/bytedeco/javacpp/Loader.java#L373-L384>
* 本项目思路是解压jar包动态库至java.io.tmpdir下动态生成一个跟时间戳相关文件夹下，使用绝对路径加载方法	System.load()加载

### 思考，tempDir应该放在一个类下或者两个类比较统一，这样才不会重复解压动态库，加载不同路径动态库

以pom方式导入后检查是否支持scala环境，不支持，按照下面方式执行

### eclipse(Mars-4.5.1版示例)添加scala插件，以及对现有java项目添加和去除scala编译环境，如下面图示
#### 图1-1 eclispe-scala插件安装
![eclispe-scala插件安装](resources/eclispe-scala插件安装.png)
#### 图1-2 eclipse-java添加scalalib
![eclipse-java添加scalalib](resources/eclipse-java添加scalalib.png)
#### 图1-3 eclipse-scala去除scalalib
![eclipse-scala去除scalalib](resources/eclipse-scala去除scalalib.png)
#### 注:如果安装失败进入[官网][3]查看

### intellij-idea(15.0.2版示例)添加scala插件，以及对现有java项目添加和去除scala编译环境，如下面图示 
#### 图2-1 intellij-idea-scala插件在线安装
![intellij-scala插件安装](resources/intellij-安装scala插件.png)
#### 注：如果安装失败，请用下面的方法
#### 图2-2 intellij-idea-scala插件离线安装[地址][4]
![intellij-scala插件安装](resources/intellij离线安装scala版本.png)
#### 图2-3 intellij-java添加和删除scalalib
![intellij添加和去除scalalib](resources/intellij-java添加和去除scalalib.png)




[1]: http://weibo.com/smirklijie
[2]: http://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html
[3]: http://scala-ide.org/
[4]: http://plugins.jetbrains.com/plugin/?idea&id=1347
[5]: https://git.oschina.net/smirkcat/scalacpptest
[6]: https://github.com/bytedeco/javacpp