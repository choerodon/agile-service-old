package db

databaseChangeLog(logicalFilePath: 'script/db/agile_sprint.groovy') {
    changeSet(id: '2018-05-14-agile-sprint', author: 'jian_zhang02@163.com') {
        createTable(tableName: "agile_sprint") {
            column(name: 'sprint_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'sprint id') {
                constraints(primaryKey: true)
            }
            column(name: 'sprint_name', type: 'VARCHAR(255)', remarks: 'sprint name') {
                constraints(nullable: false)
            }
            column(name: 'sprint_goal', type: 'VARCHAR(255)', remarks: 'sprint goal')
            column(name: 'duration', type: 'SMALLINT UNSIGNED', remarks: 'duration') {
                constraints(nullable: false)
            }
            column(name: 'start_date', type: 'DATETIME', remarks: 'start date')
            column(name: 'end_date', type: 'DATETIME', remarks: 'end date')
            column(name: 'actual_end_date', type: 'DATETIME', remarks: 'actual end date')
            column(name: 'status_code', type: 'VARCHAR(10)', remarks: 'status code') {
                constraints(nullable: false)
            }

            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id') {
                constraints(nullable: false)
            }
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
    changeSet(id: '2018-05-28-modify-column', author: 'jian_zhang02@163.com') {
        modifyDataType(tableName: 'agile_sprint', columnName: 'status_code', newDataType: "VARCHAR(255)")
    }

    changeSet(id: '2018-05-31-drop-not-null-constraint', author: 'jian_zhang02@163.com') {
        dropNotNullConstraint(schemaName: '', tableName: 'agile_sprint', columnName: 'duration', columnDataType: 'SMALLINT UNSIGNED')
    }

    changeSet(id: '2018-06-06-drop-column', author: 'jian_zhang02@163.com') {
        dropColumn(tableName: 'agile_sprint') {
            column(name: 'duration')
        }
    }
}