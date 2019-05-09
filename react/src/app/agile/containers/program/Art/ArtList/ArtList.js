import React, { Component } from 'react';
import {
  Page, Header, Content, stores, 
} from 'choerodon-front-boot';
import { Button, Spin, Icon } from 'choerodon-ui';
import moment from 'moment';
import { editArtLink } from '../../../../common/utils';
import { ArtTable, CreateArt } from './components';
import {
  getArtsByProjectId, createArt,
} from '../../../../api/ArtApi';

function formatter(values) {
  const data = { ...values };
  Object.keys(data).forEach((key) => {
    if (moment.isMoment(data[key])) {
      data[key] = moment(data[key]).format('YYYY-MM-DD HH:mm:ss');
    }
  });
  return data;
}
const { AppState } = stores;
class ArtList extends Component {
  state = {
    loading: true,
    data: [],
    createDisabled: true,
    createArtVisible: false,
    createArtLoading: false,
  }

  componentDidMount() {
    this.loadArts();
  }

  loadArts = () => {
    this.setState({
      loading: true,
    });
    getArtsByProjectId().then((res) => {
      this.setState({
        loading: false,
        data: res.content,
        createDisabled: res.content && res.content.length > 0,
      });
    });
  }

  handleCreateArtClick = () => {
    this.setState({
      createArtVisible: true,
    });
  }

  handleCreateArt = (data) => {
    const projectId = AppState.currentMenuType.id;
    const Data = { ...formatter(data), programId: projectId, piCount: 3 };
    // console.log(Data);
    this.setState({
      createArtLoading: true,
    });
    createArt(Data).then(() => {
      this.setState({
        createArtVisible: false,
        createArtLoading: false,
      });
      this.loadArts();
    }).catch(() => {
      this.setState({
        createArtVisible: false,
        createArtLoading: false,
      });
    });
  }

  handleArtCreateCancel = () => {
    this.setState({
      createArtVisible: false,
    });
  }

  handleEditArtClick = (record) => {
    const { id: artId } = record;
    const { history } = this.props;
    history.push(editArtLink(artId));
  }

  handlePaginationChange = (pagination) => {
    this.loadArts(pagination);
  }

  render() {
    const {
      // eslint-disable-next-line no-unused-vars
      data, createArtVisible, createArtLoading, loading, createDisabled,
    } = this.state;
    return (
      <Page className="c7ntest-Issue c7ntest-region">
        <Header
          title="ART列表"
        >
          {/* <Button icon="playlist_add" onClick={this.handleCreateArtClick} disabled={createDisabled}> */}
          <Button icon="playlist_add" onClick={this.handleCreateArtClick}>
            {'创建ART'}
          </Button>
          <Button funcType="flat" onClick={this.loadArts}>
            <Icon type="refresh icon" />
            <span>刷新</span>
          </Button>
        </Header>
        <Content>
          <Spin spinning={loading}>
            <CreateArt
              visible={createArtVisible}
              loading={createArtLoading}
              onCancel={this.handleArtCreateCancel}
              onSubmit={this.handleCreateArt}
            />
            <ArtTable         
              dataSource={data}
              onEditArtClick={this.handleEditArtClick}
            />
          </Spin>
        </Content>
      </Page>
    );
  }
}


export default ArtList;
