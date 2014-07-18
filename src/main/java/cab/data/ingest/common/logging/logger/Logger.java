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

public interface Logger {

	boolean isLogLevel(LogLevel logLevel, boolean forAudit, Class<?> clazz);

    void log(LogLevel logLevel, boolean isAuditMessage, Class<?> clazz, Throwable throwable,
    		String pattern, Object... arguments);
}
