package ru.kaysiodl.fitness_club.service;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.PutOption;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class OtpService {
    private final Client client;

    public OtpService(Client client) {
        this.client = client;
    }

    public void generateOtp(String userId, String code) throws Exception {
        long leaseId = client.getLeaseClient().grant(300).get().getID(); // 5 минут
        ByteSequence key = ByteSequence.from("otp/" + userId, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(code, StandardCharsets.UTF_8);
        PutOption option = PutOption.builder().withLeaseId(leaseId).build();
        client.getKVClient().put(key, value, option).get();
    }

    public Optional<String> verifyOtp(String userId, String inputCode) throws Exception {
        ByteSequence key = ByteSequence.from("otp/" + userId, StandardCharsets.UTF_8);
        GetResponse resp = client.getKVClient().get(key).get();
        if (resp.getKvs().isEmpty()) {
            return Optional.empty();
        }
        String stored = resp.getKvs().get(0).getValue().toString(StandardCharsets.UTF_8);
        if (stored.equals(inputCode)) {
            client.getKVClient().delete(key).get(); // одноразовый — удаляем сразу
            return Optional.of(stored);
        }
        return Optional.empty();
    }
}
