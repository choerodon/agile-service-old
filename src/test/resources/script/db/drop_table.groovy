package script.db


databaseChangeLog(logicalFilePath: 'drop_table.groovy') {
    changeSet(id: '2019-07-24-delete-abandon-table', author: 'shinan.chenX@gmail') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "DROP TABLE IF EXISTS agile_lookup_type;" +
            "DROP TABLE IF EXISTS agile_lookup_value;" +
            "DROP TABLE IF EXISTS event_consumer_record;" +
            "DROP TABLE IF EXISTS event_producer_record;" +
            "DROP TABLE IF EXISTS saga_task_instance_record"
        }
    }
}