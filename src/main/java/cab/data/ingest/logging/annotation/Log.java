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
package cab.data.ingest.logging.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cab.data.ingest.common.logging.logger.LogLevel;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Log {
	
	/**
	 * The log level
	 * Defaults to {@link LogLevel#INFO}.
	 */
	LogLevel level() default LogLevel.INFO;
	
	/**
	 * <code>true</code> if the auditor is enabled.
	 * Defaults to <code>false</code>.
	 * <p>This serves as a hint for the system to decide if the message
	 * should be logged as an <i>auditor</i> or a <i>logger</i>. The audit messages
	 * would go to both audit log file as well as normal log file.
	 */
	boolean audit() default false;
}
