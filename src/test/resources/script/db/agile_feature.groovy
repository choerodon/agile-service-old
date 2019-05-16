package script.db

databaseChangeLog(logicalFilePath: 'agile_feature.groovy') {
    changeSet(id: '2019-03-11-agile-feature', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_feature") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: 'issue id')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id')
            column(name: 'benfit_hypothesis', type: 'VARCHAR(255)', remarks: 'benfit hypothesis')
            column(name: 'acceptance_critera', type: 'VARCHAR(255)', remarks: 'acceptance critera')
            column(name: 'feature_type', type: 'VARCHAR(255)', remarks: 'feature type')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}