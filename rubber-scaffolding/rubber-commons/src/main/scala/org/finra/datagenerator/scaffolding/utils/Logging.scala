package org.finra.datagenerator.scaffolding.utils

import org.slf4j.LoggerFactory
/**
  * Created by dkopel on 12/5/16.
  */
trait Logging {
    @transient val logger = LoggerFactory.getLogger(getClass)
}