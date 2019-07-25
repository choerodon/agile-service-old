package script.db


databaseChangeLog(logicalFilePath: 'fd_page_field.groovy') {
    changeSet(id: '2019-03-29-create-table-page-field', author: 'shinan.chenX@gmail.com') {
        createTable(tableName: 'fd_page_field') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键') {
                constraints(primaryKey: true)
            }
            column(name: 'page_id', type: 'BIGINT UNSIGNED', remarks: '页面id') {
                constraints(nullable: false)
            }
            column(name: 'field_id', type: 'BIGINT UNSIGNED', remarks: '字段id') {
                constraints(nullable: false)
            }
            column(name: 'is_display', type: 'TINYINT UNSIGNED(1)', remarks: '是否显示该字段', defaultValue: "0"){
                constraints(nullable: false)
            }
            column(name: 'rank', type: 'VARCHAR(255)', remarks: 'rank值')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id')
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "fd_page_field", indexName: "pk_page_field_id") {
            column(name: 'id', type: 'BIGINT UNSIGNED')
        }
        createIndex(tableName: "fd_page_field", indexName: "idx_page_field_project_id") {
            column(name: "project_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "fd_page_field", indexName: "idx_page_field_organization_id") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }
}