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
package com.baidu.rigel.biplatform.ma.file.serv.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;


/**
 * 
 * 测试类
 *
 * @author david.wang
 * @version 1.0.0.1
 */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(LocalFileOperationUtils.class)
public class LocalFileOperationUtilsTest {
    
    /**
     * 系统临时目录，测试使用
     */
    private final String dir = System.getProperty("user.dir");
    
    /**
     * 
     */
    @Test
    public void testCreateFileWithEmptyPath() {
        Assert.assertFalse(LocalFileOperationUtils.createFile(null));
    }
    
    /**
     * 创建文件
     */
    @Test
    public void testCreateFileWithInvalidePath() {
        String path = dir + "/test/";
        Assert.assertFalse(LocalFileOperationUtils.createFile(path));
    }
    
    /**
     * 
     */
    @Test
    public void testCreateFileWithNotExistParentPath() {
    	new File(dir + "/test/test.txt").delete();
    	new File(dir + "/test").delete();
        boolean rs = LocalFileOperationUtils.createFile(dir + "/test/test.txt");
        Assert.assertTrue(rs);
    	new File(dir + "/test/test.txt").delete();
    	new File(dir + "/test").delete();        
    }
    
    /**
     * 创建文件
     */
    @Test
    public void testCreateFile() throws Exception {
        new File(dir + "/test.txt").delete ();
        boolean rs = LocalFileOperationUtils.createFile(dir + "/test.txt");
        Assert.assertTrue(rs);
        Assert.assertTrue(new File(dir + "/test.txt").delete ());
    }
    

    /**
     * 
     */
    @Test
    public void testWriteFileWithEmptyFile() {
        Assert.assertFalse(LocalFileOperationUtils.writeFile(null, null));
    }
    
    /**
     * 
     */
    @Test
    public void testWriteFielWithNullContent() {
        File file = PowerMockito.mock(File.class);
        Assert.assertFalse(LocalFileOperationUtils.writeFile(file, null));
    }
    
