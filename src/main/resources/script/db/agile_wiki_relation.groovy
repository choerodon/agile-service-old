package script.db
databaseChangeLog(logicalFilePath:'agile_wiki_relation.groovy') {
    changeSet(id: '2018-12-03-agile-wiki-relation', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_wiki_relation") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id') {
                constraints(nullable: false)
            }
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: 'issue id') {
                constraints(nullable: false)
            }
            column(name: 'wiki_name', type: 'VARCHAR(30)', remarks: 'wiki name') {
                constraints(nullable: false)
            }
            column(name: 'wiki_url', type: 'TINYINT UNSIGNED', remarks: 'wiki url') {
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