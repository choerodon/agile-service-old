package script.db
databaseChangeLog(logicalFilePath:'agile_quick_filter.groovyoovy') {
    changeSet(id: '2018-05-14-agile-quick-filter', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_quick_filter") {
            column(name: 'filter_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'board id') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id') {
                constraints(nullable: false)
            }
            column(name: 'name', type: 'VARCHAR(255)', remarks: 'name')
            column(name: 'sql_query', type: 'VARCHAR(1000)', remarks: 'long query')
            column(name: 'express_query', type: 'VARCHAR(1000)', remarks: 'express query')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}