/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.event.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jd.framework.job.constant.job.ExecutionType;
import com.jd.framework.job.event.type.JobExecutionEvent;
import com.jd.framework.job.event.type.JobExecutionEventThrowable;
import com.jd.framework.job.event.type.JobStatusTraceEvent;
import com.jd.framework.job.event.type.JobStatusTraceEvent.Source;
import com.jd.framework.job.event.type.JobStatusTraceEvent.State;
import static com.jd.framework.job.constant.rdb.JobTable.TABLE_LOG_JOB_EXECUTION;
import static com.jd.framework.job.constant.rdb.JobTable.TABLE_LOG_JOB_STATUS_TRACE;

/**
 * 
 * 数据库检索工具
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
@RequiredArgsConstructor
@Slf4j
public class RdbJobEventSearch {

	private static final List<String> FIELDS_LOG_JOB_EXECUTION = Lists.newArrayList("id", "hostname", "ip", "task_id",
			"job_name", "execution_source", "segment_item", "start_time", "complete_time", "is_success",
			"failure_cause");

	private static final List<String> FIELDS_JOB_STATUS_TRACE_LOG = Lists.newArrayList("id", "job_name",
			"original_task_id", "task_id", "slave_id", "source", "execution_type", "segment_item", "state", "message",
			"creation_time");

	private final DataSource dataSource;

	/**
	 * 检索作业运行执行轨迹.
	 * 
	 * @param condition
	 *            查询条件
	 * @return 作业执行轨迹检索结果
	 */
	public Result<JobExecutionEvent> findJobExecutionEvents(final Condition condition) {
		return new Result<>(getEventCount(TABLE_LOG_JOB_EXECUTION, FIELDS_LOG_JOB_EXECUTION, condition),
				getJobExecutionEvents(condition));
	}

	/**
	 * 检索作业运行状态轨迹.
	 * 
	 * @param condition
	 *            查询条件
	 * @return 作业状态轨迹检索结果
	 */
	public Result<JobStatusTraceEvent> findJobStatusTraceEvents(final Condition condition) {
		return new Result<>(getEventCount(TABLE_LOG_JOB_STATUS_TRACE, FIELDS_JOB_STATUS_TRACE_LOG, condition),
				getJobStatusTraceEvents(condition));
	}

	private List<JobExecutionEvent> getJobExecutionEvents(final Condition condition) {
		List<JobExecutionEvent> result = new LinkedList<>();
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = createDataPreparedStatement(conn, TABLE_LOG_JOB_EXECUTION,
						FIELDS_LOG_JOB_EXECUTION, condition); ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				JobExecutionEvent jobExecutionEvent = new JobExecutionEvent(resultSet.getString(1),
						resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
						JobExecutionEvent.ExecutionSource.valueOf(resultSet.getString(6)), Integer.valueOf(resultSet
								.getString(7)), new Date(resultSet.getTimestamp(8).getTime()),
						resultSet.getTimestamp(9) == null ? null : new Date(resultSet.getTimestamp(9).getTime()),
						resultSet.getBoolean(10), new JobExecutionEventThrowable(null, resultSet.getString(11)));
				result.add(jobExecutionEvent);
			}
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Fetch JobExecutionEvent from DB error:", ex);
		}
		return result;
	}

	private List<JobStatusTraceEvent> getJobStatusTraceEvents(final Condition condition) {
		List<JobStatusTraceEvent> result = new LinkedList<>();
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = createDataPreparedStatement(conn, TABLE_LOG_JOB_STATUS_TRACE,
						FIELDS_JOB_STATUS_TRACE_LOG, condition); ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				JobStatusTraceEvent jobStatusTraceEvent = new JobStatusTraceEvent(resultSet.getString(1),
						resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
						Source.valueOf(resultSet.getString(6)), ExecutionType.valueOf(resultSet.getString(7)),
						resultSet.getString(8), State.valueOf(resultSet.getString(9)), resultSet.getString(10),
						new Date(resultSet.getTimestamp(11).getTime()));
				result.add(jobStatusTraceEvent);
			}
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Fetch JobStatusTraceEvent from DB error:", ex);
		}
		return result;
	}

	private int getEventCount(final String tableName, final Collection<String> tableFields, final Condition condition) {
		int result = 0;
		try (Connection conn = dataSource.getConnection();
				PreparedStatement preparedStatement = createCountPreparedStatement(conn, tableName, tableFields,
						condition); ResultSet resultSet = preparedStatement.executeQuery()) {
			resultSet.next();
			result = resultSet.getInt(1);
		} catch (final SQLException ex) {
			// TODO 记录失败直接输出日志,未来可考虑配置化
			log.error("Fetch EventCount from DB error:", ex);
		}
		return result;
	}

	private PreparedStatement createDataPreparedStatement(final Connection conn, final String tableName,
			final Collection<String> tableFields, final Condition condition) throws SQLException {
		String sql = buildDataSql(tableName, tableFields, condition);
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		setBindValue(preparedStatement, tableFields, condition);
		return preparedStatement;
	}

	private PreparedStatement createCountPreparedStatement(final Connection conn, final String tableName,
			final Collection<String> tableFields, final Condition condition) throws SQLException {
		String sql = buildCountSql(tableName, tableFields, condition);
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		setBindValue(preparedStatement, tableFields, condition);
		return preparedStatement;
	}

	private String buildDataSql(final String tableName, final Collection<String> tableFields, final Condition condition) {
		StringBuilder sqlBuilder = new StringBuilder();
		String selectSql = buildSelect(tableName, tableFields);
		String whereSql = buildWhere(tableName, tableFields, condition);
		String orderSql = buildOrder(tableFields, condition.getSort(), condition.getOrder());
		String limitSql = buildLimit(condition.getPage(), condition.getPerPage());
		sqlBuilder.append(selectSql).append(whereSql).append(orderSql).append(limitSql);
		return sqlBuilder.toString();
	}

	private String buildCountSql(final String tableName, final Collection<String> tableFields, final Condition condition) {
		StringBuilder sqlBuilder = new StringBuilder();
		String selectSql = buildSelectCount(tableName);
		String whereSql = buildWhere(tableName, tableFields, condition);
		sqlBuilder.append(selectSql).append(whereSql);
		return sqlBuilder.toString();
	}

	private String buildSelectCount(final String tableName) {
		return String.format("SELECT COUNT(1) FROM %s", tableName);
	}

	private String buildSelect(final String tableName, final Collection<String> tableFields) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT ");
		for (String each : tableFields) {
			sqlBuilder.append(each).append(",");
		}
		sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
		sqlBuilder.append(" FROM ").append(tableName);
		return sqlBuilder.toString();
	}

	private String buildWhere(final String tableName, final Collection<String> tableFields, final Condition condition) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(" WHERE 1=1");
		if (null != condition.getFields() && !condition.getFields().isEmpty()) {
			for (String each : condition.getFields().keySet()) {
				String lowerUnderscore = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, each);
				if (null != condition.getFields().get(each) && tableFields.contains(lowerUnderscore)) {
					sqlBuilder.append(" AND ").append(lowerUnderscore).append("=?");
				}
			}
		}
		if (null != condition.getStartTime()) {
			sqlBuilder.append(" AND ").append(getTableTimeField(tableName)).append(">=?");
		}
		if (null != condition.getEndTime()) {
			sqlBuilder.append(" AND ").append(getTableTimeField(tableName)).append("<=?");
		}
		return sqlBuilder.toString();
	}

	private void setBindValue(final PreparedStatement preparedStatement, final Collection<String> tableFields,
			final Condition condition) throws SQLException {
		int index = 1;
		if (null != condition.getFields() && !condition.getFields().isEmpty()) {
			for (String each : condition.getFields().keySet()) {
				String lowerUnderscore = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, each);
				if (null != condition.getFields().get(each) && tableFields.contains(lowerUnderscore)) {
					preparedStatement.setString(index++, String.valueOf(condition.getFields().get(each)));
				}
			}
		}
		if (null != condition.getStartTime()) {
			preparedStatement.setTimestamp(index++, new Timestamp(condition.getStartTime().getTime()));
		}
		if (null != condition.getEndTime()) {
			preparedStatement.setTimestamp(index++, new Timestamp(condition.getEndTime().getTime()));
		}
	}

	private String getTableTimeField(final String tableName) {
		String result = "";
		if (TABLE_LOG_JOB_EXECUTION.equals(tableName)) {
			result = "start_time";
		} else if (TABLE_LOG_JOB_STATUS_TRACE.equals(tableName)) {
			result = "creation_time";
		}
		return result;
	}

	private String buildOrder(final Collection<String> tableFields, final String sortName, final String sortOrder) {
		if (Strings.isNullOrEmpty(sortName)) {
			return "";
		}
		String lowerUnderscore = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sortName);
		if (!tableFields.contains(lowerUnderscore)) {
			return "";
		}
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(" ORDER BY ").append(lowerUnderscore);
		switch (sortOrder.toUpperCase()) {
		case "ASC":
			sqlBuilder.append(" ASC");
			break;
		case "DESC":
			sqlBuilder.append(" DESC");
			break;
		default:
			sqlBuilder.append(" ASC");
		}
		return sqlBuilder.toString();
	}

	private String buildLimit(final int page, final int perPage) {
		StringBuilder sqlBuilder = new StringBuilder();
		if (page > 0 && perPage > 0) {
			sqlBuilder.append(" LIMIT ").append((page - 1) * perPage).append(",").append(perPage);
		} else {
			sqlBuilder.append(" LIMIT ").append(Condition.DEFAULT_PAGE_SIZE);
		}
		return sqlBuilder.toString();
	}

	/**
	 * 查询条件对象.
	 */
	@RequiredArgsConstructor
	@Getter
	public static class Condition {

		private static final int DEFAULT_PAGE_SIZE = 10;

		/** 每页大小 */
		private final int perPage;

		/** 页号 */
		private final int page;

		/** 排序 ASC DESC */
		private final String sort;

		private final String order;

		private final Date startTime;

		private final Date endTime;

		private final Map<String, Object> fields;
	}

	@RequiredArgsConstructor
	@Getter
	public static class Result<T> {

		private final Integer total;

		private final List<T> rows;
	}
}
