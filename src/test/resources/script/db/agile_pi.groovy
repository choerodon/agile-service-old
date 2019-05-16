package script.db

databaseChangeLog(logicalFilePath: 'agile_pi.groovy') {
    changeSet(id: '2019-03-11-agile-pi', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_pi") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'code', type: 'VARCHAR(255)', remarks: 'code')
            column(name: 'name', type: 'VARCHAR(255)', remarks: 'name')
            column(name: 'seq_number', type: 'VARCHAR(255)', remarks: 'seq number')
            column(name: 'status_code', type: 'VARCHAR(255)', remarks: 'status code')
            column(name: 'start_date', type: 'DATETIME', remarks: 'start date')
            column(name: 'end_date', type: 'DATETIME', remarks: 'start date')
            column(name: 'art_id', type: 'BIGINT UNSIGNED', remarks: 'art id')
            column(name: 'program_id', type: 'BIGINT UNSIGNED', remarks: 'program id')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}