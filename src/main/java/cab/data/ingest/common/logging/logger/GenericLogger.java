/*
 * Copyright (c) NDS Limited 2011.
 * All rights reserved.
 * No part of this program may be reproduced, translated or transmitted,
 * in any form or by any means, electronic, mechanical, photocopying,
 * recording or otherwise, or stored in any retrieval system of any nature,
 * without written permission of the copyright holder.
 */

/* 
 * Created on Nov 14, 2011
 */
package cab.data.ingest.common.logging.logger;

import java.text.MessageFormat;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class GenericLogger implements Logger {

	@Override
	public boolean isLogLevel(LogLevel logLevel, boolean forAudit, Class<?> clazz) {
		boolean result = false;

		switch (logLevel) {

		case DEBUG:
			result = getLoggerOrAuditor(clazz, forAudit).isDebugEnabled();
			break;
		case ERROR:
			result = getLogger(clazz).isErrorEnabled();
			break;
		case FATAL:
			result = getLogger(clazz).isFatalEnabled();
			break;
		case INFO:
			result = getLoggerOrAuditor(clazz, forAudit).isInfoEnabled();
			break;
		case TRACE:
			result = getLoggerOrAuditor(clazz, forAudit).isTraceEnabled();
			break;
		case WARN:
			result = getLogger(clazz).isWarnEnabled();
			break;
		default:
			result = false;
		}
		return result;
	}

	@Override
	public void log(LogLevel logLevel, boolean isAuditMessage, Class<?> clazz, Throwable throwable,
			String pattern, Object... arguments) {
		switch (logLevel) {

		case DEBUG:
			debug(clazz, isAuditMessage, throwable, pattern, arguments);
			break;

		case ERROR:
			error(clazz, isAuditMessage, throwable, pattern, arguments);
			break;

		case FATAL:
			fatal(clazz, isAuditMessage, throwable, pattern, arguments);
			break;

		case INFO:
			info(clazz, isAuditMessage, throwable, pattern, arguments);
			break;

		case TRACE:
			trace(clazz, isAuditMessage, throwable, pattern, arguments);
			break;

		case WARN:
			warn(clazz, isAuditMessage, throwable, pattern, arguments);
			break;
		}

	}

	private void debug( Class<?> clazz, boolean isAuditMessage, Throwable throwable,
			 String pattern,  Object... arguments) {

		if (throwable != null) {
			getLoggerOrAuditor(clazz, isAuditMessage).debug(format(pattern, arguments), throwable);
		} else {
			getLoggerOrAuditor(clazz, isAuditMessage).debug(format(pattern, arguments));
		}
	}

	private void error( Class<?> clazz, boolean isAuditMessage, Throwable throwable,
			 String pattern,  Object... arguments) {

		getLoggerOrAuditor(clazz, isAuditMessage).error(format(pattern, arguments));
		
		if (throwable != null) {
			getLoggerOrAuditor(clazz, Boolean.FALSE).error(format(pattern, arguments), throwable);
		} 
	}

	private void fatal( Class<?> clazz, boolean isAuditMessage, Throwable throwable,
			 String pattern,  Object... arguments) {

		getLoggerOrAuditor(clazz, isAuditMessage).fatal(format(pattern, arguments));
		
		if (throwable != null) {
			getLoggerOrAuditor(clazz, Boolean.FALSE).fatal(format(pattern, arguments), throwable);
		} 
	}

	private void info( Class<?> clazz, boolean isAuditMessage, Throwable throwable,
			 String pattern,  Object... arguments) {

		if (throwable != null) {
			getLoggerOrAuditor(clazz, isAuditMessage).info(format(pattern, arguments), throwable);
		} else {
			getLoggerOrAuditor(clazz, isAuditMessage).info(format(pattern, arguments));
		}
	}

	private void trace( Class<?> clazz, boolean isAuditMessage, Throwable throwable,
			 String pattern,  Object... arguments) {

		if (throwable != null) {
			getLoggerOrAuditor(clazz, isAuditMessage).trace(format(pattern, arguments), throwable);
		} else {
			getLoggerOrAuditor(clazz, isAuditMessage).trace(format(pattern, arguments));
		}
	}

	private void warn( Class<?> clazz, boolean isAuditMessage, Throwable throwable,
			 String pattern,  Object... arguments) {

		if (throwable != null) {
			getLoggerOrAuditor(clazz, isAuditMessage).warn(format(pattern, arguments), throwable);
		} else {
			getLoggerOrAuditor(clazz, isAuditMessage).warn(format(pattern, arguments));
		}
	}

	private String format( String pattern,  Object... arguments) {

		return MessageFormat.format(pattern, arguments);
	}

	private org.apache.log4j.Logger getLoggerOrAuditor( Class<?> clazz, boolean isAuditor) {
		if (isAuditor)
			return org.apache.log4j.Logger.getLogger("audit."+clazz.getName());
		return org.apache.log4j.Logger.getLogger(clazz);
	}
	
	private org.apache.commons.logging.Log getLogger(Class<?> clazz) {
		return LogFactory.getLog(clazz);
	}
}
