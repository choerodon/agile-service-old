package script.db


databaseChangeLog(logicalFilePath: 'fd_issue_type.groovy') {
    changeSet(id: '2018-08-08-create-issue-type', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: 'fd_issue_type') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称') {
                constraints(nullable: 'false')
            }
            column(name: 'type_code', type: 'VARCHAR(50)', remarks: '类型', defaultValue: "custom") {
                constraints(nullable: 'false')
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')

            column(name: 'icon', type: 'VARCHAR(64)', remarks: '图标')

            column(name: 'colour', type: 'VARCHAR(20)', remarks: '颜色') {
                constraints(nullable: 'false')
            }
            column(name: 'is_initialize', type: 'TINYINT UNSIGNED', remarks: '是否默认初始化的类型', defaultValue: "0") {
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
        createIndex(tableName: "fd_issue_type", indexName: "issue_type_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "fd_issue_type", indexName: "issue_type_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }
        createIndex(tableName: "fd_issue_type", indexName: "issue_type_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: 'fd_issue_type', indexName: 'uk_type_code', unique: true) {
            column(name: 'type_code')
            column(name: 'organization_id')
        }
    }
}