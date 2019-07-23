package script.db


databaseChangeLog(logicalFilePath: 'fd_state_machine_scheme_config_draft.groovy') {
    changeSet(id: '2018-11-19-add-table-state-machine-scheme-config-draft', author: 'shinan.chenX@gmail') {
        createTable(tableName: "fd_state_machine_scheme_config_draft") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'scheme_id', type: 'BIGINT UNSIGNED', remarks: '方案ID')
            column(name: 'issue_type_id', type: 'BIGINT UNSIGNED', remarks: '问题类型ID')
            column(name: 'state_machine_id', type: 'BIGINT UNSIGNED', remarks: '状态机Id')
            column(name: 'is_default', type: 'TINYINT UNSIGNED', remarks: '是否组织默认状态机', defaultValue: '0'){
                constraints(nullable: false)
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id')
            column(name: 'sequence', type: 'INTEGER UNSIGNED', remarks: '排序') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createIndex(tableName: "fd_state_machine_scheme_config_draft", indexName: "state_machine_scheme_config_draft_n1") {
            column(name: "scheme_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "fd_state_machine_scheme_config_draft", indexName: "state_machine_scheme_config_draft_n2") {
            column(name: "issue_type_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "fd_state_machine_scheme_config_draft", indexName: "state_machine_scheme_config_draft_n3") {
            column(name: "state_machine_id", type: "BIGINT UNSIGNED")
        }
    }
    changeSet(id: '2018-11-23-fix-table-state-machine-scheme-config-draft', author: 'shinan.chenX@gmail') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "insert into fd_state_machine_scheme_config_draft select * from fd_state_machine_scheme_config;"
        }
    }
}