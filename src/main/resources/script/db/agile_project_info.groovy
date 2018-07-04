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

    changeSet(id: '2018-06-25-agile-issue-link-type-init-data', author: 'dinghuang123@gmail.com') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "INSERT INTO agile_issue_link_type ( link_name, in_ward, out_ward, project_id ) SELECT 'Blocks','is blocked by','blocks',agile_project_info.project_id FROM agile_project_info;\n" +
                    "INSERT INTO agile_issue_link_type ( link_name, in_ward, out_ward, project_id ) SELECT 'Clones','is cloned by','clones',agile_project_info.project_id FROM agile_project_info;\n" +
                    "INSERT INTO agile_issue_link_type ( link_name, in_ward, out_ward, project_id ) SELECT 'Duplicate','is duplicated by','duplicates',agile_project_info.project_id FROM agile_project_info;\n" +
                    "INSERT INTO agile_issue_link_type ( link_name, in_ward, out_ward, project_id ) SELECT 'Relates','relates to','relates to',agile_project_info.project_id FROM agile_project_info;"
        }
    }

    changeSet(id: '2018-06-27-agile-issue-link-type-init-data-update', author: 'dinghuang123@gmail.com') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "UPDATE agile_issue_link_type set link_name = '阻塞',in_ward = '被阻塞',out_ward = '阻塞' where link_name = 'Blocks';\n" +
                    "UPDATE agile_issue_link_type set link_name = '复制',in_ward = '被复制',out_ward = '复制' where link_name = 'Duplicate';\n" +
                    "UPDATE agile_issue_link_type set link_name = '关联',in_ward = '关联',out_ward = '关联' where link_name = 'Relates';\n" +
                    "DELETE FROM agile_issue_link_type where link_name = 'Clones';"
        }
    }

}