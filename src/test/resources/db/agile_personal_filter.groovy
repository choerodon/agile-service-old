package db
databaseChangeLog(logicalFilePath:'agile_personal_filter.groovy') {
    changeSet(id: '2019-02-25-agile-personal-filter', author: 'shinan.chenX@gmail.com') {
        createTable(tableName: "agile_personal_filter") {
            column(name: 'filter_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键id') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: false)
            }
            column(name: 'name', type: 'VARCHAR(255)', remarks: 'name'){
                constraints(nullable: false)
            }
            column(name: 'user_id', type: 'BIGINT UNSIGNED', remarks: '用户id'){
                constraints(nullable: false)
            }
            column(name: 'filter_json', type: 'TEXT', remarks: '筛选条件的json') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "agile_personal_filter", indexName: "agile_personal_filter_n1") {
            column(name: "project_id", type: "BIGINT UNSIGNED")
        }
    }
}