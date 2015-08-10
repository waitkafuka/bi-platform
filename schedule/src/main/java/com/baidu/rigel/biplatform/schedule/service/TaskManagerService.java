package com.baidu.rigel.biplatform.schedule.service;

import com.baidu.rigel.biplatform.schedule.bo.ScheduleTaskInfo;
import com.baidu.rigel.biplatform.schedule.exception.ScheduleException;

/**
 * TaskManagerService接口
 * 
 * @author majun04
 *
 */
public interface TaskManagerService {
    /**
     * 添加调度任务到引擎
     * 
     * @param taskInfo 调度任务信息
     * @return 返回是否成功执行标识
     * @throws ScheduleException ScheduleException
     */
    public boolean addTaskToScheduleEngine(ScheduleTaskInfo taskInfo) throws ScheduleException;

    /**
     * 更新调度任务
     * 
     * @param taskInfo 调度任务信息
     * @return 返回是否成功执行标识
     * @throws ScheduleException ScheduleException
     */
    public boolean updateTask4ScheduleEngine(ScheduleTaskInfo taskInfo) throws ScheduleException;

    /**
     * 删除调度任务
     * 
     * @param taskInfo 调度任务信息
     * @return 返回是否成功执行标识
     * @throws ScheduleException ScheduleException
     */
    public boolean deleteTask4ScheduleEngine(ScheduleTaskInfo taskInfo) throws ScheduleException;
}
