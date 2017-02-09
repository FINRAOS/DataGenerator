package org.finra.datagenerator.scaffolding.config

/**
  * Created by dkopel on 12/13/16.
  */
case class LocalConfig(nConfs: Seq[Config[_]])(implicit oConf: Configuration) extends Configuration(oConf.provider) {
    implicit val self: Configuration = this

    def allConfs = nConfs.toList ++ oConf.confs().filter(c => !nConfs.map(nc => nc.conf.name).contains(c.conf.name))

    override def confs(name: Option[ConfigName]): List[Config[_]] = {
        allConfs.filter(c => {
            if(name.isEmpty) true
            else if(name.isDefined) c.conf.name.equals(name.get)
            else {
                false
            }
        })
    }

    def apply[A](block: =>A)(implicit conf: Configuration=self) = block
}
