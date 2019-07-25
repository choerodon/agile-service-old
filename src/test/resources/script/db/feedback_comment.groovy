package script.db

databaseChangeLog(logicalFilePath: 'script/db/feedback_comment.groovy') {
    changeSet(id: '2019-07-23-feedback-comment', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "feedback_comment") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'feedback_id', type: 'BIGINT UNSIGNED', remarks: 'feedback id')
            column(name: 'user_id', type: 'BIGINT UNSIGNED', remarks: '评论所有者')
            column(name: 'be_replied_id', type: 'BIGINT UNSIGNED', remarks: 'be replied id')
            column(name: 'content', type: 'text', remarks: 'comment text')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id')
            column(name: 'is_within', type: 'TINYINT UNSIGNED', remarks: '是否内部评论')
            column(name: 'parent_id', type: 'TINYINT UNSIGNED', remarks: '评论层级')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2019-07-24-feedback-comment-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "feedback_comment", indexName: "idx_fbc_project_id") {
            column(name: "project_id")
        }
        createIndex(tableName: "feedback_comment", indexName: "idx_fbc_feedback_id") {
            column(name: "feedback_id")
        }
    }
}