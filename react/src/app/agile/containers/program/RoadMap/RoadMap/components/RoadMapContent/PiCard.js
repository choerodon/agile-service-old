import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';
import { groupBy } from 'lodash';
import { Tooltip } from 'choerodon-ui';
import FeatureItem from './FeatureItem';
import NoFeature from '../NoFeature';
import { STATUS } from '../../../../../../common/Constant';
import './PiCard.scss';

class PiCard extends Component {
  renderFeatures = () => {
    const { pi, onFeatureClick, currentFeature } = this.props;
    const { subFeatureDTOList } = pi;
    if (subFeatureDTOList.length === 0) {
      return <NoFeature />;
    }
    const groupedFeatures = groupBy(subFeatureDTOList, 'featureType');
    const enablerFeatures = groupedFeatures.enabler || [];
    const businessFeatures = groupedFeatures.business || [];
    return (
      <Fragment>
        <div className="c7nagile-PiCard-business-list">
          {businessFeatures.map(feature => <FeatureItem selected={currentFeature === feature.issueId} feature={feature} onFeatureClick={onFeatureClick} />)}
        </div>
        {
          enablerFeatures.length > 0
          && (
            <div className="c7nagile-PiCard-enabler-list">
              {enablerFeatures.map(feature => <FeatureItem selected={currentFeature === feature.issueId} feature={feature} onFeatureClick={onFeatureClick} />)}
            </div>
          )
        }
      </Fragment>
    );
  }

  render() {
    const { pi } = this.props;
    const {
      statusCode, name, startDate, endDate,
    } = pi;
    return (
      <div
        className="c7nagile-PiCard"
        style={{ borderTop: `5px solid ${STATUS[statusCode]}` }}
      >
        <Tooltip
          title={(
            <div>
              <div>
              开始时间：
                {moment(startDate).format('YYYY-MM-DD')}
              </div>
              <div>
              结束时间：
                {moment(endDate).format('YYYY-MM-DD')}
              </div>
            </div>
        )}
          placement="topLeft"
        >
          <div className="c7nagile-PiCard-title">{name}</div>
        </Tooltip>

        <div>
          {this.renderFeatures()}
        </div>
      </div>
    );
  }
}

PiCard.propTypes = {

};

export default PiCard;
