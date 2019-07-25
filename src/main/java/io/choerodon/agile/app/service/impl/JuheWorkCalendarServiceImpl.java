package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.app.service.WorkCalendarService;
import io.choerodon.agile.infra.dataobject.WorkCalendarHolidayRefDTO;
import io.choerodon.agile.infra.config.WorkCalendarHolidayProperties;
import io.choerodon.agile.infra.scheduled.WorkCalendarHolidayRefJobs;
import io.choerodon.agile.infra.utils.DateUtil;
import io.choerodon.agile.infra.mapper.WorkCalendarHolidayRefMapper;
import io.choerodon.core.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/10
 */
@Transactional(rollbackFor = Exception.class)
public class JuheWorkCalendarServiceImpl implements WorkCalendarService {

    private static final int DEF_CONN_TIMEOUT = 30000;
    private static final int DEF_READ_TIMEOUT = 30000;
    private static final int MIN_YEAR = 2010;
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DEF_CHAT_SET = "UTF-8";
    private static final String ERROR_CODE = "error_code";
    private static final String URL = "http://v.juhe.cn/calendar/month";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
    private static final String INSERT_ERROR = "error.WorkCalendarHolidayRef.create";


    private static final Logger LOGGER = LoggerFactory.getLogger(WorkCalendarHolidayRefJobs.class);

    private WorkCalendarHolidayProperties workCalendarHolidayProperties;

    private WorkCalendarHolidayRefMapper workCalendarHolidayRefMapper;

//    private WorkCalendarHolidayRefRepository workCalendarHolidayRefRepository;

    public JuheWorkCalendarServiceImpl(WorkCalendarHolidayProperties workCalendarHolidayProperties, WorkCalendarHolidayRefMapper workCalendarHolidayRefMapper) {
        this.workCalendarHolidayProperties = workCalendarHolidayProperties;
        this.workCalendarHolidayRefMapper = workCalendarHolidayRefMapper;
//        this.workCalendarHolidayRefRepository = workCalendarHolidayRefRepository;
    }

    @Override
    public void updateWorkCalendarHolidayRef() {
        LOGGER.info("Prepare to start updating the annual holiday calendar");
        handleUpdateWorkCalendarHolidayRef(Calendar.getInstance().get(Calendar.YEAR), true);
    }

    @Override
    public void updateWorkCalendarHolidayRefByYear(Integer year) {
        handleUpdateWorkCalendarHolidayRef(year, false);
    }

    private void handleUpdateWorkCalendarHolidayRef(int y, Boolean automatic) {
        if (y < MIN_YEAR) {
            return;
        }
        if (automatic) {
            Integer year = workCalendarHolidayRefMapper.queryLastYear();
            if (year == null) {
                batchCreateWorkCalendarHolidayRef(y);
            } else {
                if (year != y) {
                    batchCreateWorkCalendarHolidayRef(y);
                }
            }
        } else {
            batchCreateWorkCalendarHolidayRef(y);
        }
    }

    private void batchCreateWorkCalendarHolidayRef(int y) {
        getRestDayByYear(y).forEach(workCalendarHolidayRef -> {
            WorkCalendarHolidayRefDTO query = new WorkCalendarHolidayRefDTO();
            query.setHoliday(workCalendarHolidayRef.getHoliday());
            if (workCalendarHolidayRefMapper.selectOne(query) == null) {
//                workCalendarHolidayRefRepository.create(workCalendarHolidayRef);
                if (workCalendarHolidayRefMapper.insert(workCalendarHolidayRef) != 1) {
                    throw new CommonException(INSERT_ERROR);
                }
            }
        });
    }

    /**
     * 获取万历年
     *
     * @param year year
     * @return List<WorkCalendarHolidayRefDTO>
     */
    @SuppressWarnings("unchecked")
    private List<WorkCalendarHolidayRefDTO> getRestDayByYear(int year) {
        Set<WorkCalendarHolidayRefDTO> workCalendarHolidayRefDTOS = new HashSet<>();
        for (int i = 1; i < 13; i++) {
            String result;
            Map params = new HashMap(2);
            params.put("key", workCalendarHolidayProperties.getApiKey());
            params.put("year-month", year + "-" + i);
            try {
                result = net(URL, params, GET);
            } catch (IOException e) {
                throw new CommonException("IOException{}", e);
            }
            JSONObject object = JSON.parseObject(result);
            if ((Integer) object.get(ERROR_CODE) == 0) {
                handleJsonToWorkCalendarHolidayRef(object, workCalendarHolidayRefDTOS);
            } else if ((Integer) object.get(ERROR_CODE) == 217701) {
                LOGGER.info("error_code:{},reason:{}", object.get(ERROR_CODE), object.get("reason"));
            } else {
                LOGGER.error("error_code:{},reason:{}", object.get(ERROR_CODE), object.get("reason"));
                break;
            }
        }
        return DateUtil.stringDateCompare().sortedCopy(workCalendarHolidayRefDTOS);
    }

