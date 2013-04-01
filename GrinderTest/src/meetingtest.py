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
from com.imseam.test import StringSerializerUtil
from java.util import Date
from java.util import ArrayList
from java.util import HashMap
from java.util import HashSet
from collections import deque
from threading import Lock, Event


# A shorter alias for the grinder.logger.info() method.
logger = grinder.logger
log = logger.info


# Create a Test with a test number and a description. The test will be
# automatically registered with The Grinder console if you are using
# it.
test1 = Test(1, "start chat")
test2 = Test(2, "startMeeting")
test3 = Test(3, "echo")
test4 = Test(4, "stopMeeting")
test5 = Test(5, "meetingContext")
test6 = Test(6, "startActiveWindow")

siteUserNumber = 3




def startChat(host, port, username, password,status, buddy):
    connector = RPCConnector(host, port)
    user = User(connector, username, password, status)
    user.login();
    return user.startChat(buddy);
    
def startMeeting(user, buddies):
    return user.startMeeting(buddies)
    
def waitForActiveWindow(host, port, username, password,status, buddy):
    connector = RPCConnector(host, port)
    user = User(connector, username, password, status)
    user.login();
    return user.waitForWindowOpen(0)
 

def echo(user, buddies):
    return user.sendTimesatmpMessage(buddies)

def stopMeeting(user, buddies):
    return user.stopMeeting(buddies)    

def meetingContext(user, buddies, value):
    return user.setValue2MeetingContext(buddies, value);

def startActiveWindow(user, buddies):
    return user.startActiveWindow(buddies);

    
# Wrap the info() method with our Test and call the result logWrapper.
# Calls to logWrapper() will be recorded and forwarded on to the real
# info() method.

startChatTest = test1.wrap(startChat)
startMeetingTest = test2.wrap(startMeeting)
echoTest = test3.wrap(echo)
stopMeetingTest = test4.wrap(stopMeeting)
meetingContextTest= test5.wrap(meetingContext)
startActiveWindowTest = test6.wrap(startActiveWindow)

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
    
sites = {}

glock1 = Lock()

def getSite(siteNumber, lock):
    try:
        log('siteNumber: ' + str(siteNumber))
        lock.acquire()
        if(siteNumber in sites):
            site = sites[siteNumber]
        else:
            site = Site(siteNumber)
            sites[siteNumber] = site
    finally:
        lock.release()
    return site

def printArray(start, array):
     for item in array:
        start = start + item
     log(start)


class Site:
    def __init__(self, siteNumber):
        self.operators=[]    
        self.clients=[]
        self.waitSiteUsers=[]
        self.siteNumber = siteNumber
        self.syncEvent= Event()
        self.syncEvent.clear()
        
    
            
