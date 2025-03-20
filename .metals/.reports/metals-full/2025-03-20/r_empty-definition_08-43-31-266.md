error id: `<error>`#`<error>`.
file://<WORKSPACE>/build.sc
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
|empty definition using fallback
non-local guesses:
	 -mill/Agg.
	 -mill/Agg#
	 -mill/Agg().
	 -scalalib/Agg.
	 -scalalib/Agg#
	 -scalalib/Agg().
	 -mill/bsp/Agg.
	 -mill/bsp/Agg#
	 -mill/bsp/Agg().
	 -Agg.
	 -Agg#
	 -Agg().
	 -scala/Predef.Agg.
	 -scala/Predef.Agg#
	 -scala/Predef.Agg().

Document text:

```scala
// import Mill dependency
import mill._
import mill.define.Sources
import mill.modules.Util
import mill.scalalib.TestModule.ScalaTest
import scalalib._
// support BSP
import mill.bsp._

object fpu extends SbtModule { m =>
  override def millSourcePath = os.pwd
  override def scalaVersion = "2.13.15"
  override def scalacOptions = Seq(
    "-language:reflectiveCalls",
    "-deprecation",
    "-feature",
    "-Xcheckinit",
  )
  override def ivyDeps = Agg(
    ivy"org.chipsalliance::chisel:6.6.0",
  )
  override def scalacPluginIvyDeps = Agg(
    ivy"org.chipsalliance:::chisel-plugin:6.6.0",
  )
  object test extends SbtModuleTests with TestModule.ScalaTest {
    override def ivyDeps = m.ivyDeps() ++ Agg(
      ivy"org.scalatest::scalatest::3.2.16"
    )
  }
}

```

#### Short summary: 

empty definition using pc, found symbol in pc: 