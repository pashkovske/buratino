scp ../.env pashkovske078@158.160.203.148:/home/pashkovske078/buratino-trader-api/.env;
ssh pashkovske078@158.160.203.148 << 'ENDSSH'
  docker pull cr.yandex/crp9v4cjakkbt4e1s2t0/buratino-trader-api:latest;
  cd /home/pashkovske078/buratino-trader-api;
  docker stop buratino-trader-api;
  docker rm buratino-trader-api;
  docker run -d \
      --name buratino-trader-api \
      -p 8080:8080 \
      --env-file .env \
      -e JAVA_OPTS="-Dspring.profiles.active=production" \
      cr.yandex/crp9v4cjakkbt4e1s2t0/buratino-trader-api:latest;
  docker start buratino-trader-api
ENDSSH
