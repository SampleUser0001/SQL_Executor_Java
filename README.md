# SQL_Executor_Java
SQLを実行して、実行結果をファイル出力する。

## 引数

```
${propertiesパス} ${SQLファイルパス} ${出力先パス}
```

## ビルド

``` bash
mvn clean 
mvn compile package -f pom_java8.xml
mvn compile package -f pom_java11.xml
```