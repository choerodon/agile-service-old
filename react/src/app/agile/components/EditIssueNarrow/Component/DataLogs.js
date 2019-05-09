import React, { Component } from 'react';
import { Icon, Popconfirm, Button } from 'choerodon-ui';
import { AppState } from 'choerodon-front-boot';
import _ from 'lodash';
import UserHead from '../../UserHead';
import WYSIWYGEditor from '../../WYSIWYGEditor';
import { IssueDescription } from '../../CommonComponent';
import {
  delta2Html, text2Delta, beforeTextUpload, formatDate, 
} from '../../../common/utils';
import { deleteWorklog, updateWorklog } from '../../../api/NewIssueApi';
import DataLog from './DataLog';

class DataLogs extends Component {
  constructor(props, context) {
    super(props, context);
    this.state = {
      user: {},
      expand: false,
    };
  }

  componentDidMount() {
  }

  setUser(user) {
    this.setState({
      user,
    });
  }

  render() {
    const {
      datalogs, typeCode, createdById, creationDate, 
    } = this.props;
    return (
      <div>
        {
          datalogs.map((datalog, i) => (
            <DataLog
              i={i}
              key={datalog.logId}
              datalog={datalog}
              typeCode={typeCode}
              origin={datalogs}
              expand={this.state.expand}
              user={this.state.user}
              callback={this.setUser.bind(this)}
            />
          ))
          // <div className="createDataLog">
          //      <UserHead
          //         user={{
          //           id: createdById,
          //           loginName: '',
          //           realName: datalog.name,
          //           avatar: datalog.imageUrl,
          //         }}
          //         hiddenText
          //         type="datalog"
          //       />
          // </div>
        }
        {
          datalogs.length > 5 && !this.state.expand ? (
            <div style={{ marginTop: 5 }}>
              <Button className="leftBtn" funcType="flat" onClick={() => this.setState({ expand: true })}>
                <Icon type="baseline-arrow_drop_down icon" style={{ marginRight: 2 }} />
                <span>展开</span>
              </Button>
            </div>
          ) : null
        }
        {
          datalogs.length > 5 && this.state.expand ? (
            <div style={{ marginTop: 5 }}>
              <Button className="leftBtn" funcType="flat" onClick={() => this.setState({ expand: false })}>
                <Icon type="baseline-arrow_drop_up icon" style={{ marginRight: 2 }} />
                <span>折叠</span>
              </Button>
            </div>
          ) : null
        }
      </div>
    );
  }
}

export default DataLogs;
