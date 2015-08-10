package com.baidu.rigel.biplatform.schedule.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import com.baidu.rigel.biplatform.api.client.service.FileService;
import com.baidu.rigel.biplatform.api.client.service.FileServiceException;
import com.baidu.rigel.biplatform.schedule.bo.ScheduleTaskInfo;
import com.baidu.rigel.biplatform.schedule.exception.ScheduleException;
import com.baidu.rigel.biplatform.schedule.service.ScheduleService;
import com.baidu.rigel.biplatform.schedule.service.TaskManagerService;
import com.baidu.rigel.biplatform.schedule.utils.ScheduleHelper;

/**
 * 调度TaskManagerServiceImpl
 * 
 * @author majun04
 *
 */
@Service("taskManagerService")
public class TaskManagerServiceImpl implements TaskManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskManagerServiceImpl.class);

    @Resource
    private FileService fileService;
    @Resource
    private ScheduleService scheduleService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.schedule.service.TaskManagerService#addTaskToScheduleEngine(com.baidu.rigel.biplatform
     * .schedule.bo.ScheduleTaskInfo)
     */
    public boolean addTaskToScheduleEngine(ScheduleTaskInfo taskInfo) throws ScheduleException {
        boolean flag = true;
        String fileSavePath = ScheduleHelper.generateFileSavePath(taskInfo);
        // 如果文件已经存在，则直接抛出异常
        if (isFileExisted(fileSavePath)) {
            throw new ScheduleException("the task file is already exist. please check the task : ["
                    + taskInfo.toString() + "]");
        }
        try {
            fileService.write(fileSavePath, SerializationUtils.serialize(taskInfo));
            scheduleService.addTaskToSchedule(taskInfo);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            flag = false;
        }
        return flag;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.schedule.service.TaskManagerService#updateTask4ScheduleEngine(com.baidu.rigel.biplatform
     * .schedule.bo.ScheduleTaskInfo)
     */
    public boolean updateTask4ScheduleEngine(ScheduleTaskInfo taskInfo) throws ScheduleException {
        boolean flag = true;
        String fileSavePath = ScheduleHelper.generateFileSavePath(taskInfo);
        try {
            ScheduleTaskInfo newTaskInfo =
                    (ScheduleTaskInfo) SerializationUtils.deserialize(fileService.read(fileSavePath));
            BeanUtils.copyProperties(taskInfo, newTaskInfo);
            fileService.write(fileSavePath, SerializationUtils.serialize(newTaskInfo), true);
            scheduleService.updateTask2Schedule(newTaskInfo);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            flag = false;
        }

        return flag;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.schedule.service.TaskManagerService#deleteTask4ScheduleEngine(com.baidu.rigel.biplatform
     * .schedule.bo.ScheduleTaskInfo)
     */
    public boolean deleteTask4ScheduleEngine(ScheduleTaskInfo taskInfo) throws ScheduleException {
        boolean returnFlag = true;
        String fileSavePath = ScheduleHelper.generateFileSavePath(taskInfo.getTaskId(), taskInfo.getProductLineName());
        if (isFileExisted(fileSavePath)) {
            try {
                fileService.rm(fileSavePath);
                scheduleService.deleteTask4Schedule(taskInfo);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                returnFlag = false;
            }
        }

        return returnFlag;
    }

    /**
     * 先判断task持久化文件是否存在
     * 
     * @param fileSavePath 文件存储位置
     * @return 返回task存在与否的标识
     */
    private boolean isFileExisted(String fileSavePath) {
        boolean isFileExisted = true;
        try {
            fileService.read(fileSavePath);
        } catch (FileServiceException fse) {
            LOG.error("task savepath : [" + fileSavePath + "],do not exist,maybe it have been deleted....", fse);
            isFileExisted = false;
        }
        return isFileExisted;
    }
}
