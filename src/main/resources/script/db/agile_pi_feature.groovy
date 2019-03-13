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
}