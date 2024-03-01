ThisBuild / scalaVersion     := "2.13.13"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val ZioVersion       = "2.0.21"
val ZioConfigVersion = "4.0.1"
val ZioJsonVersion   = "0.6.2"

lazy val root = (project in file("."))
  .settings(
    name := "zio-ticker",
    libraryDependencies ++= Seq(
      "dev.zio"             %% "zio"                 % ZioVersion,
      "dev.zio"             %% "zio-config"          % ZioConfigVersion,
      "dev.zio"             %% "zio-config-typesafe" % ZioConfigVersion,
      "dev.zio"             %% "zio-config-magnolia" % ZioConfigVersion,
      "com.github.javafaker" % "javafaker"           % "1.0.2",
      "dev.zio"             %% "zio-json"            % ZioJsonVersion,
      "dev.zio"             %% "zio-test"            % ZioVersion % Test,
      "dev.zio"             %% "zio-test-sbt"        % ZioVersion % Test,
      "dev.zio"             %% "zio-test-magnolia"   % ZioVersion % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
