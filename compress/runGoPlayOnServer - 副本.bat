chcp 65001
ssh root@IPPPPPPPPP "cd /home/upload/golplay-release/jar && lsof -t -i:8081 | xargs -r kill -9 && nohup java -jar goplay-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod &"