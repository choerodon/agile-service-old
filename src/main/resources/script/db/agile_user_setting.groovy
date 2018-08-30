package script.db

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
    changeSet(id: '2018-08-28-agile-user-setting-add-column', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'agile_user_setting') {
            column(name: 'swimlane_based_code', type: 'VARCHAR(100)', remarks: '泳道类型', defaultValue: 'swimlane_none')
        }
        addColumn(tableName: 'agile_user_setting') {
            column(name: 'is_default_board', type: 'tinyint', remarks: '是否默认泳道', defaultValue: '0')
        }
        addColumn(tableName: 'agile_user_setting') {
            column(name: 'type_code', type: 'VARCHAR(100)', remarks: '用户设置类型')
        }
        renameColumn(columnDataType: 'BIGINT UNSIGNED', newColumnName: 'board_id', oldColumnName: 'default_board_id', remarks: '看板id', tableName: 'agile_user_setting')
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "update agile_user_setting set is_default_board = 1;" +
                    "update agile_user_setting set type_code = 'board';"
        }
        dropIndex(tableName: 'agile_user_setting', indexName: 'idx_default_board_id')
        createIndex(indexName: 'idx_board_id', tableName: 'agile_user_setting') {
            column(name: 'board_id')
        }
    }

    changeSet(id: '2018-08-30-agile-user-setting-add-column', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'agile_user_setting') {
            column(name: 'storymap_swimlane_code', type: 'VARCHAR(100)', remarks: '故事地图泳道类型', defaultValue: 'swimlane_none')
        }
    }
}