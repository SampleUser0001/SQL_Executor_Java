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

import tool.sqlexecutor.enums.PropertiesEnum;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * SQLを実行する
 */
public class App {
    private Logger logger = LogManager.getLogger();
    
    public static final String URL_FORMAT = "jdbc:%s:thin:@%s:%d/%s";
    
    public void execute(String[] args) throws IOException {
        // PropertiesEnum.load(
        //     Paths.get(
        //         System.getProperty("user.dir"),
        //         "src","main","resources","connection.properties"
        //     )
        // );
        // StringBuffer sql = new StringBuffer();
        // sql.append("SELECT OWNER, TABLE_NAME").append("\n");
        // sql.append("FROM ALL_TABLES").append("\n");
        // sql.append("ORDER BY OWNER,TABLE_NAME").append("\n");

        // sql.append("select * from regions");

        logger.info("SQL Executor Start.");
        
        int argsIndex = 0;
        final String PROPERTIES_PATH = args[argsIndex++];
        logger.info(String.format("PROPERTIES_PATH : %s", PROPERTIES_PATH));
        PropertiesEnum.load(Paths.get(PROPERTIES_PATH));
        
        final String COLUMN_LIST_FILE = args[argsIndex++];
        logger.info(String.format("COLUMN_LIST_FILE : %s", COLUMN_LIST_FILE));
        List<String> columnList = Files.readAllLines(Paths.get(COLUMN_LIST_FILE));

        final String SQL_FILE_PATH = args[argsIndex++];
        logger.info(String.format("SQL_FILE_PATH : %s", SQL_FILE_PATH));
        String sql = Files.lines(Paths.get(SQL_FILE_PATH))
                          .collect(Collectors.joining(" "));
        logger.debug(sql);
        
        final String OUTPUT_FILE_PATH = args[argsIndex++];
        logger.info(String.format("OUTPUT_FILE_PATH : %s", OUTPUT_FILE_PATH));
        Path outputPath = Paths.get(OUTPUT_FILE_PATH);

        String url = String.format(
            URL_FORMAT,
            PropertiesEnum.DATABASE.getPropertiesValue(),
            PropertiesEnum.HOST.getPropertiesValue(),
            Integer.parseInt(PropertiesEnum.PORT.getPropertiesValue()),
            PropertiesEnum.SCHEMA.getPropertiesValue());

        final String DQ = Boolean.parseBoolean(PropertiesEnum.DOUBLEQUOTE.getPropertiesValue()) ? "\"" : ""; 
        
        try (Connection conn = DriverManager.getConnection(
                url, 
                PropertiesEnum.USER.getPropertiesValue(),
                PropertiesEnum.PASSWORD.getPropertiesValue());
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            try (ResultSet rs = ps.executeQuery()){
                try(BufferedWriter writer = Files.newBufferedWriter(outputPath, Charset.forName("UTF-8"), StandardOpenOption.CREATE)) {
                    if(Boolean.parseBoolean(PropertiesEnum.HEADER.getPropertiesValue())) {
                        writer.write(
                            DQ + 
                            columnList.stream()
                                      .collect(Collectors.joining(DQ + PropertiesEnum.DELIMITER.getPropertiesValue() + DQ))
                            + DQ
                        );
                        writer.write(System.getProperty("line.separator"));
                    }
                    while (rs.next()){
                        StringJoiner joiner = new StringJoiner(PropertiesEnum.DELIMITER.getPropertiesValue());
                        for(String column : columnList) {
                            joiner.add(DQ + rs.getString(column) + DQ);
                        }
                        writer.write(joiner.toString());
                        writer.write(System.getProperty("line.separator"));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }

        logger.info("SQL Executor Finish.");

    }
    
    public static void main( String[] args ) throws IOException {
        new App().execute(args);
    }
}
