package script.db

databaseChangeLog(logicalFilePath: 'script/db/feedback_data_log.groovy') {
    changeSet(id: '2019-07-23-feedback-data-log', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "feedback_data_log") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'log id') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id')
            column(name: 'field', type: 'VARCHAR(255)', remarks: 'field')
            column(name: 'old_value', type: 'text', remarks: 'old value')
            column(name: 'old_string', type: 'text', remarks: 'old string')
            column(name: 'new_value', type: 'text', remarks: 'new value')
            column(name: 'new_string', type: 'text', remarks: 'new string')
            column(name: 'feedback_id', type: 'BIGINT UNSIGNED', remarks: 'feedback id')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}