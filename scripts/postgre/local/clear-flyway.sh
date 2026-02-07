#!/bin/bash

export PGHOST=localhost
export PGPORT=5432
export PGUSER=buratino_user
export PGPASSWORD=buratino_password
export PGDATABASE=buratino_db

psql <<-EOF
  DROP TABLE flyway_schema_history;
EOF
