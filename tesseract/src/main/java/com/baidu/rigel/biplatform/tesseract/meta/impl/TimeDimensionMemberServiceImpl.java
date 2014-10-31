/**
 * Copyright (c) 2014 Baidu, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baidu.rigel.biplatform.tesseract.meta.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMember;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Level;
import com.baidu.rigel.biplatform.ac.model.LevelType;
import com.baidu.rigel.biplatform.ac.model.Member;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.util.TimeRangeDetail;
import com.baidu.rigel.biplatform.tesseract.exception.MetaException;
import com.baidu.rigel.biplatform.tesseract.meta.DimensionMemberService;
import com.baidu.rigel.biplatform.tesseract.util.TimeUtils;
import com.google.common.collect.Lists;

/**
 * 
 * 时间维度成员计算实现类
 *
 * @author wangyuxue
 * @version 1.0.0.1
 */
@Service(DimensionMemberService.TIME_MEMBER_SERVICE)
public class TimeDimensionMemberServiceImpl implements DimensionMemberService {

    /**
     * 季度名称
     */
    private static final String[] QUARTER_NAMES = new String[]{"Q1", "Q2", "Q3", "Q4"};
    
    /**
     * 季度月份对应关系
     */
    private static final String[][] QUARTER_MONTH_MAPPING =
            new String[][]{
                new String[]{"0101", "0201", "0301"},
                new String[]{"0401", "0501", "0601"},
                new String[]{"0701", "0801", "0901"},
                new String[]{"1001", "1101", "1201"}
            };
    
