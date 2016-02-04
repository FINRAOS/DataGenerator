package org.finra.datagenerator.common.Config

import java.io.{BufferedReader, File, FileReader}

import org.finra.datagenerator.common.Helpers.StringHelper.StringExtensions

import scala.collection.JavaConverters._

// scalastyle:off regex // Because we use printlns in this class.

/**
 * Strongly typed configuration base class
 * Easy to reuse config keys in other config keys and to define dependencies, on-changed events, defaults and override values, etc.
 * Easy to set from config file, command-line, code, or any combination thereof.
 * To use, extend this class and define one or more config keys in the subclass.
 * A config key should extend Key, or, preferably, a subclass of Key.
 * Each config key in the subclass should be a singleton (either Scala object or a class with private[SubclassName] constructor
 *  and with a single lazy val pointing to it).
 * Each config key, to be made configurable, should be referenced in allConfigKeys.
 * Each config key should override calculateDefaultValue, and may also override other methods.
 */
abstract class AbstractConfig {
  val configFileParam: String = "configfile"

  def setFromArgs(args: String*): Unit = {
    val configFileToken = s"${configFileParam.toLowerCase}="
    val configFileParamMaybe = args.find(_.toLowerCase().startsWith(configFileToken))
    if (configFileParamMaybe.nonEmpty) {
      val parts = configFileParamMaybe.get.splitOnChar('=')
      if (parts.size == 2 && parts(0).nonEmpty) {
        setValuesFromFileIfExists(parts(1))
      } else {
        setValuesFromDefaultConfigFileIfExists()
      }
    } else {
      setValuesFromDefaultConfigFileIfExists()
    }
    val newargs = args.filterNot(_.toLowerCase().startsWith(configFileToken))

    // Command-line parameters will override config-file parameters if both are set.
    setValues(newargs: _*)
  }

  protected lazy val lowercaseNameToConfigKey: Map[String, Key] = {
    // http://stackoverflow.com/questions/674639/scala-best-way-of-turning-a-collection-into-a-map-by-key
    allConfigKeys.map(key => (key.nameLowercase, key))(collection.breakOut): Map[String, Key]
  }

  // Programmer must keep this collection up to date whenever adding a new Key, so user can customize the key's value.
  // Class names must be unique *ignoring case*.
  protected val allConfigKeys: collection.immutable.HashSet[Key]

  /**
   * Sets configuration values from a config file "${_PRODUCTNAME}.config", either in working directory or under default ConfigDir.
   * If it doesn't exist, log a message and do nothing.
   */
  def setValuesFromDefaultConfigFileIfExists(): Unit = {
    setValuesFromFileIfExists() // default value of configFilePath is in method signature of setValuesFromFileIfExists
  }

  // Purposefully using nonstandard method names to minimize possible conflicts with user-defined config-key names.
  val _PRODUCTNAME: String
  lazy val _DEFAULTCONFIGFILENAME = s"${_PRODUCTNAME}.config"
  def _CONFIGDIR: String = System.getProperty("user.home").replace('\\', '/') // scalastyle:ignore
  def _DEFAULTCONFIGDIR: String = System.getProperty("user.home").replace('\\', '/') // scalastyle:ignore

