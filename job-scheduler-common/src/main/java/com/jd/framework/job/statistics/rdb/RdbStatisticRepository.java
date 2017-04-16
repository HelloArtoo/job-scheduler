/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.statistics.rdb;

import static com.jd.framework.job.constant.rdb.JobTable.TABLE_STATISTICS_TASK_RESULT;
import static com.jd.framework.job.constant.rdb.JobTable.TABLE_STATISTICS_TASK_RUNNING;
import static com.jd.framework.job.constant.rdb.JobTable.TABLE_STATISTICS_JOB_RUNNING;
import static com.jd.framework.job.constant.rdb.JobTable.TABLE_STATISTICS_JOB_REGISTER;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Optional;
import com.jd.framework.job.constant.statistics.StatisticInterval;
import com.jd.framework.job.statistics.type.job.JobRegisterStatistic;
import com.jd.framework.job.statistics.type.job.JobRunningStatistic;
import com.jd.framework.job.statistics.type.task.TaskResultStatistic;
import com.jd.framework.job.statistics.type.task.TaskRunningStatistic;

/**
 * 
 * 基于数据库的统计数据仓库
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
@Slf4j
public class RdbStatisticRepository {

	private final DataSource dataSource;

	/**
	 * 构造函数.
	 * 
	 * @param dataSource
	 *            数据源
	 * @throws SQLException
	 *             SQL异常
	 */
	public RdbStatisticRepository(final DataSource dataSource) throws SQLException {
		this.dataSource = dataSource;
		initTables();
	}

	private void initTables() throws SQLException {
		try (Connection conn = dataSource.getConnection()) {
			createTaskResultTableIfNeeded(conn);
			createTaskRunningTableIfNeeded(conn);
			createJobRunningTableIfNeeded(conn);
			createJobRegisterTableIfNeeded(conn);
		}
	}

	private void createTaskResultTableIfNeeded(final Connection conn) throws SQLException {
		DatabaseMetaData dbMetaData = conn.getMetaData();
		for (StatisticInterval each : StatisticInterval.values()) {
			try (ResultSet resultSet = dbMetaData.getTables(null, null, TABLE_STATISTICS_TASK_RESULT + "_" + each,
					new String[] { "TABLE" })) {
				if (!resultSet.next()) {
					createTaskResultTable(conn, each);
				}
			}
		}
	}

	private void createTaskResultTable(final Connection conn, final StatisticInterval statisticInterval)
			throws SQLException {
		String dbSchema = "CREATE TABLE `" + TABLE_STATISTICS_TASK_RESULT + "_" + statisticInterval + "` ("
				+ "`id` BIGINT NOT NULL AUTO_INCREMENT, " + "`success_count` INT(11)," + "`failed_count` INT(11),"
				+ "`statistics_time` TIMESTAMP NOT NULL," + "`creation_time` TIMESTAMP NOT NULL,"
				+ "PRIMARY KEY (`id`));";
		try (PreparedStatement preparedStatement = conn.prepareStatement(dbSchema)) {
			preparedStatement.execute();
		}
	}

	private void createTaskRunningTableIfNeeded(final Connection conn) throws SQLException {
		DatabaseMetaData dbMetaData = conn.getMetaData();
		try (ResultSet resultSet = dbMetaData.getTables(null, null, TABLE_STATISTICS_TASK_RUNNING,
				new String[] { "TABLE" })) {
			if (!resultSet.next()) {
				createTaskRunningTable(conn);
			}
		}
	}

	private void createTaskRunningTable(final Connection conn) throws SQLException {
		String dbSchema = "CREATE TABLE `" + TABLE_STATISTICS_TASK_RUNNING + "` ("
				+ "`id` BIGINT NOT NULL AUTO_INCREMENT, " + "`running_count` INT(11),"
				+ "`statistics_time` TIMESTAMP NOT NULL," + "`creation_time` TIMESTAMP NOT NULL,"
				+ "PRIMARY KEY (`id`));";
		try (PreparedStatement preparedStatement = conn.prepareStatement(dbSchema)) {
			preparedStatement.execute();
		}
	}

	private void createJobRunningTableIfNeeded(final Connection conn) throws SQLException {
		DatabaseMetaData dbMetaData = conn.getMetaData();
		try (ResultSet resultSet = dbMetaData.getTables(null, null, TABLE_STATISTICS_JOB_RUNNING,
				new String[] { "TABLE" })) {
			if (!resultSet.next()) {
				createJobRunningTable(conn);
			}
		}
	}

	private void createJobRunningTable(final Connection conn) throws SQLException {
		String dbSchema = "CREATE TABLE `" + TABLE_STATISTICS_JOB_RUNNING + "` ("
				+ "`id` BIGINT NOT NULL AUTO_INCREMENT, " + "`running_count` INT(11),"
				+ "`statistics_time` TIMESTAMP NOT NULL," + "`creation_time` TIMESTAMP NOT NULL,"
				+ "PRIMARY KEY (`id`));";
		try (PreparedStatement preparedStatement = conn.prepareStatement(dbSchema)) {
			preparedStatement.execute();
		}
	}

	private void createJobRegisterTableIfNeeded(final Connection conn) throws SQLException {
		DatabaseMetaData dbMetaData = conn.getMetaData();
		try (ResultSet resultSet = dbMetaData.getTables(null, null, TABLE_STATISTICS_JOB_REGISTER,
				new String[] { "TABLE" })) {
			if (!resultSet.next()) {
				createJobRegisterTable(conn);
			}
		}
	}

	private void createJobRegisterTable(final Connection conn) throws SQLException {
		String dbSchema = "CREATE TABLE `" + TABLE_STATISTICS_JOB_REGISTER + "` ("
				+ "`id` BIGINT NOT NULL AUTO_INCREMENT, " + "`registered_count` INT(11),"
				+ "`statistics_time` TIMESTAMP NOT NULL," + "`creation_time` TIMESTAMP NOT NULL,"
				+ "PRIMARY KEY (`id`));";
		try (PreparedStatement preparedStatement = conn.prepareStatement(dbSchema)) {
			preparedStatement.execute();
		}
	}

	/**
	 * 添加任务运行结果统计数据.
	 * 
	 * @param taskResultStatistic
	 *            任务运行结果统计数据对象
	 * @return 添加操作是否成功
	 */
	public boolean add(final TaskResultStatistic taskResultStatistic) {
		boolean result = false;
		String sql = "INSERT INTO `" + TABLE_STATISTICS_TASK_RESULT + "_" + taskResultStatistic.getStatisticInterval()
				+ "` (`success_count`, `failed_count`, `statistics_time`, `creation_time`) VALUES (?, ?, ?, ?);";
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
			preparedStatement.setInt(1, taskResultStatistic.getSuccessCount());
			preparedStatement.setInt(2, taskResultStatistic.getFailedCount());
			preparedStatement.setTimestamp(3, new Timestamp(taskResultStatistic.getStatisticTime().getTime()));
			preparedStatement.setTimestamp(4, new Timestamp(taskResultStatistic.getCreateTime().getTime()));
			preparedStatement.execute();
			result = true;
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Insert taskResultStatistic to DB error:", ex);
		}
		return result;
	}

	/**
	 * 添加运行中的任务统计数据.
	 * 
	 * @param taskRunningStatistic
	 *            运行中的任务统计数据对象
	 * @return 添加操作是否成功
	 */
	public boolean add(final TaskRunningStatistic taskRunningStatistic) {
		boolean result = false;
		String sql = "INSERT INTO `" + TABLE_STATISTICS_TASK_RUNNING
				+ "` (`running_count`, `statistics_time`, `creation_time`) VALUES (?, ?, ?);";
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
			preparedStatement.setInt(1, taskRunningStatistic.getRunningCount());
			preparedStatement.setTimestamp(2, new Timestamp(taskRunningStatistic.getStatisticTime().getTime()));
			preparedStatement.setTimestamp(3, new Timestamp(taskRunningStatistic.getCreateTime().getTime()));
			preparedStatement.execute();
			result = true;
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Insert taskRunningStatistic to DB error:", ex);
		}
		return result;
	}

	/**
	 * 添加运行中的作业统计数据.
	 * 
	 * @param jobRunningStatistic
	 *            运行中的作业统计数据对象
	 * @return 添加操作是否成功
	 */
	public boolean add(final JobRunningStatistic jobRunningStatistic) {
		boolean result = false;
		String sql = "INSERT INTO `" + TABLE_STATISTICS_JOB_RUNNING
				+ "` (`running_count`, `statistics_time`, `creation_time`) VALUES (?, ?, ?);";
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
			preparedStatement.setInt(1, jobRunningStatistic.getRunningCount());
			preparedStatement.setTimestamp(2, new Timestamp(jobRunningStatistic.getStatisticTime().getTime()));
			preparedStatement.setTimestamp(3, new Timestamp(jobRunningStatistic.getCreateTime().getTime()));
			preparedStatement.execute();
			result = true;
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Insert jobRunningStatistic to DB error:", ex);
		}
		return result;
	}

	/**
	 * 添加作业注册统计数据.
	 * 
	 * @param jobRegisterStatistic
	 *            作业注册统计数据对象
	 * @return 添加操作是否成功
	 */
	public boolean add(final JobRegisterStatistic jobRegisterStatistic) {
		boolean result = false;
		String sql = "INSERT INTO `" + TABLE_STATISTICS_JOB_REGISTER
				+ "` (`registered_count`, `statistics_time`, `creation_time`) VALUES (?, ?, ?);";
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
			preparedStatement.setInt(1, jobRegisterStatistic.getRegisteredCount());
			preparedStatement.setTimestamp(2, new Timestamp(jobRegisterStatistic.getStatisticTime().getTime()));
			preparedStatement.setTimestamp(3, new Timestamp(jobRegisterStatistic.getCreateTime().getTime()));
			preparedStatement.execute();
			result = true;
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Insert jobRegisterStatistic to DB error:", ex);
		}
		return result;
	}

	/**
	 * 获取任务运行结果统计数据集合.
	 * 
	 * @param from
	 *            统计开始时间
	 * @param statisticInterval
	 *            统计时间间隔
	 * @return 任务运行结果统计数据集合
	 */
	public List<TaskResultStatistic> findTaskResultStatistic(final Date from, final StatisticInterval statisticInterval) {
		List<TaskResultStatistic> result = new LinkedList<>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = String
				.format("SELECT id, success_count, failed_count, statistics_time, creation_time FROM %s WHERE statistics_time >= '%s' order by id ASC",
						TABLE_STATISTICS_TASK_RESULT + "_" + statisticInterval, formatter.format(from));
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				TaskResultStatistic taskResultStatistic = new TaskResultStatistic(resultSet.getLong(1),
						resultSet.getInt(2), resultSet.getInt(3), statisticInterval, new Date(resultSet.getTimestamp(4)
								.getTime()), new Date(resultSet.getTimestamp(5).getTime()));
				result.add(taskResultStatistic);
			}
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Fetch taskResultStatistic from DB error:", ex);
		}
		return result;
	}

	/**
	 * 获取合计后的任务运行结果统计数据.
	 * 
	 * @param from
	 *            统计开始时间
	 * @param statisticInterval
	 *            统计时间间隔
	 * @return 合计后的任务运行结果统计数据对象
	 */
	public TaskResultStatistic getSummedTaskResultStatistic(final Date from, final StatisticInterval statisticInterval) {
		TaskResultStatistic result = new TaskResultStatistic(0, 0, statisticInterval, new Date());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = String.format(
				"SELECT sum(success_count), sum(failed_count) FROM %s WHERE statistics_time >= '%s'",
				TABLE_STATISTICS_TASK_RESULT + "_" + statisticInterval, formatter.format(from));
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				result = new TaskResultStatistic(resultSet.getInt(1), resultSet.getInt(2), statisticInterval,
						new Date());
			}
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Fetch summed taskResultStatistic from DB error:", ex);
		}
		return result;
	}

	/**
	 * 获取最近一条任务运行结果统计数据.
	 * 
	 * @param statisticInterval
	 *            统计时间间隔
	 * @return 任务运行结果统计数据对象
	 */
	public Optional<TaskResultStatistic> findLatestTaskResultStatistic(final StatisticInterval statisticInterval) {
		TaskResultStatistic result = null;
		String sql = String
				.format("SELECT id, success_count, failed_count, statistics_time, creation_time FROM %s order by id DESC LIMIT 1",
						TABLE_STATISTICS_TASK_RESULT + "_" + statisticInterval);
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				result = new TaskResultStatistic(resultSet.getLong(1), resultSet.getInt(2), resultSet.getInt(3),
						statisticInterval, new Date(resultSet.getTimestamp(4).getTime()), new Date(resultSet
								.getTimestamp(5).getTime()));
			}
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Fetch latest taskResultStatistic from DB error:", ex);
		}
		return Optional.fromNullable(result);
	}

	/**
	 * 获取运行中的任务统计数据集合.
	 * 
	 * @param from
	 *            统计开始时间
	 * @return 运行中的任务统计数据集合
	 */
	public List<TaskRunningStatistic> findTaskRunningStatistic(final Date from) {
		List<TaskRunningStatistic> result = new LinkedList<>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = String
				.format("SELECT id, running_count, statistics_time, creation_time FROM %s WHERE statistics_time >= '%s' order by id ASC",
						TABLE_STATISTICS_TASK_RUNNING, formatter.format(from));
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				TaskRunningStatistic taskRunningStatistic = new TaskRunningStatistic(resultSet.getLong(1),
						resultSet.getInt(2), new Date(resultSet.getTimestamp(3).getTime()), new Date(resultSet
								.getTimestamp(4).getTime()));
				result.add(taskRunningStatistic);
			}
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Fetch taskRunningStatistic from DB error:", ex);
		}
		return result;
	}

	/**
	 * 获取运行中的任务统计数据集合.
	 * 
	 * @param from
	 *            统计开始时间
	 * @return 运行中的任务统计数据集合
	 */
	public List<JobRunningStatistic> findJobRunningStatistic(final Date from) {
		List<JobRunningStatistic> result = new LinkedList<>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = String
				.format("SELECT id, running_count, statistics_time, creation_time FROM %s WHERE statistics_time >= '%s' order by id ASC",
						TABLE_STATISTICS_JOB_RUNNING, formatter.format(from));
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				JobRunningStatistic jobRunningStatistic = new JobRunningStatistic(resultSet.getLong(1),
						resultSet.getInt(2), new Date(resultSet.getTimestamp(3).getTime()), new Date(resultSet
								.getTimestamp(4).getTime()));
				result.add(jobRunningStatistic);
			}
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Fetch jobRunningStatistic from DB error:", ex);
		}
		return result;
	}

	/**
	 * 获取最近一条运行中的任务统计数据.
	 * 
	 * @return 运行中的任务统计数据对象
	 */
	public Optional<TaskRunningStatistic> findLatestTaskRunningStatistic() {
		TaskRunningStatistic result = null;
		String sql = String.format(
				"SELECT id, running_count, statistics_time, creation_time FROM %s order by id DESC LIMIT 1",
				TABLE_STATISTICS_TASK_RUNNING);
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				result = new TaskRunningStatistic(resultSet.getLong(1), resultSet.getInt(2), new Date(resultSet
						.getTimestamp(3).getTime()), new Date(resultSet.getTimestamp(4).getTime()));
			}
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Fetch latest taskRunningStatistic from DB error:", ex);
		}
		return Optional.fromNullable(result);
	}

	/**
	 * 获取最近一条运行中的任务统计数据.
	 * 
	 * @return 运行中的任务统计数据对象
	 */
	public Optional<JobRunningStatistic> findLatestJobRunningStatistic() {
		JobRunningStatistic result = null;
		String sql = String.format(
				"SELECT id, running_count, statistics_time, creation_time FROM %s order by id DESC LIMIT 1",
				TABLE_STATISTICS_JOB_RUNNING);
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				result = new JobRunningStatistic(resultSet.getLong(1), resultSet.getInt(2), new Date(resultSet
						.getTimestamp(3).getTime()), new Date(resultSet.getTimestamp(4).getTime()));
			}
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Fetch latest jobRunningStatistic from DB error:", ex);
		}
		return Optional.fromNullable(result);
	}

	/**
	 * 获取作业注册统计数据集合.
	 * 
	 * @param from
	 *            统计开始时间
	 * @return 作业注册统计数据集合
	 */
	public List<JobRegisterStatistic> findJobRegisterStatistic(final Date from) {
		List<JobRegisterStatistic> result = new LinkedList<>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = String
				.format("SELECT id, registered_count, statistics_time, creation_time FROM %s WHERE statistics_time >= '%s' order by id ASC",
						TABLE_STATISTICS_JOB_REGISTER, formatter.format(from));
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				JobRegisterStatistic jobRegisterStatistic = new JobRegisterStatistic(resultSet.getLong(1),
						resultSet.getInt(2), new Date(resultSet.getTimestamp(3).getTime()), new Date(resultSet
								.getTimestamp(4).getTime()));
				result.add(jobRegisterStatistic);
			}
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Fetch jobRegisterStatistic from DB error:", ex);
		}
		return result;
	}

	/**
	 * 获取最近一条作业注册统计数据.
	 * 
	 * @return 作业注册统计数据对象
	 */
	public Optional<JobRegisterStatistic> findLatestJobRegisterStatistic() {
		JobRegisterStatistic result = null;
		String sql = String.format(
				"SELECT id, registered_count, statistics_time, creation_time FROM %s order by id DESC LIMIT 1",
				TABLE_STATISTICS_JOB_REGISTER);
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				result = new JobRegisterStatistic(resultSet.getLong(1), resultSet.getInt(2), new Date(resultSet
						.getTimestamp(3).getTime()), new Date(resultSet.getTimestamp(4).getTime()));
			}
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Fetch latest jobRegisterStatistic from DB error:", ex);
		}
		return Optional.fromNullable(result);
	}
}
