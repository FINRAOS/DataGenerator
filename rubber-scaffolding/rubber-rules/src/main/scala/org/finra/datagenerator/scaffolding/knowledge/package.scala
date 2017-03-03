package org.finra.datagenerator.scaffolding

import java.nio.file.{Files, Paths}
import java.util.UUID

import org.finra.datagenerator.scaffolding.knowledge.support.util.{SnippetAnalysis, TypeContainer}

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions
import scala.reflect.internal.util.BatchSourceFile
import scala.reflect.io.AbstractFile
import scala.reflect.runtime.universe._
/**
  * Created by dkopel on 9/21/16.
  */
package object knowledge {
    val interpreter = getInterpreter
    private def getInterpreter: IMain = {
        val s = new Settings()
        s.usejavacp.value = true
        new scala.tools.nsc.interpreter.IMain(s)
    }

    object traverser extends Traverser {
        var applies = List[Apply]()
        var argsList = Map[String, TypeContainer]()

        def getPredef(s: String): String = s match {
            case "String" => "java.lang.String"
            case "Function" => "scala.Function1"
            case "Map" => "scala.collection.immutable.Map"
            case "Set" => "scala.collection.immutable.Set"
            case "Class" => "java.lang.Class"
            case s => if(!s.contains('.')) "scala."+s else s
        }

        def doValDef(l: Any): TypeContainer = {
            if(l.isInstanceOf[Select]) {
                TypeContainer(Class.forName(getPredef(l.asInstanceOf[Select].name.toString)))
            } else if(l.isInstanceOf[AppliedTypeTree]) {
                val z = l.asInstanceOf[AppliedTypeTree]
                val t = getPredef(z.tpt.asInstanceOf[Ident].name.toString)
                TypeContainer(Class.forName(t), doTypeParams(z))
            } else {
                val id = l.asInstanceOf[Ident]
                TypeContainer(Class.forName(getPredef(id.name.toString)))
            }
        }

        def doTypeParams(l: Any): Seq[TypeContainer] = {
            val at = l.asInstanceOf[AppliedTypeTree]
            var col = List.empty[TypeContainer]
            at.args.foreach(a => {
                if(a.isInstanceOf[Ident] && !a.asInstanceOf[Ident].name.toString.startsWith("_$")) {
                    col = doValDef(a) :: col
                } else if(a.isInstanceOf[ExistentialTypeTree]) {
                    val tt = a.asInstanceOf[ExistentialTypeTree]
                    col = doValDef(tt.tpt) :: col
                }
            })
            col.reverse
        }

        override def traverse(tree: Tree): Unit = {
            (
                tree match {
                    case app@Apply(fun, args) =>
                        applies = app :: applies
                        super.traverse(fun)
                        super.traverseTrees(args)
                    case _ => super.traverse(tree)
                },
                tree match {
                    case arg@ValDef(_, _, _, _) =>
                        val l = arg.children.last

                        argsList += (
                            arg.name.toTermName.toString -> doValDef(l))
                    case _ => None
                }
                )
        }
    }

    def randomClassName: String = {
        "C"+scala.util.Random.alphanumeric.take(10).mkString
    }

    def compileLoadClass[T](code: String, className: String=randomClassName, pkg: KnowledgePackage=DefaultKnowledgePackage): Class[T] = {
        val path = Paths.get(s"/tmp/$className.scala")
        val fullName = pkg.name+"."+className
        Files.write(path, code.getBytes)
        val bf = new BatchSourceFile(AbstractFile.getFile(path.toFile))
        interpreter.compileSources(bf)
        val bytes = interpreter.classLoader.classBytes(fullName)
        val classLoader = interpreter.classLoader
        val clazz = classLoader.loadClass(fullName).asInstanceOf[Class[T]]
        val url = clazz.getProtectionDomain.getCodeSource.getLocation.getPath
        clazz
    }

    def invokeCompiled(clazz: Class[_], eval: String): Unit = {
        val instance = clazz.newInstance()
        interpreter.bind("func", clazz.getName, instance)
        interpreter.eval(eval)
    }

    def mapToKeyValue(args: Map[_, _]): String = {
        args
            .mapValues(v => if (v.isInstanceOf[String]) "\""+v+"\"" else v)
            .map(t => t._1+"="+t._2)
            .mkString(", ")
    }

    def invokeSnippet(snippet: String, methodName: String, args: Map[String, Any]=Map()) = {
        interpreter.eval(s"$snippet;$methodName(${mapToKeyValue(args)})")
    }

    def analyzeCodeSnippet(code: String): SnippetAnalysis = {
        val mir = runtimeMirror(this.getClass.getClassLoader)
        val tb = mir.mkToolBox()
        val t = tb.parse(code)
        traverser.traverse(t)
        //println(showRaw(t))
        SnippetAnalysis(code, traverser.applies.last, traverser.argsList)
    }

    val defaultKnowledgePackage = DefaultKnowledgePackage

    object DefaultKnowledgePackage extends KnowledgePackage {
        var knows = ListBuffer.empty[Knowledge[_]]
        override val id: UUID = UUID.randomUUID()
        override val name: String = "default"

        override def addKnowledge(knowledges: Seq[Knowledge[_]]): Unit = knows +: knowledges

        override def getKnowledge: Seq[Knowledge[_]] = knows

        override def contains(id: UUID): Boolean = knows.exists(k => k.id == id)

        override def contains(name: String): Boolean = knows.exists(k => k.name == name)

        override def getKnowledge(id: UUID): Knowledge[_] = knows.find(k => k.id == id).get

        override def getKnowledge(name: String): Knowledge[_] = knows.find(k => k.name == name).get
    }
}
