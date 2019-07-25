package script.db

databaseChangeLog(logicalFilePath: 'fd_config_code.groovy') {

    changeSet(author: 'shinan.chenX@gmail.com', id: '2018-10-10-config-code') {
        createTable(tableName: 'fd_config_code') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: true)
            }
            column(name: 'code', type: 'VARCHAR(255)', remarks: '编码') {
                constraints(nullable: false)
            }
            column(name: 'type', type: 'VARCHAR(30)', remarks: '类型') {
                constraints(nullable: false)
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')
            column(name: 'service', type: 'VARCHAR(64)', remarks: '服务名') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

}