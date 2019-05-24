package db

databaseChangeLog(logicalFilePath: 'script/db/agile_board_team.groovy') {
    changeSet(id: '2019-05-20-agile-board-team', author: 'shinan.chenX@gmail.com') {
        createTable(tableName: "agile_board_team") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键id') {
                constraints(primaryKey: true)
            }
            column(name: 'team_project_id', type: 'BIGINT UNSIGNED', remarks: '团队项目id') {
                constraints(nullable: false)
            }
            column(name: 'rank', type: 'VARCHAR(255)', remarks: '排序') {
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
        createIndex(tableName: "agile_board_team", indexName: "idx_board_team_program_id") {
            column(name: "program_id", type: "BIGINT UNSIGNED")
        }
    }
}