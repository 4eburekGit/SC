--
-- PostgreSQL database dump
--

\restrict JdHDbbgQWln2OkBloIucdY150KOY18RUlaLF7Oct2A3Bw2oGxEsS3mc7BHnl1ea

-- Dumped from database version 16.11 (Ubuntu 16.11-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.11 (Ubuntu 16.11-0ubuntu0.24.04.1)
BEGIN TRANSACTION;
-- Started on 2026-03-25 15:43:32 MSK

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 215 (class 1259 OID 16539)
-- Name: chicagodata; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chicagodata (
    id integer,
    title character varying(256),
    date_display character varying(256),
    description character varying(4096),
    dimensions character varying(512),
    medium_display character varying(512),
    credit_line character varying(256),
    image_id character varying(36),
    artist_title character varying(512),
    download_date timestamp without time zone,
    last_access_date timestamp without time zone
);


ALTER TABLE public.chicagodata OWNER TO kottsov;

--
-- TOC entry 3427 (class 0 OID 16539)
-- Dependencies: 215
-- Data for Name: chicagodata; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.chicagodata (id, title, date_display, description, dimensions, medium_display, credit_line, image_id, artist_title, download_date, last_access_date) FROM stdin;
\.


-- Completed on 2026-03-25 15:43:32 MSK
COMMIT;
--
-- PostgreSQL database dump complete
--

\unrestrict JdHDbbgQWln2OkBloIucdY150KOY18RUlaLF7Oct2A3Bw2oGxEsS3mc7BHnl1ea

