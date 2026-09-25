--
-- PostgreSQL database dump
--

\restrict HvcaaGJfZFOVKDVlmzbUoeH9XmfoswyEU305xnEFTyPfV9FPyLrXfh3SW8tO2zH

-- Dumped from database version 15.8
-- Dumped by pg_dump version 15.14 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: audit_log_entries; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.audit_log_entries (instance_id, id, payload, created_at, ip_address) FROM stdin;
\.


--
-- Data for Name: flow_state; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.flow_state (id, user_id, auth_code, code_challenge_method, code_challenge, provider_type, provider_access_token, provider_refresh_token, created_at, updated_at, authentication_method, auth_code_issued_at) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.users (instance_id, id, aud, role, email, encrypted_password, email_confirmed_at, invited_at, confirmation_token, confirmation_sent_at, recovery_token, recovery_sent_at, email_change_token_new, email_change, email_change_sent_at, last_sign_in_at, raw_app_meta_data, raw_user_meta_data, is_super_admin, created_at, updated_at, phone, phone_confirmed_at, phone_change, phone_change_token, phone_change_sent_at, email_change_token_current, email_change_confirm_status, banned_until, reauthentication_token, reauthentication_sent_at, is_sso_user, deleted_at, is_anonymous) FROM stdin;
\.


--
-- Data for Name: identities; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.identities (provider_id, user_id, identity_data, provider, last_sign_in_at, created_at, updated_at, id) FROM stdin;
\.


