package script.db


databaseChangeLog(logicalFilePath: 'fd_object_scheme.groovy') {
    changeSet(id: '2019-03-29-create-table-object-scheme', author: 'shinan.chenX@gmail.com') {
        createTable(tableName: "fd_object_scheme") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(255)', remarks: '名称') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')
            column(name: 'scheme_code', type: 'VARCHAR(30)', remarks: '方案编码') {
                constraints(nullable: false)
            }
            column(name: 'is_system', type: 'TINYINT UNSIGNED(1)', remarks: '是否系统方案', defaultValue: "0") {
                constraints(nullable: false)
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id'){
                constraints(nullable: false)
            }
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "fd_object_scheme", indexName: "pk_object_scheme_id") {
            column(name: 'id', type: 'BIGINT UNSIGNED')
        }
        createIndex(tableName: "fd_object_scheme", indexName: "idx_object_scheme_organization_id") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }

}