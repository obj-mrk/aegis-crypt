-- Роли
create table if not exists app_role (
                                        id bigserial primary key,
                                        code varchar(32) not null unique
    );

insert into app_role(code) values ('GUEST') on conflict do nothing;
insert into app_role(code) values ('USER')  on conflict do nothing;
insert into app_role(code) values ('ADMIN') on conflict do nothing;

-- Пользователь
create table if not exists app_user (
                                        id bigserial primary key,
                                        email varchar(255) not null unique,
    password_hash varchar(255) not null,
    display_name varchar(255) not null,

    blocked boolean not null default false,

    totp_enabled boolean not null default false,
    totp_secret varchar(255),

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
    );

-- User <-> Role (многие-ко-многим)
create table if not exists app_user_role (
                                             user_id bigint not null references app_user(id) on delete cascade,
    role_id bigint not null references app_role(id) on delete cascade,
    primary key (user_id, role_id)
    );

-- Auth session для последовательной 3FA (промежуточная сессия после 1-го фактора)
create table if not exists auth_session (
                                            id uuid primary key,
                                            user_id bigint not null references app_user(id) on delete cascade,

    stage varchar(32) not null, -- PASSWORD_OK, EMAIL_OTP_OK, TOTP_OK
    expires_at timestamptz not null,

    created_at timestamptz not null default now()
    );

-- Email OTP
create table if not exists email_otp (
                                         id uuid primary key,
                                         user_id bigint not null references app_user(id) on delete cascade,

    code_hash varchar(255) not null,
    attempts int not null default 0,
    max_attempts int not null default 5,
    consumed boolean not null default false,

    expires_at timestamptz not null,
    created_at timestamptz not null default now()
    );

create index if not exists idx_email_otp_user on email_otp(user_id);
create index if not exists idx_email_otp_expires on email_otp(expires_at);

-- Секреты пользователя (данные + результат шифрования)
create table if not exists secret_record (
                                             id bigserial primary key,
                                             owner_id bigint not null references app_user(id) on delete cascade,

    -- для лабы можно хранить plaintext (потом легко выключим флагом)
    plaintext text,

    ciphertext_base64 text not null,
    algorithm varchar(64) not null, -- RSA / KUZNECHIK / HYBRID_RSA_KUZNECHIK и т.п.

-- служебные поля под гибрид (ключ/iv/nonce и т.д. в base64 или json)
    meta_json text,

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
    );

create index if not exists idx_secret_owner on secret_record(owner_id);
