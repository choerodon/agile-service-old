package script.db
databaseChangeLog(logicalFilePath:'agile_message.groovy') {
    changeSet(id: '2018-08-14-agile-message', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_message") {
            column(name: 'event', type: 'VARCHAR(255)', remarks: 'event') {
                constraints(nullable: false)
            }
            column(name: 'notice_type', type: 'VARCHAR(255)', remarks: 'notice type') {
                constraints(nullable: false)
            }
            column(name: 'notice_name', type: 'VARCHAR(255)', remarks: 'notice name') {
                constraints(nullable: false)
            }
            column(name: 'is_enable', type: 'TINYINT UNSIGNED', remarks: 'is enable') {
                constraints(nullable: false)
            }
            column(name: 'user', type: 'VARCHAR(1000)', remarks: 'users')


            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}