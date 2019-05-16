package script.db
databaseChangeLog(logicalFilePath:'agile_data_log.groovy') {
    changeSet(id: '2018-06-20-agile-data-log', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_data_log") {
            column(name: 'log_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'log id') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id') {
                constraints(nullable: false)
            }
            column(name: 'field', type: 'VARCHAR(255)', remarks: 'field')
            column(name: 'old_value', type: 'text', remarks: 'old value')
            column(name: 'old_string', type: 'text', remarks: 'old string')
            column(name: 'new_value', type: 'text', remarks: 'new value')
            column(name: 'new_string', type: 'text', remarks: 'new string')
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: 'issue id')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2018-06-26-agile-data-log-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "agile_data_log", indexName: "idx_project_id") {
            column(name: "project_id")
        }
        createIndex(tableName: "agile_data_log", indexName: "idx_issue_id") {
            column(name: "issue_id")
        }
        createIndex(tableName: "agile_data_log", indexName: "idx_field") {
            column(name: "field")
        }
    }
}