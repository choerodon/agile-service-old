mkdir -p target

if [ ! -f target/hap-liquibase-tools.jar ]
then
    curl http://nexus.saas.hand-china.com/content/repositories/rdc/com/hand/hap/cloud/hap-liquibase-tools/1.0/hap-liquibase-tools-1.0.jar -o target/hap-liquibase-tools.jar
fi

# 初始化项目数据库
java -Dspring.datasource.url="jdbc:mysql://localhost:3306/agile_service?useUnicode=true&characterEncoding=utf-8&useSSL=false" \
    -Dspring.datasource.username=choerodon \
    -Dspring.datasource.password=123456 \
    -Ddata.init=true -Ddata.drop=false \
    -Ddata.dir=src/main/resources \
    -jar target/hap-liquibase-tools.jar