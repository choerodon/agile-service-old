package script.db

/**
 *
 * @author dinghuang123@gmail.com
 * @since 2018/10/10
 */
databaseChangeLog(logicalFilePath: 'script/db/agile_time_zone_work_calendar_ref.groovy') {
    changeSet(id: '2018-10-15-agile-time-zone-work-calendar-ref', author: 'dinghuang123@gmail.com') {
        createTable(tableName: "agile_time_zone_work_calendar_ref") {
            column(name: 'calendar_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键') {
                constraints(primaryKey: true)
            }
            column(name: 'time_zone_id', type: 'BIGINT UNSIGNED', remarks: '时区id') {
                constraints(nullable: false)
            }
            column(name: 'work_day', type: 'VARCHAR(11)', remarks: '加班日期') {
                constraints(nullable: false)
            }
            column(name: 'year', type: 'int(5)', remarks: '年份') {
                constraints(nullable: false)
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: false)
            }
            column(name: 'status', type: 'tinyint(1)', remarks: '状态，0为放假，1为补班', defaultValue: '0') {
                constraints(nullable: false)
            }
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "agile_time_zone_work_calendar_ref", indexName: "idx_time_zone_id") {
            column(name: "time_zone_id")
        }
        createIndex(tableName: "agile_time_zone_work_calendar_ref", indexName: "idx_organization_id") {
            column(name: "organization_id")
        }
    }
}