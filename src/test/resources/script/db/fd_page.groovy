package script.db


databaseChangeLog(logicalFilePath: 'fd_page.groovy') {
    changeSet(id: '2019-03-29-create-table-page', author: 'shinan.chenX@gmail.com') {
        createTable(tableName: 'fd_page') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(255)', remarks: '名称') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')
            column(name: 'scheme_code', type: 'VARCHAR(30)', remarks: '方案编码') {
                constraints(nullable: false)
            }
            column(name: 'page_code', type: 'VARCHAR(30)', remarks: '页面编码') {
                constraints(nullable: false)
            }
            column(name: 'is_system', type: 'TINYINT UNSIGNED(1)', remarks: '是否系统页面', defaultValue: "0") {
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
        createIndex(tableName: "fd_page", indexName: "pk_page_id") {
            column(name: 'id', type: 'BIGINT UNSIGNED')
        }
        createIndex(tableName: "fd_page", indexName: "idx_page_scheme_code") {
            column(name: "scheme_code", type: "VARCHAR(30)")
        }
        createIndex(tableName: "fd_page", indexName: "idx_page_page_code") {
            column(name: "page_code", type: "VARCHAR(30)")
        }
        createIndex(tableName: "fd_page", indexName: "idx_page_organization_id") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }
}