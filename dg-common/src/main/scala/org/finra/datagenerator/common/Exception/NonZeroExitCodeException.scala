package org.finra.datagenerator.common.Exception

// scalastyle:off null

/**
 * Exception thrown when remote command returns non-zero exit code
 */
class NonZeroExitCodeException(message: String = "Non-zero exit code!", cause: Throwable = null) extends RuntimeException(message, cause)

// scalastyle:on null