    private void handleJsonToWorkCalendarHolidayRef(JSONObject object, Set<WorkCalendarHolidayRefDTO> workCalendarHolidayRefDTOS) {
        JSONObject result = JSON.parseObject(object.get("result").toString());
        JSONObject data = JSON.parseObject(result.get("data").toString());
        JSONArray holidayArray = JSON.parseArray(data.get("holiday_array").toString());
        for (int j = 0; j < holidayArray.size(); j++) {
            JSONObject jsonObject = holidayArray.getJSONObject(j);
            WorkCalendarHolidayRefDTO workCalendarHolidayRefDTO = new WorkCalendarHolidayRefDTO();
            workCalendarHolidayRefDTO.setName(jsonObject.get("name") == null ? null : jsonObject.get("name").toString());
            workCalendarHolidayRefDTO.setHoliday(jsonObject.get("festival").toString());
            workCalendarHolidayRefDTO.setStatus(0);
            workCalendarHolidayRefDTO.setYear(Integer.valueOf(workCalendarHolidayRefDTO.getHoliday().split("-")[0]));
            workCalendarHolidayRefDTOS.add(workCalendarHolidayRefDTO);
            List<DateStatus> list = JSON.parseArray(jsonObject.get("list").toString(), DateStatus.class).stream().filter(dateStatus -> !dateStatus.getDate().equals(workCalendarHolidayRefDTO.getHoliday())).collect(Collectors.toList());
            list.forEach(dateStatus -> {
                WorkCalendarHolidayRefDTO day = new WorkCalendarHolidayRefDTO();
                day.setHoliday(dateStatus.getDate());
                day.setYear(Integer.valueOf(day.getHoliday().split("-")[0]));
                //接口返回的数据中，补班是2，放假是1
                day.setStatus("1".equals(dateStatus.getStatus()) ? 0 : 1);
                workCalendarHolidayRefDTOS.add(day);
            });
        }
    }

    /**
     * 处理网络请求
     *
     * @param strUrl strUrl
     * @param params params
     * @param method method
     * @return String
     */
    @SuppressWarnings("unchecked")
    private static String net(String strUrl, Map params, String method) throws IOException {
        String rs;
        HttpURLConnection conn;
        StringBuilder sb = new StringBuilder();
        if (method == null || GET.equals(method)) {
            strUrl = strUrl + "?" + urlEncode(params);
        }
        URL url = new URL(strUrl);
        conn = (HttpURLConnection) url.openConnection();
        if (method == null || GET.equals(method)) {
            conn.setRequestMethod(GET);
        } else {
            conn.setRequestMethod(POST);
            conn.setDoOutput(true);
        }
        conn.setRequestProperty("User-agent", USER_AGENT);
        conn.setUseCaches(false);
        conn.setConnectTimeout(DEF_CONN_TIMEOUT);
        conn.setReadTimeout(DEF_READ_TIMEOUT);
        conn.setInstanceFollowRedirects(false);
        conn.connect();
        if (params != null && POST.equals(method)) {
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(urlEncode(params));
        }
        InputStream is = conn.getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, DEF_CHAT_SET))) {
            String strRead;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            throw new CommonException("IOException{}", e);
        } finally {
            conn.disconnect();
        }
        return rs;
    }

    /**
     * url转码
     *
     * @param data data
     * @return String
     */
    private static String urlEncode(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", DEF_CHAT_SET)).append("&");
            } catch (UnsupportedEncodingException e) {
                throw new CommonException("UnsupportedEncodingException{}", e);
            }
        }
        return sb.toString();
    }

    static class DateStatus {

        private String date;

        private String status;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }
}
