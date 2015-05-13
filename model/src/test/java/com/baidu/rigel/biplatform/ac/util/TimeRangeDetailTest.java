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
package com.baidu.rigel.biplatform.ac.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

public class TimeRangeDetailTest {

    @Test
    public void testGetDays() {
        TimeRangeDetail detail = new TimeRangeDetail("20150207", "20150217");
        String[] days = detail.getDays();
        System.out.println(days.length);
        System.out.println(days[days.length - 1]);
    }

    @Test
    public void testGetStartTime () {
        try {
            TimeRangeDetail detail = new TimeRangeDetail("", "");
            Date date = detail.getStartTime  ();
            Calendar cal = Calendar.getInstance ();
            Calendar rs = Calendar.getInstance ();
            rs.setTime (date);
            Assert.assertEquals (cal.get (Calendar.MONTH), rs.get (Calendar.MONTH));
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testGetEndTime () {
        try {
            TimeRangeDetail detail = new TimeRangeDetail("", "");
            Date date = detail.getEndTime  ();
            Calendar cal = Calendar.getInstance ();
            Calendar rs = Calendar.getInstance ();
            rs.setTime (date);
            Assert.assertEquals (cal.get (Calendar.MONTH), rs.get (Calendar.MONTH));
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testGetStart () {
        try {
            TimeRangeDetail detail = new TimeRangeDetail("", "");
            String date = detail.getStart ();
            Assert.assertEquals ("", date);
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testGetEnd () {
        try {
            TimeRangeDetail detail = new TimeRangeDetail("", "");
            String date = detail.getEnd ();
            Assert.assertEquals ("", date);
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testGetToTime () {
        try {
            String date = TimeRangeDetail.toTime (null);
            SimpleDateFormat f = new SimpleDateFormat ("yyyyMMdd");
            String rs = f.format (new Date());
            Assert.assertEquals (date, rs);
        } catch (Exception e) {
        }
    }
}

