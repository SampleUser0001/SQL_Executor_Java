package tool.sqlexecutor.enums;

import lombok.Getter;

public enum DatabaseEnum {
    ORACLE("jdbc:oracle:thin:@%s:%d/%s"),
    SQLITE("jdbc:sqlite:%s");
    
    @Getter
    private final String url;
    
    private DatabaseEnum(String url) {
        this.url = url;
    }
    
}