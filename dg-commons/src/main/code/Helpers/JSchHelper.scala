package Helpers

import java.io.{BufferedReader, FileWriter, InputStreamReader}
import java.text.SimpleDateFormat
import java.util.Date
import Helpers.StringHelper._
import com.jcraft.jsch._

/**
 * Helper methods for SFTP and SSH exec using the Java JSch library
 */
object JSchHelper {
  /**
   * Whether or not to log remote commands.
   */
  var logRemoteCommands = true

  implicit class ChannelImplicits(val channel: Channel) {
    /**
     * Get allowable JSCH channel types
     * @return
     */
    def channelType: String = {
      channel match {
        case _: ChannelExec => "exec"
        case _: ChannelSftp => "sftp"
        case _: ChannelShell => "shell"
        //case _: ChannelX11 => "x11"
        //case _: ChannelAgentForwarding => "auth-agent@openssh.com"
        case _: ChannelDirectTCPIP => "direct-tcpip"
        case _: ChannelForwardedTCPIP => "forwarded-tcpip"
        case _: ChannelSubsystem => "subsystem"
      }
    }

    /**
     * Connect to the channel using optional timeout and number of tries.
     * @param timeout -1 for no timeout, else milliseconds before connect attempt fails.
     * @param tries Number of tries before failing.
     */
    def connectWithRetry(timeout: Int = -1, tries: Short = 10): Unit = {
      if (timeout < 0) {
        RetryHelper.retry(tries, Seq(classOf[JSchException]))(channel.connect())(try { Thread.sleep(50); channel.getSession.openChannel(channelType); Thread.sleep(50) } catch{case _:JSchException => {}})
      } else {
        RetryHelper.retry(tries, Seq(classOf[JSchException]))(channel.connect(timeout))(try { Thread.sleep(50); channel.getSession.openChannel(channelType); Thread.sleep(50) } catch{case _:JSchException => {}})
      }
    }
  }

  /**
   * Implicit methods on an Exec channel.
   * @param channelExec
   */
  implicit class ExecImplicits(val channelExec: ChannelExec) {
    /**
     * Define the command (including any parameters) to execute remotely over SSH.
     * @param command
     */
    def setCommandToExec(command: String): Unit = {
      if (logRemoteCommands) {
        println(s"${new SimpleDateFormat("yyyy_MM_dd HH-mm-ss").format(new Date())}: Executing remote command on ${channelExec.getSession.getHost}: $command")
      }
      channelExec.setCommand(command)
    }

    /**
     * Run a command over SSH exec channel and save the output to a local text file.
     * @param command Command to run remotely
     * @param localFilePath Local path to save stdout of remote command
     * @return Exit code
     */
    def runCommandAndSaveOutputLocally(command: String, localFilePath: String): Int = {
      val inputStream = new BufferedReader(new InputStreamReader(channelExec.getInputStream))
      var writer: FileWriter = null
      channelExec.setCommandToExec(command)
      channelExec.connectWithRetry(3000)
      try {
        while (!channelExec.isClosed || inputStream.ready) {
          if (inputStream.ready) {
            if (writer == null) {
              writer = new FileWriter(localFilePath)
            }
            writer.write(s"${inputStream.readLine()}\r\n")
          }
        }
        channelExec.getExitStatus
      } finally {
        if (writer != null) {
          writer.close()
        }
        inputStream.close()
        channelExec.disconnect()
      }
    }
  }

  /**
   * Implicit methods on an SFTP channel
   * @param sftpChannel
   */
  implicit class SftpImplicits(val sftpChannel: ChannelSftp) {
    /**
     * Download a file over SFTP to local, with some retries in case of failure.
     * @param src
     * @param dest
     * @param triesBeforeFailure
     */
    def download(src: String, dest: String, triesBeforeFailure: Short = 3): Unit = {
      if (logRemoteCommands) {
        println(s"${new SimpleDateFormat("yyyy_MM_dd HH-mm-ss").format(new Date())}: Downloading from ${sftpChannel.getSession.getHost}: `$src` to `$dest`")
      }
      RetryHelper.retry[Unit](3, Seq(classOf[SftpException]))(sftpChannel.get(src, dest))()
    }

    /**
     * Upload a file over SFTP from local, with some retries in case of failure.
     * @param src
     * @param dest
     * @param mode ChannelSftp mode, e.g., whether or not to overwrite
     * @param triesBeforeFailure
     */
    def upload(src: String, dest: String, mode: Int = ChannelSftp.OVERWRITE, triesBeforeFailure: Short = 3): Unit = {
      if (logRemoteCommands) {
        println(s"${new SimpleDateFormat("yyyy_MM_dd HH-mm-ss").format(new Date())}: Uploading to ${sftpChannel.getSession.getHost}: `$src` to `$dest`")
      }
      RetryHelper.retry[Unit](3, Seq(classOf[SftpException]))(sftpChannel.put(src, dest, mode))()
    }

    /**
     * Create a remote directory if it doesn't alraedy exist, and if it does, empty it.
     * @param dirPath
     * @param triesBeforeFailure
     */
    def ensureEmptyDirectoryExists(dirPath: String, triesBeforeFailure: Short = 3): Unit = {
      sftpChannel.mkdirRecursivelyIfNotExists(dirPath)
      sftpChannel.cd(dirPath)
      if (logRemoteCommands) {
        println(s"${new SimpleDateFormat("yyyy_MM_dd HH-mm-ss").format(new Date())}: Deleting * from $dirPath")
      }
      RetryHelper.retry[Unit](3, Seq(classOf[SftpException]))(sftpChannel.rm("*"))()
    }

    /**
     * Create a remote directory if it doesn't already exist.
     * @param recursiveDirToCreate
     */
    def mkdirRecursivelyIfNotExists(recursiveDirToCreate: String): Unit = {
      assert(!sftpChannel.isClosed, "SFTP channel must be open!")

      var nextDirToAdd = "/"
      val dirParts = recursiveDirToCreate.splitOnChar('/')

      dirParts.foreach(dirPart => {
        nextDirToAdd += s"${dirPart}/"

        // Instead of check-if-exists (using ls or stat) and then create if not exists, we always try to create and
        // ignore the error that will result if it already exists (SSH_FX_FAILURE).
        // Just doing mkdir without checking is less network IO.
        try {
          sftpChannel.mkdir(nextDirToAdd)
        } catch {
            case e: SftpException => if (e.id != ChannelSftp.SSH_FX_FAILURE && nextDirToAdd == recursiveDirToCreate) throw e
        }
      })
    }
  }
}
