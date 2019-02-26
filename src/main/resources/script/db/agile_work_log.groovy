package script.db

databaseChangeLog(logicalFilePath: 'agile_work_log.groovyoovy') {
    changeSet(id: '2018-05-18-agile-work-log', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_work_log") {
            column(name: 'log_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'log id') {
                constraints(primaryKey: true)
            }
            column(name: 'work_time', type: 'DECIMAL', remarks: 'work time') {
                constraints(nullable: false)
            }
            column(name: 'start_date', type: 'DATETIME', remarks: 'start date') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(5000)', remarks: 'description')
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: 'issue id') {
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
    changeSet(id: '2019-02-26-modify-data-type', author: 'shinan.chenX@gmail.com') {
        modifyDataType(tableName: 'agile_work_log', columnName: 'work_time', newDataType: "DECIMAL(10,1)")
    }
}