package dk.vip.client.domain.compute.configuration;

public interface HeadConfigurationFileHandler {

    boolean save(ConfigurationModel config);

    <T extends ConfigurationModel> T load (Class<T> config, String path);
}