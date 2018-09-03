package script.db

databaseChangeLog(logicalFilePath: 'script/db/agile_label_issue_rel.groovy') {
    changeSet(id: '2018-06-08-agile-label-issue-rel', author: 'dinghuang123@gmail.com') {
        createTable(tableName: "agile_label_issue_rel", remarks: '敏捷开发Issue标签关联') {
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: '主键') {
                constraints(nullable: false)
            }
            column(name: 'label_id', type: 'BIGINT UNSIGNED', remarks: '标签id') {
                constraints(nullable: false)
            }
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
    changeSet(id: '2018-09-03-agile-label-issue-rel-add-project-id', author: 'changpingshi0213@gmail.com') {
        addColumn(tableName: 'agile_label_issue_rel') {
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id') {
                constraints(nullable: false)
            }
        }
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "UPDATE agile_label_issue_rel alir,(SELECT ai.project_id, alir1.issue_id FROM agile_label_issue_rel alir1 right JOIN agile_issue ai ON alir1.issue_id = ai.issue_id) as res SET alir.project_id = res.project_id where alir.issue_id=res.issue_id"
        }
    }
}