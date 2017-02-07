package org.finra.datagenerator.scaffolding.knowledge.rule

import javax.annotation.PostConstruct

import org.apache.commons.lang3.reflect.ConstructorUtils
import org.finra.datagenerator.scaffolding.utils.FileUtils
import org.finra.scaffolding.knowledge._
import org.slf4j.LoggerFactory

/**
  * Created by dkopel on 9/26/16.
  */
trait CriteriaRule extends Rule {
    val criteria: Map[String, CriteriaContainer]
}
trait CriteriaRuleFactory {
    def newInstance(criteria: Map[String, CriteriaContainer]): CriteriaRule
}

object RuleCompiler {
    val logger = LoggerFactory.getLogger(getClass)
    val i = interpreter
    val DEFAULT_EVALUATOR = " && "
    val TEMPLATE_FILE = "templates/rule.scala"
    val in = getClass.getClassLoader.getResourceAsStream(TEMPLATE_FILE)
    var TEMPLATE: String = FileUtils.loadResource(in)

    def templateVars(code: String, className: String, pkg: KnowledgePackage): Map[String, String] = {
        List("PACKAGE"->pkg.name, "CLASSNAME"->className, "CODE"->code).toMap[String, String]
    }

    def template(code: String, className: String, pkg: KnowledgePackage): Class[CriteriaRule] = {
        logger.debug("Raw Template {}", TEMPLATE)
        val vars = templateVars(code, className, pkg)
        var parsedCode = TEMPLATE
        vars.foreach(v => parsedCode = parsedCode.replaceAll(v._1, v._2))
        logger.debug("Parsed Template {}", parsedCode)
        compileLoadClass[CriteriaRule](parsedCode, className, pkg)
    }

    private def replaceCriteria(criteria: String) = "criteria(\""+criteria+"\").test"

    def compile(
                   criteria: Map[String, CriteriaContainer],
                   name: String=randomClassName,
                   pkg: KnowledgePackage=DefaultKnowledgePackage,
                   evaluator: String=" && ",
                   code: Option[String]=Option.empty): Class[CriteriaRule] = {
        template(
            if(code.isDefined) {
                var e = code.get
                criteria.keys.foreach(c => e = e.replaceAll(c, replaceCriteria(c)))
                e
            }
            else criteria.keys.map(replaceCriteria).mkString(evaluator),
            name,
            pkg
        )
    }

    def factory(clazz: Class[CriteriaRule]) = {
        new CriteriaRuleFactory {
            override def newInstance(criteria: Map[String, CriteriaContainer]): CriteriaRule = {
                ConstructorUtils.invokeConstructor(clazz, criteria)
            }
        }
    }

    def quickRule(criteria: Map[String, CriteriaContainer], code: Option[String]=Option.empty): CriteriaRule = factory(compile(criteria, code=code)).newInstance(criteria)
}