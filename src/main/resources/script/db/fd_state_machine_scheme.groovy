package script.db


databaseChangeLog(logicalFilePath: 'fd_state_machine_scheme.groovy') {
    changeSet(id: '2018-08-07-add-table-state-machine-scheme', author: 'shinan.chenX@gmail') {
        createTable(tableName: "fd_state_machine_scheme") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称')
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "fd_state_machine_scheme", indexName: "state_machine_scheme_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "fd_state_machine_scheme", indexName: "state_machine_scheme_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }
        createIndex(tableName: "fd_state_machine_scheme", indexName: "state_machine_scheme_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }
    changeSet(id: '2018-11-19-add-column-state-machine-scheme', author: 'shinan.chenX@gmail') {
        addColumn(tableName: 'fd_state_machine_scheme') {
            column(name: 'status', type: 'VARCHAR(30)', remarks: '状态机方案的状态', defaultValue: "create") {
                constraints(nullable: false)
            }
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "update fd_state_machine_scheme set status = 'active';"
        }
    }
    changeSet(id: '2018-11-28-add-column-state-machine-scheme', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'fd_state_machine_scheme') {
            column(name: 'deploy_progress', type: 'int', remarks: '发布状态机方案更新agile服务进度', defaultValue: "0")
        }
    }
    changeSet(id: '2018-12-12-add-column-state-machine-scheme', author: 'shinan.chenX@gmail.com') {
        addColumn(tableName: 'fd_state_machine_scheme') {
            column(name: 'deploy_status', type: 'VARCHAR(30)', remarks: '状态机方案的发布状态', defaultValue: "done")
        }
    }
}