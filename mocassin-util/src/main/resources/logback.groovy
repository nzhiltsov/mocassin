import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.status.OnConsoleStatusListener

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO

statusListener(OnConsoleStatusListener)

def USER_HOME = System.getProperty("user.home")
def MOCASSIN_HOME = "/opt/mocassin"
def productionFlag = "linglab"

def isProduction = false
if (USER_HOME.contains(productionFlag)) {
	isProduction = true
	println "Production logging mode is initialized"
}

if (!isProduction) {
appender("STDOUT", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS}\t[%thread]\t%-5level\t%logger{36}\t-\t%msg%n"
  }
}
}
appender("FILE", FileAppender) {
	file = "${MOCASSIN_HOME}/logs/common.log"
	encoder(PatternLayoutEncoder) {
	  pattern = "%d{HH:mm:ss.SSS}\t[%thread]\t%-5level\t%logger{36}\t-\t%msg%n"
	}
  }

logger("com.hp.hpl.jena.util", INFO)
logger("o.o.query.parser", INFO)
logger("org.openrdf.rio", INFO)

if (isProduction) {
	root(INFO, ["FILE"])
} else {
root(DEBUG, ["STDOUT"])
}