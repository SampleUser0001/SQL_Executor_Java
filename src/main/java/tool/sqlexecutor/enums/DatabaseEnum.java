package tool.sqlexecutor.enums;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DatabaseEnum {
    Oracle("jdbc:oracle:thin:@%s:%d/%s") {
        @Override
        protected String generateUrl() {
            return this.generateUrl(
                PropertiesEnum.HOST.getPropertiesValue(),
                Integer.parseInt(PropertiesEnum.PORT.getPropertiesValue()),
                PropertiesEnum.SCHEMA.getPropertiesValue()
            );
        }
        @Override
        protected Connection generateConnection() throws SQLException {
            return this.generateConnection(
                this.generateUrl(),
                PropertiesEnum.USER.getPropertiesValue(),
                PropertiesEnum.PASSWORD.getPropertiesValue());
        }
    },
    SQLite("jdbc:sqlite:%s") {
        @Override
        protected String generateUrl() {
            return this.generateUrl(PropertiesEnum.SQLITE_FILEPATH.getPropertiesValue());
        }
        @Override
        protected Connection generateConnection() throws SQLException {
            return this.generateConnection(this.generateUrl());
        }
    },
    Access("jdbc:ucanaccess://%s") {
        @Override
        protected String generateUrl() {
            return this.generateUrl(PropertiesEnum.ACCESS_FILEPATH.getPropertiesValue());
        }
        @Override
        protected Connection generateConnection() throws SQLException {
            return this.generateConnection(this.generateUrl());
        }
    };
    
    protected final String urlFormat;
    
    private DatabaseEnum(String urlFormat) {
        this.urlFormat = urlFormat;
    }

    protected abstract String generateUrl();
    protected String generateUrl(String host, int port, String schema) {
        return String.format(this.urlFormat, host, port, schema);
    }
    protected String generateUrl(String filepath) {
        return String.format(this.urlFormat, filepath);
    }
    
    protected abstract Connection generateConnection() throws SQLException;
    protected Connection generateConnection(String url, String user, String pass) throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
    protected Connection generateConnection(String url) throws SQLException {
        return DriverManager.getConnection(url);
    }
    
    public static Connection getConnection() throws SQLException {
        return DatabaseEnum.valueOf(PropertiesEnum.DATABASE.getPropertiesValue())
                           .generateConnection();
    }
    

}