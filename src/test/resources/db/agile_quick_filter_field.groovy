package db
databaseChangeLog(logicalFilePath:'agile_quick_filter_field.groovy'){
    changeSet(id: '2018-06-14-agile-quick-filter-field', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_quick_filter_field") {
            column(name: 'field_code', type: 'VARCHAR(255)',  remarks: 'field code') {
                constraints(primaryKey: true)
            }
            column(name: 'type', type: 'VARCHAR(255)', remarks: 'type')
            column(name: 'name', type: 'VARCHAR(255)', remarks: 'name')
            column(name: 'field', type: 'VARCHAR(255)', remarks: 'field')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}