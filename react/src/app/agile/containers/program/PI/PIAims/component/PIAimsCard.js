import React from 'react';
import PropTypes from 'prop-types';
import { Divider } from 'choerodon-ui';
import _ from 'lodash';
import './PIAimsCard.scss';

const propTypes = {
  aimsCategory: PropTypes.oneOf(['team', 'program']).isRequired,
  piName: PropTypes.string.isRequired,
  aimsInfo: PropTypes.array.isRequired,
  stretchAimsInfo: PropTypes.array,
};

const Line = (({ dot = true, data, style }) => (
  <div style={{ display: 'flex', margin: '5px 0' }}>
    <div style={{
      flex: 1,
      overflow: 'hidden',
      textOverflow: 'ellipsis', 
      display: 'flex',
      alignItems: 'center',
      ...style, 
    }}
    >
      {dot && (
      <div style={{ 
        width: 5, height: 5, background: '#00BFA5', borderRadius: '50%', marginRight: 10,
      }}
      />
      )}
      <div style={{
        flex: 1, 
        overflow: 'hidden',
        textOverflow: 'ellipsis', 
      }}
      >
        {data[0]}
      </div>
      
    </div>
    <div style={{ width: 50 }}>{data[1] || '-'}</div>
    <div style={{ width: 50 }}>{data[2] || '-'}</div>
  </div>
));
const ProjectCard = ({ team }) => {
  const { name, piAims } = team;
  const aims = piAims.map((aim) => {
    const { name: PIname, planBv, actualBv } = aim;
    return <Line data={[PIname, planBv, actualBv]} />;
  });
  return (
    <div style={{
      border: '1px solid #CCCCCC',
      borderRadius: 2,
      borderTop: '5px solid #00BFA5',
      margin: 10,
      padding: 10,
      width: 362,     
    }}
    >
      <Line data={[`${name}的PI目标`, '计划BV', '实际BV']} style={{ fontWeight: 500 }} dot={false} />
      {aims}
    </div>
  );
};
const PIAimsCard = ({
  aimsCategory, piName, aimsInfo, stretchAimsInfo, teams,
}) => {
  const totalPlanBv = (aimsCategory === 'program' && aimsInfo && _.reduce(_.map(aimsInfo, 'planBv'), (sum, n) => sum + n, 0)) || '-';
  const totalActualBv = (aimsCategory === 'program' && aimsInfo && stretchAimsInfo && _.reduce(_.map(aimsInfo, 'actualBv'), (sum, n) => sum + n, 0) + _.reduce(_.map(stretchAimsInfo, 'actualBv'), (sum, n) => sum + n, 0)) || '-';
  // eslint-disable-next-line no-nested-ternary
  const percent = Number.isInteger(totalPlanBv) ? (Number.isInteger(totalPlanBv) && Number.isInteger(totalActualBv) ? `${(totalActualBv / totalPlanBv * 100).toFixed(2)}%` : '0%') : '-';
 
  const groups = [];
  const colomns = 3;

  // 把一个数组分割成 n 个一组
  for (let i = 0, len = teams.length; i < len; i += colomns) {
    groups.push(teams.slice(i, i + colomns));
  }

  return (
    <div>
      <div className="c7n-pi-card" style={{ borderTop: `5px solid ${aimsCategory === 'program' ? '#4D90FE' : '#00BFA5'}`, margin: 'auto' }}>
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
      {groups.length > 0 && (
        <div className="line-container">      
          {Array(Math.min(teams.length, 3)).fill(0).map(team => <div className="line" />)}
          <div className="line" />
        </div>
      )}
      {groups.map((group, index) => (
        <div style={{
          display: 'flex', marginTop: 10, justifyContent: 'center',
        }}
        >
          {group.map(team => <ProjectCard team={team} />)}
          {group.length >= colomns ? null : Array(colomns - group.length).fill(0).map(() => (
            <div style={{      
              margin: 10,
              padding: 10,
              width: 362,     
            }}
            />
          ))}
        </div>
      ))}
    </div>
  );
};

PIAimsCard.propTypes = propTypes;

export default PIAimsCard;