class TestUser:
    def __init__(self, window, isOperator, isWaitSiteUser, userNumber, site):
        self.window = window
        self.isOperator = isOperator
        self.userNumber = userNumber
        self.receivedMessagQueue = deque()
        self.site = site
        if(isOperator):
            site.operators.append(self)
        else:
            if(isWaitSiteUser):
                site.waitSiteUsers.append(self)
            else:
                site.clients.append(self)

    def wait(self):
        log('wait user: ' + str(self.userNumber) +', operator numbers:' + str(len(self.site.operators)) + ', client numbers: ' + str(len(self.site.clients)) +', waitSiteUser numbers: ' + str(len(self.site.waitSiteUsers)))
        
        if (( len(self.site.operators) + len(self.site.clients) + len(self.site.waitSiteUsers)) != siteUserNumber):
            self.site.syncEvent.wait()
        else:
            self.site.syncEvent.set()

    def startMeeting(self, buddies):
        expectedResponses = []
        message = 'startMeeting'
        for buddy in buddies:
            expectedResponses.append('meeting started:::' + buddy.userNumber)
            message = message + ':::' + buddy.userNumber
        
        expectedResponses.append('meeting started:::' + message)
        window = self.window
        log('start meeting message:' + message)
        window.sendMsg(message)
        startmeetingResponse = window.waitForTextMessage(0).content.strip()
        log(self.userNumber +' startmeetingResponse:' + startmeetingResponse) 
        messagesFromBuddy = [startmeetingResponse]
        expectedResponses.remove(startmeetingResponse)
        for i in range(len(buddies)):
            responseMessage =window.waitForTextMessage(0).content.strip()
            log(self.userNumber +' startmeeting responseMessage from buddies:' + responseMessage)
            messagesFromBuddy.append(responseMessage)
            expectedResponses.remove(responseMessage)
        log('len(expectedResponses):' + str(len(expectedResponses)))
        if(len(expectedResponses) > 0) :
            log('not received: '.join(expectedResponses) + ' , received:'.join(messagesFromBuddy))
            return False
        else:
            log('meeting started successfully')
        return True
    
    def sendTimesatmpMessage(self, buddies):
        expectedResponses = []
        message = 'send:::' + str(Date().getTime())
        expectedResponses.append(message)
        for buddy in buddies:
            expectedResponses.append('recieved:::' + message + ':::' +buddy.userNumber)
            log('Excepting: ' + 'recieved:::' + message + ':::' +buddy.userNumber)
        window = self.window            
        window.sendMsg(message)
        messagesFromBuddy = []
        for i in range(len(buddies) + 1):
            responseMessage =window.waitForTextMessage(0).content.strip()
            log(self.userNumber +' sendTimesatmpMessage response:' + responseMessage)
            messagesFromBuddy.append(responseMessage)
            expectedResponses.remove(responseMessage)
        if(len(expectedResponses) > 0) :
            printArray('not received: ', expectedResponses)
            printArray('received: ', messagesFromBuddy)
            return False
        return True
    
    def stopMeeting(self, buddies):
        expectedResponses = []
        message = 'stopmeeting'
        expectedResponses.append('stoppedMeeting' +':::' + self.userNumber)            
        window = self.window            
        window.sendMsg(message)
        responseMessage = window.waitForTextMessage(0).content.strip()
        log(self.userNumber +' stopMeeting response:' + responseMessage)
 
        expectedResponses.remove(responseMessage)
        if(len(expectedResponses) > 0) :
            printArray('not received: ', expectedResponses)
            return False
        return True
    
    def setValue2MeetingContext(self, buddies, encodedStr):
        expectedResponses = []
        message = "setValueMeetingContext:::" + encodedStr
        expectedResponses.append(message)
        for buddy in buddies:
            expectedResponses.append(encodedStr + ':::' +buddy.userNumber)
        window = self.window            
        window.sendMsg(message)
        messagesFromBuddy = []
        for i in range(len(buddies) + 1):
            responseMessage =window.waitForTextMessage(0).content.strip()
            log(self.userNumber +' setValue2MeetingContext response:' + responseMessage)
            messagesFromBuddy.append(responseMessage)
            expectedResponses.remove(responseMessage)
        if(len(expectedResponses) > 0) :
            printArray('not received: ', expectedResponses)
            printArray('received: ', messagesFromBuddy)
            return False
        return True
    
    def startActiveWindow(self, buddies):
        expectedResponses = []
        message = 'startActiveWindow'
        for buddy in buddies:
            expectedResponses.append('meeting started:::' + buddy.userNumber)
            message = message + ':::' + buddy.userNumber
        window = self.window
        log('start active window message:' + message)
        window.sendMsg(message)
        messagesFromBuddy = []
        for i in range(len(buddies)):
            responseMessage =window.waitForTextMessage(0).content.strip()
            log(self.userNumber +' startActiveWindow responseMessage from buddies:' + responseMessage)
            messagesFromBuddy.append(responseMessage)
            expectedResponses.remove(responseMessage)
        log('len(expectedResponses):' + str(len(expectedResponses)))
        if(len(expectedResponses) > 0) :
            log('not received: '.join(expectedResponses) + ' , received:'.join(messagesFromBuddy))
            return False
        else:
            log('startActiveWindowsuccessfully')
        return True
        
    
def isOperator(threadNumber):
    return (threadNumber % siteUserNumber) == 0       

def isWaitSiteUser(threadNumber):
    log('thread number:' + str(threadNumber) + '(threadNumber % siteUserNumber)' + str(threadNumber % siteUserNumber) + '(siteUserNumber - 1)' + str(siteUserNumber - 1))
    return (threadNumber % siteUserNumber) == (siteUserNumber - 1)      

