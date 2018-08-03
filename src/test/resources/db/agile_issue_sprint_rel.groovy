package db

databaseChangeLog(logicalFilePath: 'script/db/agile_issue_sprint_rel.groovy') {
    changeSet(id: '2018-06-15-agile_issue_sprint_rel', author: 'jian_zhang02@163.com') {
        createTable(tableName: "agile_issue_sprint_rel") {
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: 'issue id') {
                constraints(nullable: false)
            }
            column(name: 'sprint_id', type: 'BIGINT UNSIGNED', remarks: 'sprint id') {
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

    changeSet(id: '2018-06-19-agile-issue-sprint-rel-add-index', author: 'jian_zhang02@163.com') {
        createIndex(schemaName: '', tablespace: '', tableName: 'agile_issue_sprint_rel', indexName: 'uk_sprint_id_issue_id', unique: true) {
            column(name: 'sprint_id')
            column(name: 'issue_id')
        }
    }

    changeSet(id: '2018-06-26-agile-issue-sprint-rel-init-data', author: 'jian_zhang02@163.com') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "INSERT IGNORE INTO agile_issue_sprint_rel(issue_id, sprint_id, project_id) SELECT ai.issue_id, ai.sprint_id, ai.project_id FROM agile_issue ai WHERE ai.sprint_id IS NOT NULL AND ai.sprint_id != 0;"
        }
    }
}