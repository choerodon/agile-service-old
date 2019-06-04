package script.db

databaseChangeLog(logicalFilePath: 'agile_storymap_width.groovy') {
    changeSet(id: '2019-06-03-agile-storymap-width', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_storymap_width") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'type', type: 'VARCHAR(255)', remarks: 'width type')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id')
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: 'epic id or feature id')
            column(name: 'width', type: 'BIGINT UNSIGNED', remarks: 'width')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}