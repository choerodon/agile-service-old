package db

databaseChangeLog(logicalFilePath: 'script/db/agile_board_depend.groovy') {
    changeSet(id: '2019-05-13-agile-board-depend', author: 'shinan.chenX@gmail.com') {
        createTable(tableName: "agile_board_depend") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键id') {
                constraints(primaryKey: true)
            }
            column(name: 'board_feature_id', type: 'BIGINT UNSIGNED', remarks: '公告板特性问题id') {
                constraints(nullable: false)
            }
            column(name: 'depend_board_feature_id', type: 'BIGINT UNSIGNED', remarks: '依赖的公告板特性问题id') {
                constraints(nullable: false)
            }
            column(name: 'pi_id', type: 'BIGINT UNSIGNED', remarks: 'piID') {
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
        createIndex(tableName: "agile_board_depend", indexName: "idx_board_depend_pi_id") {
            column(name: "pi_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "agile_board_depend", indexName: "idx_board_depend_program_id") {
            column(name: "program_id", type: "BIGINT UNSIGNED")
        }
    }
}