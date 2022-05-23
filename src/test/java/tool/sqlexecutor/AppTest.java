package tool.sqlexecutor;

import java.nio.file.Paths;
import java.lang.StringBuffer;

import org.junit.Test;
import org.junit.Before;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import tool.sqlexecutor.enums.PropertiesEnum;

import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private static final String TEST_RESOURCES_HOME = Paths.get(System.getProperty("user.dir"), "src", "test", "resources").toString();

    private App app;

    @Before
    public void setup() {
        app = new App();
    }
    
    /**
     * SQLiteを使用した実行結果取得テスト。
     * ヘッダ：あり, デリミタ:タブ, ダブルクォーテーション:なし
     */
    @Test
    public void testBySQLite() throws IOException {
        String[] args = {
            Paths.get(TEST_RESOURCES_HOME , "sample.connection.properties").toString(),
            Paths.get(TEST_RESOURCES_HOME , "test_column_list.txt").toString(),
            Paths.get(TEST_RESOURCES_HOME , "test.sql").toString(),
            Paths.get(TEST_RESOURCES_HOME).toString()
        };
        app.load(args);
        String result = app.executeSQL(
            app.getSql(),
            app.getColumnList(),
            Boolean.parseBoolean(PropertiesEnum.HEADER.getPropertiesValue()),
            app.getDq()
        );
        StringBuffer expected = new StringBuffer();
        expected.append("id\tvalue").append(System.getProperty("line.separator"));
        expected.append("1\thoge").append(System.getProperty("line.separator"));
        expected.append("2\tpiyo").append(System.getProperty("line.separator"));
        expected.append("3\tfuga").append(System.getProperty("line.separator"));
        
        assertThat(result, is(equalTo(expected.toString())));

    }
    

}
