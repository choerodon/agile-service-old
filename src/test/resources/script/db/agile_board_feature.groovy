package db
databaseChangeLog(logicalFilePath:'script/db/agile_board_feature.groovy') {
    changeSet(id: '2019-05-13-agile-board-feature', author: 'shinan.chenX@gmail.com') {
        createTable(tableName: "agile_board_feature") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键id') {
                constraints(primaryKey: true)
            }
            column(name: 'feature_id', type: 'BIGINT UNSIGNED', remarks: '特性问题id') {
                constraints(nullable: false)
            }
            column(name: 'sprint_id', type: 'BIGINT UNSIGNED', remarks: '冲刺id') {
                constraints(nullable: false)
            }
            column(name: 'pi_id', type: 'BIGINT UNSIGNED', remarks: 'piID') {
                constraints(nullable: false)
            }
            column(name: 'team_project_id', type: 'BIGINT UNSIGNED', remarks: '团队项目id') {
                constraints(nullable: false)
            }
            column(name: 'rank', type: 'VARCHAR(255)', remarks: 'rank值')
            column(name: 'program_id', type: 'BIGINT UNSIGNED', remarks: '项目群id') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "agile_board_feature", indexName: "idx_board_feature_pi_id") {
            column(name: "pi_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "agile_board_feature", indexName: "idx_board_feature_program_id") {
            column(name: "program_id", type: "BIGINT UNSIGNED")
        }
    }
}