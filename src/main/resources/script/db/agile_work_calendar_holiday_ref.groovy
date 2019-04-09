package script.db

/**
 *
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
databaseChangeLog(logicalFilePath: 'script/db/agile_work_calendar_holiday_ref.groovy') {
    changeSet(id: '2018-10-14-agile-work-calendar-holiday-ref', author: 'dinghuang123@gmail.com') {
        createTable(tableName: "agile_work_calendar_holiday_ref") {
            column(name: 'calendar_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(30)', remarks: '节假日名称')
            column(name: 'holiday', type: 'VARCHAR(11)', remarks: '日期') {
                constraints(nullable: false)
            }
            column(name: 'status', type: 'tinyint(1)', remarks: '状态，0为放假，1为补班') {
                constraints(nullable: false)
            }
            column(name: 'year', type: 'int(5)', remarks: '年份') {
                constraints(nullable: false)
            }
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(indexName: 'uk_holiday', tableName: 'agile_work_calendar_holiday_ref', unique: true) {
            column(name: 'holiday')
        }
    }
    changeSet(id: '2019-03-15-delete-5.1-dirty-date-data', author: 'shinan.chenX@gmail.com') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "DELETE FROM agile_work_calendar_holiday_ref WHERE holiday = '2019-4-27';" +
                    "DELETE FROM agile_work_calendar_holiday_ref WHERE holiday = '2019-4-28';" +
                    "DELETE FROM agile_work_calendar_holiday_ref WHERE holiday = '2019-4-29';" +
                    "DELETE FROM agile_work_calendar_holiday_ref WHERE holiday = '2019-4-30';"
        }
    }
    changeSet(id: '2019-04-09-delete-5.1-dirty-date-data', author: 'shinan.chenX@gmail.com') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "INSERT INTO agile_work_calendar_holiday_ref(name,holiday,status,year) " +
            "values (null,'2019-4-28',1,2019)," +
                    "(null,'2019-5-2',0,2019)," +
                    "(null,'2019-5-3',0,2019)," +
                    "(null,'2019-5-4',0,2019)," +
                    "(null,'2019-5-5',1,2019);"
        }
    }
}