    /**
     * 
     */
    @Test
    public void testWriteFielWithEmptyContent() {
        File file = PowerMockito.mock(File.class);
        Assert.assertFalse(LocalFileOperationUtils.writeFile(file, new byte[0]));
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testWriteFile() throws Exception {
        File file = PowerMockito.mock(File.class);
        byte[] contents = new byte[1024];
        try {
            LocalFileOperationUtils.writeFile(file, contents);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }
    
    /**
     * 
     */
    @Test
    public void testMvWithNotExistSrcFile() {
        new File (dir + "/test.txt").delete ();
        Map<String, Object> rs = LocalFileOperationUtils.mv(dir + "/test.txt", dir + "/test_bak.txt", true);
        Assert.assertNotNull(rs);
        Assert.assertEquals("fail", rs.get("result"));
        Assert.assertEquals("原文件不存在", rs.get("msg"));
    }
    
    /**
     * 
     */
    @Test
    public void testMvWithExistTargetFileAndNotOverride() {
        File fSource = new File (dir + "/test.txt");
        File fTarget = new File (dir + "/test_bak.txt");
        fSource.delete ();
        fTarget.delete ();
        try {
            fSource.createNewFile ();
            fTarget.createNewFile ();
        } catch (IOException e) {
            e.printStackTrace ();
            Assert.fail ();
        }
        Map<String, Object> rs = LocalFileOperationUtils.mv(dir + "/test.txt", dir + "/test_bak.txt", false);
        Assert.assertNotNull(rs);
        Assert.assertEquals("fail", rs.get("result"));
        Assert.assertEquals("新文件已经存在", rs.get("msg"));
    }
    
    /**
     * 
     */
    @Test
    public void testMvWithNotExistTargetFile() {
        File fSource = new File (dir + "/test.txt");
        File fTarget = new File (dir + "/test_bak.txt");
        fSource.delete ();
        fTarget.delete ();
        try {
            fSource.createNewFile ();
        } catch (IOException e) {
            e.printStackTrace ();
            Assert.fail ();
        }
        Map<String, Object> rs = LocalFileOperationUtils.mv(dir + "/test.txt", dir + "/test_bak.txt", false);
        Assert.assertNotNull(rs);
        Assert.assertEquals("success", rs.get("result"));
        Assert.assertEquals("文件复制/移动成功", rs.get("msg"));
        Assert.assertTrue (new File (dir + "/test_bak.txt").delete ());
    }
    
    /**
     * 
     */
    @Test
    public void testMvWithInvalidTargetFilename() throws Exception {
        File f = new File (dir + "/test.txt");
        f.createNewFile();
        Map<String, Object> rs = LocalFileOperationUtils.mv(dir + "/test.txt", "ggg/jjj/test_bak.txt", true);
        Assert.assertNotNull(rs);
        Assert.assertEquals("fail", rs.get("result"));
        Assert.assertEquals("新文件创建失败",rs.get("msg"));
    }
    
    /**
     * 
     */
    @Test
    public void testMvUsedForDir() throws Exception {
    	File fSource = new File(dir + "/test");
    	fSource.mkdir();
    	File fTarget = new File(dir + "/test_bak");
    	fTarget.mkdir();
        Map<String, Object> rs = LocalFileOperationUtils.mv(dir + "/test", dir + "/test_bak", true);
        Assert.assertNotNull(rs);
        Assert.assertEquals("fail", rs.get("result"));
        Assert.assertEquals("读取原文件内容，写入新文件失败", rs.get("msg"));
        fSource.delete();
        fTarget.delete();
    }
    
    /**
     * 
     */
    @Test
    public void testMvDelSrcFileFailed() throws Exception {
    	File fSource = new File(dir + "/test.txt");
    	fSource.createNewFile();
    	// FileInputStream fileInputStream = new FileInputStream(fSource);
    	File fTarget = new File(dir + "/test_bak.txt");
    	fTarget.createNewFile();
        Map<String, Object> rs = LocalFileOperationUtils.mv(dir + "/test.txt", dir + "/test_bak.txt", true);
        Assert.assertNotNull(rs);
        // Assert.assertEquals("fail", rs.get("result"));
        // Assert.assertEquals("原文件删除失败", rs.get("msg"));
        // fileInputStream.close();
        fSource.delete();
        fTarget.delete();
    }
    /**
     * 
     */
    @Test
    public void testMv() throws Exception {
        File f = new File (dir + "/test.txt");
        f.delete ();
        try {
            f.createNewFile ();
        } catch (IOException e) {
            e.printStackTrace ();
            Assert.fail ();
        }
        Map<String, Object> rs = LocalFileOperationUtils.mv(dir + "/test.txt", dir + "/test_bak.txt", true);
        Assert.assertNotNull(rs);
        Assert.assertEquals("success", rs.get("result"));
        Assert.assertTrue (new File(dir + "/test_bak.txt").delete ());
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCopy() throws Exception {
        File f = new File (dir + "/test.txt");
        f.delete ();
        try {
            f.createNewFile ();
        } catch (IOException e) {
            e.printStackTrace ();
            Assert.fail ();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(f);
        fileOutputStream.write(new byte[1024]);
        Map<String, Object> rs = LocalFileOperationUtils.copy(dir + "/test.txt", dir + "/test_cp.txt", true);
        Assert.assertNotNull(rs);
        Assert.assertEquals("success", rs.get("result"));
        Assert.assertTrue (new File (dir + "/test_cp.txt").delete ());
        fileOutputStream.close();
        new File (dir + "/test.txt").delete();
    }
    
    /**
     * 
     */
    @Test
    public void testCopyWithSrcFileNotExist() {
    	File file = new File(dir + "/test.txt");
    	file.delete();
        Map<String, Object> rs = LocalFileOperationUtils.copy(dir + "/test.txt", dir + "/test_cp.txt", true);
        Assert.assertNotNull(rs);
        Assert.assertEquals("fail", rs.get("result"));
        Assert.assertEquals("原文件不存在", rs.get("msg"));
    }
}
