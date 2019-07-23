package script.db


databaseChangeLog(logicalFilePath: 'fd_issue_type.groovy') {
    changeSet(id: '2018-08-08-create-issue-type', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: 'fd_issue_type') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称') {
                constraints(nullable: 'false')
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')

            column(name: 'icon', type: 'VARCHAR(64)', remarks: '图标')

            column(name: 'colour', type: 'VARCHAR(20)', remarks: '颜色') {
                constraints(nullable: 'false')
            }

            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: 'false')
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "fd_issue_type", indexName: "issue_type_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "fd_issue_type", indexName: "issue_type_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }
        createIndex(tableName: "fd_issue_type", indexName: "issue_type_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }

    changeSet(id: '2018-10-24-add-column', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'fd_issue_type') {
            column(name: 'type_code', type: 'VARCHAR(50)', remarks: '类型', defaultValue: "custom") {
                constraints(nullable: 'false')
            }
        }
        addColumn(tableName: 'fd_issue_type') {
            column(name: 'is_initialize', type: 'TINYINT UNSIGNED', remarks: '是否默认初始化的类型', defaultValue: "0") {
                constraints(nullable: 'false')
            }
        }
        createIndex(tableName: 'fd_issue_type', indexName: 'uk_type_code', unique: true) {
            column(name: 'type_code')
            column(name: 'organization_id')
        }
    }
    changeSet(id: '2018-12-07-fix-add-default-issue-auto-test', author: 'shinan.chenX@gmail') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "insert into fd_issue_type(name,description,colour,icon,type_code,is_initialize,organization_id) " +
                    "select '自动化测试' as name,'自动化测试' as description,'#00BFA5' as colour,'auto_test' as icon,'issue_auto_test' as type_code,'1' as is_initialize, organization_id " +
                    "from fd_issue_type where type_code='issue_test'"
        }
    }
    changeSet(id: '2018-12-07-fix-update-issue-test', author: 'shinan.chenX@gmail') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "update fd_issue_type set colour = '#4D90FE', icon = 'table_chart' where type_code = 'issue_test'"
        }
    }
    changeSet(id: '2019-03-20-fix-update-icon-and-colour', author: 'shinan.chenX@gmail') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "update fd_issue_type set colour = '#4D90FE', icon = 'test-case' where type_code = 'issue_test';"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "update fd_issue_type set colour = '#FA8C16', icon = 'test-automation' where type_code = 'issue_auto_test';"
        }
    }
    changeSet(id: '2019-03-12-fix-add-default-feature', author: 'shinan.chenX@gmail') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "insert into fd_issue_type(name,description,colour,icon,type_code,is_initialize,organization_id) " +
                    "select '特性' as name,'特性' as description,'#29B6F6' as colour,'agile-feature' as icon,'feature' as type_code,'1' as is_initialize, organization_id " +
                    "from fd_issue_type where type_code='issue_test'"
        }
    }
    changeSet(id: '2019-07-15-fix-update-colour', author: 'shinan.chenX@gmail') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "update fd_issue_type set colour = '#3D5AFE' where type_code = 'feature';"
        }
    }
}