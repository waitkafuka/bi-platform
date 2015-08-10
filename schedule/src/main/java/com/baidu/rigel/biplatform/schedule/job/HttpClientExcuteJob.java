package com.baidu.rigel.biplatform.schedule.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.util.HttpRequest;
import com.baidu.rigel.biplatform.schedule.constant.ScheduleConstant;

/**
 * 该job只负责做httpclient的job处理
 * 
 * @author majun04
 *
 */
@Service
public class HttpClientExcuteJob implements Job {
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientExcuteJob.class);
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final String SEPARATOR = "/";

    @Value("${schedule.excuteActionUrlHost}")
    private String excuteActionUrlHost = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String excuteAction = String.valueOf(jobDataMap.get(ScheduleConstant.EXCUTE_ACTION_KEY));
        System.out.println(SDF.format(new Date()) + "-----------------------HttpClientExcuteJob's excuteAction is : ["
                + excuteAction + "]");
        LOG.info("-----------------------HttpClientExcuteJob's excuteAction is : [" + excuteAction + "]");
        StringBuffer sb = new StringBuffer(excuteActionUrlHost);
        if (!excuteActionUrlHost.endsWith(SEPARATOR)) {
            sb.append("/");
        }
        sb.append(excuteAction);
        // TODO 联调时需要补充http参数部分逻辑
        HttpRequest.sendGet(excuteActionUrlHost, null);
    }
}