  /**
   * Sets configuration values from a config file. If it doesn't exist, log a message and do nothing.
   * @param configFilePath Path of config file. Defaults to ${_PRODUCTNAME}.config (either in working directory or under default ConfigDir) if not passed in.
   */
  def setValuesFromFileIfExists(configFilePath: String = _DEFAULTCONFIGFILENAME): Unit = {
    // Search priority:
    // 1. Passed-in value
    // 2. Environment variable
    // 3. Default locations
    val file1 = new File(configFilePath)
    val fileMaybe: Option[File] = if (file1.exists) {
      Some(file1)
    } else {
      println(s"Could not find ${_PRODUCTNAME} config file in ${file1.getAbsolutePath}.")
      val file2Maybe: Option[File] = System.getenv().asScala.find(_._1.toUpperCase() == s"${_PRODUCTNAME.toUpperCase}_CONFIG_PATH")
        .map(envKeyVal => new File(envKeyVal._2))
      if (file2Maybe.nonEmpty && file2Maybe.get.exists) {
        file2Maybe
      } else {
        println(s"Could not find ${_PRODUCTNAME} config file using environment variable ${_PRODUCTNAME.toUpperCase}_CONFIG_PATH${if (file2Maybe.nonEmpty) {
          s" (set to ${file2Maybe.get.getAbsolutePath})" } else ""}.")
        val file3 = new File(s"${_CONFIGDIR}${configFilePath}")
        if (file3.exists) {
          Some(file3)
        } else {
          if (file3.getAbsolutePath != file1.getAbsolutePath && file3.getAbsolutePath != file2Maybe.map(_.getAbsolutePath)) {
            println(s"Could not find ${_PRODUCTNAME} config file in ${file3.getAbsolutePath}.")
          }
          val file4 = new File(s"${_CONFIGDIR}${_DEFAULTCONFIGFILENAME}")
          if (file4.exists) {
            Some(file4)
          } else {
            if (file4.getAbsolutePath != file1.getAbsolutePath && file4.getAbsolutePath != file2Maybe.map(_.getAbsolutePath)
              && file4.getAbsolutePath != file3.getAbsolutePath) {
              println(s"Could not find ${_PRODUCTNAME} config file in ${file4.getAbsolutePath}.")
            }
            val file5 = new File(s"${_DEFAULTCONFIGDIR}${_DEFAULTCONFIGFILENAME}")
            if (file5.exists) {
              Some(file5)
            } else {
              if (file5.getAbsolutePath != file1.getAbsolutePath && file5.getAbsolutePath != file2Maybe.map(_.getAbsolutePath) &&
                file5.getAbsolutePath != file3.getAbsolutePath && file5.getAbsolutePath != file4.getAbsolutePath) {
                println(s"Could not find ${_PRODUCTNAME} config file in ${file5.getAbsolutePath}.")
              }
              None
            }
          }
        }
      }
    }

