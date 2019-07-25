package script.db

databaseChangeLog(logicalFilePath: 'script/db/feedback.groovy') {
    changeSet(id: '2019-07-23-feedback', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "feedback") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'feedback_num', type: 'VARCHAR(255)', remarks: 'feedback number')
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: 'organization id')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id')
            column(name: 'type', type: 'VARCHAR(255)', remarks: 'feedback type')
            column(name: 'summary', type: 'VARCHAR(255)', remarks: 'feedback title')
            column(name: 'description', type: 'text', remarks: 'feedback description')
            column(name: 'reporter', type: 'VARCHAR(255)', remarks: 'feedback reporter')
            column(name: 'assignee_id', type: 'BIGINT UNSIGNED', remarks: 'feedback assignee id')
            column(name: 'status', type: 'VARCHAR(255)', remarks: 'feedback status')
            column(name: 'email', type: 'VARCHAR(255)', remarks: 'user email')
            column(name: 'application_id', type: 'BIGINT UNSIGNED', remarks: '应用id')
            column(name: 'screen_size', type: 'VARCHAR(255)', remarks: '屏幕尺寸')
            column(name: 'browser', type: 'VARCHAR(500)', remarks: '浏览器')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2019-07-24-feedback-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "feedback", indexName: "idx_fb_project_id") {
            column(name: "project_id")
        }
    }
}