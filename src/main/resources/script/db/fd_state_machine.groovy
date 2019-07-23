package script.db

databaseChangeLog(logicalFilePath: 'fd_state_machine.groovy') {
    changeSet(author: 'shinan.chenX@gmail.com', id: '2018-07-30-state-machine') {
        createTable(tableName: 'fd_state_machine') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID,主键') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')
            column(name: 'status', type: 'VARCHAR(30)', remarks: '状态机的状态') {
                constraints(nullable: false)
            }
            column(name: 'is_default', type: 'TINYINT UNSIGNED', remarks: '是否组织默认状态机', defaultValue: '0'){
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
        createIndex(tableName: "fd_state_machine", indexName: "state_machine_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "fd_state_machine", indexName: "state_machine_n2") {
            column(name: "status", type: "VARCHAR(30)")
        }
        createIndex(tableName: "fd_state_machine", indexName: "state_machine_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }
}