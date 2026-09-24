package ru.kaysiodl.fitness_club.service;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.TxnResponse;
import io.etcd.jetcd.op.Cmp;
import io.etcd.jetcd.op.CmpTarget;
import io.etcd.jetcd.op.Op;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class LoginAttemptService {
    private final Client client;
    private static final int MAX_ATTEMPTS = 5;

    public LoginAttemptService(Client client) {
        this.client = client;
    }

    public boolean registerFailedAttempt(String username) throws Exception {
        ByteSequence key = ByteSequence.from("login_attempts/" + username, StandardCharsets.UTF_8);
        while (true) {
            GetResponse resp = client.getKVClient().get(key).get();
            long currentVersion = resp.getKvs().isEmpty() ? 0 : resp.getKvs().get(0).getVersion();
            int currentCount = resp.getKvs().isEmpty() ? 0 :
                    Integer.parseInt(resp.getKvs().get(0).getValue().toString(StandardCharsets.UTF_8));
            int newCount = currentCount + 1;
            ByteSequence newValue = ByteSequence.from(String.valueOf(newCount), StandardCharsets.UTF_8);

            var txn = client.getKVClient().txn()
                    .If(new Cmp(key, Cmp.Op.EQUAL, CmpTarget.version(currentVersion)))
                    .Then(Op.put(key, newValue, PutOption.DEFAULT))
                    .Else(Op.get(key, GetOption.DEFAULT));
            TxnResponse txnResp = txn.commit().get();
            if (txnResp.isSucceeded()) {
                return newCount >= MAX_ATTEMPTS;
            }
        }
    }

    public void resetAttempts(String username) throws Exception {
        ByteSequence key = ByteSequence.from("login_attempts/" + username, StandardCharsets.UTF_8);
        client.getKVClient().delete(key).get();
    }
}
