package script.db
databaseChangeLog(logicalFilePath:'agile_issue_status.groovyoovy') {
    changeSet(id: '2018-05-14-agile-issue-status', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_issue_status") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: false)
            }
            column(name: 'name', type: 'VARCHAR(255)', remarks: 'name') {
                constraints(nullable: false)
            }
            column(name: 'is_enable', type: 'TINYINT UNSIGNED', remarks: 'enabled flag') {
                constraints(nullable: false)
            }
            column(name: 'category_code', type: 'VARCHAR(255)', remarks: 'category code') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2018-06-05-agile-issue-status-add-column', author: 'fuqianghuang01@gmail.com') {
        addColumn(tableName: 'agile_issue_status') {
            column(name: 'is_completed', type: 'TINYINT UNSIGNED', remarks: 'is completed')
        }
    }

    changeSet(id: '2018-06-06-agile-issue-status-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "agile_issue_status", indexName: "idx_category_code") {
            column(name: "category_code")
        }
    }

    changeSet(id: '2018-10-23-status-add-column-status-id', author: 'fuqianghuang01@gmail.com') {
        addColumn(tableName: 'agile_issue_status') {
            column(name: 'status_id', type: 'BIGINT UNSIGNED', remarks: 'status id')
        }
    }

    changeSet(id: '2018-11-29-status-add-index', author: 'dinghuang123@gmail.com') {
        createIndex(indexName: 'uk_status_id_project_id', tableName: 'agile_issue_status', unique: true) {
            column(name: 'status_id')
            column(name: 'project_id')
        }
    }
}