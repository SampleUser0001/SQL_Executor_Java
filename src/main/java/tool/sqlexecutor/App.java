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

import java.io.IOException;

/**
 */
public class App {
    
    public static final String URL_FORMAT = "jdbc:%s:thin:@%s:%d/%s";
    
    public static void main( String[] args ) throws IOException {

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
        
        int argsIndex = 0;
        PropertiesEnum.load(Paths.get(args[argsIndex++]));
        List<String> columnList = Files.readAllLines(Paths.get(args[argsIndex++]));
        String sql = Files.lines(Paths.get(args[argsIndex++]))
                          .collect(Collectors.joining(" "));
        Path outputPath = Paths.get(args[argsIndex++]);

        String url = String.format(
            URL_FORMAT,
            PropertiesEnum.DATABASE.getPropertiesValue(),
            PropertiesEnum.HOST.getPropertiesValue(),
            Integer.parseInt(PropertiesEnum.PORT.getPropertiesValue()),
            PropertiesEnum.SCHEMA.getPropertiesValue());

        try (Connection conn = DriverManager.getConnection(
                url, 
                PropertiesEnum.USER.getPropertiesValue(),
                PropertiesEnum.PASSWORD.getPropertiesValue());
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            try (ResultSet rs = ps.executeQuery()){
                try(BufferedWriter writer = Files.newBufferedWriter(outputPath, Charset.forName("UTF-8"), StandardOpenOption.CREATE)) {
                    if(Boolean.parseBoolean(PropertiesEnum.HEADER.getPropertiesValue())) {
                        writer.write(
                            columnList.stream()
                                      .collect(Collectors.joining(PropertiesEnum.DELIMITER.getPropertiesValue()))
                        );
                        writer.write(System.getProperty("line.separator"));
                    }
                    while (rs.next()){
                        StringJoiner joiner = new StringJoiner(PropertiesEnum.DELIMITER.getPropertiesValue());
                        for(String column : columnList) {
                            joiner.add(rs.getString(column));
                        }
                        writer.write(joiner.toString());
                        writer.write(System.getProperty("line.separator"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
