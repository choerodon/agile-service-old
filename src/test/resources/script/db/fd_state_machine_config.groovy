package script.db

databaseChangeLog(logicalFilePath: 'fd_state_machine_config.groovy') {
    changeSet(author: 'shinan.chenX@gmail.com', id: '2018-09-12-state-machine-config') {
        createTable(tableName: 'fd_state_machine_config') {
            column(name: 'id', type: 'BIGINT UNSIGNED', remarks: 'ID,主键') {
                constraints(primaryKey: true)
            }
            column(name: 'transform_id', type: 'BIGINT UNSIGNED', remarks: '转换id') {
                constraints(nullable: false)
            }
            column(name: 'state_machine_id', type: 'BIGINT UNSIGNED', remarks: '状态机id') {
                constraints(nullable: false)
            }
            column(name: 'code', type: 'VARCHAR(255)', remarks: '编码') {
                constraints(nullable: false)
            }
            column(name: 'type', type: 'VARCHAR(30)', remarks: '类型') {
                constraints(nullable: false)
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: false)
            }
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "fd_state_machine_config", indexName: "state_machine_config_n1") {
            column(name: "transform_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "fd_state_machine_config", indexName: "state_machine_config_n2") {
            column(name: "type", type: "VARCHAR(30)")
        }
    }

}