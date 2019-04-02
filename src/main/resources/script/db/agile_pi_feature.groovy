package script.db

databaseChangeLog(logicalFilePath: 'script/db/agile_pi_feature.groovy') {
    changeSet(id: '2019-03-12-agile-pi-feature', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_pi_feature") {
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: 'issue id') {
                constraints(nullable: false)
            }
            column(name: 'pi_id', type: 'BIGINT UNSIGNED', remarks: 'pi id') {
                constraints(nullable: false)
            }
            column(name: 'program_id', type: 'BIGINT UNSIGNED', remarks: 'program id') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2019-04-02-agile-pi-feature-add-uk-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: 'agile_pi_feature', indexName: 'uk_pi_feature', unique: true) {
            column(name: 'issue_id')
            column(name: 'pi_id')
        }
    }

    changeSet(id: '2019-04-02-agile-pi-feature-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "agile_pi_feature", indexName: "idx_issue_id") {
            column(name: "issue_id")
        }
        createIndex(tableName: "agile_pi_feature", indexName: "idx_pi_id") {
            column(name: "pi_id")
        }
        createIndex(tableName: "agile_pi_feature", indexName: "idx_program_id") {
            column(name: "program_id")
        }
    }
}