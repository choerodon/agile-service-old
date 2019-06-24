package script.db

databaseChangeLog(logicalFilePath: 'agile_rank.groovy') {
    changeSet(id: '2019-06-24-agile-rank', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_rank") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id')
            column(name: 'type', type: 'VARCHAR(255)', remarks: 'rank type')
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: 'issue id')
            column(name: 'rank', type: 'VARCHAR(765)', remarks: 'rank')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createIndex(tableName: 'agile_rank', indexName: 'uk_project_issue', unique: true) {
            column(name: 'project_id')
            column(name: 'issue_id')
        }

        createIndex(tableName: "agile_rank", indexName: "idx_project_id") {
            column(name: "project_id")
        }
        createIndex(tableName: "agile_rank", indexName: "idx_type") {
            column(name: "type")
        }
        createIndex(tableName: "agile_rank", indexName: "idx_issue_id") {
            column(name: "issue_id")
        }
    }

}