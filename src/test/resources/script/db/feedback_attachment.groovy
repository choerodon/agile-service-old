package script.db

databaseChangeLog(logicalFilePath: 'script/db/feedback_attachment.groovy') {
    changeSet(id: '2019-07-23-feedback-attachment', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "feedback_attachment") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'feedback_id', type: 'BIGINT UNSIGNED', remarks: 'feedback id')
            column(name: 'comment_id', type: 'BIGINT UNSIGNED', remarks: 'comment id')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id')
            column(name: 'url', type: 'VARCHAR(255)', remarks: 'url')
            column(name: 'file_name', type: 'VARCHAR(255)', remarks: 'file name')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2019-07-24-feedback-attachment-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "feedback_attachment", indexName: "idx_fba_project_id") {
            column(name: "project_id")
        }
        createIndex(tableName: "feedback_attachment", indexName: "idx_fba_feedback_id") {
            column(name: "feedback_id")
        }
    }
}