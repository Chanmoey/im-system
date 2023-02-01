package com.moon.im.tcp;

import com.moon.im.codec.config.BootstrapConfig;
import com.moon.im.tcp.server.LimServer;
import com.moon.im.tcp.server.LimWebSocketServer;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Starter {

    private static BootstrapConfig bootstrapConfig;

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            start(args[0]);
        }
    }

    private static void start(String configPath) throws IOException {

        try {
            InputStream in = Files.newInputStream(Paths.get(configPath));
            Yaml yaml = new Yaml();
            bootstrapConfig = yaml.loadAs(in, BootstrapConfig.class);
            new LimServer(bootstrapConfig.getLim()).start();
            new LimWebSocketServer(bootstrapConfig.getLim()).start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(500);
        }
    }
}