    /**
     * 
     * @param cube
     * @param name
     * @param level
     * @param dataSourceInfo
     * @param parentMember
     * @param params
     * @return
     * @throws MetaException 
     */
    private List<MiniCubeMember> getMembers(Cube cube, String name, Level level,
            DataSourceInfo dataSourceInfo, MiniCubeMember parentMember, 
            Map<String, String> params) throws MetaException {
        List<MiniCubeMember> members = Lists.newArrayList();
        // 判断是否依据父节点获取成员信息
        
        if (parentMember != null) {
            return getMembers(cube, level, dataSourceInfo, parentMember, params);
        }
        // 如果父成员为空，根据level获取默认成员信息
        // （当前年份、当前年的季度、当前年的月份、当前年的星期、当前年的天的信息）
        try {
            getMembersByStartDate(level, name, members);
        } catch (Exception e) {
            throw new MetaException(e.getMessage(), e);
        }
        return members;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MiniCubeMember> getMembers(Cube cube, Level level, DataSourceInfo dataSourceInfo,
            Member parentMember, Map<String, String> params) throws MetaException {
        List<MiniCubeMember> members = Lists.newArrayList();
        // 判断是否依据父节点获取成员信息
        
        if (parentMember != null) {
            switch (parentMember.getLevel().getType()) {
                case TIME_YEARS:
                    List<MiniCubeMember> membersWithYearParent = genMembersWithYearParent(level, parentMember);
                    members.addAll(membersWithYearParent);
                    return members;
                case TIME_QUARTERS:
                    List<MiniCubeMember> membersWithQuarterParent = genMembersWithQuarterParent(level, parentMember);
                    members.addAll(membersWithQuarterParent);
                    return members;
                case TIME_MONTHS:
                    List<MiniCubeMember> membersWithMonthParent = genMembersWithMonthParent(level, parentMember);
                    members.addAll(membersWithMonthParent);
                    return members;
                case TIME_WEEKS:
                    List<MiniCubeMember> membersWithWeekParent = genMembersWithWeekParent(level, parentMember);
                    members.addAll(membersWithWeekParent);
                    return members;
                case TIME_DAYS:
                    List<MiniCubeMember> membersWithDayParent = genMembersWithDayParent(level, parentMember);
                    members.addAll(membersWithDayParent);
                    return members;
                default:
                    throw new IllegalArgumentException("Invalidate time dimension level type : " 
                            + parentMember.getLevel().getType()); 
            }
        }
        // 如果父成员为空，根据level获取默认成员信息
        // （当前年份、当前年的季度、当前年的月份、当前年的星期、当前年的天的信息）
        genDefaultMembers(level, parentMember, members);
        return members;
    }

    /**
     * 
     * @param level
     * @param parentMember
     * @return
     */
    private List<MiniCubeMember> genMembersWithDayParent(Level level, Member parentMember) {
        try {
            return genDayMembersWithParent(level, parentMember, TimeUtils.getMonthDays(null));
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    /**
     * 获取成员信息（父成员为星期）
     * @param level
     * @param parentMember
     * @return
     */
    private List<MiniCubeMember> genMembersWithWeekParent(Level level, Member parentMember) {
        switch (level.getType()) {
            case TIME_DAYS:
                try {
                    String name = parentMember.getName();
                    Date date = TimeRangeDetail.getTime(name);
                    TimeRangeDetail monthRange = TimeUtils.getWeekDays(date);
                    return genDayMembersWithParent(level, parentMember, monthRange);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            case TIME_MONTHS:
            case TIME_YEARS:
            case TIME_QUARTERS:
            case TIME_WEEKS:
            default:
                throw new IllegalArgumentException("Invalidate level type : " + level.getType() 
                        + " with parent type : " + parentMember.getLevel().getType());
        }
    }

    /**
     * 获取成员信息（父成员为月份）
     * @param level
     * @param parentMember
     * @return
     */
    private List<MiniCubeMember> genMembersWithMonthParent(Level level, Member parentMember) {
        // [Time].[year].[month]"
        // eg:[Time].[2014].[01]
        String parentName = parentMember.getName();
        String[] tmpArray = parentName.split(".");
        if (tmpArray.length != 3) {
            throw new IllegalStateException("parent member name is invalidate : " + parentName);
        }
        switch (level.getType()) {
            case TIME_DAYS:
                String year = tmpArray[1].substring(1, 5);
                String month = tmpArray[2].substring(1, 3);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Integer.valueOf(year));
                cal.set(Calendar.MONTH, Integer.valueOf(month) - 1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                TimeRangeDetail monthRange = TimeUtils.getMonthDays(cal.getTime(), 0, 0);
                try {
                    return genDayMembersWithParent(level, parentMember, monthRange);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            case TIME_MONTHS:
            case TIME_YEARS:
            case TIME_QUARTERS:
            case TIME_WEEKS:
            default:
                throw new IllegalArgumentException("Invalidate level type : " + level.getType() 
                        + " with parent type : " + parentMember.getLevel().getType());
        }
    }

    /**
     * 
     * @param level
     * @param parentMember
     * @param members
     * 
     */
    private void genDefaultMembers(Level level, Member parentMember, List<MiniCubeMember> members) {
        switch (level.getType()) {
            case TIME_YEARS:
                MiniCubeMember member = genMemberWithCurrentYear(level, parentMember);
                members.add(member);
                break;
            case TIME_QUARTERS:
                int quarterYear = TimeUtils.getCurrentYear();
                List<MiniCubeMember> quarterMembers = genQuarterMembersWithYear(quarterYear, level, parentMember);
                members.addAll(quarterMembers);
                break;
            case TIME_MONTHS:
                int year = TimeUtils.getCurrentYear();
                List<MiniCubeMember> monthMembers = genMonthMembersWithYear(year, level, parentMember);
                members.addAll(monthMembers);
                break;
            case TIME_WEEKS:
                MiniCubeMember weekMember = genWeekMemberWithCurrentYear(level, parentMember);
                members.add(weekMember);
                break;
            case TIME_DAYS:
                MiniCubeMember dayMember = genDayMemberWithCurrentTime(level, parentMember);
                members.add(dayMember);
                break;
            default:
                throw new IllegalStateException("Invalidate time dimension level type : " + level.getType());
        }
    }

    /**
     * 通过父成员获取成员信息（父成员为季度）
     * @param level
     * @param parentMember
     * @return
     * 
     */
    private List<MiniCubeMember> genMembersWithQuarterParent(Level level, Member parentMember) {
        // [Time].[year].[quarter]"
        // eg:[Time].[2014].[Q1]
        String name = parentMember.getName();
        String[] tmpArray = name.split(".");
        if (tmpArray.length != 3) {
            throw new IllegalArgumentException("parent member name is invalidate : " + name);
        }
        int quarterIndex = Integer.valueOf(tmpArray[2].substring(2, 3)) - 1;
        switch (level.getType()) {
            case TIME_MONTHS:
                return genMonthMemberWithQuarterParent(level, parentMember, 
                        name, tmpArray, quarterIndex);
            case TIME_DAYS:
                String time = tmpArray[1].substring(1, 5) 
                        + QUARTER_MONTH_MAPPING[quarterIndex];
                try {
                    TimeRangeDetail monthRange = TimeUtils.getMonthDays(TimeRangeDetail.getTime(time), 0, 0);
                    try {
                        return genDayMembersWithParent(level, parentMember, monthRange);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            case TIME_YEARS:
            case TIME_QUARTERS:
            case TIME_WEEKS:
            default:
                throw new IllegalArgumentException("Invalidate level type : " + level.getType() 
                        + " with parent type : " + parentMember.getLevel().getType());
        }
    }

    /**
     * 通过父成员获取天成员信息（父成员为季度）
     * @param level
     * @param parentMember
     * @param time
     * @return
     * @throws Exception
     * 
     */
    private List<MiniCubeMember> genDayMembersWithParent(Level level, Member parentMember, TimeRangeDetail range)
            throws Exception {
        String[] days = range.getDays();
        List<MiniCubeMember> members = Lists.newArrayList();
        for (String day : days) {
            MiniCubeMember dayMember = new MiniCubeMember(day);
            dayMember.setCaption(day);
            dayMember.setLevel(level);
            dayMember.setParent(parentMember);
            dayMember.setName(day);
            dayMember.setVisible(true);
            members.add(dayMember);
        }
        return members;
    }

    /**
     * 通过父成员获取月成员信息（父成员为季度）
     * @param level
     * @param parentMember
     * @param name
     * @param tmpArray
     * @param quarterIndex
     * 
     */
    private List<MiniCubeMember> genMonthMemberWithQuarterParent(Level level, Member parentMember, String name,
        String[] tmpArray, int quarterIndex) {
        List<MiniCubeMember> members = Lists.newArrayList();
        String[] months = QUARTER_MONTH_MAPPING[quarterIndex];
        for (String month : months) {
            String memberName = "[Time]." + tmpArray[1] + ".[" + month.substring(0, 2) + "]";
            MiniCubeMember monthMember = new MiniCubeMember(memberName);
            monthMember.setCaption(month.substring(0, 2) + "月");
            monthMember.setLevel(level);
            monthMember.setName(name);
            monthMember.setParent(parentMember);
            monthMember.setVisible(true);
            members.add(monthMember);
        }
        return members;
    }

    /**
     * 通过父成员获取成员信息（父成员为年）
     * @param level
     * @param parentMember
     */
    private List<MiniCubeMember> genMembersWithYearParent(Level level, Member parentMember) {
        int year = Integer.valueOf(parentMember.getCaption());
        switch (level.getType()) {
            case TIME_QUARTERS:
                return genQuarterMembersWithYear(year, level, parentMember);
            case TIME_MONTHS:
                return genMonthMembersWithYear(year, level, parentMember);
            case TIME_DAYS:
                return genDayOfYearMembers(level, parentMember, year);
            case TIME_WEEKS:
            case TIME_YEARS:
            default:
                throw new IllegalArgumentException("Invalidate level type : " + level.getType() 
                        + " with parent type : " + parentMember.getLevel().getType());
                
        }
    }

    /**
     * 
     * @param level
     * @param parentMember
     * @param year
     * @return
     * 
     */
    private List<MiniCubeMember> genDayOfYearMembers(Level level, Member parentMember, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        TimeRangeDetail yearDays = TimeUtils.getYearDays(cal.getTime());
        String[] days = yearDays.getDays();
        List<MiniCubeMember> members = Lists.newArrayList();
        for (String day : days) {
            MiniCubeMember dayMember = new MiniCubeMember(day);
            dayMember.setCaption(day);
            dayMember.setLevel(level);
            dayMember.setParent(parentMember);
            dayMember.setName(day);
            dayMember.setVisible(true);
            members.add(dayMember);
        }
        return members;
    }

    /**
     * 
     * @param level
     * @param parentMember
     * @return
     * 
     */
    private MiniCubeMember genDayMemberWithCurrentTime(Level level, Member parentMember) {
        String current = TimeUtils.getDays(0, 0).getStart();
        MiniCubeMember dayMember = new MiniCubeMember(current);
        dayMember.setCaption(current);
        dayMember.setLevel(level);
        dayMember.setParent(parentMember);
        dayMember.setName(current);
        dayMember.setVisible(true);
        return dayMember;
    }

    /**
     * 
     * @param level
     * @param parentMember
     * @return
     * 
     */
    private MiniCubeMember genWeekMemberWithCurrentYear(Level level, Member parentMember) {
        String firstDayOfWeek = TimeUtils.getWeekDays(null).getStart();
        MiniCubeMember weekMember = new MiniCubeMember(firstDayOfWeek);
        try {
            weekMember.setCaption(firstDayOfWeek.substring(0, 4) +  "-" 
                    + TimeUtils.getWeekIndex(firstDayOfWeek) + "W");
        } catch (Exception e) {
            throw new RuntimeException("Invalidate date formate, expected [yyyyMMdd] "
                    + "partten, but was : " + firstDayOfWeek);
        }
        weekMember.setLevel(level);
        weekMember.setParent(parentMember);
        weekMember.setVisible(true);
        return weekMember;
    }

    /**
     * 
     * @param level
     * @param parentMember
     * 
     */
    private List<MiniCubeMember> genMonthMembersWithYear(int year, Level level, Member parentMember) {
        List<MiniCubeMember> rs = Lists.newArrayList();
        for (int i = 1; i <= 12; ++i) {
            String name = "[Time].[" + year + "].[";
            if (i < 10) {
                name += 0;
            }
            name = name + i + "]";
            MiniCubeMember monthMember = new MiniCubeMember(name);
            monthMember.setCaption(i + "月");
            monthMember.setLevel(level);
            monthMember.setName(name);
            monthMember.setParent(parentMember);
            monthMember.setVisible(true);
            rs.add(monthMember);
        }
        return rs;
    }

    /**
     * 
     * @param level
     * @param parentMember
     * 
     */
    private List<MiniCubeMember> genQuarterMembersWithYear(int year, Level level, Member parentMember) {
        List<MiniCubeMember> rs = Lists.newArrayList();
        for (int i = 0; i < 4; ++i) {
            String name = "[Time].[" + year + "].[" + QUARTER_NAMES[i] + "]"; 
            MiniCubeMember quarterMember = new MiniCubeMember(name);
            quarterMember.setCaption(QUARTER_NAMES[i]);
            quarterMember.setLevel(level);
            quarterMember.setName(name);
            quarterMember.setParent(parentMember);
            quarterMember.setVisible(true);
            rs.add(quarterMember);
        }
        return rs;
    }

    /**
     * 
     * 依据当前时间生成时间维度成员（适用于年粒度，返回当前年）
     * @param level
     * @param parentMember
     * @return
     * 
     */
    private MiniCubeMember genMemberWithCurrentYear(Level level, Member parentMember) {
        int year = TimeUtils.getCurrentYear();
        MiniCubeMember member = new MiniCubeMember("");
        member.setCaption(String.valueOf(year));
        member.setLevel(level);
        member.setParent(parentMember);
        member.setVisible(true);
        member.setName("[Time].[" + year + "]");
        return member;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MiniCubeMember getMemberFromLevelByName(DataSourceInfo dataSourceInfo, Cube cube,
            Level level, String name, MiniCubeMember parent, Map<String, String> params)
            throws MetaException {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name can not be null");
        }
        /**
         * TODO 要修改
         */
        if (level.getType() == LevelType.TIME_DAYS) {
            MiniCubeMember dayMember = new MiniCubeMember(name);
            dayMember.setCaption(name);
            dayMember.setLevel(level);
            dayMember.setParent(null);
            dayMember.setName(name);
            dayMember.setVisible(true);
            return dayMember;
        }
        List<MiniCubeMember> members = getMembers(cube, name, level, dataSourceInfo, parent, params);
        for (MiniCubeMember m : members) {
            if (name.equals(m.getName())) {
                return m;
            }
        }
        return null;
    }


    /**
     * 
     * @param level
     * @param name
     * @param members
     * @throws Exception 
     */
    private void getMembersByStartDate(Level level, String name, List<MiniCubeMember> members) throws Exception {
        Date date = TimeRangeDetail.getTime(name);
        switch (level.getType()) {
            case TIME_YEARS:
                TimeRangeDetail detail = TimeUtils.getYearDays(date);
                for (String day : detail.getDays()) {
                    MiniCubeMember yearDayMember = new MiniCubeMember(day);
                    yearDayMember.setCaption(day);
                    yearDayMember.setLevel(level);
                    yearDayMember.setParent(null);
                    yearDayMember.setName(day);
                    yearDayMember.setVisible(true);
                    members.add(yearDayMember);
                }
                break;
            case TIME_QUARTERS:
                TimeRangeDetail qurterDetail = TimeUtils.getQuarterDays(date);
                for (String day : qurterDetail.getDays()) {
                    MiniCubeMember quarterDayMember = new MiniCubeMember(day);
                    quarterDayMember.setCaption(name);
                    quarterDayMember.setLevel(level);
                    quarterDayMember.setParent(null);
                    quarterDayMember.setName(name);
                    quarterDayMember.setVisible(true);
                    members.add(quarterDayMember);
                }
                break;
            case TIME_MONTHS:
                TimeRangeDetail monthDetail = TimeUtils.getMonthDays(date);
                for (String day : monthDetail.getDays()) {
                    MiniCubeMember monthDayMember = new MiniCubeMember(day);
                    monthDayMember.setCaption(day);
                    monthDayMember.setLevel(level);
                    monthDayMember.setParent(null);
                    monthDayMember.setName(day);
                    monthDayMember.setVisible(true);
                    members.add(monthDayMember);
                }               
                break;
            case TIME_WEEKS:
                TimeRangeDetail weekDetail = TimeUtils.getWeekDays(date);
                for (String day : weekDetail.getDays()) {
                    MiniCubeMember weekDayMember = new MiniCubeMember(day);
                    weekDayMember.setCaption(day);
                    weekDayMember.setLevel(level);
                    weekDayMember.setParent(null);
                    weekDayMember.setName(day);
                    weekDayMember.setVisible(true);
                    members.add(weekDayMember);
                }               
                break;
            case TIME_DAYS:
                MiniCubeMember dayMember = new MiniCubeMember(name);
                dayMember.setCaption(name);
                dayMember.setLevel(level);
                dayMember.setParent(null);
                dayMember.setName(name);
                dayMember.setVisible(true);
                members.add(dayMember);
                break;
            default:
                throw new IllegalStateException("Invalidate time dimension level type : " + level.getType());
        }
    }
}
