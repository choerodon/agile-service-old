package script.db


databaseChangeLog(logicalFilePath: 'fd_field_option.groovy') {
    changeSet(id: '2019-03-29-create-table-field-field-option', author: 'shinan.chenX@gmail.com') {
        createTable(tableName: "fd_field_option") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'field_id', type: 'BIGINT UNSIGNED', remarks: '字段id') {
                constraints(nullable: false)
            }
            column(name: 'code', type: 'VARCHAR(30)', remarks: '选项值编码') {
                constraints(nullable: false)
            }
            column(name: 'value', type: 'VARCHAR(30)', remarks: '选项值') {
                constraints(nullable: false)
            }
            column(name: 'parent_id', type: 'BIGINT UNSIGNED', remarks: '父选项id')
            column(name: 'sequence', type: 'int', remarks: '排序', defaultValue: "0")
            column(name: 'is_enabled', type: 'TINYINT UNSIGNED(1)', remarks: '是否启用', defaultValue: "1") {
                constraints(nullable: false)
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "fd_field_option", indexName: "pk_field_option_id") {
            column(name: 'id', type: 'BIGINT UNSIGNED')
        }
        createIndex(tableName: "fd_field_option", indexName: "idx_field_option_field_id") {
            column(name: "field_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "fd_field_option", indexName: "idx_field_option_organization_id") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }

}