def getSiteNumber(threadNumber):
    return threadNumber / siteUserNumber      
 


# A TestRunner instance is created for each thread. It can be used to
# store thread-specific data.
class TestRunner:

    # This method is called for every run.
    def __call__(self):
        grinder.statistics.delayReports = 1
        userNumber = str(grinder.processNumber) +'-' + str(grinder.threadNumber) + '-' +str(grinder.runNumber)
        siteNumber = getSiteNumber(grinder.threadNumber)
        site = getSite(siteNumber, glock1)
        userName = "Test user " + userNumber
        if(isOperator(grinder.threadNumber)):
            userName = userName + "-operator"
            
        if(isWaitSiteUser(grinder.threadNumber)):
            log('wait site user:' + userName)
            user = TestUser(None, isOperator(grinder.threadNumber), isWaitSiteUser(grinder.threadNumber), userName, site)
            user.wait()

            window = waitForActiveWindow("localhost", 17001, userName, "no password", Constants.online, None)
            log(userNumber +' waitForActiveWindow window Id:' + window.windowId())
            user.window = window
            recievedMessage = window.waitForTextMessage(0).content.strip()
            log(userNumber +' welcome message from wait site user:' + recievedMessage)
        else:
            window = startChatTest("localhost", 17001, userName, "no password", Constants.online, None)
            recievedMessage = window.waitForTextMessage(0).content.strip()
            log(userNumber +' welcome message:' + recievedMessage)
            user = TestUser(window, isOperator(grinder.threadNumber), isWaitSiteUser(grinder.threadNumber), userName, site)
            user.wait()
        
        if(user.isOperator):
            started = startMeetingTest(user, site.clients)
            if(not started):
                stats.forLastTest.success = 0
            stats.report
            for i in range(3):
                suc = echoTest(user, site.clients)
                if(not suc):
                    stats.forLastTest.success = 0
                stats.report

            encodedStr = StringSerializerUtil.fromDate(Date())
            suc = meetingContextTest(user, site.clients, encodedStr)
            if(not suc):
                stats.forLastTest.success = 0
            stats.report
            
            map1 = HashMap()
            map1.put('key1', 'value1')
            map1.put('key2', 'value2')
            map1.put('key3', 'value3')
            encodedStr = StringSerializerUtil.fromMap(map1)
            suc = meetingContextTest(user, site.clients, encodedStr)
            if(not suc):
                stats.forLastTest.success = 0
            stats.report

            list1 = ArrayList()
            list1.add('element1')
            list1.add('element2')
            list1.add('element2')
            list1.add('element3')
            encodedStr = StringSerializerUtil.fromList(list1)
            suc = meetingContextTest(user, site.clients, encodedStr)
            if(not suc):
                stats.forLastTest.success = 0
            stats.report

            set1 = HashSet()
            set1.add('element1')
            set1.add('element2')
            set1.add('element2')
            set1.add('element3')
            encodedStr = StringSerializerUtil.fromSet(set1)
            suc = meetingContextTest(user, site.clients, encodedStr)
            if(not suc):
                stats.forLastTest.success = 0
            stats.report

            suc = startActiveWindowTest(user, site.waitSiteUsers)
            if(not suc):
               stats.forLastTest.success = 0
            stats.report
                
            suc = stopMeetingTest(user, site.clients)
            if(not suc):
               stats.forLastTest.success = 0
            stats.report
        else:
            while True:
                recievedMessage = window.waitForTextMessage(0).content.strip()
                log(userNumber +' client recievedMessage:' + recievedMessage)
                if('meeting started' not in recievedMessage):
                    if('stop' not in recievedMessage):
                        log(userNumber +' client send back:' + 'recieved:::'+recievedMessage+':::'+userName)
#                        grinder.sleep(1000)
                        window.sendMsg('recieved:::'+recievedMessage+':::'+userName)
                    else:
                        log(userNumber +' client recieved:::'+recievedMessage+':::'+userName)
#                        window.sendMsg(recievedMessage)
                        break;
                else:
                    log('Got meeting started:' + recievedMessage)
