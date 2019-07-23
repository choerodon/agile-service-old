package script.db


databaseChangeLog(logicalFilePath: 'fd_priority.groovy') {
    changeSet(id: '2018-08-21-create-priority', author: 'cong.cheng@hand-china.com') {
        createTable(tableName: "fd_priority") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID,主键') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(32)', remarks: '名称') {
                constraints(nullable: 'false')
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')
            column(name: 'colour', type: 'VARCHAR(20)', remarks: '颜色') {
                constraints(nullable: 'false')
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: 'false')
            }
            column(name: 'is_default', type: 'TINYINT UNSIGNED', defaultValue: "0", remarks: '是否默认') {
                constraints(nullable: 'false')
            }
            column(name: 'sequence', type: 'DECIMAL', defaultValue: "0", remarks: '排序') {
                constraints(nullable: 'false')
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "fd_priority", indexName: "priority_n1") {
            column(name: "name", type: "VARCHAR(32)")
        }
        createIndex(tableName: "fd_priority", indexName: "priority_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }

        createIndex(tableName: "fd_priority", indexName: "priority_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "fd_priority", indexName: "priority_n4") {
            column(name: "is_default", type: "TINYINT UNSIGNED")
        }
    }

    changeSet(id: '2019-03-04-add-column-is-enable', author: 'shinan.chenX@gmail.com') {
        addColumn(tableName: 'fd_priority') {
            column(name: 'is_enable', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: 'is enable flag') {
                constraints(nullable: 'false')
            }
        }
    }
}