package db

databaseChangeLog(logicalFilePath: 'script/db/agile_product_version.groovy') {
    changeSet(id: '2018-05-14-agile-product-version', author: 'jian_zhang02@163.com') {
        createTable(tableName: "agile_product_version") {
            column(name: 'version_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'version_id') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(255)', remarks: 'name') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(5000)', remarks: 'description')
            column(name: 'start_date', type: 'DATETIME', remarks: 'start date')
            column(name: 'release_date', type: 'DATETIME', remarks: 'release date')
            column(name: 'status_code', type: 'VARCHAR(255)', remarks: 'status id') {
                constraints(nullable: false)
            }
            column(name: 'process', type: 'DECIMAL', remarks: 'process', defaultValue: "0")

            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id') {
                constraints(nullable: false)
            }
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2018-05-24-add-column', author: 'jian_zhang02@163.com') {
        addColumn(tableName: 'agile_product_version') {
            column(name: 'archived_date', type: 'DATETIME', remarks: 'archived date')
        }
    }

    changeSet(id: '2018-06-06-drop-column', author: 'jian_zhang02@163.com') {
        dropColumn(tableName: 'agile_product_version') {
            column(name: 'process')
        }
    }

    changeSet(id: '2018-06-20-add-column', author: 'jian_zhang02@163.com') {
        addColumn(tableName: 'agile_product_version') {
            column(name: 'old_status_code', type: 'VARCHAR(255)', remarks: 'old status code')
        }
    }

    changeSet(id: '2018-07-26-add-column', author: 'dinghuang123@gmail.com') {
        addColumn(tableName: 'agile_product_version') {
            column(name: 'sequence', type: 'int', remarks: '排序字段', defaultValue: "0")
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "update agile_product_version set sequence = version_id;"
        }
    }

    changeSet(id: '2018-12-03-version-add-column', author: 'fuqianghuang01@gmail.com') {
        addColumn(tableName: 'agile_product_version') {
            column(name: 'expect_release_date', type: 'DATETIME', remarks: 'expect release date')
        }
    }
}