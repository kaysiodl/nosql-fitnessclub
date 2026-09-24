package ru.kaysiodl.fitness_club.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.PutOption;
import org.springframework.stereotype.Service;
import ru.kaysiodl.fitness_club.entity.Product;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;

@Service
public class ProductCacheService {
    private final Client client;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String CACHE_KEY = "cache/products";

    public ProductCacheService(Client client) {
        this.client = client;
    }

    public List<Product> getProducts(Supplier<List<Product>> loadFromDb) throws Exception {
        ByteSequence key = ByteSequence.from(CACHE_KEY, StandardCharsets.UTF_8);
        GetResponse resp = client.getKVClient().get(key).get();
        if (!resp.getKvs().isEmpty()) {
            String json = resp.getKvs().get(0).getValue().toString(StandardCharsets.UTF_8);
            return mapper.readValue(json, new TypeReference<List<Product>>() {});
        }
        List<Product> products = loadFromDb.get(); // промах кэша — идём в БД
        long leaseId = client.getLeaseClient().grant(60).get().getID();
        String json = mapper.writeValueAsString(products);
        PutOption option = PutOption.builder().withLeaseId(leaseId).build();
        client.getKVClient().put(key, ByteSequence.from(json, StandardCharsets.UTF_8), option).get();
        return products;
    }

    public void invalidate() throws Exception {
        ByteSequence key = ByteSequence.from(CACHE_KEY, StandardCharsets.UTF_8);
        client.getKVClient().delete(key).get();
    }
}
