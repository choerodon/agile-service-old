package script.db

databaseChangeLog(logicalFilePath: 'fd_status.groovy') {

    changeSet(author: 'shinan.chenX@gmail.com', id: '2018-08-06-status') {
        createTable(tableName: 'fd_status') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称') {
                constraints(nullable: false)
            }
            column(name: 'code', type: 'VARCHAR(30)', remarks: '编码')
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')
            column(name: 'type', type: 'VARCHAR(30)', remarks: '类型') {
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
        createIndex(tableName: "fd_status", indexName: "status_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "fd_status", indexName: "status_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }
        createIndex(tableName: "fd_status", indexName: "status_n3") {
            column(name: "type", type: "VARCHAR(30)")
        }
        createIndex(tableName: "fd_status", indexName: "status_n4") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }
    changeSet(id: '2019-03-12-fix-add-status-prepare', author: 'shinan.chenX@gmail') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "insert into fd_status(name,code,description,type,organization_id) " +
                    "select '准备' as name,'prepare' as code, '准备' as description,'prepare' as type,organization_id " +
                    "from fd_status where code='create'"
        }
    }
}