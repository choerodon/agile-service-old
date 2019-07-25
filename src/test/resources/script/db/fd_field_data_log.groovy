package script.db


databaseChangeLog(logicalFilePath: 'fd_field_data_log.groovy') {
    changeSet(id: '2019-06-19-create-table-field-data-log', author: 'shinan.chenX@gmail.com') {
        createTable(tableName: 'fd_field_data_log') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID,主键') {
                constraints(primaryKey: true)
            }
            column(name: 'field_id', type: 'BIGINT UNSIGNED', remarks: '字段id') {
                constraints(nullable: false)
            }
            column(name: 'old_value', type: 'text', remarks: 'old value')
            column(name: 'old_string', type: 'text', remarks: 'old string')
            column(name: 'new_value', type: 'text', remarks: 'new value')
            column(name: 'new_string', type: 'text', remarks: 'new string')
            column(name: 'instance_id', type: 'BIGINT UNSIGNED', remarks: '实体id')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: false)
            }
            column(name: 'scheme_code', type: 'VARCHAR(30)', remarks: '方案编码') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "fd_field_data_log", indexName: "idx_field_data_log_project_id") {
            column(name: "project_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "fd_field_data_log", indexName: "idx_field_data_log_scheme_code") {
            column(name: "scheme_code", type: "VARCHAR(30)")
        }
    }
}