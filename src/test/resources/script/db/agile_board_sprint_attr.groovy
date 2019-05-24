package db

databaseChangeLog(logicalFilePath: 'script/db/agile_board_sprint_attr.groovy') {
    changeSet(id: '2019-05-14-agile-board-sprint-attr', author: 'shinan.chenX@gmail.com') {
        createTable(tableName: "agile_board_sprint_attr") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键id') {
                constraints(primaryKey: true)
            }
            column(name: 'sprint_id', type: 'BIGINT UNSIGNED', remarks: '冲刺id') {
                constraints(nullable: false)
            }
            column(name: 'column_width', type: 'INTEGER(2)', remarks: '列宽') {
                constraints(nullable: false)
            }
            column(name: 'program_id', type: 'BIGINT UNSIGNED', remarks: '项目群id') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "agile_board_sprint_attr", indexName: "idx_board_sprint_attr_program_id") {
            column(name: "program_id", type: "BIGINT UNSIGNED")
        }
    }
}