--
-- Data for Name: instances; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.instances (id, uuid, raw_base_config, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: sessions; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.sessions (id, user_id, created_at, updated_at, factor_id, aal, not_after, refreshed_at, user_agent, ip, tag) FROM stdin;
\.


--
-- Data for Name: mfa_amr_claims; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.mfa_amr_claims (session_id, created_at, updated_at, authentication_method, id) FROM stdin;
\.


--
-- Data for Name: mfa_factors; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.mfa_factors (id, user_id, friendly_name, factor_type, status, created_at, updated_at, secret, phone, last_challenged_at, web_authn_credential, web_authn_aaguid) FROM stdin;
\.


--
-- Data for Name: mfa_challenges; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.mfa_challenges (id, factor_id, created_at, verified_at, ip_address, otp_code, web_authn_session_data) FROM stdin;
\.


--
-- Data for Name: one_time_tokens; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.one_time_tokens (id, user_id, token_type, token_hash, relates_to, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: refresh_tokens; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.refresh_tokens (instance_id, id, token, user_id, revoked, created_at, updated_at, parent, session_id) FROM stdin;
\.


--
-- Data for Name: sso_providers; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.sso_providers (id, resource_id, created_at, updated_at, disabled) FROM stdin;
\.


--
-- Data for Name: saml_providers; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.saml_providers (id, sso_provider_id, entity_id, metadata_xml, metadata_url, attribute_mapping, created_at, updated_at, name_id_format) FROM stdin;
\.


--
-- Data for Name: saml_relay_states; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.saml_relay_states (id, sso_provider_id, request_id, for_email, redirect_to, created_at, updated_at, flow_state_id) FROM stdin;
\.


--
-- Data for Name: schema_migrations; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.schema_migrations (version) FROM stdin;
20171026211738
20171026211808
20171026211834
20180103212743
20180108183307
20180119214651
20180125194653
00
20210710035447
20210722035447
20210730183235
20210909172000
20210927181326
20211122151130
20211124214934
20211202183645
20220114185221
20220114185340
20220224000811
20220323170000
20220429102000
20220531120530
20220614074223
20220811173540
20221003041349
20221003041400
20221011041400
20221020193600
20221021073300
20221021082433
20221027105023
20221114143122
20221114143410
20221125140132
20221208132122
20221215195500
20221215195800
20221215195900
20230116124310
20230116124412
20230131181311
20230322519590
20230402418590
20230411005111
20230508135423
20230523124323
20230818113222
20230914180801
20231027141322
20231114161723
20231117164230
20240115144230
20240214120130
20240306115329
20240314092811
20240427152123
20240612123726
20240729123726
20240802193726
20240806073726
20241009103726
20250717082212
\.


--
-- Data for Name: sso_domains; Type: TABLE DATA; Schema: auth; Owner: supabase_auth_admin
--

COPY auth.sso_domains (id, sso_provider_id, domain, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: key; Type: TABLE DATA; Schema: pgsodium; Owner: supabase_admin
--

COPY pgsodium.key (id, status, created, expires, key_type, key_id, key_context, name, associated_data, raw_key, raw_key_nonce, parent_key, comment, user_data) FROM stdin;
\.


--
-- Data for Name: audit_logs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.audit_logs (id, created_at, updated_at, action, email, entity_id, ip, resource, username) FROM stdin;
d426f055-03f1-4014-ab88-a5ea604f9efd	2025-08-15 05:14:06.993	2025-08-15 05:14:06.993	POST	tuyishimekyrie	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	tuyishimekyrie
e6ce6b5f-eec3-4096-9ff9-36a62d32eeb5	2025-08-15 05:54:46.884	2025-08-15 05:54:46.884	POST	tuyishimekyrie	pregnancy-records	0:0:0:0:0:0:0:1	/api/pregnancy-records	tuyishimekyrie
37bf7053-8335-4340-b3e3-b0e2c4a30cd9	2025-08-15 05:55:44.379	2025-08-15 05:55:44.379	DELETE	tuyishimekyrie	2af2989e-d285-4f72-a355-e1c04b355eba	0:0:0:0:0:0:0:1	/api/pregnancy-records/2af2989e-d285-4f72-a355-e1c04b355eba	tuyishimekyrie
a2e55a34-fa52-4154-933c-fa303e269a84	2025-08-15 05:58:07.38	2025-08-15 05:58:07.38	DELETE	tuyishimekyrie	2af2989e-d285-4f72-a355-e1c04b355eba	0:0:0:0:0:0:0:1	/api/pregnancy-records/2af2989e-d285-4f72-a355-e1c04b355eba	tuyishimekyrie
5c2ecf62-a0f4-4d46-802b-cabbd62bc20b	2025-08-15 06:01:13.075	2025-08-15 06:01:13.075	DELETE	tuyishimekyrie	2af2989e-d285-4f72-a355-e1c04b355eba	0:0:0:0:0:0:0:1	/api/pregnancy-records/2af2989e-d285-4f72-a355-e1c04b355eba	tuyishimekyrie
0c285035-cf68-4ac2-95a3-c7a107234ad7	2025-08-15 06:05:50.978	2025-08-15 06:05:50.978	DELETE	tuyishimekyrie	2af2989e-d285-4f72-a355-e1c04b355eba	0:0:0:0:0:0:0:1	/api/pregnancy-records/2af2989e-d285-4f72-a355-e1c04b355eba	tuyishimekyrie
3872980a-b23d-4d81-b62d-215d91e02ccf	2025-08-15 06:13:14.433	2025-08-15 06:13:14.433	DELETE	tuyishimekyrie	2af2989e-d285-4f72-a355-e1c04b355eba	0:0:0:0:0:0:0:1	/api/pregnancy-records/2af2989e-d285-4f72-a355-e1c04b355eba	tuyishimekyrie
5d947baf-aec7-489a-ae43-311aaeedbd28	2025-08-15 06:17:38.319	2025-08-15 06:17:38.319	DELETE	tuyishimekyrie	2af2989e-d285-4f72-a355-e1c04b355eba	0:0:0:0:0:0:0:1	/api/pregnancy-records/2af2989e-d285-4f72-a355-e1c04b355eba	tuyishimekyrie
bc596b1d-4a30-43e4-a331-66a037b25433	2025-08-15 06:25:27.569	2025-08-15 06:25:27.569	DELETE	tuyishimekyrie	2af2989e-d285-4f72-a355-e1c04b355eba	0:0:0:0:0:0:0:1	/api/pregnancy-records/2af2989e-d285-4f72-a355-e1c04b355eba	tuyishimekyrie
b08fce68-3bdb-4359-b36a-a901e0a00585	2025-08-15 06:32:33.546	2025-08-15 06:32:33.546	DELETE	tuyishimekyrie	2af2989e-d285-4f72-a355-e1c04b355eba	0:0:0:0:0:0:0:1	/api/pregnancy-records/2af2989e-d285-4f72-a355-e1c04b355eba	tuyishimekyrie
af38b6b4-5d3b-4689-9a6d-06506657ff4a	2025-08-15 06:34:09.809	2025-08-15 06:34:09.809	DELETE	tuyishimekyrie	2af2989e-d285-4f72-a355-e1c04b355eba	0:0:0:0:0:0:0:1	/api/pregnancy-records/2af2989e-d285-4f72-a355-e1c04b355eba	tuyishimekyrie
e88e1b68-ff59-4539-a154-b427b0360793	2025-08-15 06:37:08.79	2025-08-15 06:37:08.79	DELETE	tuyishimekyrie	2af2989e-d285-4f72-a355-e1c04b355eba	0:0:0:0:0:0:0:1	/api/pregnancy-records/2af2989e-d285-4f72-a355-e1c04b355eba	tuyishimekyrie
6a9d3b9d-b000-4ee3-996a-1fa784ead7e6	2025-08-15 06:40:15.371	2025-08-15 06:40:15.371	POST	tuyishimekyrie	pregnancy-records	0:0:0:0:0:0:0:1	/api/pregnancy-records	tuyishimekyrie
c91c8e7e-4335-4573-a606-15f29264d0d3	2025-08-15 06:46:10.272	2025-08-15 06:46:10.272	DELETE	tuyishimekyrie	2af2989e-d285-4f72-a355-e1c04b355eba	0:0:0:0:0:0:0:1	/api/pregnancy-records/2af2989e-d285-4f72-a355-e1c04b355eba	tuyishimekyrie
917f557d-4732-4799-b9ae-7949d421590f	2025-08-15 06:46:34.295	2025-08-15 06:46:34.295	DELETE	tuyishimekyrie	df7ac592-ad5e-4fdf-b6d5-fc05c828c58a	0:0:0:0:0:0:0:1	/api/pregnancy-records/df7ac592-ad5e-4fdf-b6d5-fc05c828c58a	tuyishimekyrie
a6930b1a-b1ae-4be4-ae79-20d0e565f15c	2025-08-15 06:47:03.421	2025-08-15 06:47:03.421	POST	tuyishimekyrie	pregnancy-records	0:0:0:0:0:0:0:1	/api/pregnancy-records	tuyishimekyrie
92928f98-177f-4a6a-a697-185fd3e9a2a9	2025-08-15 06:47:21.412	2025-08-15 06:47:21.412	DELETE	tuyishimekyrie	dd8f0926-10ce-4ae1-a3a0-7aa1173786a5	0:0:0:0:0:0:0:1	/api/pregnancy-records/dd8f0926-10ce-4ae1-a3a0-7aa1173786a5	tuyishimekyrie
5ee00962-76d0-4d35-ae38-faf5c4bf5536	2025-08-15 07:00:45.622	2025-08-15 07:00:45.622	PUT	tuyishimekyrie	11306c62-41e7-47ee-8b20-b93040aef4e6	0:0:0:0:0:0:0:1	/api/health-workers/11306c62-41e7-47ee-8b20-b93040aef4e6	tuyishimekyrie
8d52fd96-454b-4324-9147-2cbefefcd54c	2025-08-15 07:01:46.801	2025-08-15 07:01:46.801	DELETE	tuyishimekyrie	11306c62-41e7-47ee-8b20-b93040aef4e6	0:0:0:0:0:0:0:1	/api/health-workers/11306c62-41e7-47ee-8b20-b93040aef4e6	tuyishimekyrie
1e48d836-0813-4d62-97bf-55ea1bda2ee3	2025-08-15 09:20:48.492	2025-08-15 09:20:48.492	POST	tuyishimekyrie	notify	0:0:0:0:0:0:0:1	/api/vaccinations/notify	tuyishimekyrie
8e47e4d2-1ee6-4c84-9224-e5fa779746c8	2025-08-16 12:19:16.306	2025-08-16 12:19:16.306	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
11168728-eb77-435c-bd55-4d495d88e8c5	2025-08-16 12:19:50.511	2025-08-16 12:19:50.511	POST	Irving	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	Irving
13837915-a078-49a4-a846-fddb118e3b66	2025-08-16 15:20:14.527	2025-08-16 15:20:14.527	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
0241de9c-799e-4a24-aba4-883d141def6d	2025-08-16 15:21:17.454	2025-08-16 15:21:17.454	POST	\N	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	\N
302dd087-d4e9-4d06-ad18-32be2aa23bc5	2025-08-16 15:21:19.752	2025-08-16 15:21:19.752	POST	Irving	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	Irving
1d5d7c47-93ad-4fb0-9f7b-9334ef1f527a	2025-08-16 16:35:10.916	2025-08-16 16:35:10.916	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
01967d90-df0d-4493-880d-4711b5d3c184	2025-08-16 16:35:26.809	2025-08-16 16:35:26.809	POST	\N	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	\N
5bad1e6a-72e4-44c8-a210-851c44836c9f	2025-08-17 09:54:02.786	2025-08-17 09:54:02.786	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
5f995a36-4c1a-48c0-81b1-45a928ce2f31	2025-08-17 09:54:28.841	2025-08-17 09:54:28.841	POST	arthurdoe	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	arthurdoe
0b0e9f8b-e126-413f-9f25-f582be83aef1	2025-08-17 10:14:21.674	2025-08-17 10:14:21.674	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
8129e0df-db40-4001-a1b5-d111a3546316	2025-08-17 10:14:40.624	2025-08-17 10:14:40.624	POST	\N	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	\N
eb2f1d9a-8f91-4ad3-94d0-96fc9a92bfd1	2025-08-17 10:18:20.012	2025-08-17 10:18:20.012	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
34e1f969-ab91-4792-9541-2f48db5613ff	2025-08-17 10:18:42.085	2025-08-17 10:18:42.085	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
d2f3a795-422d-472c-b97d-46bae0800f49	2025-08-17 10:18:58.049	2025-08-17 10:18:58.049	POST	Irving	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	Irving
bff08902-5c1b-45ed-90fa-1ef55258e97a	2025-08-17 10:37:38.624	2025-08-17 10:37:38.624	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
fa53ee01-2cfa-46ce-aa1c-01df9e1a8ab7	2025-08-17 10:38:03.539	2025-08-17 10:38:03.539	POST	arthurdoe	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	arthurdoe
79131baf-dc8c-4749-89b0-9354b2e18683	2025-08-17 11:00:32.894	2025-08-17 11:00:32.894	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
a10e7adb-dd04-4268-b76b-92cbc33db699	2025-08-17 11:00:52.934	2025-08-17 11:00:52.934	POST	tuyishimekyrie	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	tuyishimekyrie
34614997-4ed8-4b52-8082-6bda2c083721	2025-08-17 11:07:53.223	2025-08-17 11:07:53.223	POST	arianedushime	register	0:0:0:0:0:0:0:1	/api/parents/register	arianedushime
40d916b9-c646-47c5-8f04-8b9dc407e719	2025-08-17 11:10:35.555	2025-08-17 11:10:35.555	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
430318c4-48dd-4084-b5a2-15f519bbb481	2025-08-17 11:10:54.983	2025-08-17 11:10:54.983	POST	\N	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	\N
d0e9ef82-c78c-4577-9ac7-7f498083493e	2025-08-17 11:11:15.287	2025-08-17 11:11:15.287	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
fc0fe841-107e-48dc-8434-300d9793669c	2025-08-17 11:11:27.846	2025-08-17 11:11:27.846	POST	\N	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	\N
74bae7d7-9b72-475f-a65b-cd4018721f27	2025-08-17 11:12:14.167	2025-08-17 11:12:14.167	POST	arianedushime	register	0:0:0:0:0:0:0:1	/api/parents/register	arianedushime
f0e2694f-05fd-46b8-842c-fe89300b9c71	2025-08-17 11:20:06.892	2025-08-17 11:20:06.892	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
cc203505-7d6f-4030-93b5-6630c9333f5b	2025-08-17 11:20:32.32	2025-08-17 11:20:32.32	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
adc6ae19-d868-45d1-962d-bde5df42b10d	2025-08-17 11:20:48.874	2025-08-17 11:20:48.874	POST	Irving	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	Irving
fc1630a8-2c2f-43e5-92a7-42c7694580a5	2025-08-17 12:18:40.541	2025-08-17 12:18:40.541	POST	arianedushime	visits	0:0:0:0:0:0:0:1	/api/visits	arianedushime
906aee34-e187-43e1-a076-dce8f4de1e9c	2025-08-17 12:47:40.644	2025-08-17 12:47:40.644	POST	\N	register	0:0:0:0:0:0:0:1	/api/v1/auth/register	\N
4db24822-085c-49d3-b5e1-a13016fd02ef	2025-08-17 12:48:23.143	2025-08-17 12:48:23.143	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
73f358fc-69ab-4689-961a-b7109b3fffcb	2025-08-17 12:48:52.796	2025-08-17 12:48:52.796	POST	\N	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	\N
84d37a11-a898-4c67-a4b1-e296688ad914	2025-08-17 12:57:13.073	2025-08-17 12:57:13.073	POST	melissaikirezi	register	0:0:0:0:0:0:0:1	/api/parents/register	melissaikirezi
eaa2c688-3099-4f79-a2ab-bef9ee1fd437	2025-08-17 13:00:50.12	2025-08-17 13:00:50.12	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
679d990c-0ff8-4e1a-91cd-a2a0ad9da8f4	2025-08-17 13:01:22.333	2025-08-17 13:01:22.333	POST	arthurdoe	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	arthurdoe
23bef719-b918-4066-a12c-ee666e4ae156	2025-08-17 13:02:15.209	2025-08-17 13:02:15.209	POST	tuyishimekyrie	register	0:0:0:0:0:0:0:1	/api/parents/register	tuyishimekyrie
05a46cd6-c0b8-490b-84a4-f8754c699a6a	2025-08-17 13:06:46.794	2025-08-17 13:06:46.794	POST	arianedushime	register	0:0:0:0:0:0:0:1	/api/parents/register	arianedushime
05245742-b839-49ee-9bde-9ab7fc7527f8	2025-08-17 13:10:15.849	2025-08-17 13:10:15.849	POST	tuyishimekyrie	infants	0:0:0:0:0:0:0:1	/api/infants	tuyishimekyrie
3eca565c-08dd-4e8f-a2c9-9f82f22dc86a	2025-08-17 13:17:07.464	2025-08-17 13:17:07.464	POST	\N	register	0:0:0:0:0:0:0:1	/api/v1/auth/register	\N
ac33fb63-de8f-4dd5-921e-251cd177fbaa	2025-08-17 13:05:30.03	2025-08-17 13:05:30.03	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
dec416a6-4712-44ed-89e1-e796f8b6d3e9	2025-08-17 13:05:46.922	2025-08-17 13:05:46.922	POST	\N	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	\N
b2e1b940-367d-4bf8-848b-bdcdcab35c06	2025-08-18 16:00:20.367	2025-08-18 16:00:20.367	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
8ef85634-c6c1-4c97-8851-732db2e75c07	2025-08-18 16:00:42.397	2025-08-18 16:00:42.397	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
c570a064-ccf4-484c-b9e2-afbd374bfa8b	2025-08-18 16:00:59.284	2025-08-18 16:00:59.284	POST	\N	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	\N
73300e30-9174-4cfd-a2ef-184c7e8ec191	2025-08-18 16:09:34.91	2025-08-18 16:09:34.91	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
8d7ef24a-ab72-4d7a-926b-82a700c47b52	2025-08-18 16:09:51.437	2025-08-18 16:09:51.437	POST	arthurdoe	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	arthurdoe
ac396072-5cef-4b0e-a09e-a70e325303ab	2025-08-18 16:35:24.199	2025-08-18 16:35:24.199	POST	\N	send-otp	0:0:0:0:0:0:0:1	/api/v1/auth/send-otp	\N
2258bda9-0a0e-4df9-93db-f61dbbed4e06	2025-08-18 16:35:42.352	2025-08-18 16:35:42.352	POST	Irving	login	0:0:0:0:0:0:0:1	/api/v1/auth/login	Irving
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, created_at, updated_at, email, enabled, first_name, last_name, password, phone_number, username, role, otp, otp_expiration_time, reset_token, reset_token_expiration, gender, province, district, date_of_birth, "position", sector) FROM stdin;
0a61d1c5-acdb-434f-8ab5-de524e376445	2025-07-19 20:27:57.901	2025-07-19 20:27:57.901	janesmith@gmail.com	t	jane	smith	$2a$10$E23sDD0KDYa5DfoLuKeblOGoNnINJ.rVkKemcHlVk.mejnDMlNeZG	0787878787	janesmith	HEALTH_WORKER	\N	0	\N	\N	female	western	nyamagabe	\N	\N	kigali
cc02e376-1941-441f-970d-96f47d8be86b	2025-07-20 12:56:52.083	2025-07-20 12:56:52.083	james@gmail.com	t	Cyuzuzo	James	$2a$10$CYKsyT5MrVEWaf75Tt3bCeJmG.8tp3/JWiAhUovYiBTuEHu71btti	+250786125117	cyuzuzojames	HEALTH_WORKER	\N	0	\N	\N	MALE	KIGALI	RUSIZI	\N	\N	sector1
4af6be38-18f3-451b-8041-4f4d5ec1b612	2025-07-05 22:16:21.601	2025-07-29 12:02:28.978	hope.tuyishime@auca.ac.rw	t	John	Doe	$2a$10$vrEoeQpYl//pfG0KY5dYcenXyOmmEr09Ccb44lzRQONY8UFU8LXFa	+250786125117	hopetuyishime	HEALTH_WORKER	673209	1753791748892	\N	0	Male	Kigali	Gasabo	1995-08-22	Nurse	Kacyiru
7e12d687-234a-4b76-b466-96b82523fc2d	2025-08-17 12:47:35.611	2025-08-17 12:48:10.658	cyuzuzomelissa@gmail.com	t	Melissa	Ikirezi	$2a$10$Ar.dtN5WauShilDFDzvGwOkD5/vjUC6A3Yy.1lNmHxNwSZkbYmIQi	+250780602658	melissaikirezi	HEALTH_WORKER	967289	1755428890658	\N	\N	FEMALE	KIGALI	KICUKIRO	\N	\N	Nyarugunga
2ca09551-e3dd-4394-98c1-aa820c81ed71	2025-08-13 03:21:51.519	2025-08-17 10:59:55.022	arianedushime941@gmail.com	t	Ariane	Dushime	$2a$10$h4cI6zyZAvj4ehX2PYjqDuG7sTQPH4NZxlyLWy9xYVc274SIMVZke	+250790112811	arianedushime	HEALTH_WORKER	837785	1755422394936	\N	\N	FEMALE	KIGALI	KICUKIRO	\N	\N	Nyarugunga
39246310-f952-410e-8185-a27758aa20b2	2025-08-17 13:17:05.149	2025-08-17 13:05:10.387	kezacindy830@gmail.com	t	keza	Cindy	$2a$10$2vJhTqbhAU3by2vj.QPHTuA0aLbQBGtiQyxsUFu1sFq4pGfqdkTke	+250784957052	kezacindy	DATA_ANALYST	841556	1755429909835	\N	\N	Female	Kigali	Gasabo	2002-12-27	Nurse	Kacyiru
9e68d436-36d3-4616-b333-2d33bf433616	2025-08-13 21:31:18.185	2025-08-18 16:00:35.89	arthur@example.com	t	Arthur	Doe	$2a$10$t1KgnHW8A0be/.gSiX0lSeJ3DqyZVC1K6PaULq4rgIl2tcmJ6O7f6	+250786125117	arthurdoe	DATA_ANALYST	182053	1755526835889	\N	\N	MALE	KIGALI	GASABO	\N	\N	Kinyinya
48703613-3c99-453b-97af-b466b7cb15b6	2025-06-12 07:26:50.771	2025-08-18 16:09:29.153	tuyishimehope01@gmail.com	t	Kyrie	Irving	$2a$10$KVGZNn/chwqH6StZXpyn3O6t1I20xuVtdu1GJ1U3pxVtfcmppEYDC	+250786125117	Irving	ADMIN	851909	1755527369152	\N	\N	Admin	kigali	gasabo	male	\N	\N
20eca614-1d4b-4164-beeb-0ce7f3d52f85	2025-07-06 10:54:33.022	2025-08-18 16:35:18.427	tuyishimekyrie@gmail.com	t	tuyishime	kyrie	$2a$10$hT96s5mCXW4ELVBMAG0tiOm.j8QerPil5vVFbJzMGuBxRJWEAmd6u	+250786125117	tuyishimekyrie	HEALTH_WORKER	424490	1755528918423	\N	\N	Male	Kigali	Gasabo	1995-08-22	Nurse	Kacyiru
\.


--
-- Data for Name: health_worker; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.health_worker (id, created_at, updated_at, email, first_name, last_name, phone_number, qualification, service_area, user_id) FROM stdin;
327caa33-a88f-4661-9628-d788e9b4f478	2025-07-19 20:27:59.08	2025-07-19 20:27:59.08	janesmith@gmail.com	jane	smith	0787878787	\N	\N	0a61d1c5-acdb-434f-8ab5-de524e376445
5fb8d37b-08e1-405b-b5ab-7eb2dd5648ff	2025-07-20 12:56:53.415	2025-07-20 12:56:53.415	james@gmail.com	Cyuzuzo	James	+250786125117	\N	\N	cc02e376-1941-441f-970d-96f47d8be86b
8a7bad75-85b8-4e41-997a-e3ada2bdeacb	2025-08-13 03:21:52.29	2025-08-13 03:21:52.29	arianedushime941@gmail.com	Ariane	Dushime	+250790112811	\N	\N	2ca09551-e3dd-4394-98c1-aa820c81ed71
11306c62-41e7-47ee-8b20-b93040aef4e6	2025-07-06 10:54:34.251	2025-08-15 07:00:44.723	alice.smith@gmail.com	Alice	Smith	+250786125117	Registered Nurse	Maternal Health	20eca614-1d4b-4164-beeb-0ce7f3d52f85
5be7370b-72e2-43cf-ba4c-003b3255229f	2025-08-17 12:47:38.593	2025-08-17 12:47:38.593	cyuzuzomelissa@gmail.com	Melissa	Ikirezi	+250780602658	\N	\N	7e12d687-234a-4b76-b466-96b82523fc2d
c11e0084-8ad4-4b10-9971-405bb0bab294	2025-08-17 13:17:06.491	2025-08-17 13:17:06.491	kezacindy830@gmail.com	keza	Cindy	+250784957052	\N	\N	39246310-f952-410e-8185-a27758aa20b2
\.


--
-- Data for Name: parent; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.parent (id, created_at, updated_at, blood_group, cell, district, email, emergency_contact_full_name, emergency_contact_number, emergency_contact_relationship, expected_delivery_date, first_name, high_risk, last_name, marital_status, phone, sector, village, address) FROM stdin;
57d5b4df-2ed8-4769-8bf8-919b47a6b3a6	2025-07-05 23:04:19.424	2025-07-05 23:04:19.424	O+	Kacyiru	Kigali	alice.smith@gmail.com	John Smith	+0987654321	Husband	2025-09-10	Alice	f	Smith	Single	+1234567890	Gasabo	Umudugudu A	\N
0579f0b0-bb30-49cf-bb02-a0fb32a4f8e7	2025-07-10 08:10:28.566	2025-07-10 08:10:28.566	A+	nonko	Kicukiro	jane@gmail.com	Johndoe	+2507878676667	boyfriend	2026-01-02	Jane	f	Doe	single	+250786125117	Kinyinya	kavumu	\N
f9c2937e-410c-4a2c-8ac7-762f8bcde89a	2025-07-20 12:33:31.857	2025-07-20 12:33:31.857	O+	Kacyiru	Kigali	alice.smith@gmail.com	John Smith	+0987654321	Husband	2025-09-10	Alice	f	Smith	Single	+1234567890	Gasabo	Umudugudu A	\N
b29b3e2d-b878-45c5-9190-7c08b201316d	2025-07-20 12:39:01.745	2025-07-20 12:39:01.745	O+	Nyakabanda	Huye	mukamana.munyaneza0@gmail.com	Contact Munyaneza	+250721970009	Brother	2025-08-28	Mukamana	f	Munyaneza	Widowed	+250782815605	Kacyiru	Umudugudu B	\N
834f8faf-cf66-4781-86a1-ae54117eb825	2025-07-20 12:39:02.688	2025-07-20 12:39:02.688	AB+	Kimironko	Musanze	josiane.niyonsaba1@gmail.com	Contact Niyonsaba	+250727077150	Brother	2025-09-15	Josiane	f	Niyonsaba	Widowed	+250787183839	Remera	Umudugudu B	\N
924942e7-cf52-4a5c-a953-d64af66f8e7c	2025-07-20 12:39:03.521	2025-07-20 12:39:03.521	B+	Kimironko	Musanze	aline.uwimana2@gmail.com	Contact Uwimana	+250723634169	Sister	2025-09-28	Aline	f	Uwimana	Widowed	+250783032865	Kacyiru	Umudugudu C	\N
8f19efe2-88ee-4bb8-85f6-014aa2d0448a	2025-07-20 12:39:04.35	2025-07-20 12:39:04.35	A+	Nyakabanda	Huye	samuel.twagirimana3@gmail.com	Contact Twagirimana	+250729535164	Brother	2025-09-20	Samuel	f	Twagirimana	Single	+250784753268	Gisozi	Umudugudu A	\N
f8137e5f-d0fc-4787-b2a4-57f8471def6b	2025-07-20 12:39:05.271	2025-07-20 12:39:05.271	O+	Nyakabanda	Muhanga	sandrine.umutoni4@gmail.com	Contact Umutoni	+250721666625	Sister	2025-09-11	Sandrine	f	Umutoni	Widowed	+250789986526	Nyamirambo	Umudugudu B	\N
ef2d146b-210e-4979-afe3-577a968aded2	2025-07-20 12:39:06.1	2025-07-20 12:39:06.1	O+	Kigabiro	Rwamagana	jeanette.umutoni5@gmail.com	Contact Umutoni	+250722478053	Sister	2025-11-07	Jeanette	f	Umutoni	Married	+250788080944	Nyamirambo	Umudugudu B	\N
36f16fc5-c8a3-4655-8b3e-dae71f9ae4d0	2025-07-20 12:39:06.923	2025-07-20 12:39:06.923	AB+	Nyakabanda	Muhanga	josiane.uwamahoro6@gmail.com	Contact Uwamahoro	+250726337777	Wife	2025-09-15	Josiane	f	Uwamahoro	Single	+250783850809	Remera	Umudugudu B	\N
ef59b1d7-4820-4084-96bc-34f987874d5a	2025-07-20 12:39:07.765	2025-07-20 12:39:07.765	A+	Rugando	Kigali	jeanette.mutoni7@gmail.com	Contact Mutoni	+250723592430	Husband	2025-11-22	Jeanette	f	Mutoni	Married	+250784394039	Nyamirambo	Umudugudu C	\N
ccd7a5f0-5f7d-4216-af3b-e945297cdf15	2025-07-20 12:39:08.694	2025-07-20 12:39:08.694	B+	Nyakabanda	Musanze	clarisse.uwamahoro8@gmail.com	Contact Uwamahoro	+250721531258	Sister	2025-09-11	Clarisse	f	Uwamahoro	Single	+250781471579	Gisozi	Umudugudu A	\N
3fd4031b-2717-46b7-a673-400ff8b76155	2025-07-20 12:39:09.567	2025-07-20 12:39:09.567	O+	Kimironko	Muhanga	aline.irakoze9@gmail.com	Contact Irakoze	+250725901272	Husband	2025-11-21	Aline	f	Irakoze	Married	+250786350801	Nyamirambo	Umudugudu B	\N
73345edb-1eeb-456b-aed6-87c1bdef1062	2025-07-20 12:39:10.421	2025-07-20 12:39:10.421	A+	Kimironko	Kigali	yves.twagirimana10@gmail.com	Contact Twagirimana	+250726489726	Husband	2025-08-25	Yves	f	Twagirimana	Widowed	+250786238443	Gasabo	Umudugudu B	\N
194c32e6-1744-4dd6-8ba6-79571148915c	2025-07-20 12:39:11.391	2025-07-20 12:39:11.391	B+	Nyakabanda	Huye	innocent.twagirimana11@gmail.com	Contact Twagirimana	+250724429491	Brother	2025-11-22	Innocent	f	Twagirimana	Married	+250785170132	Gisozi	Umudugudu A	\N
c23e702b-03b1-44f3-83ad-eeda19377549	2025-07-20 12:39:12.227	2025-07-20 12:39:12.227	A+	Kigabiro	Kigali	yves.umutoni12@gmail.com	Contact Umutoni	+250721621323	Husband	2025-09-21	Yves	f	Umutoni	Married	+250783726021	Kacyiru	Umudugudu A	\N
fde03b0a-cff0-4292-900d-1ea14d41faa7	2025-07-20 12:39:13.176	2025-07-20 12:39:13.176	B+	Kimironko	Huye	aline.twagirimana13@gmail.com	Contact Twagirimana	+250729464834	Brother	2025-08-19	Aline	f	Twagirimana	Single	+250786709103	Gasabo	Umudugudu B	\N
b99c464b-fb84-4bc0-9605-5b42ab40f4b2	2025-07-20 12:39:14.019	2025-07-20 12:39:14.019	AB+	Nyakabanda	Rubavu	innocent.niyonsaba14@gmail.com	Contact Niyonsaba	+250722508649	Husband	2025-10-25	Innocent	f	Niyonsaba	Single	+250783911548	Kacyiru	Umudugudu A	\N
0855bae6-f822-47ee-8ebb-11ff21bd9e8b	2025-07-20 12:39:14.874	2025-07-20 12:39:14.874	A+	Kigabiro	Musanze	mugisha.irakoze15@gmail.com	Contact Irakoze	+250723358589	Sister	2025-09-24	Mugisha	f	Irakoze	Widowed	+250788166911	Remera	Umudugudu A	\N
d9a7fb88-8ee7-40b7-9f24-6e8129bd4587	2025-07-20 12:39:15.787	2025-07-20 12:39:15.787	O+	Kigabiro	Muhanga	chantal.mutoni16@gmail.com	Contact Mutoni	+250723432202	Husband	2025-09-15	Chantal	f	Mutoni	Widowed	+250789100290	Nyamirambo	Umudugudu A	\N
38e1d562-73d2-4216-9cdf-39c60efc6ae6	2025-07-20 12:39:16.631	2025-07-20 12:39:16.631	AB+	Rugando	Rwamagana	emmanuel.twagirimana17@gmail.com	Contact Twagirimana	+250724062406	Brother	2025-09-30	Emmanuel	f	Twagirimana	Widowed	+250783049589	Kacyiru	Umudugudu C	\N
b6cf2bdc-1f52-47bf-9f96-294bafd60024	2025-07-20 12:39:17.494	2025-07-20 12:39:17.494	AB+	Nyakabanda	Huye	emmanuel.umutoni18@gmail.com	Contact Umutoni	+250722248499	Sister	2025-11-20	Emmanuel	f	Umutoni	Single	+250788374582	Gasabo	Umudugudu B	\N
50f1fc8f-53df-4d91-a599-d887c4a11645	2025-07-20 12:39:18.433	2025-07-20 12:39:18.433	A+	Kigabiro	Kigali	jean.twagirimana19@gmail.com	Contact Twagirimana	+250721951007	Wife	2025-09-08	Jean	f	Twagirimana	Single	+250789570878	Nyamirambo	Umudugudu B	\N
7c10b985-57dc-4cda-8dd4-c41358e6c681	2025-07-20 12:39:19.304	2025-07-20 12:39:19.304	A+	Kimironko	Rubavu	emmanuel.uwimana20@gmail.com	Contact Uwimana	+250726060542	Husband	2025-11-25	Emmanuel	f	Uwimana	Widowed	+250788747325	Gisozi	Umudugudu C	\N
cdf39d92-07a9-403c-89f6-f87710d85ece	2025-07-20 12:39:20.227	2025-07-20 12:39:20.227	B+	Nyakabanda	Rubavu	emmanuel.nshimiyimana21@gmail.com	Contact Nshimiyimana	+250722418548	Husband	2025-09-15	Emmanuel	f	Nshimiyimana	Married	+250781241242	Remera	Umudugudu B	\N
773df864-e59f-415e-8ca2-1bd83b109568	2025-07-20 12:39:21.088	2025-07-20 12:39:21.088	AB+	Rugando	Muhanga	samuel.mutoni22@gmail.com	Contact Mutoni	+250721493451	Sister	2025-10-01	Samuel	f	Mutoni	Married	+250789183094	Gasabo	Umudugudu B	\N
38417bf0-b7c6-4580-87d8-614c40de8d02	2025-07-20 12:39:21.942	2025-07-20 12:39:21.942	O+	Rugando	Musanze	aline.mutoni23@gmail.com	Contact Mutoni	+250721645705	Sister	2025-11-01	Aline	f	Mutoni	Widowed	+250782913794	Gasabo	Umudugudu B	\N
d5cee62b-9cee-4bc8-9f5c-53d800084f58	2025-07-20 12:39:22.789	2025-07-20 12:39:22.789	AB+	Kimironko	Huye	josiane.nshimiyimana24@gmail.com	Contact Nshimiyimana	+250725477355	Wife	2025-11-04	Josiane	f	Nshimiyimana	Widowed	+250785087957	Gasabo	Umudugudu B	\N
901244bb-6ace-4549-ba19-40ca8888cbf7	2025-07-20 12:39:23.681	2025-07-20 12:39:23.681	B+	Nyakabanda	Musanze	aline.irakoze25@gmail.com	Contact Irakoze	+250725461558	Husband	2025-09-20	Aline	f	Irakoze	Single	+250784237696	Gisozi	Umudugudu B	\N
4569d57e-4a3c-4f19-acdf-a8b03ca326a3	2025-07-20 12:39:24.536	2025-07-20 12:39:24.536	B+	Kimironko	Rubavu	divine.uwamahoro26@gmail.com	Contact Uwamahoro	+250728661611	Sister	2025-10-31	Divine	f	Uwamahoro	Widowed	+250784020502	Remera	Umudugudu C	\N
96896ad9-5dbb-41ba-b06f-2af8df9210d4	2025-07-20 12:39:25.471	2025-07-20 12:39:25.471	B+	Kigabiro	Huye	sandrine.uwamahoro27@gmail.com	Contact Uwamahoro	+250723109389	Brother	2025-10-14	Sandrine	f	Uwamahoro	Married	+250786252087	Nyamirambo	Umudugudu C	\N
c3f36479-dcec-4f2e-bef5-2e93765ebf77	2025-07-20 12:39:26.3	2025-07-20 12:39:26.3	A+	Rugando	Rubavu	samuel.uwimana28@gmail.com	Contact Uwimana	+250729197806	Sister	2025-08-31	Samuel	f	Uwimana	Married	+250785532467	Remera	Umudugudu A	\N
5660e0e6-699e-4bf0-a2e9-e41a8cba9b27	2025-07-20 12:39:27.172	2025-07-20 12:39:27.172	B+	Kigabiro	Rwamagana	eric.twagirimana29@gmail.com	Contact Twagirimana	+250727260476	Sister	2025-09-26	Eric	f	Twagirimana	Single	+250789704970	Remera	Umudugudu B	\N
4ad3d9da-ff3d-4efe-acdb-9a3b8d9635ba	2025-07-20 12:39:28.012	2025-07-20 12:39:28.012	AB+	Kigabiro	Musanze	patrick.mutoni30@gmail.com	Contact Mutoni	+250721988284	Wife	2025-09-10	Patrick	f	Mutoni	Married	+250783238936	Gasabo	Umudugudu A	\N
176e1936-b6e0-44a5-b6c1-85612d761471	2025-07-20 12:39:28.932	2025-07-20 12:39:28.932	O+	Kigabiro	Musanze	divine.nshimiyimana31@gmail.com	Contact Nshimiyimana	+250722449022	Sister	2025-11-03	Divine	f	Nshimiyimana	Widowed	+250781052595	Remera	Umudugudu A	\N
76463855-712f-4224-9f02-992fd42d59d0	2025-07-20 12:39:29.837	2025-07-20 12:39:29.837	AB+	Rugando	Musanze	mukamana.irakoze32@gmail.com	Contact Irakoze	+250723795595	Husband	2025-09-25	Mukamana	f	Irakoze	Married	+250789672439	Remera	Umudugudu B	\N
fd340dba-8684-4b0f-a5b6-3ed40d0d877d	2025-07-20 12:39:30.74	2025-07-20 12:39:30.74	A+	Kigabiro	Muhanga	sandrine.uwimana33@gmail.com	Contact Uwimana	+250722966049	Brother	2025-10-01	Sandrine	f	Uwimana	Single	+250786631847	Gisozi	Umudugudu C	\N
c2b6ce14-b25c-4d72-a589-7395fe70fe3e	2025-07-20 12:39:31.59	2025-07-20 12:39:31.59	O+	Nyakabanda	Rwamagana	yves.uwamahoro34@gmail.com	Contact Uwamahoro	+250729878794	Wife	2025-09-05	Yves	f	Uwamahoro	Widowed	+250788545988	Kacyiru	Umudugudu C	\N
a1d04f87-52dc-4ef5-9a4b-8ab5fd86b337	2025-07-20 12:39:32.425	2025-07-20 12:39:32.425	A+	Rugando	Kigali	sandrine.uwimana35@gmail.com	Contact Uwimana	+250729991418	Brother	2025-08-28	Sandrine	f	Uwimana	Married	+250782264495	Gisozi	Umudugudu B	\N
fe7257fd-0c06-4030-9ae7-a15b0cae7098	2025-07-20 12:39:33.29	2025-07-20 12:39:33.29	O+	Kimironko	Muhanga	patrick.twagirimana36@gmail.com	Contact Twagirimana	+250728129624	Brother	2025-10-31	Patrick	f	Twagirimana	Widowed	+250789286260	Kacyiru	Umudugudu C	\N
dec54c82-12b5-40d9-9aec-640e0f995bca	2025-07-20 12:39:34.14	2025-07-20 12:39:34.14	O+	Kimironko	Musanze	divine.uwimana37@gmail.com	Contact Uwimana	+250727034724	Husband	2025-11-04	Divine	f	Uwimana	Married	+250783064355	Remera	Umudugudu B	\N
c2b78d46-3248-45f7-a4cb-9f2db90f3e00	2025-07-20 12:39:34.983	2025-07-20 12:39:34.983	AB+	Kigabiro	Rwamagana	clarisse.umutoni38@gmail.com	Contact Umutoni	+250723475779	Sister	2025-09-07	Clarisse	f	Umutoni	Single	+250785971188	Remera	Umudugudu B	\N
6735ad0a-2a9e-412a-9c15-e223ddf5c0df	2025-07-20 12:39:35.822	2025-07-20 12:39:35.822	A+	Kigabiro	Musanze	jeanette.habimana39@gmail.com	Contact Habimana	+250727505125	Brother	2025-11-14	Jeanette	f	Habimana	Single	+250787477606	Remera	Umudugudu B	\N
f43154d7-f3df-4c10-9531-230110d66c0a	2025-07-20 12:39:36.724	2025-07-20 12:39:36.724	O+	Kimironko	Rwamagana	clarisse.mutoni40@gmail.com	Contact Mutoni	+250724956697	Brother	2025-11-06	Clarisse	f	Mutoni	Widowed	+250785146560	Remera	Umudugudu A	\N
61c77c13-30f7-4aeb-b2bf-a6cd967aa037	2025-07-20 12:39:37.59	2025-07-20 12:39:37.59	B+	Rugando	Huye	chantal.uwimana41@gmail.com	Contact Uwimana	+250725465753	Husband	2025-10-10	Chantal	f	Uwimana	Single	+250789622264	Kacyiru	Umudugudu A	\N
e7ba0866-7041-4758-bfc0-26fe7210c0d9	2025-07-20 12:39:38.415	2025-07-20 12:39:38.415	O+	Nyakabanda	Muhanga	claudine.uwamahoro42@gmail.com	Contact Uwamahoro	+250726937347	Brother	2025-09-19	Claudine	f	Uwamahoro	Single	+250788729339	Gisozi	Umudugudu B	\N
5c5a06a7-0a0d-4aff-a149-3ae4560c2e96	2025-07-20 12:39:39.384	2025-07-20 12:39:39.384	A+	Kigabiro	Muhanga	mukamana.niyonsaba43@gmail.com	Contact Niyonsaba	+250725223991	Wife	2025-09-06	Mukamana	f	Niyonsaba	Single	+250781259995	Gasabo	Umudugudu C	\N
35bc206d-df47-4574-b10e-561259515d21	2025-07-20 12:39:40.232	2025-07-20 12:39:40.232	O+	Nyakabanda	Rwamagana	esther.umutoni44@gmail.com	Contact Umutoni	+250727346024	Sister	2025-09-18	Esther	f	Umutoni	Widowed	+250785198801	Nyamirambo	Umudugudu C	\N
cb0c07fa-f234-4bd6-b514-337492630be9	2025-07-20 12:39:41.195	2025-07-20 12:39:41.195	B+	Rugando	Rubavu	claudine.uwimana45@gmail.com	Contact Uwimana	+250721846326	Brother	2025-10-29	Claudine	f	Uwimana	Single	+250782003739	Nyamirambo	Umudugudu B	\N
65bad578-3268-457c-b0c9-22117a851c8a	2025-07-20 12:39:42.047	2025-07-20 12:39:42.047	O+	Kigabiro	Kigali	mugisha.irakoze46@gmail.com	Contact Irakoze	+250727245183	Sister	2025-08-23	Mugisha	f	Irakoze	Married	+250789340385	Nyamirambo	Umudugudu B	\N
856bfa4b-4523-485a-9a40-0e3a6cab707d	2025-07-20 12:39:42.896	2025-07-20 12:39:42.896	A+	Rugando	Rubavu	mukamana.nshimiyimana47@gmail.com	Contact Nshimiyimana	+250724773943	Husband	2025-10-03	Mukamana	f	Nshimiyimana	Single	+250784252034	Nyamirambo	Umudugudu C	\N
073cee4b-d473-45cf-ab3e-68ef85d0b872	2025-07-20 12:39:43.735	2025-07-20 12:39:43.735	O+	Kimironko	Huye	emmanuel.uwamahoro48@gmail.com	Contact Uwamahoro	+250722569534	Husband	2025-10-24	Emmanuel	f	Uwamahoro	Married	+250781421785	Gasabo	Umudugudu A	\N
ed0307d0-c25a-408a-ac75-cd8ac2a46392	2025-07-20 12:39:44.622	2025-07-20 12:39:44.622	AB+	Rugando	Musanze	eric.uwamahoro49@gmail.com	Contact Uwamahoro	+250727870628	Sister	2025-11-01	Eric	f	Uwamahoro	Married	+250782651943	Kacyiru	Umudugudu C	\N
882cad3f-fdab-4af5-8b1e-13290cf96f36	2025-07-20 12:39:45.479	2025-07-20 12:39:45.479	O+	Rugando	Muhanga	patrick.mutoni50@gmail.com	Contact Mutoni	+250727232602	Sister	2025-10-11	Patrick	f	Mutoni	Married	+250781848765	Gisozi	Umudugudu B	\N
9436c3c0-358a-4bd6-8f67-c1b0e408967e	2025-07-20 12:39:46.341	2025-07-20 12:39:46.341	AB+	Nyakabanda	Kigali	emmanuel.nshimiyimana51@gmail.com	Contact Nshimiyimana	+250722346666	Wife	2025-09-16	Emmanuel	f	Nshimiyimana	Single	+250784820058	Nyamirambo	Umudugudu B	\N
70ea9d91-daa0-4500-b04f-cf4d126e88fb	2025-07-20 12:39:47.209	2025-07-20 12:39:47.209	A+	Kigabiro	Kigali	mukamana.uwimana52@gmail.com	Contact Uwimana	+250726503855	Husband	2025-11-02	Mukamana	f	Uwimana	Married	+250788482755	Nyamirambo	Umudugudu A	\N
70e0521a-b863-42aa-a5c2-a9959406273f	2025-07-20 12:39:48.111	2025-07-20 12:39:48.111	B+	Kigabiro	Kigali	jeanette.uwamahoro53@gmail.com	Contact Uwamahoro	+250722545368	Sister	2025-09-02	Jeanette	f	Uwamahoro	Single	+250787460347	Remera	Umudugudu C	\N
ffab02b4-0e93-4aba-865d-21d27593e71d	2025-07-20 12:39:48.944	2025-07-20 12:39:48.944	AB+	Kimironko	Rubavu	emmanuel.uwamahoro54@gmail.com	Contact Uwamahoro	+250728758360	Brother	2025-09-11	Emmanuel	f	Uwamahoro	Married	+250783030070	Kacyiru	Umudugudu A	\N
23e4cdde-242c-4afa-a2be-9e1ca64274bd	2025-07-20 12:39:49.885	2025-07-20 12:39:49.885	B+	Kigabiro	Rwamagana	alice.uwimana55@gmail.com	Contact Uwimana	+250725056160	Wife	2025-09-19	Alice	f	Uwimana	Single	+250784608043	Gisozi	Umudugudu B	\N
57a19566-c3d4-459f-a8a1-1d92a7e5e2ca	2025-07-20 12:39:50.71	2025-07-20 12:39:50.71	AB+	Kigabiro	Rwamagana	divine.habimana56@gmail.com	Contact Habimana	+250728910283	Sister	2025-09-25	Divine	f	Habimana	Married	+250788267146	Gisozi	Umudugudu A	\N
fe8f798e-b914-4cbf-945d-69d35599e804	2025-07-20 12:39:51.576	2025-07-20 12:39:51.576	B+	Kimironko	Kigali	alice.uwamahoro57@gmail.com	Contact Uwamahoro	+250723972642	Brother	2025-09-08	Alice	f	Uwamahoro	Widowed	+250783410849	Remera	Umudugudu B	\N
5b162ff3-a0fd-428a-9987-1917ab1ef323	2025-07-20 12:39:52.731	2025-07-20 12:39:52.731	O+	Rugando	Rubavu	chantal.niyonsaba58@gmail.com	Contact Niyonsaba	+250724209486	Wife	2025-08-21	Chantal	f	Niyonsaba	Single	+250789442030	Gisozi	Umudugudu A	\N
56116611-633c-4022-b8ff-46cb56c03c08	2025-07-20 12:39:53.793	2025-07-20 12:39:53.793	AB+	Nyakabanda	Huye	mugisha.umutoni59@gmail.com	Contact Umutoni	+250727310371	Wife	2025-09-19	Mugisha	f	Umutoni	Married	+250785740108	Nyamirambo	Umudugudu C	\N
46270dfc-3dcd-44b2-acd9-76c146a010e7	2025-07-20 12:39:54.634	2025-07-20 12:39:54.634	O+	Kigabiro	Huye	eric.uwimana60@gmail.com	Contact Uwimana	+250725873556	Husband	2025-09-26	Eric	f	Uwimana	Widowed	+250788099820	Remera	Umudugudu B	\N
26f91fa8-1b78-44ee-be0e-afdcf8572d20	2025-07-20 12:39:55.478	2025-07-20 12:39:55.478	A+	Kigabiro	Rubavu	aline.twagirimana61@gmail.com	Contact Twagirimana	+250725787754	Wife	2025-10-16	Aline	f	Twagirimana	Widowed	+250786192941	Kacyiru	Umudugudu A	\N
bbb3f916-8d9d-42fa-be86-179852366538	2025-07-20 12:39:56.332	2025-07-20 12:39:56.332	O+	Kigabiro	Rubavu	emmanuel.twagirimana62@gmail.com	Contact Twagirimana	+250725836850	Brother	2025-09-05	Emmanuel	f	Twagirimana	Single	+250789844041	Nyamirambo	Umudugudu A	\N
6cb643e3-5b94-400f-bb07-ea7de9a9c309	2025-07-20 12:39:57.252	2025-07-20 12:39:57.252	AB+	Nyakabanda	Rubavu	divine.mutoni63@gmail.com	Contact Mutoni	+250728061586	Sister	2025-09-05	Divine	f	Mutoni	Married	+250789884810	Kacyiru	Umudugudu A	\N
2da134f4-16fa-48de-bce1-5d8b13415c72	2025-07-20 12:39:58.175	2025-07-20 12:39:58.175	A+	Nyakabanda	Muhanga	alice.mutoni64@gmail.com	Contact Mutoni	+250725024911	Brother	2025-09-15	Alice	f	Mutoni	Widowed	+250781051106	Gasabo	Umudugudu B	\N
c98ee7d9-ac2f-4fde-a0bb-054cc978c511	2025-07-20 12:39:59.042	2025-07-20 12:39:59.042	A+	Kimironko	Rubavu	innocent.twagirimana65@gmail.com	Contact Twagirimana	+250724303438	Husband	2025-11-05	Innocent	f	Twagirimana	Single	+250789949686	Nyamirambo	Umudugudu B	\N
a6667950-8660-48c0-822d-c721b67be7ef	2025-07-20 12:39:59.879	2025-07-20 12:39:59.879	B+	Rugando	Muhanga	alain.habimana66@gmail.com	Contact Habimana	+250724557129	Wife	2025-10-06	Alain	f	Habimana	Married	+250782258388	Nyamirambo	Umudugudu A	\N
f2acef0a-002d-4879-b209-0e2c16fba009	2025-07-20 12:40:00.743	2025-07-20 12:40:00.743	AB+	Kigabiro	Huye	yves.mutoni67@gmail.com	Contact Mutoni	+250722856740	Wife	2025-10-12	Yves	f	Mutoni	Married	+250786095820	Kacyiru	Umudugudu C	\N
ba43d087-ad2e-42c6-8dac-0cdd0cd24d74	2025-07-20 12:40:01.635	2025-07-20 12:40:01.635	O+	Kimironko	Huye	alice.uwimana68@gmail.com	Contact Uwimana	+250722435181	Wife	2025-10-11	Alice	f	Uwimana	Widowed	+250781602439	Remera	Umudugudu B	\N
fd1bdcb6-9b05-4353-94f6-7ccffd4e6208	2025-07-20 12:40:02.486	2025-07-20 12:40:02.486	A+	Kimironko	Rwamagana	chantal.twagirimana69@gmail.com	Contact Twagirimana	+250728975260	Brother	2025-10-04	Chantal	f	Twagirimana	Widowed	+250789554694	Gasabo	Umudugudu A	\N
7347fcc7-6355-4051-82ea-63e47a8cd12d	2025-07-20 12:40:03.423	2025-07-20 12:40:03.423	A+	Kigabiro	Rubavu	chantal.niyonsaba70@gmail.com	Contact Niyonsaba	+250723158313	Sister	2025-09-19	Chantal	f	Niyonsaba	Married	+250788714939	Remera	Umudugudu A	\N
f7ed5d1a-4cab-4e45-acb9-14c1bc52515a	2025-07-20 12:40:04.309	2025-07-20 12:40:04.309	B+	Nyakabanda	Huye	esther.munyaneza71@gmail.com	Contact Munyaneza	+250722086205	Husband	2025-11-18	Esther	f	Munyaneza	Single	+250789639796	Kacyiru	Umudugudu A	\N
2a55782a-e20c-405a-af38-9b049aea9c4c	2025-07-20 12:40:05.378	2025-07-20 12:40:05.378	AB+	Kimironko	Musanze	mukamana.uwamahoro72@gmail.com	Contact Uwamahoro	+250725997965	Brother	2025-09-16	Mukamana	f	Uwamahoro	Widowed	+250785632245	Nyamirambo	Umudugudu C	\N
0ebecb59-f7e1-4efd-86b8-3187d4291494	2025-07-20 12:40:06.262	2025-07-20 12:40:06.262	AB+	Rugando	Rwamagana	esther.munyaneza73@gmail.com	Contact Munyaneza	+250721032062	Sister	2025-09-10	Esther	f	Munyaneza	Married	+250782903656	Remera	Umudugudu C	\N
58d366bb-661a-4570-b455-36abef5378b6	2025-07-20 12:40:07.134	2025-07-20 12:40:07.134	A+	Kigabiro	Huye	josiane.umutoni74@gmail.com	Contact Umutoni	+250727943299	Brother	2025-09-14	Josiane	f	Umutoni	Married	+250785442180	Gisozi	Umudugudu C	\N
4d6dcaab-9d09-4680-9b51-05aa73699a11	2025-07-20 12:40:08.033	2025-07-20 12:40:08.033	A+	Nyakabanda	Kigali	jeanette.niyonsaba75@gmail.com	Contact Niyonsaba	+250727421751	Sister	2025-11-04	Jeanette	f	Niyonsaba	Widowed	+250783769005	Remera	Umudugudu A	\N
d7eabc4d-bf66-46b6-be6f-7a607d4a4767	2025-07-20 12:40:08.972	2025-07-20 12:40:08.972	A+	Kimironko	Huye	alain.uwamahoro76@gmail.com	Contact Uwamahoro	+250727692934	Husband	2025-09-07	Alain	f	Uwamahoro	Married	+250784838839	Gisozi	Umudugudu A	\N
555a2573-ffd2-4a2e-bb09-3a0992efc833	2025-07-20 12:40:09.826	2025-07-20 12:40:09.826	A+	Kimironko	Musanze	sandrine.habimana77@gmail.com	Contact Habimana	+250723853166	Husband	2025-10-02	Sandrine	f	Habimana	Widowed	+250786418478	Remera	Umudugudu A	\N
4a61306c-f89c-4513-b792-bd991687940e	2025-07-20 12:40:10.669	2025-07-20 12:40:10.669	A+	Rugando	Kigali	sandrine.uwimana78@gmail.com	Contact Uwimana	+250722871672	Wife	2025-09-18	Sandrine	f	Uwimana	Single	+250788544608	Nyamirambo	Umudugudu C	\N
602dc4a8-b318-4140-882c-bd7bbfbb4b33	2025-07-20 12:40:11.64	2025-07-20 12:40:11.64	AB+	Kigabiro	Muhanga	claudine.mutoni79@gmail.com	Contact Mutoni	+250728680055	Brother	2025-08-21	Claudine	f	Mutoni	Widowed	+250783074640	Gisozi	Umudugudu B	\N
a206bd20-afef-46e9-b7b2-278affed435e	2025-07-20 12:40:12.578	2025-07-20 12:40:12.578	AB+	Nyakabanda	Musanze	sandrine.twagirimana80@gmail.com	Contact Twagirimana	+250722280426	Wife	2025-11-16	Sandrine	f	Twagirimana	Widowed	+250782271873	Gasabo	Umudugudu B	\N
f7349dee-ad2b-411d-9347-f485e8d264f7	2025-07-20 12:40:13.428	2025-07-20 12:40:13.428	O+	Kigabiro	Kigali	jeanette.habimana81@gmail.com	Contact Habimana	+250721409474	Husband	2025-11-06	Jeanette	f	Habimana	Married	+250784428049	Kacyiru	Umudugudu A	\N
27c43afd-4ba4-4698-b7ec-06dd43eb0387	2025-07-20 12:40:14.328	2025-07-20 12:40:14.328	AB+	Nyakabanda	Kigali	mukamana.niyonsaba82@gmail.com	Contact Niyonsaba	+250721541551	Sister	2025-10-15	Mukamana	f	Niyonsaba	Widowed	+250787338658	Gasabo	Umudugudu B	\N
22e75b76-522a-49b7-ac75-5de1990b09a2	2025-07-20 12:40:15.174	2025-07-20 12:40:15.174	B+	Kimironko	Muhanga	yves.umutoni83@gmail.com	Contact Umutoni	+250724251330	Wife	2025-11-15	Yves	f	Umutoni	Widowed	+250784585043	Kacyiru	Umudugudu B	\N
be678903-1fa6-414d-9bc3-4794b3fca2ea	2025-07-20 12:40:16.096	2025-07-20 12:40:16.096	O+	Rugando	Muhanga	alain.uwimana84@gmail.com	Contact Uwimana	+250729332870	Brother	2025-11-22	Alain	f	Uwimana	Married	+250783709113	Gisozi	Umudugudu B	\N
3f81b6b7-92b0-4b9f-b8da-1d7ad2f15826	2025-07-20 12:40:16.931	2025-07-20 12:40:16.931	AB+	Kigabiro	Huye	esther.uwimana85@gmail.com	Contact Uwimana	+250721021009	Sister	2025-10-04	Esther	f	Uwimana	Married	+250786476233	Remera	Umudugudu A	\N
d51795d9-28dd-458b-addf-754faccd64a2	2025-07-20 12:40:17.82	2025-07-20 12:40:17.82	O+	Kigabiro	Rwamagana	claudine.munyaneza86@gmail.com	Contact Munyaneza	+250724610104	Brother	2025-08-21	Claudine	f	Munyaneza	Married	+250787034830	Gasabo	Umudugudu A	\N
f83f8d95-b3f1-4a71-b62b-a5fafc29ac40	2025-07-20 12:40:18.668	2025-07-20 12:40:18.668	AB+	Nyakabanda	Kigali	alain.uwamahoro87@gmail.com	Contact Uwamahoro	+250724039163	Wife	2025-09-20	Alain	f	Uwamahoro	Married	+250786481899	Kacyiru	Umudugudu C	\N
d13a115c-9a00-44bb-b539-1726bc53c3af	2025-07-20 12:40:19.515	2025-07-20 12:40:19.515	AB+	Rugando	Kigali	aline.nshimiyimana88@gmail.com	Contact Nshimiyimana	+250728347806	Sister	2025-11-13	Aline	f	Nshimiyimana	Married	+250789388679	Remera	Umudugudu A	\N
b438da5b-6225-4cd1-a2af-75be408a2e8b	2025-07-20 12:40:20.37	2025-07-20 12:40:20.37	AB+	Kigabiro	Rubavu	mukamana.umutoni89@gmail.com	Contact Umutoni	+250721715550	Brother	2025-11-12	Mukamana	f	Umutoni	Married	+250787285829	Gasabo	Umudugudu A	\N
097f92f2-1b7e-404c-9b98-9039759a741d	2025-07-20 12:40:21.2	2025-07-20 12:40:21.2	A+	Kigabiro	Musanze	divine.nshimiyimana90@gmail.com	Contact Nshimiyimana	+250723768761	Sister	2025-10-10	Divine	f	Nshimiyimana	Married	+250789551218	Nyamirambo	Umudugudu C	\N
927c88b6-1660-4a60-9daa-b2c680bc71cb	2025-07-20 12:40:22.089	2025-07-20 12:40:22.089	O+	Kigabiro	Rwamagana	patrick.nshimiyimana91@gmail.com	Contact Nshimiyimana	+250722793099	Husband	2025-11-06	Patrick	f	Nshimiyimana	Married	+250783824423	Gasabo	Umudugudu C	\N
faa7aa93-d8dc-402e-8e6b-5999c8fa5688	2025-07-20 12:40:22.928	2025-07-20 12:40:22.928	O+	Rugando	Huye	chantal.habimana92@gmail.com	Contact Habimana	+250727880020	Husband	2025-10-22	Chantal	f	Habimana	Married	+250785930456	Nyamirambo	Umudugudu A	\N
f4cc449a-98e3-4f19-839a-c9867a1ea795	2025-07-20 12:40:23.775	2025-07-20 12:40:23.775	AB+	Kimironko	Kigali	josiane.twagirimana93@gmail.com	Contact Twagirimana	+250724053962	Brother	2025-10-26	Josiane	f	Twagirimana	Married	+250789299942	Nyamirambo	Umudugudu B	\N
166a08c9-ff07-4daf-8151-c7ba791be852	2025-07-20 12:40:24.604	2025-07-20 12:40:24.604	B+	Kimironko	Huye	mukamana.umutoni94@gmail.com	Contact Umutoni	+250725904183	Husband	2025-10-12	Mukamana	f	Umutoni	Single	+250787049191	Kacyiru	Umudugudu B	\N
2a93f8ed-3a08-4c81-85f9-928678d1c396	2025-07-20 12:40:25.519	2025-07-20 12:40:25.519	AB+	Nyakabanda	Huye	yves.umutoni95@gmail.com	Contact Umutoni	+250722521975	Brother	2025-10-06	Yves	f	Umutoni	Married	+250782283760	Nyamirambo	Umudugudu B	\N
1bf83e1f-85d6-4659-b46b-bcb9fe3e4bef	2025-07-20 12:40:26.345	2025-07-20 12:40:26.345	O+	Rugando	Huye	alain.uwimana96@gmail.com	Contact Uwimana	+250725921684	Husband	2025-09-07	Alain	f	Uwimana	Single	+250784827667	Gisozi	Umudugudu B	\N
8bfd5de1-313e-48a5-9138-a2cb80b2488c	2025-07-20 12:40:27.205	2025-07-20 12:40:27.205	O+	Kimironko	Rubavu	clarisse.habimana97@gmail.com	Contact Habimana	+250726997132	Husband	2025-08-31	Clarisse	f	Habimana	Single	+250783852002	Remera	Umudugudu C	\N
18163007-0e9b-491b-932d-6ce209995e56	2025-07-20 12:40:28.534	2025-07-20 12:40:28.534	A+	Kigabiro	Rubavu	samuel.niyonsaba98@gmail.com	Contact Niyonsaba	+250729528578	Wife	2025-09-22	Samuel	f	Niyonsaba	Married	+250781686137	Kacyiru	Umudugudu A	\N
cdb43c40-1e28-46f7-8028-9bf127732524	2025-07-20 12:40:29.426	2025-07-20 12:40:29.426	AB+	Kigabiro	Rwamagana	yves.irakoze99@gmail.com	Contact Irakoze	+250723030931	Sister	2025-09-11	Yves	f	Irakoze	Single	+250781825731	Kacyiru	Umudugudu C	\N
8770f0e4-20b0-45cf-b32d-0fc6a5e97701	2025-08-13 11:18:49.405	2025-08-13 11:18:49.405	A-	Nonko	Kicukiro	marydoe@example.com	John Doe	+250898989899	Family Member	2025-09-10	Mary	f	Doe	Single	+25078787878	Nyarugunga	Kavumu	\N
a01cf6c9-980a-47a5-a782-a386a80dc0f4	2025-08-17 13:02:14.308	2025-08-17 13:02:14.308	A+	Nonko	Kicukiro	jdoe@gmail.com	John Doe	+250898989899	Family Member	2025-05-10	Jane	f	Doe Mary	Single	+25078787878	Bugesera	Kavumu	\N
\.


--
-- Data for Name: infant; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.infant (id, created_at, updated_at, birth_height_cm, birth_weight_grams, blood_group, date_of_birth, first_name, gender, last_name, special_conditions, mother_id) FROM stdin;
e4a157db-2d71-4ffe-8903-533218adfb6e	2025-07-06 09:29:52.617	2025-07-06 09:29:52.617	50.5	3.2	A+	2024-12-01	Baby	FEMALE	Doe	None	57d5b4df-2ed8-4769-8bf8-919b47a6b3a6
a524cae6-fec5-4e1f-b11a-1609348f603f	2025-07-19 10:11:28.634	2025-07-19 10:11:28.634	50	3	A	2025-07-19	Cyuzuzo	MALE	Danny	None	0579f0b0-bb30-49cf-bb02-a0fb32a4f8e7
50800e64-da60-46c7-9172-042f1457c1f3	2025-08-17 13:10:14.937	2025-08-17 13:10:14.937	50	3	A+	2025-08-17	Cyusa	MALE	Ivan		fd340dba-8684-4b0f-a5b6-3ed40d0d877d
\.


--
-- Data for Name: pregnancy_record; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pregnancy_record (id, created_at, updated_at, gravity, last_menstrual_period, medical_history, parity, pregnancy_complications, parent_id) FROM stdin;
7f23b834-49eb-4a97-9baf-14979a114c4b	2025-07-20 13:09:14.792	2025-07-20 13:09:14.792	G1	\N	\N	0	\N	57d5b4df-2ed8-4769-8bf8-919b47a6b3a6
\.


--
-- Data for Name: parent_pregancyrecord; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.parent_pregancyrecord (parent_id, pregancyrecord_id) FROM stdin;
\.


--
-- Data for Name: parent_pregnancy_record; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.parent_pregnancy_record (parent_id, pregnancy_record_id) FROM stdin;
\.


--
-- Data for Name: vaccination; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.vaccination (id, created_at, updated_at, administered_date, description, name, next_due_date, notes, notification_sent, health_worker_id, infant_id) FROM stdin;
0d697275-f55f-4552-b0a6-88b5694fa8c6	2025-07-06 10:57:31.714	2025-07-06 10:57:31.714	2025-07-01	Bacille Calmette-Guerin (TB vaccine)	BCG	2026-07-01	Given at birth	f	11306c62-41e7-47ee-8b20-b93040aef4e6	e4a157db-2d71-4ffe-8903-533218adfb6e
43a16eeb-c979-4564-aed1-640b9ad32762	2025-07-06 10:59:44.441	2025-07-06 10:59:44.441	2025-07-01	Oral Polio Vaccine (birth dose)	OPV-0	2025-08-01	First dose	f	11306c62-41e7-47ee-8b20-b93040aef4e6	e4a157db-2d71-4ffe-8903-533218adfb6e
e9f8fe63-5918-46c9-93dd-d5e330adea2f	2025-07-06 11:00:03.033	2025-07-06 11:00:03.033	2025-08-15	Combined Diphtheria, Tetanus, Pertussis, Hepatitis B, Hib vaccine	DTP-HepB-Hib 1	2025-09-15	No complications	f	11306c62-41e7-47ee-8b20-b93040aef4e6	e4a157db-2d71-4ffe-8903-533218adfb6e
afbd1871-22de-4d61-b5e2-1b723fd6bfe9	2025-07-06 11:00:31.474	2025-07-06 11:00:31.474	2025-08-15	Pneumococcal Conjugate Vaccine - first dose	PCV1	2025-09-15	Taken together with DTP-HepB-Hib	f	11306c62-41e7-47ee-8b20-b93040aef4e6	e4a157db-2d71-4ffe-8903-533218adfb6e
b79add65-7854-4885-8621-7be8cfc3e9c3	2025-07-06 11:00:36.97	2025-07-06 11:00:36.97	2025-08-15	First dose of rotavirus vaccine	Rotavirus 1	2025-09-15	Oral dose	f	11306c62-41e7-47ee-8b20-b93040aef4e6	e4a157db-2d71-4ffe-8903-533218adfb6e
08808dd5-2ad5-4070-bea3-56597f19adf1	2025-07-06 11:00:48.703	2025-07-06 11:00:48.703	2025-09-15	Second dose of combined vaccine	DTP-HepB-Hib 2	2025-10-15	Mild fever after injection	f	11306c62-41e7-47ee-8b20-b93040aef4e6	e4a157db-2d71-4ffe-8903-533218adfb6e
672d6729-6c32-439d-9ebd-aad8629142b7	2025-07-06 11:01:01.09	2025-07-06 11:01:01.09	2025-09-15	Second dose of pneumococcal vaccine	PCV2	2025-10-15	Given with DTP	f	11306c62-41e7-47ee-8b20-b93040aef4e6	e4a157db-2d71-4ffe-8903-533218adfb6e
19c9774d-f0de-4696-92e0-bfdfb5846101	2025-07-06 11:01:13.853	2025-07-06 11:01:13.853	2025-09-15	Second dose of rotavirus	Rotavirus 2	2026-03-15	Allergic reaction monitored	f	11306c62-41e7-47ee-8b20-b93040aef4e6	e4a157db-2d71-4ffe-8903-533218adfb6e
e4e21fb6-cff0-449d-9209-f3b81ed18a80	2025-07-06 11:01:34.906	2025-07-06 11:01:34.906	2026-04-01	Measles and Rubella Vaccine - first dose	MR 1	2027-04-01	First MR dose	f	11306c62-41e7-47ee-8b20-b93040aef4e6	e4a157db-2d71-4ffe-8903-533218adfb6e
6d0fdc28-16de-40d1-b460-8d22dd1c1e6e	2025-07-06 11:01:46.118	2025-07-06 11:01:46.118	2026-04-01	Vitamin A supplement for vision and immunity	Vitamin A	2026-10-01	Given at 9 months	f	11306c62-41e7-47ee-8b20-b93040aef4e6	e4a157db-2d71-4ffe-8903-533218adfb6e
\.


--
-- Data for Name: visit; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.visit (id, created_at, updated_at, actual_end_time, actual_start_time, location, mode_of_communication, scheduled_time, status, summary, visit_type, health_worker_id, parent_id, actual_time) FROM stdin;
c1ea6552-cdd8-47e0-9013-7617ce79f995	2025-07-06 13:04:51.909	2025-07-06 13:04:52.264	\N	2024-11-12 16:15:00	Dental Clinic B, Room 2	SMS	2024-11-12 16:00:00	Scheduled		Dental Cleaning	11306c62-41e7-47ee-8b20-b93040aef4e6	57d5b4df-2ed8-4769-8bf8-919b47a6b3a6	\N
ddbfc044-0e63-4b77-a1a1-371c91934242	2025-07-17 14:57:42.3	2025-07-17 14:57:42.593	\N	2024-11-12 16:15:00	Dental Clinic B, Room 2	SMS	2024-11-12 16:00:00	Scheduled		Dental Cleaning	11306c62-41e7-47ee-8b20-b93040aef4e6	57d5b4df-2ed8-4769-8bf8-919b47a6b3a6	\N
61c52a7d-ff92-4059-8513-97ac614e40ef	2025-07-17 20:02:00.073	2025-07-17 20:02:00.073	2025-07-17 20:01:57.532	2025-07-17 20:01:57.532	kigali	SMS	2025-07-26 12:00:00	Scheduled	care	care	11306c62-41e7-47ee-8b20-b93040aef4e6	57d5b4df-2ed8-4769-8bf8-919b47a6b3a6	\N
f102d462-3c12-4c42-b0ad-fa2cdc05d02b	2025-07-24 12:50:37.848	2025-07-24 12:50:38.131	2025-07-24 12:50:34.852	2025-07-24 12:50:34.852	Kigali	SMS	2025-07-26 12:00:00	Scheduled	Hygiene care	Hygience	11306c62-41e7-47ee-8b20-b93040aef4e6	b29b3e2d-b878-45c5-9190-7c08b201316d	\N
233c25d2-c150-4186-afeb-d356b283b318	2025-08-13 03:32:16.689	2025-08-13 03:32:16.821	2025-08-13 03:32:12.993	2025-08-13 03:32:12.993	Patient Home	SMS	2025-08-13 08:33:00	Scheduled	Mubyeyi warakize? 	Follow Up	8a7bad75-85b8-4e41-997a-e3ada2bdeacb	834f8faf-cf66-4781-86a1-ae54117eb825	\N
5edf8508-9b9b-4f09-93ed-3fb6c9b4c731	2025-08-17 12:18:38.41	2025-08-17 12:18:38.802	2025-08-17 12:18:33.012	2025-08-17 12:18:33.012	Kavumu	faceToFace	2025-08-19 12:16:00	Scheduled	Mubyeyi warakize? Umeze ute?	Follow Up	8a7bad75-85b8-4e41-997a-e3ada2bdeacb	3fd4031b-2717-46b7-a673-400ff8b76155	\N
\.


--
-- Data for Name: visit_note; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.visit_note (id, created_at, updated_at, observation, recommendations, vital_signs, visit_id, attachments) FROM stdin;
aace2a8e-e3ac-42c5-a3d4-93a00ae39411	2025-07-06 13:04:52.26	2025-07-06 13:04:52.265	Teeth cleaning performed. No cavities found.	Maintain regular dental hygiene.	N/A	c1ea6552-cdd8-47e0-9013-7617ce79f995	\N
412172f4-6ed2-4ce0-859e-198de0ee718d	2025-07-06 13:37:00.727	2025-07-06 13:37:00.737	Patient had elevated blood pressure during the visit.	Monitor blood pressure daily and return for follow-up in one week.	Blood Pressure: 150/95 mmHg, Heart Rate: 92 bpm, Temperature: 37.5°C	c1ea6552-cdd8-47e0-9013-7617ce79f995	\N
18c0ce8d-887f-4bc9-b20e-ecb8da403f8b	2025-07-17 14:57:42.586	2025-07-17 14:57:42.594	Teeth cleaning performed. No cavities found.	Maintain regular dental hygiene.	N/A	ddbfc044-0e63-4b77-a1a1-371c91934242	\N
f6006766-0a0f-477d-8136-ec460b855a8d	2025-07-24 12:50:38.125	2025-07-24 12:50:38.125				f102d462-3c12-4c42-b0ad-fa2cdc05d02b	\N
753ca722-de71-422c-b6fa-c9fe73443b18	2025-08-13 03:32:16.816	2025-08-13 03:32:16.816				233c25d2-c150-4186-afeb-d356b283b318	\N
a9b04e87-0bf1-470f-a55b-3149fc6350b1	2025-08-17 12:18:38.774	2025-08-17 12:18:38.774				5edf8508-9b9b-4f09-93ed-3fb6c9b4c731	\N
\.


--
-- Data for Name: visit_note_attachments; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.visit_note_attachments (visit_note_id, attachments) FROM stdin;
412172f4-6ed2-4ce0-859e-198de0ee718d	blood_pressure_chart.jpg
412172f4-6ed2-4ce0-859e-198de0ee718d	prescription.pdf
\.


--
-- Data for Name: schema_migrations; Type: TABLE DATA; Schema: realtime; Owner: supabase_admin
--

COPY realtime.schema_migrations (version, inserted_at) FROM stdin;
20211116024918	2024-11-03 05:00:54
20211116045059	2024-11-03 05:00:54
20211116050929	2024-11-03 05:00:54
20211116051442	2024-11-03 05:00:54
20211116212300	2024-11-03 05:00:54
20211116213355	2024-11-03 05:00:55
20211116213934	2024-11-03 05:00:55
20211116214523	2024-11-03 05:00:55
20211122062447	2024-11-03 05:00:55
20211124070109	2024-11-03 05:00:55
20211202204204	2024-11-03 05:00:55
20211202204605	2024-11-03 05:00:56
20211210212804	2024-11-03 05:00:56
20211228014915	2024-11-03 05:00:56
20220107221237	2024-11-03 05:00:56
20220228202821	2024-11-03 05:00:57
20220312004840	2024-11-03 05:00:57
20220603231003	2024-11-03 05:00:57
20220603232444	2024-11-03 05:00:57
20220615214548	2024-11-03 05:00:57
20220712093339	2024-11-03 05:00:57
20220908172859	2024-11-03 05:00:58
20220916233421	2024-11-03 05:00:58
20230119133233	2024-11-03 05:00:58
20230128025114	2024-11-03 05:00:58
20230128025212	2024-11-03 05:00:58
20230227211149	2024-11-03 05:00:58
20230228184745	2024-11-03 05:00:59
20230308225145	2024-11-03 05:00:59
20230328144023	2024-11-03 05:00:59
20231018144023	2024-11-03 05:00:59
20231204144023	2024-11-03 05:00:59
20231204144024	2024-11-03 05:01:00
20231204144025	2024-11-03 05:01:00
20240108234812	2024-11-03 05:01:00
20240109165339	2024-11-03 05:01:00
20240227174441	2024-11-03 05:01:00
20240311171622	2024-11-03 05:01:00
20240321100241	2024-11-03 05:01:01
20240401105812	2024-11-03 05:01:01
20240418121054	2024-11-03 05:01:01
20240523004032	2024-11-03 05:01:02
20240618124746	2024-11-03 05:01:02
20240801235015	2024-11-03 05:01:02
20240805133720	2024-11-03 05:01:03
20240827160934	2024-11-03 05:01:03
20240919163303	2024-12-19 08:08:53
20240919163305	2024-12-19 08:08:53
20241019105805	2024-12-19 08:08:54
20241030150047	2024-12-19 08:08:57
20241108114728	2024-12-19 08:08:58
20241121104152	2024-12-19 08:08:59
20241130184212	2024-12-19 08:09:00
20241220035512	2025-05-01 20:00:54
20241220123912	2025-05-01 20:00:54
20241224161212	2025-05-01 20:00:55
20250107150512	2025-05-01 20:00:56
20250110162412	2025-05-01 20:00:57
20250123174212	2025-05-01 20:00:58
20250128220012	2025-05-01 20:00:58
20250506224012	2025-06-22 10:25:51
20250523164012	2025-06-22 10:25:53
20250714121412	2025-08-07 18:18:33
\.


--
-- Data for Name: subscription; Type: TABLE DATA; Schema: realtime; Owner: supabase_admin
--

COPY realtime.subscription (id, subscription_id, entity, filters, claims, created_at) FROM stdin;
\.


--
-- Data for Name: buckets; Type: TABLE DATA; Schema: storage; Owner: supabase_storage_admin
--

COPY storage.buckets (id, name, owner, created_at, updated_at, public, avif_autodetection, file_size_limit, allowed_mime_types, owner_id) FROM stdin;
\.


--
-- Data for Name: migrations; Type: TABLE DATA; Schema: storage; Owner: supabase_storage_admin
--

COPY storage.migrations (id, name, hash, executed_at) FROM stdin;
0	create-migrations-table	e18db593bcde2aca2a408c4d1100f6abba2195df	2024-11-03 04:59:32.393377
1	initialmigration	6ab16121fbaa08bbd11b712d05f358f9b555d777	2024-11-03 04:59:32.409868
2	storage-schema	5c7968fd083fcea04050c1b7f6253c9771b99011	2024-11-03 04:59:32.465008
3	pathtoken-column	2cb1b0004b817b29d5b0a971af16bafeede4b70d	2024-11-03 04:59:32.53877
4	add-migrations-rls	427c5b63fe1c5937495d9c635c263ee7a5905058	2024-11-03 04:59:32.616741
5	add-size-functions	79e081a1455b63666c1294a440f8ad4b1e6a7f84	2024-11-03 04:59:32.647741
6	change-column-name-in-get-size	f93f62afdf6613ee5e7e815b30d02dc990201044	2024-11-03 04:59:32.665099
7	add-rls-to-buckets	e7e7f86adbc51049f341dfe8d30256c1abca17aa	2024-11-03 04:59:32.722379
8	add-public-to-buckets	fd670db39ed65f9d08b01db09d6202503ca2bab3	2024-11-03 04:59:32.780182
9	fix-search-function	3a0af29f42e35a4d101c259ed955b67e1bee6825	2024-11-03 04:59:32.83565
10	search-files-search-function	68dc14822daad0ffac3746a502234f486182ef6e	2024-11-03 04:59:32.887791
11	add-trigger-to-auto-update-updated_at-column	7425bdb14366d1739fa8a18c83100636d74dcaa2	2024-11-03 04:59:32.94546
12	add-automatic-avif-detection-flag	8e92e1266eb29518b6a4c5313ab8f29dd0d08df9	2024-11-03 04:59:33.011572
13	add-bucket-custom-limits	cce962054138135cd9a8c4bcd531598684b25e7d	2024-11-03 04:59:33.065717
14	use-bytes-for-max-size	941c41b346f9802b411f06f30e972ad4744dad27	2024-11-03 04:59:33.121647
15	add-can-insert-object-function	934146bc38ead475f4ef4b555c524ee5d66799e5	2024-11-03 04:59:33.165406
16	add-version	76debf38d3fd07dcfc747ca49096457d95b1221b	2024-11-03 04:59:33.219994
17	drop-owner-foreign-key	f1cbb288f1b7a4c1eb8c38504b80ae2a0153d101	2024-11-03 04:59:33.234146
18	add_owner_id_column_deprecate_owner	e7a511b379110b08e2f214be852c35414749fe66	2024-11-03 04:59:33.293278
19	alter-default-value-objects-id	02e5e22a78626187e00d173dc45f58fa66a4f043	2024-11-03 04:59:33.347961
20	list-objects-with-delimiter	cd694ae708e51ba82bf012bba00caf4f3b6393b7	2024-11-03 04:59:33.400683
21	s3-multipart-uploads	8c804d4a566c40cd1e4cc5b3725a664a9303657f	2024-11-03 04:59:33.475868
22	s3-multipart-uploads-big-ints	9737dc258d2397953c9953d9b86920b8be0cdb73	2024-11-03 04:59:33.514629
23	optimize-search-function	9d7e604cddc4b56a5422dc68c9313f4a1b6f132c	2024-11-03 04:59:33.589257
24	operation-function	8312e37c2bf9e76bbe841aa5fda889206d2bf8aa	2024-11-03 04:59:33.647819
25	custom-metadata	d974c6057c3db1c1f847afa0e291e6165693b990	2024-11-03 04:59:33.67366
\.


--
-- Data for Name: objects; Type: TABLE DATA; Schema: storage; Owner: supabase_storage_admin
--

COPY storage.objects (id, bucket_id, name, owner, created_at, updated_at, last_accessed_at, metadata, version, owner_id, user_metadata) FROM stdin;
\.


--
-- Data for Name: s3_multipart_uploads; Type: TABLE DATA; Schema: storage; Owner: supabase_storage_admin
--

COPY storage.s3_multipart_uploads (id, in_progress_size, upload_signature, bucket_id, key, version, owner_id, created_at, user_metadata) FROM stdin;
\.


--
-- Data for Name: s3_multipart_uploads_parts; Type: TABLE DATA; Schema: storage; Owner: supabase_storage_admin
--

COPY storage.s3_multipart_uploads_parts (id, upload_id, size, part_number, bucket_id, key, etag, owner_id, version, created_at) FROM stdin;
\.


--
-- Data for Name: secrets; Type: TABLE DATA; Schema: vault; Owner: supabase_admin
--

COPY vault.secrets (id, name, description, secret, key_id, nonce, created_at, updated_at) FROM stdin;
\.


--
-- Name: refresh_tokens_id_seq; Type: SEQUENCE SET; Schema: auth; Owner: supabase_auth_admin
--

SELECT pg_catalog.setval('auth.refresh_tokens_id_seq', 1, false);


--
-- Name: key_key_id_seq; Type: SEQUENCE SET; Schema: pgsodium; Owner: supabase_admin
--

SELECT pg_catalog.setval('pgsodium.key_key_id_seq', 1, false);


--
-- Name: subscription_id_seq; Type: SEQUENCE SET; Schema: realtime; Owner: supabase_admin
--

SELECT pg_catalog.setval('realtime.subscription_id_seq', 1, false);


--
-- PostgreSQL database dump complete
--

\unrestrict HvcaaGJfZFOVKDVlmzbUoeH9XmfoswyEU305xnEFTyPfV9FPyLrXfh3SW8tO2zH

