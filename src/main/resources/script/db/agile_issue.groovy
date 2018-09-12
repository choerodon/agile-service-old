package script.db

databaseChangeLog(logicalFilePath: 'script/db/agile_issue.groovy') {
    changeSet(id: '2018-05-14-agile-issue', author: 'dinghuang123@gmail.com') {
        createTable(tableName: "agile_issue", remarks: '敏捷开发Issue') {
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键') {
                constraints(primaryKey: true)
            }
            column(name: 'issue_num', type: 'VARCHAR(255)', remarks: 'issue编号')
            column(name: 'type_code', type: 'VARCHAR(255)', remarks: '类型code') {
                constraints(nullable: false)
            }
            column(name: 'status_id', type: 'BIGINT UNSIGNED', remarks: '状态id') {
                constraints(nullable: false)
            }
            column(name: 'summary', type: 'VARCHAR(255)', remarks: '概要')
            column(name: 'priority_code', type: 'VARCHAR(255)', remarks: '优先级code') {
                constraints(nullable: false)
            }
            column(name: 'reporter_id', type: 'BIGINT UNSIGNED', remarks: 'issue负责人id') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'text', remarks: '描述')
            column(name: 'assignee_id', type: 'BIGINT UNSIGNED', remarks: '受让人id')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: false)
            }
            column(name: 'epic_id', type: 'BIGINT UNSIGNED', remarks: 'epic的id')
            column(name: 'sprint_id', type: 'BIGINT UNSIGNED', remarks: '冲刺id')
            column(name: 'parent_issue_id', type: 'BIGINT UNSIGNED', remarks: '父issue的id')
            column(name: 'story_points', type: 'INTEGER UNSIGNED', remarks: '故事点', defaultValue: "0")
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
    changeSet(id: '2018-05-25-agile-issue', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'agile_issue') {
            column(name: 'estimate_time', type: 'DECIMAL', remarks: '预估时间')
            column(name: 'remaining_time', type: 'DECIMAL', remarks: '剩余时间')
        }
    }

    changeSet(id: '2018-05-28-agile-issue', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'agile_issue') {
            column(name: 'color', type: 'VARCHAR(20)', remarks: '卡片颜色')
        }
    }

    changeSet(id: '2018-05-28-add-column', author: 'jian_zhang02@163.com') {
        addColumn(tableName: 'agile_issue') {
            column(name: 'rank', type: 'VARCHAR(255)', remarks: 'rank')
            column(name: 'epic_name', type: 'VARCHAR(255)', remarks: 'epic name')
        }
    }

    changeSet(id: '2018-06-06-agile-issue-add-index', author: 'dinghuang123@gmail.com') {
        createIndex(indexName: 'uk_issue_num', tableName: 'agile_issue', unique: true) {
            column(name: 'issue_num')
        }
        createIndex(indexName: "idx_type_code", tableName: "agile_issue") {
            column(name: "type_code")
        }
        createIndex(indexName: "idx_status_id", tableName: "agile_issue") {
            column(name: "status_id")
        }
        createIndex(indexName: "idx_priority_code", tableName: "agile_issue") {
            column(name: "priority_code")
        }
        createIndex(indexName: "idx_epic_id", tableName: "agile_issue") {
            column(name: "epic_id")
        }
        createIndex(indexName: "idx_sprint_id", tableName: "agile_issue") {
            column(name: "sprint_id")
        }
        createIndex(indexName: "idx_parent_issue_id", tableName: "agile_issue") {
            column(name: "parent_issue_id")
        }
    }

    changeSet(id: '2018-06-07-agile-issue-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "agile_issue", indexName: "idx_assignee_id") {
            column(name: "assignee_id")
        }
    }

    changeSet(id: '2018-06-07-agile-issue-drop-index', author: 'dinghuang123@gmail.com') {
        dropIndex(tableName: 'agile_issue', indexName: 'uk_issue_num')
        createIndex(indexName: 'uk_issue_num_project_id', tableName: 'agile_issue', unique: true) {
            column(name: 'issue_num')
            column(name: 'project_id')
        }
    }

    changeSet(id: '2018-06-07-agile-issue-rename-column', author: 'dinghuang123@gmail.com') {
        renameColumn(columnDataType: 'VARCHAR(20)', newColumnName: 'color_code', oldColumnName: 'color', remarks: '颜色代码', tableName: 'agile_issue')
    }

    changeSet(id: '2018-06-08-agile-issue-rename-column', author: 'dinghuang123@gmail.com') {
        renameColumn(columnDataType: 'VARCHAR(50)', newColumnName: 'color_code', oldColumnName: 'color_code', remarks: '颜色代码', tableName: 'agile_issue')
    }

    changeSet(id: '2018-07-30-add-column', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'agile_issue') {
            column(name: 'epic_sequence', type: 'int', remarks: '排序字段')
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "update agile_issue set epic_sequence = issue_id where type_code = 'issue_epic';"
        }
    }

    changeSet(id: '2018-08-07-agile-issue-add-index', author: 'dinghuang123@gmail.com') {
        createIndex(indexName: "idx_project_id", tableName: "agile_issue") {
            column(name: "project_id")
        }
    }

    changeSet(id: '2018-09-05-add-column', author: 'fuqianghuang01@gmail.com') {
        addColumn(tableName: 'agile_issue') {
            column(name: 'map_rank', type: 'VARCHAR(765)', remarks: 'issue map rank')
        }
    }
}