    if (fileMaybe.isEmpty) {
      println(s"Continuing with no ${_PRODUCTNAME} config file.")
    } else {
      val file = fileMaybe.get
      println(s"Found ${_PRODUCTNAME} configuration file `${file.getAbsolutePath}`.")
      val reader = new BufferedReader(new FileReader(file))
      try {
        var line: String = ""
        while ( {
          line = reader.readLine()
          line != null // scalastyle:ignore
        }) {
          // Ignore lines that look like comments
          if (!line.startsWith("//") && !line.startsWith("#") && !line.startsWith("::")) {
            val parts = line.splitOnChar('=')
            if (parts.size == 2 && parts(0).nonEmpty) {
              setValue(parts(0), parts(1))
            }
          }
        }
      } finally {
        reader.close()
      }
    }
  }

  def setValues(args: String*): Unit = {
    args.foreach(arg => {
      val parts = arg.splitOnChar('=', removeEmptyEntries = false)
      val errorMessage =
        s"""Invalid command-line parameter "${arg}" -- parameter name and value should be separated by a = with no whitespace between.
           | All arguments are optional and can be combined however you want.""".stripMargin
      require(parts.size == 2, errorMessage)

      setValue(parts(0), parts(1))
    })
  }

  def setValue(argKey: String, argValue: String): Unit = {
    val lowercasedArgKey = argKey.toLowerCase()
    val matchingKeyMaybe = lowercaseNameToConfigKey.get(lowercasedArgKey)
    require(matchingKeyMaybe.nonEmpty, s"Config key ${argKey} does not exist!")
    matchingKeyMaybe.get.setValueFromString(argValue)
  }

  def getKey(argKey: String): Key = {
    val lowercasedArgKey = argKey.toLowerCase()
    val matchingKeyMaybe = lowercaseNameToConfigKey.get(lowercasedArgKey)
    require(matchingKeyMaybe.nonEmpty, s"Config key ${argKey} does not exist!")
    matchingKeyMaybe.get
  }

  def printAllValues(): Unit = {
    allConfigKeys.foreach(key => println(key.toDescriptiveString))
  }

  trait Key {
    protected type T
    def instance: Key = this
    def getName(): String = name
    def name: String = getClass.getSimpleName.remove("$")
    def nameLowercase: String = name.toLowerCase
    override def equals(other: Any): Boolean = {
      if (other.isInstanceOf[Key]) {
        nameLowercase == other.asInstanceOf[Key].nameLowercase
      } else {
        false
      }
    }
    override def hashCode: Int = nameLowercase.hashCode
    override def toString: String = valueAsString
    def toDescriptiveString: String = s"ConfigKey${
      if (userSpecifiedValueMaybe.nonEmpty) {
        " (Overridden)"
      }
      else {
        " (Default)   "
      }} : ${name}=${valueAsString}"
    protected def onValueChanged(oldValue: Option[T], newValue: Option[T]): Unit = {}
    private var _cachedDefaultValueMaybe: Option[T] = None
    protected def cachedDefaultValueMaybe: Option[T] = _cachedDefaultValueMaybe
    protected def cachedDefaultValueMaybe_=(value: Option[T]) = {
      if (userSpecifiedValueMaybe.isEmpty) {
        onValueChanged(_cachedDefaultValueMaybe, value)
      }
      _cachedDefaultValueMaybe = value
    }
    protected def calculateDefaultValue: T
    protected def clearDefaultValue: Unit = {
      cachedDefaultValueMaybe = None
      dependentKeys.foreach(_.clearDefaultValue)
    }
    protected def defaultValue: T = {
      if (cachedDefaultValueMaybe.isEmpty) {
        cachedDefaultValueMaybe = Some(calculateDefaultValue)
      }
      cachedDefaultValueMaybe.get
    }
    private var _userSpecifiedValueMaybe: Option[T] = None
    protected def userSpecifiedValueMaybe: Option[T] = _userSpecifiedValueMaybe
    protected def userSpecifiedValueMaybe_=(value: Option[T]): Unit = {
      if (userSpecifiedValueMaybe.isEmpty) {
        onValueChanged(_cachedDefaultValueMaybe, value)
      } else {
        onValueChanged(_userSpecifiedValueMaybe, value)
      }
      _userSpecifiedValueMaybe = value
    }
    def getValue(): T = value
    def value: T = {
      userSpecifiedValueMaybe.getOrElse(defaultValue)
    }
    def getValueAsString(): String = valueAsString
    def valueAsString: String = {
      value.toString
    }
    def setValue(_value: T): Unit = value_=(_value)
    def value_=(_value: T): Unit = {
      userSpecifiedValueMaybe = Some(_value)
      clearDefaultValue
    }
    def setValueFromString(value: String): Unit

    protected def dependentKeys: Set[Key] = collection.immutable.HashSet[Key]()
  }

  trait BooleanKey extends Key {
    type T = Boolean
    def setValueFromString(value: String): Unit = {
      value_=(value.toBoolean)
    }
  }

  trait StringKey extends Key {
    type T = String
    def setValueFromString(value: String): Unit = {
      value_=(value)
    }
  }

  trait SlashedPath extends StringKey {
    def valueWithoutEndSlash: String = value.substring(0, value.length - 1)
    protected override def onValueChanged(oldValue: Option[T], newValue: Option[T]): Unit = {
      if (newValue.nonEmpty) {
        require(newValue.get.endsWith("/"), s"Value of ${name} must end in slash ('/')!")
      }

      super.onValueChanged(oldValue, newValue)
    }
  }

  trait IntKey extends Key {
    type T = Int
    def setValueFromString(value: String): Unit = {
      value_=(value.toInt)
    }
  }

  trait ShortKey extends Key {
    type T = Short
    def setValueFromString(value: String): Unit = {
      value_=(value.toShort)
    }
  }

  trait LongKey extends Key {
    type T = Long
    def setValueFromString(value: String): Unit = {
      value_=(value.toLong)
    }
  }

  trait DoubleKey extends Key {
    type T = Double
    def setValueFromString(value: String): Unit = {
      value_=(value.toDouble)
    }
  }

  trait RangeKey extends Key {
    type T = Range
    def setValueFromString(value: String): Unit = {
      val parts = value.splitOnChar('-', removeEmptyEntries = false)
      val invalidMessage = s"Invalid range string: ${value}` -- expected Int-Int, e.g., 2-5"
      require(parts.size == 2, invalidMessage)
      val part1 = parts.head.toIntMaybe
      require(part1.nonEmpty, invalidMessage)
      val part2 = parts(1).toIntMaybe
      require(part2.nonEmpty, invalidMessage)
      value_=(part1.get to part2.get)
    }
  }

  trait OptionKey[S] extends Key {
    type T = Option[S]
    override def valueAsString: String = {
      value.map(_.toString).getOrElse("")
    }
    protected def convertFromString(value: String): S
    def setValueFromString(value: String): Unit = {
      if (value.isEmpty) {
        value_=(None)
      } else {
        value_=(Some(convertFromString(value)))
      }
    }
  }

  trait StringOptionKey extends OptionKey[String] {
    def convertFromString(value: String): String = {
      value
    }
  }

  trait ShortOptionKey extends OptionKey[Short] {
    def convertFromString(value: String): Short = {
      value.toShort
    }
  }

  trait IntOptionKey extends OptionKey[Int] {
    def convertFromString(value: String): Int = {
      value.toInt
    }
  }
}

// scalastyle:on regex