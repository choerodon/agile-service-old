package script.db


databaseChangeLog(logicalFilePath: 'fd_field_value.groovy') {
    changeSet(id: '2019-03-29-create-table-field-value', author: 'shinan.chenX@gmail.com') {
        createTable(tableName: 'fd_field_value') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID,主键') {
                constraints(primaryKey: true)
            }
            column(name: 'instance_id', type: 'BIGINT UNSIGNED', remarks: '实例id') {
                constraints(nullable: false)
            }
            column(name: 'field_id', type: 'BIGINT UNSIGNED', remarks: '字段id') {
                constraints(nullable: false)
            }
            column(name: 'option_id', type: 'BIGINT UNSIGNED', remarks: '字段选项id')
            column(name: 'string_value', type: 'VARCHAR(255)', remarks: '字符串值')
            column(name: 'number_value', type: 'DECIMAL(10,2)', remarks: '数值')
            column(name: 'text_value', type: 'VARCHAR(255)', remarks: '文本值')
            column(name: 'date_value', type: 'DATETIME', remarks: '日期')
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
        createIndex(tableName: "fd_field_value", indexName: "pk_field_value_id") {
            column(name: 'id', type: 'BIGINT UNSIGNED')
        }
        createIndex(tableName: "fd_field_value", indexName: "idx_field_value_instance_id") {
            column(name: 'instance_id', type: 'BIGINT UNSIGNED')
        }
        createIndex(tableName: "fd_field_value", indexName: "idx_field_value_field_id") {
            column(name: 'field_id', type: 'BIGINT UNSIGNED')
        }
        createIndex(tableName: "fd_field_value", indexName: "idx_field_value_project_id") {
            column(name: "project_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "fd_field_value", indexName: "idx_field_value_scheme_code") {
            column(name: "scheme_code", type: "VARCHAR(30)")
        }
    }
}