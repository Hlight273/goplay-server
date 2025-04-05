chcp 65001
@echo off
cd ../
call mvn clean package -Pprod
if errorlevel 0 (
    echo Maven构建完成!
)

echo 准备传输jar包!
wsl rsync -av target/goplay-server-0.0.1-SNAPSHOT.jar target/jaudiotagger-2.2.6-SNAPSHOT.jar root@IPPPPPPPP:/home/upload/golplay-release/jar
echo jar包传输完成!
pause