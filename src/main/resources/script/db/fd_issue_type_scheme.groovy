package script.db


databaseChangeLog(logicalFilePath: 'fd_issue_type_scheme.groovy') {
    changeSet(id: '2018-08-10-create-issue-type-scheme', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: 'fd_issue_type_scheme') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称') {
                constraints(nullable: 'false')
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')
            column(name: 'apply_type', type: 'VARCHAR(30)', remarks: '方案应用类型')
            column(name: 'default_issue_type_id', type: 'BIGINT UNSIGNED', remarks: '默认问题类型id') {
                constraints(nullable: 'false')
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: 'false')
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "fd_issue_type_scheme", indexName: "issue_type_scheme_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "fd_issue_type_scheme", indexName: "issue_type_scheme_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }
        createIndex(tableName: "fd_issue_type_scheme", indexName: "issue_type_scheme_n3") {
            column(name: "default_issue_type_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "fd_issue_type_scheme", indexName: "issue_type_scheme_n4") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }
}