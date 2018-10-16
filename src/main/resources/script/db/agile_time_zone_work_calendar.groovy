package script.db

/**
 *
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
databaseChangeLog(logicalFilePath: 'script/db/agile_time_zone_work_calendar.groovy') {
    changeSet(id: '2018-05-14-agile-time-zone-work-calendar', author: 'dinghuang123@gmail.com') {
        createTable(tableName: "agile_time_zone_work_calendar") {
            column(name: 'time_zone_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键') {
                constraints(primaryKey: true)
            }
            column(name: 'area_code', type: 'VARCHAR(100)', remarks: '地区code') {
                constraints(nullable: false)
            }
            column(name: 'time_zone_code', type: 'VARCHAR(100)', remarks: '时区code') {
                constraints(nullable: false)
            }
            column(name: 'work_type_code', type: 'VARCHAR(10)', remarks: '工作类型', defaultValue: 'none') {
                constraints(nullable: false)
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: false)
            }
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(indexName: 'uk_organization_id', tableName: 'agile_time_zone_work_calendar', unique: true) {
            column(name: 'organization_id')
        }
    }
    changeSet(id: '2018-10-16-agile-time-zone-work-calendar', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'agile_time_zone_work_calendar') {
            column(name: 'use_holiday', type: 'tinyint(1)', remarks: '是否包含节假日', defaultValue: '0') {
                constraints(nullable: false)
            }
            column(name: 'saturday_work', type: 'tinyint(1)', remarks: '是否包含周六', defaultValue: '0') {
                constraints(nullable: false)
            }
            column(name: 'sunday_work', type: 'tinyint(1)', remarks: '是否包含周日', defaultValue: '0') {
                constraints(nullable: false)
            }
        }
        dropColumn(tableName: 'agile_time_zone_work_calendar') {
            column(name: 'work_type_code')
        }
    }
}