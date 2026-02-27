@echo off
cd /d D:\Projects\kuaishop-project-master\kuaishop-project-master\kuaishop-back\springbootdemo
set DB_USERNAME=root
set DB_PASSWORD=root123456
cmd /k mvn -pl app spring-boot:run