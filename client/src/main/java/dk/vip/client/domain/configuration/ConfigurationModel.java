package dk.vip.client.domain.configuration;

public abstract class ConfigurationModel {
    
    private final String path;

    public ConfigurationModel(String path) {
        this.path = path;
    }
    
    public String getPath(){
        return path;
    }
}