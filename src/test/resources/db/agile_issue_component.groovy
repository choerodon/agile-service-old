package db
databaseChangeLog(logicalFilePath:'agile_issue_component.groovyoovy') {
    changeSet(id: '2018-05-14-agile-issue-component', author: 'fuqianghuang01@gmail.com') {
        createTable(tableName: "agile_issue_component") {
            column(name: 'component_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id') {
                constraints(nullable: false)
            }
            column(name: 'name', type: 'VARCHAR(255)', remarks: 'name') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(5000)', remarks: 'description')
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
    changeSet(id:  '2018-05-31-agile-issue-component-add-column', author: 'fuqianghuang01@gmail.com') {
        addColumn(tableName: 'agile_issue_component') {
            column(name: 'manager_id', type: 'BIGINT UNSIGNED', remarks: 'manager id')
            column(name: 'default_assignee_role', type: 'VARCHAR(255)', remarks: 'default assignee role')
        }
    }
}