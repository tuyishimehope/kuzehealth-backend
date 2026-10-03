-- Baseline: the schema Hibernate generated before Flyway was introduced.
-- Existing databases are baselined at this version and never run this file.

CREATE TABLE audit_logs (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    action character varying(10) NOT NULL,
    email character varying(256),
    entity_id character varying(128),
    ip character varying(64),
    resource character varying(512) NOT NULL,
    username character varying(128)
);

CREATE TABLE health_worker (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    email character varying(255) NOT NULL,
    first_name character varying(255),
    last_name character varying(255),
    phone_number character varying(255),
    qualification character varying(255),
    service_area character varying(255),
    user_id uuid
);

CREATE TABLE infant (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    birth_height_cm double precision,
    birth_weight_grams double precision,
    blood_group character varying(255),
    date_of_birth date,
    first_name character varying(255),
    gender character varying(255),
    last_name character varying(255),
    special_conditions character varying(255),
    mother_id uuid NOT NULL
);

CREATE TABLE parent (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    blood_group character varying(255),
    cell character varying(255),
    district character varying(255),
    email character varying(255),
    emergency_contact_full_name character varying(255),
    emergency_contact_number character varying(255),
    emergency_contact_relationship character varying(255),
    expected_delivery_date date,
    first_name character varying(255),
    high_risk boolean NOT NULL,
    last_name character varying(255),
    marital_status character varying(255),
    phone character varying(255),
    sector character varying(255),
    village character varying(255)
);

CREATE TABLE parent_pregnancy_record (
    parent_id uuid NOT NULL,
    pregnancy_record_id uuid NOT NULL
);

CREATE TABLE pregnancy_record (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    gravity character varying(255),
    last_menstrual_period timestamp(6) without time zone,
    medical_history character varying(255),
    parity integer NOT NULL,
    pregnancy_complications character varying(255),
    parent_id uuid NOT NULL
);

CREATE TABLE users (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    date_of_birth character varying(255),
    district character varying(255),
    email character varying(255) NOT NULL,
    enabled boolean NOT NULL,
    first_name character varying(255) NOT NULL,
    gender character varying(255),
    last_name character varying(255) NOT NULL,
    otp character varying(512),
    otp_expiration_time bigint,
    password character varying(255) NOT NULL,
    phone_number character varying(255),
    "position" character varying(255),
    province character varying(255),
    reset_token character varying(255),
    reset_token_expiration bigint,
    role character varying(255),
    sector character varying(255),
    username character varying(255),
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'HEALTH_WORKER'::character varying, 'DATA_ANALYST'::character varying])::text[])))
);

CREATE TABLE vaccination (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    administered_date date NOT NULL,
    description character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    next_due_date date,
    notes character varying(255),
    notification_sent boolean NOT NULL,
    health_worker_id uuid NOT NULL,
    infant_id uuid NOT NULL
);

CREATE TABLE visit (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    actual_end_time timestamp(6) without time zone,
    actual_start_time timestamp(6) without time zone,
    location character varying(255) NOT NULL,
    mode_of_communication character varying(255) NOT NULL,
    scheduled_time timestamp(6) without time zone NOT NULL,
    status character varying(255) NOT NULL,
    summary text,
    visit_type character varying(255) NOT NULL,
    health_worker_id uuid,
    parent_id uuid NOT NULL
);

CREATE TABLE visit_note (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    observation character varying(255),
    recommendations text,
    vital_signs text,
    visit_id uuid NOT NULL
);

CREATE TABLE visit_note_attachments (
    visit_note_id uuid NOT NULL,
    attachments character varying(255)
);

ALTER TABLE ONLY audit_logs
    ADD CONSTRAINT audit_logs_pkey PRIMARY KEY (id);

ALTER TABLE ONLY health_worker
    ADD CONSTRAINT health_worker_pkey PRIMARY KEY (id);

ALTER TABLE ONLY infant
    ADD CONSTRAINT infant_pkey PRIMARY KEY (id);

ALTER TABLE ONLY parent
    ADD CONSTRAINT parent_pkey PRIMARY KEY (id);

ALTER TABLE ONLY pregnancy_record
    ADD CONSTRAINT pregnancy_record_pkey PRIMARY KEY (id);

ALTER TABLE ONLY health_worker
    ADD CONSTRAINT uk40tppcu2dr28k6qgdg1126ayg UNIQUE (user_id);

ALTER TABLE ONLY users
    ADD CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);

ALTER TABLE ONLY health_worker
    ADD CONSTRAINT ukb8hv1nqvkgttfmokq359sv9dc UNIQUE (email);

ALTER TABLE ONLY parent_pregnancy_record
    ADD CONSTRAINT ukecervi0nwpr3t0p1208wkat1p UNIQUE (pregnancy_record_id);

ALTER TABLE ONLY users
    ADD CONSTRAINT ukr43af9ap4edm43mmtq01oddj6 UNIQUE (username);

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE ONLY vaccination
    ADD CONSTRAINT vaccination_pkey PRIMARY KEY (id);

ALTER TABLE ONLY visit_note
    ADD CONSTRAINT visit_note_pkey PRIMARY KEY (id);

ALTER TABLE ONLY visit
    ADD CONSTRAINT visit_pkey PRIMARY KEY (id);

ALTER TABLE ONLY vaccination
    ADD CONSTRAINT fk1352gpuv17ewh5nyhjwn4u7n0 FOREIGN KEY (infant_id) REFERENCES infant(id);

ALTER TABLE ONLY health_worker
    ADD CONSTRAINT fk1rqaw2sbqtw3vdxp4o2ttrt54 FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE ONLY vaccination
    ADD CONSTRAINT fk2t2aay4wtblmhs6t4v4bg8sfb FOREIGN KEY (health_worker_id) REFERENCES health_worker(id);

ALTER TABLE ONLY parent_pregnancy_record
    ADD CONSTRAINT fk39k8146xdsdegvleohta11yfr FOREIGN KEY (parent_id) REFERENCES parent(id);

ALTER TABLE ONLY visit_note
    ADD CONSTRAINT fk8jj4646wumpybwjlwudcyre6d FOREIGN KEY (visit_id) REFERENCES visit(id);

ALTER TABLE ONLY visit
    ADD CONSTRAINT fk8vewdlhi0smdokn2vwi4bp85l FOREIGN KEY (parent_id) REFERENCES parent(id);

ALTER TABLE ONLY visit_note_attachments
    ADD CONSTRAINT fkdgg4m5g0jl99k8gbxeuy9x0ls FOREIGN KEY (visit_note_id) REFERENCES visit_note(id);

ALTER TABLE ONLY infant
    ADD CONSTRAINT fkpcieadxrdw4wc9iojijpxslin FOREIGN KEY (mother_id) REFERENCES parent(id);

ALTER TABLE ONLY parent_pregnancy_record
    ADD CONSTRAINT fkpd4qtfnqs28awhrh4n74jmjnb FOREIGN KEY (pregnancy_record_id) REFERENCES pregnancy_record(id);

ALTER TABLE ONLY visit
    ADD CONSTRAINT fkpdrhc08qiirex9jgjaf7r698p FOREIGN KEY (health_worker_id) REFERENCES health_worker(id);

ALTER TABLE ONLY pregnancy_record
    ADD CONSTRAINT fksr4y0p9v08f3sdq43rt8hwigt FOREIGN KEY (parent_id) REFERENCES parent(id);

