package script.db
databaseChangeLog(logicalFilePath:'agile_board.groovyoovy') {
    changeSet(id: '2018-05-14-agile-board', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_board") {
            column(name: 'board_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'board id') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(255)', remarks: 'name') {
                constraints(nullable: false)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id') {
                constraints(nullable: false)
            }
            column(name: 'administrator_id', type: 'BIGINT UNSIGNED', remarks: 'administrator id')
            column(name: 'column_constraint', type: 'VARCHAR(30)', remarks: 'column constraint') {
                constraints(nullable: false)
            }
            column(name: 'is_day_in_column', type: 'TINYINT UNSIGNED', remarks: 'is day in column') {
                constraints(nullable: false)
            }
            column(name: 'swimlane_based_code', type: 'VARCHAR(30)', remarks: 'swimlane based code') {
                constraints(nullable: false)
            }
            column(name: 'estimation_statistic', type: 'VARCHAR(30)', remarks: 'estimation statistic') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}