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

## 実行

### Oracle

``` bash
# exec
export PROPERTIES=$(pwd)/src/main/resources/sample.connection.properties
export COLUMN_LIST=$(pwd)/src/main/resources/columnList.txt
export SQL=$(pwd)/src/main/resources/sample.sql
export RESULT=$(pwd)/output/result.tsv

echo ${PROPERTIES}
echo ${COLUMN_LIST}
echo ${SQL}
echo ${RESULT}

rm $RESULT

mvn clean compile exec:java -Dexec.mainClass="tool.sqlexecutor.App" -Dexec.args="'${PROPERTIES}' '${COLUMN_LIST}' '${SQL}' '${RESULT}'" -f pom_java8.xml

less $RESULT
```

### SQLite(テスト)

``` sql
create table sample_table(id integer primary key autoincrement , value text);
insert into sample_table (value) values ('hoge');
insert into sample_table (value) values ('piyo');
insert into sample_table (value) values ('fuga');
```

``` bash
# test
export PROPERTIES=$(pwd)/src/test/resources/sample.connection.properties
export COLUMN_LIST=$(pwd)/src/test/resources/test_column_listcolumnList.txt
export SQL=$(pwd)/src/test/resources/test.sql
export RESULT=$(pwd)/src/test/resources/output/result.tsv

echo ${PROPERTIES}
echo ${COLUMN_LIST}
echo ${SQL}
echo ${RESULT}

rm $RESULT

mvn clean compile test -f pom_java8.xml
less $RESULT
```

## 参考

- [JDBC and UCP Download page : Oracle](https://sampleuser0001.github.io/cloud9_note/Java/Java.html)
- [Maven Central Developers Guide : Oracle](https://www.oracle.com/database/technologies/maven-central-guide.html)