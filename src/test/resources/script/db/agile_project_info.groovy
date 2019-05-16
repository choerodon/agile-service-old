package script.db

databaseChangeLog(logicalFilePath: 'script/db/agile_project_info.groovy') {
    changeSet(id: '2018-05-30-agile-project_info', author: 'dinghuang123@gmail.com') {
        createTable(tableName: "agile_project_info", remarks: '项目issue编号基准表') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id')
            column(name: 'project_code', type: 'VARCHAR(255)', remarks: '项目code')
            column(name: 'issue_max_num', type: 'BIGINT UNSIGNED', remarks: 'issue编号最大值')
        }
    }

    changeSet(id: '2018-06-01-agile-project-info', author: 'dinghuang123@gmail.com') {
        renameColumn(columnDataType: 'BIGINT UNSIGNED', newColumnName: 'info_id', oldColumnName: 'id', remarks: '主键', tableName: 'agile_project_info')
        addAutoIncrement(schemaName: '', tableName: 'agile_project_info', columnName: 'info_id', columnDataType: 'BIGINT UNSIGNED')
    }

    changeSet(id: '2018-06-15-agile-project-info', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'agile_project_info') {
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2018-07-03-agile-project-info-add-column', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'agile_project_info') {
            column(name: "default_assignee_id", type: "BIGINT UNSIGNED")
            column(name: "default_assignee_type", type: "VARCHAR(32)")
            column(name: "default_priority_code", type: "VARCHAR(32)")
        }
    }

}