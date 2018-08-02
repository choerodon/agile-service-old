package script.db

databaseChangeLog(logicalFilePath: 'script/db/agile_lookup_type.groovy') {
    changeSet(id: '2018-05-15-agile-lookup-type', author: 'dinghuang123@gmail.com') {
        createTable(tableName: "agile_lookup_type", remarks: '敏捷开发code键值类型') {
            column(name: 'type_code', type: 'VARCHAR(255)', remarks: '主键') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(255)', remarks: '名称') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}