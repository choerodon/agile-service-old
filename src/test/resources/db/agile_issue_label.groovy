package db

databaseChangeLog(logicalFilePath: 'script/db/agile_issue_label.groovy') {
    changeSet(id: '2018-05-14-agile-issue-label', author: 'dinghuang123@gmail.com') {
        createTable(tableName: "agile_issue_label", remarks: '敏捷开发Issue标签') {
            column(name: 'label_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键') {
                constraints(primaryKey: true)
            }
            column(name: 'label_name', type: 'VARCHAR(255)', remarks: '标签名称')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
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