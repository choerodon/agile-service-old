package script.db

databaseChangeLog(logicalFilePath: 'fd_state_machine_config_draft.groovy') {
    changeSet(id: '2018-12-13-delete-table-state-machine-config-draft', author: 'shinan.chenX@gmail') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "DROP TABLE IF EXISTS fd_state_machine_config_draft"
        }
    }
    changeSet(author: 'shinan.chenX@gmail.com', id: '2018-12-13-state-machine-config-draft') {
        createTable(tableName: 'fd_state_machine_config_draft') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: 'true')
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
        createIndex(tableName: "fd_state_machine_config_draft", indexName: "state_machine_config_draft_n1") {
            column(name: "transform_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "fd_state_machine_config_draft", indexName: "state_machine_config_draft_n2") {
            column(name: "type", type: "VARCHAR(30)")
        }
    }

}