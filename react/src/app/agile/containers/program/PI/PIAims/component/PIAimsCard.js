import React from 'react';
import PropTypes from 'prop-types';
import { Divider } from 'choerodon-ui';
import _ from 'lodash';
import './PIAimsCard.scss';

const propTypes = {
  aimsCategory: PropTypes.oneOf(['team', 'program']).isRequired,
  piName: PropTypes.string.isRequired,
  // eslint-disable-next-line react/forbid-prop-types
  aimsInfo: PropTypes.array.isRequired,
  // eslint-disable-next-line react/forbid-prop-types
  stretchAimsInfo: PropTypes.array,
};
const PIAimsCard = ({ 
  aimsCategory, piName, aimsInfo, stretchAimsInfo, 
}) => {
  const totalPlanBv = (aimsCategory === 'program' && aimsInfo && _.reduce(_.map(aimsInfo, 'planBv'), (sum, n) => sum + n, 0)) || '-';
  const totalActualBv = (aimsCategory === 'program' && aimsInfo && stretchAimsInfo && _.reduce(_.map(aimsInfo, 'actualBv'), (sum, n) => sum + n, 0) + _.reduce(_.map(stretchAimsInfo, 'actualBv'), (sum, n) => sum + n, 0)) || '-';
  // eslint-disable-next-line no-nested-ternary
  const percent = Number.isInteger(totalPlanBv) ? (Number.isInteger(totalPlanBv) && Number.isInteger(totalActualBv) ? `${(totalActualBv / totalPlanBv * 100).toFixed(2)}%` : '0%') : '-';
  return (
    <div style={{ display: 'flex', justifyContent: 'center' }}>
      <div className="c7n-pi-card" style={{ borderTop: `5px solid ${aimsCategory === 'program' ? '#4D90FE' : '#00BFA5'}` }}>
        <table style={{ width: '100%' }}>
          <thead>
            <tr>
              <th className="th" style={{ width: '60%' }}>PI目标</th>
              <th style={{ width: '20%' }}>计划BV</th>
              <th style={{ width: '20%' }}>实际BV</th>
            </tr>
          </thead>
          <tbody>
            {
              aimsInfo && aimsInfo.length > 0 && aimsInfo.map(item => (
                <tr key={item.id}>
                  <td valign="top" style={{ display: 'flex' }}>
                    <span style={{
                      display: 'flex', flexShrink: 0, marginTop: 8, marginRight: 10, width: 5, height: 5, borderRadius: '50%', background: `${aimsCategory === 'program' ? '#4D90FE' : '#00BFA5'}`, 
                    }}
                    />
                    <div style={{ overflow: 'hidden', textOverflow: 'ellipsis' }}>{item.name}</div>                    
                  </td>
                  <td valign="top">{item.planBv ? item.planBv : '-'}</td>
                  <td valign="top">{item.actualBv ? item.actualBv : '-'}</td>
                </tr>
              ))
            }
          </tbody>
        </table>
        {
          aimsCategory === 'program' && (<Divider style={{ margin: '7px 0 15px' }} />)
        }
        {
            aimsCategory === 'program' && (
              <div>
                <table style={{ width: '100%' }}>
                  <thead>
                    <tr>
                      <th style={{ width: '60%' }}>延伸目标</th>
                      <th style={{ width: '20%' }} />
                      <th style={{ width: '20%' }} />
                    </tr>
                  </thead>
                  <tbody>
                    {
                      stretchAimsInfo && stretchAimsInfo.length > 0 && stretchAimsInfo.map(item => (
                        <tr>
                          <td valign="top" style={{ display: 'flex' }}>
                            <span style={{
                              display: 'flex', flexShrink: 0, marginTop: 8, marginRight: 10, width: 5, height: 5, borderRadius: '50%', background: '#9B9B9B', 
                            }}
                            />
                            <div style={{ overflow: 'hidden', textOverflow: 'ellipsis' }}>{item.name}</div> 
                          </td>
                          <td valign="top">{item.planBv ? item.planBv : '-'}</td>
                          <td valign="top">{item.actualBv ? item.actualBv : '-'}</td>
                        </tr>
                      ))
                    }
                  </tbody>
                </table>
              </div>
            )
          }

        {
          aimsCategory === 'program' && (
          <div>
            <Divider style={{ margin: '7px 0 15px' }} />
            <table style={{ width: '100%' }}>
              <tbody>
                <tr>
                  <td valign="top" style={{ width: '60%' }}>统计</td>
                  <td valign="top" style={{ width: '20%' }}>{totalPlanBv}</td>
                  <td valign="top" style={{ width: '20%' }}>{totalActualBv}</td>
                </tr>
                <tr>
                  <td />
                  <td valign="top" style={{ overflow: 'visible' }}>{percent}</td>
                  <td />
                </tr>
              </tbody>
            </table>
          </div>
          )
        }
      </div>
    </div>
  );
};

PIAimsCard.propTypes = propTypes;

export default PIAimsCard;
