package com.baidu.rigel.biplatform.ma.datasource.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.ma.datasource.exception.DsConnectionException;
import com.baidu.rigel.biplatform.ma.datasource.service.DataSourceConnectionService;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
/**s
 * 数据源连接信息之关系型数据库连接实现类
 * @author jiangyichao
 *
 */
public class RelationDBConnectionServiceImpl implements
		DataSourceConnectionService<Connection> {

	/**
	 * 日志对象
	 */
	private static final Logger LOG = LoggerFactory.getLogger(RelationDBConnectionServiceImpl.class);
	/**
	 * @{inheritDoc}
	 */
	@Override
	public Connection createConnection(DataSourceDefine ds, String securityKey) throws DsConnectionException {
		String dbUser = ds.getDbUser();
		String dbPwd = ds.getDbPwd();
		DataSourceType type = ds.getDataSourceType();
		String connUrl = this.getDataSourceConnUrl(ds);
		Connection conn = null;
        try {
            Class.forName(type.getDriver());
            String pwd = AesUtil.getInstance().decodeAnddecrypt(dbPwd, securityKey);
            LOG.info("[INFO]--- --- --- --- connect to database with user : {}", dbUser);
            StringBuilder pwdStr = new StringBuilder();
            for (char c : pwd.toCharArray()) {
                pwdStr.append(c >> 1);
            }
            LOG.info("[INFO]--- --- --- --- connect to database with pwd : {}", pwdStr.toString());
            conn = DriverManager.getConnection(connUrl, dbUser, pwd);
        } catch (ClassNotFoundException e) {
            LOG.error("[ERROR] --- --- --- --- connection to database error : {}", e.getMessage());
            LOG.error("[ERROR] --- --- --- --- stackTrace :", e);
            throw new DsConnectionException("ClassNotFoundException when create Relation Database DataSouceConnection! ", e);
        } catch (Exception e) {
        	LOG.error("[ERROR] --- --- --- --- connection to db error : {}", e.getMessage());
        	LOG.error("[ERROR] --- --- --- --- stackTrace :", e);
            throw new DsConnectionException("SQLException when create Relation Database DataSouceConnection! ", e);
        }
		return conn;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean closeConnection(Connection conn) throws DsConnectionException {
		if(conn != null) {
			try {
				conn.close();
				LOG.info("[INFO]--- --- --- --- close connection success");
				return true;
			} catch (SQLException e) {
				LOG.error("[ERROR] --- --- --- --- close connection: {}", e.getMessage());
			}
		}
		return false;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public String getDataSourceConnUrl(DataSourceDefine ds) throws DsConnectionException {
        if (ds == null) {
            throw new DsConnectionException("Datasource can not be null! ");
        }
        DataSourceType type = ds.getDataSourceType();
        String connUrl = type.getPrefix() + ds.getHostAndPort() + type.getDiv() + ds.getDbInstance();
        if (StringUtils.hasText(ds.getEncoding())) {
            if (type == DataSourceType.MYSQL || type == DataSourceType.MYSQL_DBPROXY) {
                connUrl = connUrl + "?useUniCode=true&characterEncoding=" + ds.getEncoding();
            }
        }
        LOG.debug("Conn URL: " + connUrl);
        return connUrl;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public boolean isValidateDataSource(DataSourceDefine ds, String securityKey) {
		Connection conn = null;
		try {
			conn = this.createConnection(ds, securityKey);	
			if (conn != null) {
				return true;
			}
			return false;
		} catch (DsConnectionException e) {
			LOG.error("fail to create ds connection");
		} finally {
			if (conn != null) {
				try {
					this.closeConnection(conn);
				} catch (DsConnectionException e) {
					LOG.error("fail to create ds connection");
				}
			}			
		}
		return false;
	}
	/**
	 * @{inheritDoc}
	 */
	@Override
	public SqlDataSourceInfo parseToDataSourceInfo(DataSourceDefine ds,
			String security) throws DsConnectionException {
		// TODO Auto-generated method stub
		return null;
	}

}
