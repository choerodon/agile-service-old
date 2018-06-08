# Agile Service
`Agile Service` is the core service of Choerodon.  

The service is responsible for Agile process management and providing users with a better user experience through rich display.

## Features
- **Version Management**
- **Sprint Management**
- **Issues Management** 
- **Backlog Management**
- **Component Management**

## Requirements
- Java8
- [File Service](https://github.com/choerodon/file-service.git)
- [Iam Service](https://github.com/choerodon/iam-service.git)
- [MySQL](https://www.mysql.com)
- [Kafka](https://kafka.apache.org)

## Installation and Getting Started
1. init database

    ```sql
    CREATE USER 'choerodon'@'%' IDENTIFIED BY "choerodon";
    CREATE DATABASE agile_service DEFAULT CHARACTER SET utf8;
    GRANT ALL PRIVILEGES ON agile_service.* TO choerodon@'%';
    FLUSH PRIVILEGES;
    ```
1. run command `sh init-local-database.sh`
1. run command as follow or run `AgileServiceApplication` in IntelliJ IDEA

    ```bash
    mvn clean spring-boot:run
    ```

## Dependencies
- `go-register-server`: Register server
- `iam-service`：iam service
- `kafka`
- `mysql`: agile_service database
- `api-gateway`: api gateway server
- `gateway-helper`: gateway helper server
- `oauth-server`: oauth server
- `manager-service`: manager service

## Reporting Issues
If you find any shortcomings or bugs, please describe them in the  [issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).

## How to Contribute
Pull requests are welcome! [Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) to know for more information on how to contribute.
