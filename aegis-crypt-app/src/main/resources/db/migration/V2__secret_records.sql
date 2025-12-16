create table if not exists secret_record (
                                             id bigserial primary key,
                                             owner_id bigint not null references app_user(id) on delete cascade,

    plaintext text,
    ciphertext_base64 text not null,
    algorithm varchar(64) not null,
    meta_json text,

    created_at timestamptz not null default now()
    );