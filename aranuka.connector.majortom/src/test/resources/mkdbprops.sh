#!/bin/sh

# shell script to create db.properties for hudson

# database connection properties for the unit tests

#  The host where the database is running or the path to the database for derby or h2.
echo db_host=$1 > db.properties

#name of the database
db_name=$2 >> db.properties

#The port where the database is reachable
echo db_port=$3 >> db.properties

# login for the database
echo db_login=$4 >> db.properties

#password of the login
echo db_password=$5 >> db.properties

#database system: h2, mysql, postgresql, derby
echo databasesystem=$6 >> db.properties