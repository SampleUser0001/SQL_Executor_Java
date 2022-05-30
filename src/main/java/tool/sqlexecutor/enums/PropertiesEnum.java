package tool.sqlexecutor.enums;

import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public enum PropertiesEnum {
    DATABASE("database"),
    HOST("database.host"),
    PORT("database.port"),
    USER("database.user"),
    PASSWORD("database.pass"),
    SCHEMA("database.schema"),
    SQLITE_FILEPATH("database.sqlite.filepath"),
    ACCESS_FILEPATH("database.access.filepath"),
    HEADER("output.header"),
    DELIMITER("output.delimiter"),
    DOUBLEQUOTE("output.doublequote");

    private static Properties properties;
    
    private final String key;

    private PropertiesEnum(String key) {
        this.key = key;
    }
    
    public static void load(Path propertiesPath) throws IOException {
        properties = new Properties();
        properties.load(
            Files.newBufferedReader(propertiesPath, StandardCharsets.UTF_8)
        );
    }
    
    public String getPropertiesValue() {
        return properties.getProperty(this.key);
    }
    
    
}