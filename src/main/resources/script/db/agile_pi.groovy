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

    changeSet(id: '2019-04-02-agile-pi-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "agile_pi", indexName: "idx_program_id") {
            column(name: "program_id")
        }
        createIndex(tableName: "agile_pi", indexName: "idx_art_id") {
            column(name: "art_id")
        }
    }

    changeSet(id: '2019-04-10-agile-pi-add-column', author: 'fuqianghuang01@gmail.com') {
        addColumn(tableName: 'agile_pi') {
            column(name: 'actual_start_date', type: 'DATETIME', remarks: 'actual start date')
            column(name: 'actual_end_date', type: 'DATETIME', remarks: 'actual end date')
        }
    }
}