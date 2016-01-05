package org.finra.datagenerator.common.Ssh

import com.jcraft.jsch._
import org.finra.datagenerator.common.Exception.NonZeroExitCodeException
import JSchHelper._
import org.finra.datagenerator.common.Helpers.{FileHelper, RandomHelper, RetryHelper}

import scala.collection.JavaConverters._

// scalastyle:off null // Some Java interop code requires null keyword.

/**
 * Wrapper used to make SSH/SFTP calls over a single long-lived SSH connection
 */
abstract class SshAutomator extends AutoCloseable {
  val user: String
  val host: String
  val port: Short

  def publicKeyPath: Option[String]
  def privateKeyPath: String
  def passphrase: Option[String]
  def copyUnixLogsToLocal: Boolean

  val sudoUser: String
  val remoteLogDir: String
  val remoteLogDirWhenSudoing: String
  val useSeparateDirForSudoLogs: Boolean = false
  val productName: String

  protected var _firstTimeAutomationRun = true
  def firstTimeAutomationRun: Boolean = {
    _firstTimeAutomationRun
  }
  protected def firstTimeAutomationRun_=(value: Boolean): Unit = {
    _firstTimeAutomationRun = value
  }

  def ensureRemoteLogDirectoriesExist(): Unit = {
    ensureEmptyRemoteDirectoryExists(remoteLogDir)
    ensureEmptyRemoteDirectoryExists(remoteLogDirWhenSudoing, useSudo = true)
  }

  def ensureEmptyRemoteDirectoryExists(remoteDirPath: String, useSudo: Boolean = false): Unit = {
    // Safety check to make sure we're not deleting anything other than stuff for this product.
    require(productName != null && productName.nonEmpty && remoteDirPath.contains(productName), s"Not allowed to delete dir ${remoteDirPath}!")
    val path = if (remoteDirPath.endsWith("/")) remoteDirPath else remoteDirPath + "/"
    val command = if (useSudo) {
      s"""sudo -u $sudoUser -i rm -rf "$path" && sudo -u $sudoUser -i mkdir -p "$path" && sudo -u $sudoUser -i chmod -R a+rw "$path""""
    } else {
      s"""rm -rf "$path" && mkdir -p "$path" && chmod -R a+rw "$path""""
    }
    execRemoteCommand(command)
  }

  def downloadLogsToLocal(): Unit

  protected var _sessionMaybe: Option[Session] = None
  protected var _session: Session = null
  def getOpenSession(): Session = {
    _sessionMaybe = Some(JSchHelper.getOpenSession(_sessionMaybe, host, user, port, publicKeyPath,
      Some(privateKeyPath), passphrase, getPasswordMaybe))
    _session = _sessionMaybe.get
    _sessionMaybe.get
  }

  def getPasswordMaybe: Option[String]

  private var _sftp: ChannelSftp = null
  def getOpenSftp(): ChannelSftp = {
    if (_session == null ||_sftp == null || !_session.isConnected || !_sftp.isConnected || _sftp.isClosed) {
      val channelSftp = getOpenSession().openChannel("sftp")
      channelSftp.connectWithRetry(3000)
      _sftp = channelSftp.asInstanceOf[ChannelSftp]
    }
    require(!_sftp.isClosed)
    _sftp
  }

  private var _exec: ChannelExec = null
  def getOpenExec(): ChannelExec = {
    if (_session == null || _exec == null || !_session.isConnected || !_exec.isConnected || _exec.isClosed) {
      val channelExec = getOpenSession().openChannel("exec")
      _exec = channelExec.asInstanceOf[ChannelExec]
    }
    require(!_exec.isClosed)
    _exec
  }

  def close(): Unit = {
    if (copyUnixLogsToLocal) {
      downloadLogsToLocal()
    }

    if (_sftp != null && _sftp.isConnected) {
      _sftp.disconnect()
    }
    _sftp = null
    if (_exec != null && _exec.isConnected) {
      _exec.disconnect()
    }
    _exec = null
    if (_session != null && _session.isConnected) {
      _session.disconnect()
    }
    _session = null
    firstTimeAutomationRun = true
  }

  def execRemoteCommandWithRetry(command: String, outRemoteFileName: String = "", errRemoteFileName: String = ""
                                 , waitForExit: Boolean = true, maxTries: Short = 3): Option[Int] = {
    var out = outRemoteFileName
    var err = errRemoteFileName
    var tryNum = 1
    RetryHelper.retry(
      maxTries, Seq(classOf[NonZeroExitCodeException]))(
      execRemoteCommand(command, out, err, waitForExit, failOnNonZeroExitCode = true))({
      println("Command failed... retrying.") // scalastyle:ignore
      tryNum += 1
      val outLastDot = outRemoteFileName.lastIndexOf('.')
      if (outLastDot > -1) {
        out = s"${outRemoteFileName.substring(0, outLastDot)}_try$tryNum${outRemoteFileName.substring(outLastDot)}"
      } else {
        out = s"${outRemoteFileName}_try$tryNum"
      }
      val errLastDot = errRemoteFileName.lastIndexOf('.')
      if (errLastDot > -1) {
        err = s"${errRemoteFileName.substring(0, errLastDot)}_try$tryNum${errRemoteFileName.substring(errLastDot)}"
      } else {
        err = s"${errRemoteFileName}_try$tryNum"
      }
      Thread.sleep(500)
    })
  }

