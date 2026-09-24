package ru.kaysiodl.fitness_club.config;

import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtcdConfig {

    @Value("${etcd.endpoints}")
    private String etcdEndpoints;

    @Bean(destroyMethod = "close")
    public Client etcdClient() {
        return Client.builder()
                .endpoints(etcdEndpoints.split(","))
                .build();
    }
}
