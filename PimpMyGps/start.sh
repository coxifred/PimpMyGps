./gradlew fatjar
nohup java -Xmx256M -jar build/libs/PimpMyGps.jar ./aCore.xml > /tmp/pimpMyGps.log 2>&1 &
sleep 2
tail -100f /tmp/pimpMyGps.log