  def execRemoteCommand(command: String): Int = {
    execRemoteCommand(command, "", "", true, false).get
  }

  def execRemoteCommand(command: String, outRemoteFileName: String = "", errRemoteFileName: String = ""
                        , waitForExit: Boolean = true, failOnNonZeroExitCode: Boolean = false /*true*/): Option[Int] = {
    val logDir = if (command.startsWith(s"sudo -u $sudoUser")) remoteLogDirWhenSudoing else remoteLogDir
    var modifiedCommand = command
    if (outRemoteFileName != "") {
      modifiedCommand += s" > ${logDir}${outRemoteFileName.replace("|", "_").replace("\\", "")}"
    }
    if (errRemoteFileName != "") {
      modifiedCommand += s" 2> ${logDir}${errRemoteFileName.replace("|", "_").replace("\\", "")}"
    }

    val exec = getOpenExec()
    exec.setCommandToExec(modifiedCommand)

    try {
      exec.connectWithRetry(3000)

      if (waitForExit) {
        while (!exec.isClosed) {
          Thread.sleep(100)
        }
        val exitCode = exec.getExitStatus
        if (failOnNonZeroExitCode) {
          if (exitCode != 0) {
           throw new NonZeroExitCodeException(s"Non-zero exit code ${exitCode} in command `${modifiedCommand}`")
          }
        } else if (exitCode != 0) {
          println(s"\tNon-zero exit code ${exitCode} in command `${modifiedCommand}`") // scalastyle:ignore
        }
        Some(exitCode)
      } else {
        None
      }
    } finally {
      exec.disconnect()
    }
  }

  def commandSendingOutputToFilesAndStandardStreams(baseCommand: String, outRemoteFileName: String, errRemoteFileName: String): String = {
    val logDir = if (useSeparateDirForSudoLogs && baseCommand.startsWith(s"sudo -u $sudoUser")) remoteLogDirWhenSudoing else remoteLogDir
    s"$baseCommand > >(tee ${logDir}$outRemoteFileName) 2> >(tee ${logDir}$errRemoteFileName >&2)"
  }

  /**
  * Copy file(s) from HDFS location to local, keeping same filenames from HDFS
  * Temporarily writes the files to /tmp/dg-common-tmp-copy-[rand-string]/, then deletes this remote dir when done
  * @param hdfsPath Remote path in HDFS from where to get file(s)
  * @param localDirPath Local dir path to copy file(s) to
  * @param createLocalDirIfNotExists Whether or not to create the local directory if not exists (default true)
  * @return Exit code of hadoop -copyToLocal command
    */
  def copyFromHdfsToLocal(hdfsPath: String, localDirPath: String, createLocalDirIfNotExists: Boolean = true): Int = {
    if (createLocalDirIfNotExists) {
      FileHelper.createDirIfNotExists(localDirPath)
    }

    val remoteTempDir = s"/tmp/${productName}-tmp-copy-${RandomHelper.randomAlphanumericString(7)}/"
    ensureEmptyRemoteDirectoryExists(remoteTempDir, useSudo = true)

    val sudoPrefix = s"sudo -u ${sudoUser} -i"

    getOpenExec().runCommand(s"""${sudoPrefix} chmod -R a+rwx "${remoteTempDir}"""")

    // Exec hadoop fs -copyToLocal
    val exitCode = getOpenExec().runCommand(s"${sudoPrefix} hadoop fs -copyToLocal ${hdfsPath} ${remoteTempDir}")

    // Copy from remote to local
    FileHelper.ensureDirectoryExists(localDirPath)
    getOpenSftp().ls(remoteTempDir + "*").asScala.foreach(obj => {
      // Scala has no syntax to import a non-static inner Java class, so we have to do this ugly cast with #,
      // because by default inner classes in Scala are members of the enclosing object, whereas in Java they are members of the enclosing class.
      val lsEntry = obj.asInstanceOf[ChannelSftp#LsEntry]
      if (!lsEntry.getAttrs.isDir) {
        getOpenSftp().downloadFile(remoteTempDir + lsEntry.getFilename, localDirPath + lsEntry.getFilename)
      }
    })

    // Delete remote dir
    getOpenExec().runCommand(s"${sudoPrefix} rm -rf ${remoteTempDir}")

    exitCode
  }
}

// scalastyle:on null