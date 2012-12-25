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
from collections import deque


# A shorter alias for the grinder.logger.info() method.
logger = grinder.logger
log = logger.info


# Create a Test with a test number and a description. The test will be
# automatically registered with The Grinder console if you are using
# it.
test1 = Test(1, "start chat")
test2 = Test(2, "welcome")
test3 = Test(3, "echo")



def startChat(host, port, username, password,status, buddy):
    connector = RPCConnector(host, port)
    user = User(connector, username, password, status)
    user.login();
    return user.startChat(buddy);
    
def welcome(window, message):
    window.sendMsg(message);
    return window.waitForTextMessage(0)
 
def echo(window, message):
    window.sendMsg(message);
    return window.waitForTextMessage(0)

# Wrap the info() method with our Test and call the result logWrapper.
# Calls to logWrapper() will be recorded and forwarded on to the real
# info() method.
startChatTest = test1.wrap(startChat)
welcomeTest = test2.wrap(welcome)
echoTest = test3.wrap(echo)
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
    stats.forLastTest.setLong("userLong0", deliveryTime)
    

# A TestRunner instance is created for each thread. It can be used to
# store thread-specific data.
class TestRunner:

    # This method is called for every run.
    def __call__(self):
        grinder.statistics.delayReports = 1
        userNumber = str(grinder.processNumber) +'-' + str(grinder.threadNumber) + '-' +str(grinder.runNumber)
        window = startChatTest("localhost", 17001, "Test user " + userNumber, "no password", Constants.online, None)
        welcomeMessage = welcomeTest(window, 'echo')
        if(welcomeMessage is None or welcomeMessage.content.find('Welcome') == -1):       
            stats.forLastTest.success = 0
            log(welcomeMessage.content)
        stats.report()
        
        for i in range(0, 10):
            stats.delayReports = 1
            seq = userNumber + '-' + str(i)
            sendTime = Date()
            sendingMessage = seq + ": " + str(sendTime.getTime())
            #log("before "+ sendingMessage)
            echoedMessage = echoTest(window, sendingMessage)
            #log("after " + echoedMessage.content)
            
            if(echoedMessage is not None):
                #log(sendingMessage +":" + echoedMessage.content)
                receivedTime = echoedMessage.receivedTime;
                recordDeliveryTime(receivedTime.getTime() - sendTime.getTime())
                if(sendingMessage.strip() != echoedMessage.content.strip()):       
                    stats.forLastTest.success = 0
                    log(sendingMessage +":" + echoedMessage.content)
            else:      
                log("Only sent: "  + sendingMessage)
                stats.forLastTest.success = 0
                recordDeliveryTime(2000)   

                         
            stats.report()
 
        
