package script.db

databaseChangeLog(logicalFilePath: 'script/db/agile_issue_link.groovy') {
    changeSet(id: '2018-05-14-agile-issue-link', author: 'dinghuang123@gmail.com') {
        createTable(tableName: "agile_issue_link", remarks: '敏捷开发Issue链接') {
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: '主键') {
                constraints(nullable: false)
            }
            column(name: 'issue_link_type_code', type: 'VARCHAR(255)', remarks: '链接code') {
                constraints(nullable: false)
            }
            column(name: 'linked_issue_id', type: 'BIGINT UNSIGNED', remarks: '链接的issue的id') {
                constraints(nullable: false)
            }
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2018-06-06-agile-issue-link-add-index', author: 'dinghuang123@gmail.com') {
        createIndex(indexName: 'uk_issue_id_linked_issue_id', tableName: 'agile_issue_link', unique: true) {
            column(name: 'issue_id')
            column(name: 'linked_issue_id')
        }
    }
}