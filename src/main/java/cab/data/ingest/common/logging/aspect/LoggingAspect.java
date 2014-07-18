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
package cab.data.ingest.common.logging.aspect;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import cab.data.ingest.common.logging.logger.LogLevel;
import cab.data.ingest.common.logging.logger.Logger;
import cab.data.ingest.logging.annotation.Log;




@Aspect
@Component
public class LoggingAspect {

	private static String BEFORE_STRING = "[ Entering [ {0} ] ]";

	private static String BEFORE_WITH_PARAMS_STRING = "[ Entering [ {0} ] with params {1} ]";

	private static String AFTER_THROWING_EXCEPTION = "[ Exception thrown [ {0} ] exception message {1} with params {2} ]";
	
	private static String AFTER_THROWING_DOMAIN_ERROR = "[ Error while processing [ {0} ] with message {1} ]";

	private static String AFTER_RETURNING = "[ Leaving [ {0} ] returning {1} ]";

	//private static String AFTER_RETURNING_VOID = "[ Leaving [ {0} ] ]";

	@Resource
	private Logger logger;

	@Before(value = "@annotation(log)", argNames = "joinPoint, log")
	public void before(JoinPoint joinPoint, Log log) {

		Class<? extends Object> clazz = joinPoint.getTarget().getClass();

		if (!logger.isLogLevel(log.level(), log.audit(), clazz))
			return;
		String name = joinPoint.getSignature().getName();

		if (ArrayUtils.isEmpty(joinPoint.getArgs())) {
			logger.log(log.level(), log.audit(), clazz, null, BEFORE_STRING,
					name, constructArgumentsString(clazz, joinPoint.getArgs()));
		} else {
			logger.log(log.level(), log.audit(), clazz, null,
					BEFORE_WITH_PARAMS_STRING, name,
					constructArgumentsString(joinPoint.getArgs()));
		}
	}

	/*
	 * @Around(value = "@annotation(log)", argNames = "joinPoint, log") public
	 * void around(JoinPoint joinPoint, Log log) {
	 * 
	 * }
	 */

	@AfterThrowing(value = "@annotation(cab.data.ingest.logging.annotation.Log)", throwing = "throwable", argNames = "joinPoint, throwable")
	public void afterThrowing(JoinPoint joinPoint, Throwable throwable) {

		Class<? extends Object> clazz = joinPoint.getTarget().getClass();
		String name = joinPoint.getSignature().getName();
		if (throwable instanceof Exception && throwable.getCause() instanceof Exception)
			logger.log(LogLevel.ERROR, Boolean.TRUE, clazz, null,
					AFTER_THROWING_DOMAIN_ERROR, name, constructMessageWithErrorCode(((Exception)throwable).getMessage()));
		else 
			logger.log(LogLevel.ERROR, Boolean.TRUE, clazz, throwable,
					AFTER_THROWING_EXCEPTION, name, throwable.getMessage(),
					constructArgumentsString(joinPoint.getArgs()));
	}

	@AfterReturning(value = "@annotation(log)", returning = "returnValue", argNames = "joinPoint, log, returnValue")
	public void afterReturning(JoinPoint joinPoint, Log log, Object returnValue) {

		Class<? extends Object> clazz = joinPoint.getTarget().getClass();

		if (!logger.isLogLevel(log.level(), log.audit(), clazz))
			return;
		String name = joinPoint.getSignature().getName();


		logger.log(log.level(), log.audit(), clazz, null, AFTER_RETURNING,
				name, constructArgumentsString(returnValue));
	}

	private String constructArgumentsString(Object... arguments) {

		StringBuffer buffer = new StringBuffer();
		for (Object object : arguments) {
			buffer.append(object);
		}

		return buffer.toString();
	}
	
	private String constructMessageWithErrorCode(String code) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[ ");
		buffer.append(code);
		buffer.append(" : ");
	// TODO: Need to check with vikas on this
	//	buffer.append(MessageResolver.getMessage(code));
		buffer.append(" ].");
		return buffer.toString();
	}
}
