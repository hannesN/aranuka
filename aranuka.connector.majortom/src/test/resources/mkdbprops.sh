#!/bin/sh

echo "# shell script to create db.properties for hudson" > db.properties

echo "# database connection properties for the unit tests" >> db.properties

echo "#  The host where the database is running or the path to the database for derby or h2." >> db.properties
echo db_host=$1 >> db.properties

echo "#name of the database"  >> db.properties
echo db_name=$2 >> db.properties

echo "#The port where the database is reachable"  >> db.properties
echo db_port=$3 >> db.properties

echo "# login for the database"  >> db.properties
echo db_login=$4 >> db.properties

echo "#password of the login"  >> db.properties
echo db_password=$5 >> db.properties

echo "#database system: h2, mysql, postgresql, derby"  >> db.properties
echo databasesystem=$6 >> db.properties