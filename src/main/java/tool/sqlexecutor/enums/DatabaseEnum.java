package tool.sqlexecutor.enums;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DatabaseEnum {
    Oracle("jdbc:oracle:thin:@%s:%d/%s") {
        @Override
        protected String generateUrl() {
            return String.format(
                this.urlFormat,
                PropertiesEnum.HOST.getPropertiesValue(),
                Integer.parseInt(PropertiesEnum.PORT.getPropertiesValue()),
                PropertiesEnum.SCHEMA.getPropertiesValue()
            );
        }
        
        @Override
        public Connection generateConnection() throws SQLException {
            return DriverManager.getConnection(
                this.generateUrl(),
                PropertiesEnum.USER.getPropertiesValue(),
                PropertiesEnum.PASSWORD.getPropertiesValue()
            );
        }
    },
    SQLite("jdbc:sqlite:%s") {
        @Override
        protected String generateUrl() {
            return String.format(
                this.urlFormat,
                PropertiesEnum.SQLITE_FILEPATH.getPropertiesValue()
            );
        }
        
        @Override
        public Connection generateConnection() throws SQLException {
            return DriverManager.getConnection(this.generateUrl());
        }
    };
    
    protected final String urlFormat;
    
    private DatabaseEnum(String urlFormat) {
        this.urlFormat = urlFormat;
    }

    protected abstract String generateUrl();
    protected abstract Connection generateConnection() throws SQLException ;
    public static Connection getConnection() throws SQLException {
        return DatabaseEnum.valueOf(PropertiesEnum.DATABASE.getPropertiesValue())
                           .generateConnection();
    }
    
    
}