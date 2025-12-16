package aegiscrypt.secrets.service;

import aegiscrypt.secrets.api.DecryptSecretResponse;
import aegiscrypt.secrets.api.SecretResponse;
import aegiscrypt.secrets.domain.SecretAlgorithm;
import aegiscrypt.secrets.domain.SecretRecordEntity;
import aegiscrypt.secrets.repo.SecretRecordRepository;
import aegiscrypt.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecretService {

    private final SecretRecordRepository repo;
    private final UserRepository userRepo;
    private final SecretCryptoService crypto;

    @Transactional
    public SecretResponse create(Long userId, String plaintext, SecretAlgorithm alg) {
        var user = userRepo.findById(userId).orElseThrow();

        var enc = crypto.encrypt(plaintext, alg);

        var rec = new SecretRecordEntity();
        rec.setOwner(user);
        rec.setPlaintext(plaintext);
        rec.setCiphertextBase64(enc.ciphertextBase64());
        rec.setAlgorithm(alg);
        rec.setMetaJson(enc.metaJson());

        repo.save(rec);

        return toDto(rec);
    }

    public List<SecretResponse> listForUser(Long userId) {
        return repo.findByOwnerId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    public List<SecretResponse> listAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    private SecretResponse toDto(SecretRecordEntity e) {
        return new SecretResponse(
                e.getId(),
                e.getPlaintext(),
                e.getCiphertextBase64(),
                e.getAlgorithm(),
                e.getCreatedAt()
        );
    }

    public DecryptSecretResponse decryptForUser(Long userId, Long secretId) {
        var rec = repo.findById(secretId).orElseThrow();

        if (!rec.getOwner().getId().equals(userId)) {
            throw new SecurityException("forbidden");
        }

        String plaintext = crypto.decrypt(
                rec.getCiphertextBase64(),
                rec.getAlgorithm(),
                rec.getMetaJson()
        );

        return new DecryptSecretResponse(rec.getId(), rec.getAlgorithm(), plaintext);
    }

    public DecryptSecretResponse decryptForAdmin(Long secretId) {
        var rec = repo.findById(secretId).orElseThrow();

        String plaintext = crypto.decrypt(
                rec.getCiphertextBase64(),
                rec.getAlgorithm(),
                rec.getMetaJson()
        );

        return new DecryptSecretResponse(rec.getId(), rec.getAlgorithm(), plaintext);
    }
}
