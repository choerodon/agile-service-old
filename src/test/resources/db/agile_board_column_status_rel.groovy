package db
databaseChangeLog(logicalFilePath:'agile_board_column_status_rel.groovyoovy') {
    changeSet(id: '2018-05-14-agile-board-column-status-rel', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_board_column_status_rel") {
            column(name: 'position', type: 'INTEGER UNSIGNED', remarks: 'position') {
                constraints(nullable: false)
            }
            column(name: 'status_id', type: 'BIGINT UNSIGNED', remarks: 'status id') {
                constraints(nullable: false)
            }
            column(name: 'column_id', type: 'BIGINT UNSIGNED', remarks: 'column id') {
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
}