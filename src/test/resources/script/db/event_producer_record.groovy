package script.db

databaseChangeLog(logicalFilePath: 'event_producer_record.groovy') {
    changeSet(id: '2018-07-09-add-table-event-producer-record', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "event_producer_record") {
            column(name: 'uuid', type: 'VARCHAR(50)', autoIncrement: false, remarks: 'uuid') {
                constraints(primaryKey: true)
            }
            column(name: 'type', type: 'VARCHAR(50)', remarks: '业务类型') {
                constraints(nullable: false)
            }
            column(name: 'create_time', type: 'DATETIME', remarks: '创建时间')
        }
    }
}