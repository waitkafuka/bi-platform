package com.baidu.rigel.biplatform.ma.ds.service;

import java.util.Map;

import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceOperationException;
import com.baidu.rigel.biplatform.ma.ds.service.impl.RelationDBInfoReaderServiceImpl;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
import com.google.common.collect.Maps;
/**
 * 数据源信息读取服务DataSourceInfoReader的实例化工厂类
 * @author jiangyichao
 *
 */
public final class DataSourceInfoReaderServiceFactory {
    
    private DataSourceInfoReaderServiceFactory () {
    }
    
    /**
     * 元数据服务实例仓库
     */
    private static final Map<String, DataSourceInfoReaderService> SERVICE_REPOSITORY = Maps.newHashMap ();
    
     static {
         SERVICE_REPOSITORY.put (DataSourceType.MYSQL.name (), new RelationDBInfoReaderServiceImpl ());
         SERVICE_REPOSITORY.put (DataSourceType.H2.name (), new RelationDBInfoReaderServiceImpl ());
         SERVICE_REPOSITORY.put (DataSourceType.ORACLE.name (), new RelationDBInfoReaderServiceImpl ());
         SERVICE_REPOSITORY.put (DataSourceType.MYSQL_DBPROXY.name (), new RelationDBInfoReaderServiceImpl ());
     }
     
     /**
      * 依据datasourceType注册相应元数据相关服务
      * @param type 数据源类型
      * @param service 数据源元数据服务
      */
     public static void registryDataSourceService (String type, DataSourceInfoReaderService service) {
         if (SERVICE_REPOSITORY.containsKey (type)) {
             throw new IllegalStateException ("can not support registry mutilple service for type :" + type);
         }
         SERVICE_REPOSITORY.put (type, service);
     }
    /**
     * 
     * @param type
     * @return DataSourceInfoReaderService
     * @throws DataSourceOperationException
     */
    public static DataSourceInfoReaderService getDataSourceInfoReaderServiceInstance(
            String type) throws DataSourceOperationException {
        return SERVICE_REPOSITORY.get (type);
//        switch (dataDourceType) {
//        // 关系数据库
//            case MYSQL:
//            case MYSQL_DBPROXY:
//            case H2:
//            case ORACLE:
//                return new RelationDBInfoReaderServiceImpl ();
//                // 列式数据库
//            case COL_DATABASE:
//                break;
//            // EXCEL文件
//            case EXCEL:
//                // CSV文件
//            case CSV:
//                // TXT文件
//            case TXT:
//                // HDFS文件系统
//            case HDFS:
//                // 未支持数据源
//            default:
//                throw new DataSourceOperationException (
//                        "unknow datasource type:" + dataDourceType);
//        }
//        return null;
    }
}
