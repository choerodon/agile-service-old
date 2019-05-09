import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { stores } from 'choerodon-front-boot';
import { Modal, Radio } from 'choerodon-ui';
import FileSaver from 'file-saver';
import { exportFeatures } from '../../../../../api/FeatureApi';

const RadioGroup = Radio.Group;
const { AppState } = stores;
const radioStyle = {
  display: 'block',
  height: '30px',
  lineHeight: '30px',
};
const propTypes = {
  searchDTO: PropTypes.shape({}).isRequired,
  tableShowColumns: PropTypes.arrayOf(PropTypes.string).isRequired,
  onCancel: PropTypes.func.isRequired,
};
class ExportIssue extends PureComponent {
  state = {
    mode: 'all',
  }

  handleExportChange = (e) => {
    this.setState({
      mode: e.target.value,
    });
  }

  /**
   * 输出 excel
   */
  exportExcel = () => {
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    const { searchDTO, tableShowColumns, onCancel } = this.props;
    const { mode } = this.state;
    const exportFieldCodes = mode === 'all' ? [] : this.getExportFieldCodes(tableShowColumns);
    const search = {
      ...searchDTO,
      exportFieldCodes,
    };
    exportFeatures(search)
      .then((data) => {
        const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const fileName = `${AppState.currentMenuType.name}.xlsx`;
        FileSaver.saveAs(blob, fileName);
        Choerodon.prompt('导出成功');        
        onCancel();
      });
  };

  getExportFieldCodes=(tableShowColumns) => {
    const transform = {
      issueNum: 'issueNum',
      summary: 'summary',
      featureType: 'typeName',
      assignee: 'assigneeName',
      reporter: 'reporterName',
      statusList: 'statusName',
      sprint: 'sprintName',
      creationDate: 'creationDate',
      lastUpdateDate: 'lastUpdateDate',
      priorityId: 'priorityName',
      version: 'versionName',  
      label: 'labelName',
      storyPoints: 'storyPoints',
      component: 'componentName',
      epicList: 'epicName',
      piList: 'piName',
      benfitHypothesis: 'benfitHypothesis',
      acceptanceCritera: 'acceptanceCritera',
    };

    return tableShowColumns.map(key => transform[key]);
  }

  render() {
    const { mode } = this.state;
    const { visible, onCancel } = this.props;
    const projectName = AppState.currentMenuType.name;
    return (
      <Modal
        title="问题列表导出确认"
        visible={visible}
        onOk={this.exportExcel}
        onCancel={onCancel}
      >
        <div style={{ margin: '10px 0' }}>
          您正在导出
          {' '}
          <span style={{ fontWeight: 500 }}>{projectName}</span> 
          {' '}       
          的问题，请选择你需要导出的字段
        </div>
        <RadioGroup onChange={this.handleExportChange} value={mode}>
          <Radio style={radioStyle} value="show">当前页面显示字段</Radio>
          <Radio style={radioStyle} value="all">全部字段</Radio>
        </RadioGroup>
      </Modal>
    );
  }
}

ExportIssue.propTypes = propTypes;

export default ExportIssue;
