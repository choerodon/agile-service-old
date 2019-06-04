package script.db

databaseChangeLog(logicalFilePath: 'agile_art.groovy') {
    changeSet(id: '2019-03-11-agile-art', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_art") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'code', type: 'VARCHAR(255)', remarks: 'code')
            column(name: 'name', type: 'VARCHAR(255)', remarks: 'name')
            column(name: 'description', type: 'text', remarks: 'description')
            column(name: 'seq_number', type: 'VARCHAR(255)', remarks: 'seq number')
            column(name: 'is_enabled', type: 'TINYINT UNSIGNED', remarks: 'enabled flag')
            column(name: 'rte_id', type: 'BIGINT UNSIGNED', remarks: 'rte id')
            column(name: 'start_date', type: 'DATETIME', remarks: 'start date')
            column(name: 'end_date', type: 'DATETIME', remarks: 'start date')
            column(name: 'ip_weeks', type: 'BIGINT UNSIGNED', remarks: 'ip workdays')
            column(name: 'pi_code_prefix', type: 'VARCHAR(255)', remarks: 'pi code prefix')
            column(name: 'pi_code_number', type: 'BIGINT UNSIGNED', remarks: 'pi code number')
            column(name: 'interation_count', type: 'BIGINT UNSIGNED', remarks: 'interation count')
            column(name: 'interation_weeks', type: 'BIGINT UNSIGNED', remarks: 'interation weeks')
            column(name: 'pi_count', type: 'BIGINT UNSIGNED', remarks: 'pi count')
            column(name: 'status_code', type: 'VARCHAR(255)', remarks: 'status code')
            column(name: 'sprint_complete_setting', type: 'VARCHAR(255)', remarks: 'sprint complete setting')
            column(name: 'program_id', type: 'BIGINT UNSIGNED', remarks: 'program id')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}