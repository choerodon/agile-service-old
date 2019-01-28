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
    changeSet(id: '2018-06-14-agile-issue-link-change', author: 'dinghuang123@gmail.com') {
        dropIndex(tableName: 'agile_issue_link', indexName: 'uk_issue_id_linked_issue_id')
        addColumn(tableName: 'agile_issue_link') {
            column(name: 'link_id', type: 'BIGINT UNSIGNED', remarks: 'link id')
            column(name: 'link_type_id', type: 'BIGINT UNSIGNED', remarks: 'link类型id')
        }
        dropColumn(tableName: 'agile_issue_link') {
            column(name: 'issue_link_type_code')
        }
        addNotNullConstraint(tableName: 'agile_issue_link', columnName: 'link_id', columnDataType: "BIGINT UNSIGNED")
        addNotNullConstraint(tableName: 'agile_issue_link', columnName: 'link_type_id', columnDataType: "BIGINT UNSIGNED")
        addPrimaryKey(tableName: 'agile_issue_link', columnNames: 'link_id', constraintName: '')
        addAutoIncrement(tableName: 'agile_issue_link', columnName: 'link_id', columnDataType: 'BIGINT UNSIGNED')
        createIndex(indexName: 'idx_issue_id', tableName: 'agile_issue_link') {
            column(name: 'issue_id')
        }
        createIndex(indexName: 'idx_link_type_id', tableName: 'agile_issue_link') {
            column(name: 'link_type_id')
        }
        createIndex(indexName: 'idx_linked_issue_id', tableName: 'agile_issue_link') {
            column(name: 'linked_issue_id')
        }
    }

    changeSet(id: '2019-01-28-agile-issue-link-add-project_id', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'agile_issue_link') {
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id')
        }
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "UPDATE agile_issue_link ail,( SELECT ailt.project_id, ailt.link_type_id FROM agile_issue_link_type ailt) AS res SET ail.project_id = res.project_id WHERE ail.link_type_id = res.link_type_id"
        }
        createIndex(indexName: 'idx_project_id', tableName: 'agile_issue_link') {
            column(name: 'project_id')
        }
        addNotNullConstraint(tableName: 'agile_issue_link', columnName: 'project_id', columnDataType: "BIGINT UNSIGNED")
    }

}