package script.db
databaseChangeLog(logicalFilePath:'agile_board_column.groovyoovy') {
    changeSet(id: '2018-05-14-agile-board-column', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_board_column") {
            column(name: 'column_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'column id') {
                constraints(primaryKey: true)
            }
            column(name: 'status_code', type: 'VARCHAR(30)', remarks: 'status code') {
                constraints(nullable: false)
            }
            column(name: 'name', type: 'VARCHAR(255)', remarks: 'name') {
                constraints(nullable: false)
            }
            column(name: 'board_id', type: 'BIGINT UNSIGNED', remarks: 'board id') {
                constraints(nullable: false)
            }
            column(name: 'min_num', type: 'BIGINT UNSIGNED', remarks: 'min num')
            column(name: 'max_num', type: 'BIGINT UNSIGNED', remarks: 'max num')
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
    changeSet(id: '2018-05-24-agile-board-column-add-column', author: 'fuqianghuang01@gmail.com') {
        addColumn(tableName: 'agile_board_column') {
            column(name: 'sequence', type: 'INTEGER UNSIGNED', remarks: '列排序') {
                constraints(nullable: false)
            }
        }
    }
    changeSet(id:  '2018-05-24-agile-board-column-rename-column', author: 'fuqianghuang01@gmail.com') {
        renameColumn(columnDataType: 'VARCHAR(30)', newColumnName: 'category_code', oldColumnName: 'status_code', remarks: 'status code',tableName: 'agile_board_column')
    }
    changeSet(id: '2018-06-05-agile-board-column-add-column', author: 'fuqianghuang01@gmail.com') {
        addColumn(tableName: 'agile_board_column') {
            column(name: 'color', type: 'VARCHAR(20)', remarks: '颜色') {
                constraints(nullable: false)
            }
        }
    }
    changeSet(id: '2018-06-06-agile-board-column-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "agile_board_column", indexName: "idx_board_id ") {
            column(name: "board_id")
        }
    }
    changeSet(id: '2018-06-05-agile-board-column-rename-color', author: 'fuqianghuang01@gmail.com') {
        renameColumn(columnDataType: 'VARCHAR(50)', newColumnName: 'color_code', oldColumnName: 'color', remarks: 'color code',tableName: 'agile_board_column')
    }
}