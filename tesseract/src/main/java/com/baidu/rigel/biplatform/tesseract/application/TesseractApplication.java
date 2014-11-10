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
package com.baidu.rigel.biplatform.tesseract.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Tesseract项目启动入口
 * 
 * @author chenxiaoming01
 *
 */
@Configuration
@ComponentScan(basePackages = "com.baidu.rigel.biplatform.tesseract")
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@ImportResource("conf/applicationContext-tesseract.xml")
public class TesseractApplication {

    /**
     * 启动Tesseract
     * 
     * @param args 启动参数
     */
    public static void main(String[] args) {

        // ConfigurableApplicationContext context =
        SpringApplication.run(TesseractApplication.class);

        // QueryService queryService = context.getBean(QueryService.class);
        // BufferedReader br = null;
        // try {
        // File f = new File("c:/questioModel.json");
        // br = new BufferedReader(new FileReader(f));
        // StringBuilder sb = new StringBuilder();
        // String lineStr = null;
        // while((lineStr = br.readLine()) != null){
        // sb.append(lineStr);
        // sb.append(System.getProperty("line.separator"));
        // }
        // System.out.println(sb.toString());
        // ConfigQuestionModel question = AnswerCoreConstant.GSON.fromJson(sb.toString(), ConfigQuestionModel.class);
        //
        // JsonUnSeriallizableUtils.fillCubeInfo(question.getCube());
        // System.out.println(question);
        //
        // SqlDataSourceManagerImpl manager = SqlDataSourceManagerImpl.getInstance();
        // try {
        // manager.initDataSource(question.getDataSourceInfo());
        //
        // System.out.println(manager.getDataSourceByKey(question.getDataSourceInfo().getDataSourceKey()));
        // } catch (DataSourceException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
        // DataModel dataModel = queryService.query(question, null, null);
        // System.out.println(AnswerCoreConstant.GSON.toJson(dataModel));
        //
        // MetaDataService memberService = context.getBean(MetaDataService.class);
        // MiniCubeMember member = memberService.lookUp(question.getDataSourceInfo(), question.getCube(), "[trade].[1]",
        // null);
        // System.out.println(member.getUniqueName());
        //
        // } catch (JsonSyntaxException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (FileNotFoundException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (MiniCubeQueryException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (MetaException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } finally{
        // if(br != null){
        // try {
        // br.close();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
        //GIT commit test
        // }
    }
}