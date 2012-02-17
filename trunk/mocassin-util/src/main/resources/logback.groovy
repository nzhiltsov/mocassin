import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.status.OnConsoleStatusListener

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO

statusListener(OnConsoleStatusListener)

def HOSTNAME=hostname
def USER_HOME = System.getProperty("user.home")
def MOCASSIN_HOME = "/opt/mocassin"
def productionFlag = "server318"
def testFlag = "tomcat"

def PATTERN = "%d{HH:mm:ss.SSS}\t[%thread]\t%-5level\t%logger{36}\t-\t%msg%n"

enum Mode {
	DEV, TEST, PRODUCTION
}

def mode = Mode.DEV

if (HOSTNAME.contains(productionFlag) & USER_HOME.contains(testFlag)) {
	mode = Mode.TEST
	println "Test logging mode will be initialized"
} else if (HOSTNAME.contains(productionFlag)) {
	mode = Mode.PRODUCTION
	println "Production logging mode will be initialized"
} else {
println "Development logging mode will be initialized"
}

switch (mode) {
	case Mode.DEV:
		appender("STDOUT", ConsoleAppender) {
			encoder(PatternLayoutEncoder) { pattern = PATTERN }
		}
		root(DEBUG, ["STDOUT"])
		break

	case Mode.TEST:
		appender("TESTFILE", FileAppender) {
			file = "${MOCASSIN_HOME}/logs/common-test.log"
			append = false
			encoder(PatternLayoutEncoder) { pattern = PATTERN }
		}
		root(DEBUG, ["TESTFILE"])
		break

	case Mode.PRODUCTION:
		appender("FILE", FileAppender) {
			file = "${MOCASSIN_HOME}/logs/common.log"
			encoder(PatternLayoutEncoder) { pattern = PATTERN }
		}
		root(INFO, ["FILE"])
		break

	default:
		println "Failed to recognize the logging mode. See logback.groovy file."
		break
}

logger("com.hp.hpl.jena.util", INFO)
logger("o.o.query.parser", INFO)
logger("org.openrdf.rio", INFO)
