package com.moon.im.tcp;

import com.moon.im.codec.config.BootstrapConfig;
import com.moon.im.tcp.mq.RabbitMqFactory;
import com.moon.im.tcp.mq.receiver.RabbitMqMessageReceiver;
import com.moon.im.tcp.redis.RedisManager;
import com.moon.im.tcp.register.RegistryZK;
import com.moon.im.tcp.register.ZKit;
import com.moon.im.tcp.server.LimServer;
import com.moon.im.tcp.server.LimWebSocketServer;
import org.I0Itec.zkclient.ZkClient;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

            RedisManager.init(bootstrapConfig);

            RabbitMqFactory.init(bootstrapConfig.getLim().getRabbitmq());
            RabbitMqMessageReceiver.init();

            registerZK(bootstrapConfig);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(500);
        }
    }

    public static void registerZK(BootstrapConfig config) throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        ZkClient zkClient = new ZkClient(config.getLim().getZkConfig().getZkAddr(),
                config.getLim().getZkConfig().getZkConnectTimeOut());
        ZKit zKit = new ZKit(zkClient);
        RegistryZK registryZK = new RegistryZK(zKit, hostAddress, config.getLim());
        new Thread(registryZK).start();
    }
}
