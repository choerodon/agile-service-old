package db

databaseChangeLog(logicalFilePath: 'event_consumer_record.groovy') {
    changeSet(id: '2018-05-22-add-table-event-consumer-record', author: 'HunagFuqiang') {
        createTable(tableName: "event_consumer_record") {
            column(name: 'uuid', type: 'VARCHAR(50)', autoIncrement: false, remarks: 'uuid') {
                constraints(primaryKey: true)
            }
            column(name: 'create_time', type: 'DATETIME', remarks: '创建时间')
        }
    }
}