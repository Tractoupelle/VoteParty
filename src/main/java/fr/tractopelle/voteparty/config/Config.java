package fr.tractopelle.voteparty.config;


import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Config extends YamlConfiguration {

    /**
     * Config system made by SYRQL
     */

    private String fileName;
    private JavaPlugin plugin;

    public Config(JavaPlugin plugin, String fileName) {
        this(plugin, fileName, ".yml");
    }

    public Config(JavaPlugin plugin, String fileName, String fileExtension) {
        this.plugin = plugin;
        this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
        this.createFile();
    }

    public String getFileName() {
        return this.fileName;
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    private void createFile() {
        File folder = this.plugin.getDataFolder();

        try {
            File file = new File(folder, this.fileName);

            if (!file.exists()) {
                if (this.plugin.getResource(this.fileName) != null) {
                    this.plugin.saveResource(this.fileName, false);
                } else {
                    this.save(file);
                }
                this.load(file);
            } else {
                this.load(file);
                this.save(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        File folder = this.plugin.getDataFolder();

        try {
            this.save(new File(folder, this.fileName));
        } catch (Exception ignored) {
        }
    }

}