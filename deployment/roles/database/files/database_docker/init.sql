CREATE DATABASE model_service ENCODING utf8 LC_COLLATE 'fi_FI.utf8';
CREATE USER model_service PASSWORD 'model_service';
GRANT ALL PRIVILEGES ON DATABASE model_service TO model_service;

\c model_service
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;
