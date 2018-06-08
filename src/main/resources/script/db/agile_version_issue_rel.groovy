package script.db

databaseChangeLog(logicalFilePath: 'script/db/agile_version_issue_rel.groovy') {
    changeSet(id: '2018-05-14-agile-version-issue-rel', author: 'jian_zhang02@163.com') {
        createTable(tableName: "agile_version_issue_rel") {
            column(name: 'version_id', type: 'BIGINT UNSIGNED', remarks: 'version id') {
                constraints(nullable: false)
            }
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: 'issue id') {
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
    changeSet(id: '2018-05-22-create-index', author: 'jian_zhang02@163.com') {
        createIndex(schemaName: '', tablespace: '', tableName: 'agile_version_issue_rel', indexName: 'uk_version_id_issue_id', unique: true) {
            column(name: 'version_id')
            column(name: 'issue_id')
        }
    }
    changeSet(id: '2018-05-24-agile-version-issue-rel', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'agile_version_issue_rel') {
            column(name: 'relation_type', type: 'VARCHAR(32)', remarks: '版本类型')
        }
    }
    changeSet(id: '2018-05-24-agile-version-issue-rel', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'agile_version_issue_rel') {
            column(name: 'relation_type', type: 'VARCHAR(32)', remarks: '版本类型')
        }
    }
    changeSet(id: '2018-06-05-agile-version-issue-rel', author: 'dinghuang123@gmail.com') {
        dropIndex(tableName: 'agile_version_issue_rel', indexName: 'uk_version_id_issue_id')
        createIndex(schemaName: '', tablespace: '', tableName: 'agile_version_issue_rel', indexName: 'uk_version_id_issue_id_relation_type', unique: true) {
            column(name: 'version_id')
            column(name: 'issue_id')
            column(name: 'relation_type')
        }
    }
    changeSet(id: '2018-06-07-agile-version-issue-rel-add-not-null-constraint', author: 'dinghuang123@gmail.com') {
        addNotNullConstraint(tableName: 'agile_version_issue_rel', columnName: 'relation_type', columnDataType: "VARCHAR(32)")
    }
    changeSet(id: '2018-06-08-agile-version-issue-rel-drop-index', author: 'dinghuang123@gmail.com') {
        dropIndex(tableName: 'agile_version_issue_rel', indexName: 'uk_version_id_issue_id_relation_type')
    }
    changeSet(id: '2018-06-08-agile-version-issue-rel-add-index', author: 'dinghuang123@gmail.com') {
        createIndex(schemaName: '', tablespace: '', tableName: 'agile_version_issue_rel', indexName: 'uk_version_id_issue_id_relation_type', unique: true) {
            column(name: 'version_id')
            column(name: 'issue_id')
            column(name: 'relation_type')
        }
    }
}