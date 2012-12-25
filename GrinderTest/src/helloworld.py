# Hello World
#
# A minimal script that tests The Grinder logging facility.
#
# This script shows the recommended style for scripts, with a
# TestRunner class. The script is executed just once by each worker
# process and defines the TestRunner class. The Grinder creates an
# instance of TestRunner for each worker thread, and repeatedly calls
# the instance for each run of that thread.

from net.grinder.script.Grinder import grinder
from net.grinder.script import Test
from com.imseam.test import User
from com.imseam.test.connector.netty import RPCConnector
from com.imseam.test import Constants
from java.util import Date


# A shorter alias for the grinder.logger.info() method.
logger = grinder.logger
log = logger.info


# Create a Test with a test number and a description. The test will be
# automatically registered with The Grinder console if you are using
# it.
test1 = Test(1, "Log method")
connector = RPCConnector("localhost", 17001)
user = User(connector, "Test user", "server", Constants.online)

# Wrap the info() method with our Test and call the result logWrapper.
# Calls to logWrapper() will be recorded and forwarded on to the real
# info() method.
logWrapper = test1.wrap(log)
stats = grinder.statistics 

# Add two statistics expressions:
# 1. Delivery time:- the mean time taken between the server sending
#    the message and the receiver receiving the message.
# 2. Mean delivery time:- the delivery time averaged over all tests.
# We use the userLong0 statistic to represent the "delivery time".

stats.registerDataLogExpression("Delivery time", "userLong0")
stats.registerSummaryExpression(
              "Mean delivery time",
              "(/ userLong0 (count timedTests))")
#                        "(/ userLong0(+ timedTests untimedTests))")

# We record each message receipt against a single test. The
# test time is meaningless.
def recordDeliveryTime(deliveryTime):
    stats.forCurrentTest.setLong("userLong0", deliveryTime)

recordTest = Test(1, "Receive messages").wrap(recordDeliveryTime)


# A TestRunner instance is created for each thread. It can be used to
# store thread-specific data.
class TestRunner:

    # This method is called for every run.
    def __call__(self):
        logWrapper("Hello World")
 
        user.login();
        sendTime = Date()
#        System.out.println(sendTime)
        logWrapper(sendTime.toString())
        window = user.startChat("test buddy 1");
        message = window.waitForTextMessage(0)
        logWrapper(message.content)
        window.sendMsg("meeting");
        message = window.waitForTextMessage(0)
        logWrapper(message.content)
        receivedTime = message.receivedTime;
        logWrapper(receivedTime.toString())
#        System.out.println(receivedTime.getClass())
        #logWrapper(receivedTime.getClass() + "")
        timedif = receivedTime.getTime() - sendTime.getTime()
#        System.out.println(timedif)
        logWrapper(str(timedif))
        recordTest(timedif)
        
        
