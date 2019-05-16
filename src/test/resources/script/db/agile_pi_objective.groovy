package script.db

databaseChangeLog(logicalFilePath: 'agile_pi_objective.groovy') {
    changeSet(id: '2019-03-11-agile-pi-objective', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_pi_objective") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(255)', remarks: 'name')
            column(name: 'plan_bv', type: 'BIGINT UNSIGNED', remarks: 'plan bussiness value')
            column(name: 'actual_bv', type: 'BIGINT UNSIGNED', remarks: 'actual bussiness value')
            column(name: 'is_stretch', type: 'TINYINT UNSIGNED', remarks: 'is stretch')
            column(name: 'level_code', type: 'VARCHAR(255)', remarks: 'level code')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id')
            column(name: 'pi_id', type: 'BIGINT UNSIGNED', remarks: 'pi id')
            column(name: 'program_id', type: 'BIGINT UNSIGNED', remarks: 'program id')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}