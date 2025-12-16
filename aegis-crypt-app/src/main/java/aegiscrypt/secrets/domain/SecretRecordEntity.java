package aegiscrypt.secrets.domain;

import aegiscrypt.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "secret_record")
@Getter @Setter
public class SecretRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    // для учебного проекта разрешено хранить
    @Column(columnDefinition = "text")
    private String plaintext;

    @Column(name = "ciphertext_base64", nullable = false, columnDefinition = "text")
    private String ciphertextBase64;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    private SecretAlgorithm algorithm;

    // JSON: wrappedKey, iv, meta и т.п.
    @Column(name = "meta_json", columnDefinition = "text")
    private String metaJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = OffsetDateTime.now();
    }
}
