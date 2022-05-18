# SQL_Executor_Java
SQLを実行して、実行結果をファイル出力する。

## 引数

```
${propertiesパス} ${列名一覧ファイルパス} ${SQLファイルパス} ${出力先パス}
```

## ビルド

``` bash
mvn clean 
mvn compile package -f pom_java8.xml
mvn compile package -f pom_java11.xml
```

## 参考

- [JDBC and UCP Download page : Oracle](https://sampleuser0001.github.io/cloud9_note/Java/Java.html)
- [Maven Central Developers Guide : Oracle](https://www.oracle.com/database/technologies/maven-central-guide.html)