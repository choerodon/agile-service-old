package script.db
databaseChangeLog(logicalFilePath:'agile_file_operation_history.groovy') {
    changeSet(id: '2019-02-25-agile-file_operation-history', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_file_operation_history") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'board id') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id') {
                constraints(nullable: false)
            }
            column(name: 'action', type: 'VARCHAR(255)', remarks: 'action')
            column(name: 'success_count', type: 'BIGINT UNSIGNED', remarks: 'success count')
            column(name: 'fail_count', type: 'BIGINT UNSIGNED', remarks: 'fail count')
            column(name: 'status', type: 'VARCHAR(255)', remarks: 'status')
            column(name: 'file_url', type: 'VARCHAR(1000)', remarks: 'file url')
            column(name: 'user_id', type: 'BIGINT UNSIGNED', remarks: 'user id')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}