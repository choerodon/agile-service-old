package script.db


databaseChangeLog(logicalFilePath: 'fd_project_page_field.groovy') {
    changeSet(id: '2019-03-29-create-table-project-page-field', author: 'shinan.chenX@gmail.com') {
        createTable(tableName: 'fd_project_page_field') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键') {
                constraints(primaryKey: true)
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: false)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "fd_project_page_field", indexName: "pk_project_page_field_id") {
            column(name: 'id', type: 'BIGINT UNSIGNED')
        }
        createIndex(tableName: "fd_project_page_field", indexName: "idx_project_page_field_project_id") {
            column(name: "project_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "fd_project_page_field", indexName: "idx_project_page_field_organization_id") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }
}