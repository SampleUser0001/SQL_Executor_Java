package tool.sqlexecutor;

import java.lang.StringBuffer;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Getter;

import tool.sqlexecutor.enums.PropertiesEnum;
import tool.sqlexecutor.enums.DatabaseEnum;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * SQLを実行する
 */
public class App {
    private Logger logger = LogManager.getLogger();
    
    @Getter
    private List<String> columnList;
    
    @Getter
    private String sql;
    
    @Getter
    private Path outputPath;
    
    @Getter
    private String dq;

    /**
     * ツール実行する。
     * @param String[] args 起動引数
     * @Exception IOException
     */
    public void execute(String[] args) throws IOException {
        logger.debug(String.format("args.length : %d", args.length));

        logger.info("SQL Executor Start.");
        
        this.load(args);
        String result = this.executeSQL(
            this.sql,
            this.columnList,
            Boolean.parseBoolean(PropertiesEnum.HEADER.getPropertiesValue()),
            PropertiesEnum.DELIMITER.getPropertiesValue()
        );
        this.exportResult(this.outputPath, result);
        logger.info("SQL Executor Finish.");

    }
    
    /**
     * 引数を
     */
    public void load(String[] args) throws IOException {
        int argsIndex = 0;
        logger.info("load start.");

        final String PROPERTIES_PATH = args[argsIndex++];
        logger.info(String.format("PROPERTIES_PATH : %s", PROPERTIES_PATH));
        PropertiesEnum.load(Paths.get(PROPERTIES_PATH));
        
        final String COLUMN_LIST_FILE = args[argsIndex++];
        logger.info(String.format("COLUMN_LIST_FILE : %s", COLUMN_LIST_FILE));
        this.columnList = Files.readAllLines(Paths.get(COLUMN_LIST_FILE));

        final String SQL_FILE_PATH = args[argsIndex++];
        logger.info(String.format("SQL_FILE_PATH : %s", SQL_FILE_PATH));
        this.sql = Files.lines(Paths.get(SQL_FILE_PATH))
                          .collect(Collectors.joining(" "));
        logger.debug(sql);
        
        final String OUTPUT_FILE_PATH = args[argsIndex++];
        logger.info(String.format("OUTPUT_FILE_PATH : %s", OUTPUT_FILE_PATH));
        this.outputPath = Paths.get(OUTPUT_FILE_PATH);

        this.dq = Boolean.parseBoolean(PropertiesEnum.DOUBLEQUOTE.getPropertiesValue()) ? "\"" : ""; 
        
        logger.info("load finish.");
    }

    /**
     * SQLを実行する。
     * @param String url 接続URL
     * @param String user 接続ユーザ
     * @param String password 接続パスワード
     * @param String sql 実行SQL
     * @param List<String> columnList 出力対象のカラム
     * @param boolean header ヘッダ行を出力するか
     * @param String delimiter 区切り文字の指定
     * @return String SQLの実行結果
     */
    public String executeSQL(
        String sql,
        List<String> columnList,
        boolean header,
        String delimiter) {

        logger.info("executeSQL start.");
            
        StringBuilder builder = new StringBuilder();
        try (Connection conn = DatabaseEnum.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            try (ResultSet rs = ps.executeQuery()){
                    if(Boolean.parseBoolean(PropertiesEnum.HEADER.getPropertiesValue())) {
                        builder.append(
                            this.dq + 
                            columnList.stream()
                                      .collect(Collectors.joining(this.dq + PropertiesEnum.DELIMITER.getPropertiesValue() + this.dq))
                            + this.dq
                        );
                        builder.append(System.getProperty("line.separator"));
                    }
                    while (rs.next()){
                        StringJoiner joiner = new StringJoiner(PropertiesEnum.DELIMITER.getPropertiesValue());
                        for(String column : columnList) {
                            joiner.add(this.dq + rs.getString(column) + this.dq);
                        }
                        builder.append(joiner.toString());
                        builder.append(System.getProperty("line.separator"));
                    }
                }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.info("executeSQL finish.");
        return builder.toString();
        
    }
    
    /**
     * SQL実行結果を出力する。
     * @param Path 出力パス
     * @param String SQL実行結果
     */
    public void exportResult(Path outputPath, String result) {
        logger.info("exportResult start.");

        try(BufferedWriter writer = Files.newBufferedWriter(outputPath, Charset.forName("UTF-8"), StandardOpenOption.CREATE)) {
            writer.write(result);
        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.info("exportResult finish.");
    }
    
    public static void main( String[] args ) throws IOException {
        new App().execute(args);
    }
}
