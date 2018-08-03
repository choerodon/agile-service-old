package db

databaseChangeLog(logicalFilePath: 'agile_issue_attachment.groovyoovy') {
    changeSet(id: '2018-05-14-agile-issue-attachment', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_issue_attachment") {
            column(name: 'attachment_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'attachment id') {
                constraints(primaryKey: true)
            }
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: 'issue id') {
                constraints(nullable: false)
            }
            column(name: 'comment_id', type: 'BIGINT UNSIGNED', remarks: 'comment id') {
                constraints(nullable: false)
            }
            column(name: 'url', type: 'VARCHAR(255)', remarks: 'url') {
                constraints(nullable: false)
            }
            column(name: 'file_name', type: 'VARCHAR(255)', remarks: 'file name') {
                constraints(nullable: false)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id') {
                constraints(nullable: false)
            }
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}