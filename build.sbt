import play.Project._

name := "OnlineJudge"

version := "1.0"

libraryDependencies ++= Seq(
  "net.vz.mongodb.jackson" %% "play-mongo-jackson-mapper" % "1.1.0",
  "commons-io" % "commons-io" % "2.4"
)

playJavaSettings