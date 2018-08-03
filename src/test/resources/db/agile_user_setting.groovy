package db

databaseChangeLog(logicalFilePath: 'script/db/agile_user_setting.groovy') {
    changeSet(id: '2018-07-04-agile-user-setting', author: 'dinghuang123@gmail.com') {
        createTable(tableName: "agile_user_setting", remarks: '用户设置表') {
            column(name: 'setting_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键') {
                constraints(primaryKey: true)
            }
            column(name: 'user_id', type: 'BIGINT UNSIGNED', remarks: '用户id') {
                constraints(nullable: false)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: false)
            }
            column(name: 'default_board_id', type: 'BIGINT UNSIGNED', remarks: '默认看板id')
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(indexName: 'idx_default_board_id', tableName: 'agile_user_setting') {
            column(name: 'default_board_id')
        }
    }
}