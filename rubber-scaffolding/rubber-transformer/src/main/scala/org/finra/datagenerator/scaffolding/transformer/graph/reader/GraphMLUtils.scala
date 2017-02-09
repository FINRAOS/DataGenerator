package org.finra.datagenerator.scaffolding.transformer.graph.reader

import scala.xml.{Elem, Node}

/**
  * Created by dkopel on 11/3/16.
  */
object GraphMLUtils {
    class GraphMLBuilder {
        var globals = Map.empty[String, Any]
        var nodes = Seq.empty[Elem]
        val _clazz = GraphReader.clazz

        def dataXML(props: Map[String, Any]): Seq[Node] = props.map(t => <data key={t._1}>{t._2}</data>).toSeq

        def clazzXML(clazz: Class[_]): Node = <data key={_clazz}>{ clazz }</data>

        def elementXML(tag: String, attrs: Map[String, Any], body: Seq[Node]): Elem = {
            val as = attrs.map(t => t._1+"=\""+t._2+"\"").mkString(" ")
            xml.XML.loadString(s"<$tag $as>${body.mkString("")}</$tag>")
        }

        def globals(props: Map[String, Any]): Unit = globals = globals ++ props

        def node(id: Any, clazz: Class[_], props: Map[String, Any]) = {
            val body = Seq(clazzXML(clazz)) ++ dataXML(props)
            val n = elementXML("node", Map("id"->id), body)
            nodes = nodes :+ n
        }

        def edge(id: Any, source: Any, target: Any, clazz: Class[_], props: Map[String, Any]) = {
            val body = Seq(clazzXML(clazz)) ++ dataXML(props)
            val n = elementXML("edge", Map("id"->id, "source"->source, "target"->target), body)
            nodes = nodes :+ n
        }

        def graph: Elem = xml.XML.loadString("<graphml><globals>"+dataXML(globals).mkString("")+"</globals><graph>"+ nodes.mkString("")+ "</graph></graphml>")

        def byId(id: Any): Node = (graph \\ "_").filter(n => n.\@("id")==id.toString).head

        def byProperty(tag: String, prop: (String, Any)): Seq[Node] = (graph \\ tag)
            .filter(n =>
                (n \ "data").count(nn => nn.\@("key")==prop._1.toString && nn.text==prop._2.toString)>0
            )

        def nodeByProperty(prop: (String,Any)): Seq[Node] = byProperty("node", prop)

        def edgeByProperty(prop: (String,Any)): Seq[Node] = byProperty("edge", prop)
    }

    def newGraph: GraphMLBuilder = new GraphMLBuilder()

    def toMap(elem: Node): Map[String, Any] = (elem \ "data").map(n => n\@("key")->n.text).toMap
}
