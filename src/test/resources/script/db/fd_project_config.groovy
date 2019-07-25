package script.db


databaseChangeLog(logicalFilePath: 'fd_project_config.groovy') {
    changeSet(id: '2018-11-01-create-project-config', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: 'fd_project_config') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: '主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: 'false')
            }
            column(name: 'scheme_id', type: 'BIGINT UNSIGNED', remarks: '方案id') {
                constraints(nullable: 'false')
            }
            column(name: 'scheme_type', type: 'VARCHAR(30)', remarks: '方案类型') {
                constraints(nullable: 'true')
            }
            column(name: 'apply_type', type: 'VARCHAR(30)', remarks: '应用类型') {
                constraints(nullable: 'true')
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "fd_project_config", indexName: "project_config_n1") {
            column(name: "project_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "fd_project_config", indexName: "project_config_n2") {
            column(name: "scheme_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "fd_project_config", indexName: "project_config_n3") {
            column(name: "scheme_type", type: "VARCHAR(30)")
        }
        createIndex(tableName: "fd_project_config", indexName: "project_config_n4") {
            column(name: "apply_type", type: "VARCHAR(30)")
        }
    }
}