package me.redned.geyser.extension.hidecommands;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.command.Command;
import org.geysermc.geyser.api.command.CommandSource;
import org.geysermc.geyser.api.event.java.ServerDefineCommandsEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCommandsEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class HideCommands implements Extension {
    private Set<String> commands;

    @SuppressWarnings("unchecked")
    @Subscribe
    public void onPostInitialize(GeyserPostInitializeEvent event) {
        try {
            Path commandsPath = this.dataFolder().resolve("commands.yml");
            this.saveDefaultConfig(commandsPath);

            this.commands = new HashSet<>(((ArrayList<String>) new Yaml().loadAs(Files.newBufferedReader(commandsPath), LinkedHashMap.class).get("commands")));
        } catch (IOException ex) {
            this.logger().error("Failed to load commands!", ex);
            this.setEnabled(false);
        }

        this.logger().info("Loaded " + this.commands.size() + " commands to hide!");
    }

    @Subscribe
    public void onCommands(ServerDefineCommandsEvent event) {
        event.commands().removeIf(command -> this.commands.contains(command.name()));
    }

    @Subscribe
    public void onCommandDefine(GeyserDefineCommandsEvent event) {
        event.register(Command.builder(this)
                .source(CommandSource.class)
                .name("hiddencommands")
                .description("Shows all the hidden commands.")
                .permission("hidecommands.hiddencommands")
                .executor((source, command, args) -> source.sendMessage("Hidden commands: " + String.join(", ", this.commands)))
                .build());
    }

    private void saveDefaultConfig(Path commandsPath) throws IOException {
        if (Files.exists(commandsPath)) {
            return;
        }

        if (Files.notExists(this.dataFolder())) {
            Files.createDirectory(this.dataFolder());
        }

        try {
            URI uri = this.getClass().getResource("/commands.yml").toURI();
            try (FileSystem fileSystem = FileSystems.newFileSystem(uri, new HashMap<>(), null)) {
                Path path = fileSystem.getPath("commands.yml");
                Files.copy(path, commandsPath);
            }
        } catch (IOException | URISyntaxException ex) {
            this.logger().error("Failed to create commands.yml!", ex);
            this.setEnabled(false);
        }
    }
}
