package org.finra.datagenerator.scaffolding.random.core

import org.finra.datagenerator.scaffolding.config.Configuration
import org.finra.datagenerator.scaffolding.utils.Logging
import org.finra.datagenerator.scaffolding.random.Parameter
import org.finra.datagenerator.scaffolding.random.types._
/**
  * Created by dkopel on 11/25/16.
  */
class RubberRandomImpl(
                          override val basePackages: Seq[String]=Seq("org.finra.datagenerator.scaffolding"),
                          override val conf: Option[Configuration]=Option.empty
                      )
    extends RubberRandom(basePackages, conf) with Logging {
    override def generateTypeContainer[T](typeContainer: TypeContainer[T], args: Seq[Parameter[_]])(implicit configuration: Configuration): T = {
        logger.info("Generating type container {}", typeContainer)
        logger.debug("TypeContainer Before: {}", typeContainer)

        val result: T = {
            implicit val rr: JavaPrimitives = this
            val pred = findAndApplyPredicate(typeContainer)
            if(pred.isDefined) {
                pred.get
            } else {
                implicit val rz: RubberRandom = this
                typeContainer match {
                    // Create type array
                    case tt if ArrayTypeProcessor.eval(tt) => ArrayTypeProcessor(tt)
                    // Different types of collections
                    case tt if CollectionTypes.eval(tt) => CollectionTypes(tt)
                    // Enum
                    case tt if EnumTypeProcessor.eval(tt) => EnumTypeProcessor(tt)
                    // Custom class, field introspection
                    case _ => generateCustomWithStrategy(typeContainer, args)
                }
            }
        }

        logger.debug("TypeContainer After: {}", typeContainer)
        logger.debug("Result: {}", result)
        result
    }
}
object RubberRandomImpl {
    def apply(): RubberRandomImpl = new RubberRandomImpl()
}
class JavaRubberRandom(
                          override val basePackages: Seq[String]=Seq("org.finra.datagenerator.scaffolding"),
                          override val conf: Option[Configuration]=Option.empty
                      )
    extends RubberRandomImpl(basePackages, conf) {
    @Override
    def generate[T](clazz: Class[T]): T = generateTypeContainer(TypeContainer(clazz)(this), Seq.empty)(this)

    @Override
    def generate[T](clazz: Class[T], args: Seq[Parameter[_]]): T = generateTypeContainer(TypeContainer(clazz)(this), args)(this)

    @Override
    def generate[T](parameterizedType: ParameterizedTypeReference[T]): T = generateTypeContainer(TypeContainer[T](parameterizedType)(this), Seq.empty)(this)
}
object JavaRubberRandom {
    def apply(): JavaRubberRandom = new JavaRubberRandom()
}