#!/bin/sh


# Control Script for the RAPTOR Server
#
# Environment Variable Prerequisites
#
#   Do not set the variables in this script. Instead put them into a script
#   setenv.sh in RAPTOR_BASE/bin to keep your customizations separate.
#
#   RAPTOR_HOME   May point at your RAPTOR "build" directory.
#
#   RAPTOR_BASE   (Optional) Base directory for resolving dynamic portions
#                   of a RAPTOR installation.  If not present, resolves to
#                   the same directory that RAPTOR_HOME points to.
#
#   RAPTOR_OUT    (Optional) Full path to a file where stdout and stderr
#                   will be redirected.
#                   Default is $RAPTOR_BASE/logs/RAPTOR.out
#
#   RAPTOR_OPTS   (Optional) Java runtime options used when the "start",
#                   "run" or "debug" command is executed.
#                   Include here and not in JAVA_OPTS all options, that should
#                   only be used by Tomcat itself, not by the stop process,
#                   the version command etc.
#                   Examples are heap size, GC logging, JMX ports etc.
#
#   RAPTOR_TMPDIR (Optional) Directory path location of temporary directory
#                   the JVM should use (java.io.tmpdir).  Defaults to
#                   $RAPTOR_BASE/temp.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#                   Required to run the with the "debug" argument.
#
#   JRE_HOME        Must point at your Java Runtime installation.
#                   Defaults to JAVA_HOME if empty. If JRE_HOME and JAVA_HOME
#                   are both set, JRE_HOME is used.
#
#   JAVA_OPTS       (Optional) Java runtime options used when any command
#                   is executed.
#                   Include here and not in RAPTOR_OPTS all options, that
#                   should be used by Tomcat and also by the stop process,
#                   the version command etc.
#                   Most options should go into RAPTOR_OPTS.
#
#   JAVA_ENDORSED_DIRS (Optional) Lists of of colon separated directories
#                   containing some jars in order to allow replacement of APIs
#                   created outside of the JCP (i.e. DOM and SAX from W3C).
#                   It can also be used to update the XML parser implementation.
#                   Defaults to $RAPTOR_HOME/endorsed.
#
#   RAPTOR_PID    (Optional) Path of the file which should contains the pid
#                   of the RAPTOR startup java process, when start (fork) is
#                   used
#
#   LOGGING_CONFIG  (Optional) Override Tomcat's logging config file
#                   Example (all one line)
#                   LOGGING_CONFIG="-Djava.util.logging.config.file=$RAPTOR_BASE/conf/logging.properties"
#
#   LOGGING_MANAGER (Optional) Override Tomcat's logging manager
#                   Example (all one line)
#                   LOGGING_MANAGER="-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager"
#
# $Id: RAPTOR.sh 1449412 2013-02-23 21:31:48Z kkolinko $
# -----------------------------------------------------------------------------

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
darwin=false
os400=false
case "`uname`" in
CYGWIN*) cygwin=true;;
Darwin*) darwin=true;;
OS400*) os400=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"
#echo the command name: $PRG
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set RAPTOR_HOME if not already set
[ -z "$RAPTOR_HOME" ] && RAPTOR_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

# Copy RAPTOR_BASE from RAPTOR_HOME if not already set
[ -z "$RAPTOR_BASE" ] && RAPTOR_BASE="$RAPTOR_HOME"

# Ensure that any user defined CLASSPATH variables are not used on startup,
# but allow them to be specified in setenv.sh, in rare case when it is needed.
CLASSPATH=

if [ -r "$RAPTOR_BASE/bin/setenv.sh" ]; then
  . "$RAPTOR_BASE/bin/setenv.sh"
elif [ -r "$RAPTOR_HOME/bin/setenv.sh" ]; then
  . "$RAPTOR_HOME/bin/setenv.sh"
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
echo cygwin: $cygwin
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$JRE_HOME" ] && JRE_HOME=`cygpath --unix "$JRE_HOME"`
  [ -n "$RAPTOR_HOME" ] && RAPTOR_HOME=`cygpath --unix "$RAPTOR_HOME"`
  [ -n "$RAPTOR_BASE" ] && RAPTOR_BASE=`cygpath --unix "$RAPTOR_BASE"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND

  # Enable multi threading
  export QIBM_MULTI_THREADED=Y
fi

# Get standard Java environment variables
if $os400; then
  # -r will Only work on the os400 if the files are:
  # 1. owned by the user
  # 2. owned by the PRIMARY group of the user
  # this will not work if the user belongs in secondary groups
  . "$RAPTOR_HOME"/bin/setclasspath.sh
else
  if [ -r "$RAPTOR_HOME"/bin/setclasspath.sh ]; then
    . "$RAPTOR_HOME"/bin/setclasspath.sh
  else
    echo "Cannot find $RAPTOR_HOME/bin/setclasspath.sh"
    echo "This file is needed to run this program"
    exit 1
  fi
fi

# Add on extra jar files to CLASSPATH
if [ ! -z "$CLASSPATH" ] ; then
  CLASSPATH="$CLASSPATH":
fi
CLASSPATH="$CLASSPATH""$RAPTOR_HOME"/lib/*:

if [ -z "$RAPTOR_OUT" ] ; then
  RAPTOR_OUT="$RAPTOR_BASE"/logging/RAPTOR.out
fi

if [ -z "$RAPTOR_TMPDIR" ] ; then
  # Define the java.io.tmpdir to use for RAPTOR
  RAPTOR_TMPDIR="$RAPTOR_BASE"/temp
fi

LOGGING_CONFIG_FILE="$RAPTOR_BASE/config/logging.properties"
COMMONS_LOGGING_LOGGER=-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Jdk14Logger

# Bugzilla 37848: When no TTY is available, don't output to console
have_tty=0
if [ "`tty`" != "not a tty" ]; then
    have_tty=1
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  JRE_HOME=`cygpath --absolute --windows "$JRE_HOME"`
  RAPTOR_HOME=`cygpath --absolute --windows "$RAPTOR_HOME"`
  RAPTOR_BASE=`cygpath --absolute --windows "$RAPTOR_BASE"`
  RAPTOR_TMPDIR=`cygpath --absolute --windows "$RAPTOR_TMPDIR"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  LOGGING_CONFIG_FILE=`cygpath --path --windows "$LOGGING_CONFIG_FILE"`
  JAVA_ENDORSED_DIRS=`cygpath --path --windows "$JAVA_ENDORSED_DIRS"`
fi

# ----- Execute The Requested Command -----------------------------------------

# Bugzilla 37848: only output this if we have a TTY
if [ $have_tty -eq 1 ]; then
  echo "Using RAPTOR_BASE:   $RAPTOR_BASE"
  echo "Using RAPTOR_HOME:   $RAPTOR_HOME"
  echo "Using RAPTOR_TMPDIR: $RAPTOR_TMPDIR"
  if [ "$1" = "debug" ] ; then
    echo "Using JAVA_HOME:       $JAVA_HOME"
  else
    echo "Using JRE_HOME:        $JRE_HOME"
  fi
  echo "Using CLASSPATH:       $CLASSPATH"
  if [ ! -z "$RAPTOR_PID" ]; then
    echo "Using RAPTOR_PID:    $RAPTOR_PID"
  fi
fi


if [ "$1" = "run" ]; then

  shift
  eval exec \"$_RUNJAVA\" \"-Djava.util.logging.config.file=$LOGGING_CONFIG_FILE\" $LOGGING_MANAGER $JAVA_OPTS $RAPTOR_OPTS \
      -Djava.endorsed.dirs=\"$JAVA_ENDORSED_DIRS\" -classpath \"$CLASSPATH\" \
      -DRAPTOR.base=\"$RAPTOR_BASE\" \
      -DRAPTOR.home=\"$RAPTOR_HOME\" \
      -Djava.io.tmpdir=\"$RAPTOR_TMPDIR\" \
      com.imseam.raptor.startup.Raptor "$@" start
elif [ "$1" = "start" ] ; then
  if [ ! -z "$RAPTOR_PID" ]; then
    if [ -f "$RAPTOR_PID" ]; then
      if [ -s "$RAPTOR_PID" ]; then
        echo "Existing PID file found during start."
        if [ -r "$RAPTOR_PID" ]; then
          PID=`cat "$RAPTOR_PID"`
          ps -p $PID >/dev/null 2>&1
          if [ $? -eq 0 ] ; then
            echo "Tomcat appears to still be running with PID $PID. Start aborted."
            exit 1
          else
            echo "Removing/clearing stale PID file."
            rm -f "$RAPTOR_PID" >/dev/null 2>&1
            if [ $? != 0 ]; then
              if [ -w "$RAPTOR_PID" ]; then
                cat /dev/null > "$RAPTOR_PID"
              else
                echo "Unable to remove or clear stale PID file. Start aborted."
                exit 1
              fi
            fi
          fi
        else
          echo "Unable to read PID file. Start aborted."
          exit 1
        fi
      else
        rm -f "$RAPTOR_PID" >/dev/null 2>&1
        if [ $? != 0 ]; then
          if [ ! -w "$RAPTOR_PID" ]; then
            echo "Unable to remove or write to empty PID file. Start aborted."
            exit 1
          fi
        fi
      fi
    fi
  fi
  
  shift
  touch "$RAPTOR_OUT"

  echo $_RUNJAVA -Djava.util.logging.config.file=$LOGGING_CONFIG_FILE $COMMONS_LOGGING_LOGGER $JAVA_OPTS $RAPTOR_OPTS \
      -Djava.endorsed.dirs=$JAVA_ENDORSED_DIRS -classpath $CLASSPATH \
      -DRAPTOR.base=$RAPTOR_BASE \
      -DRAPTOR.home=$RAPTOR_HOME \
      -Djava.io.tmpdir=\"$RAPTOR_TMPDIR\" \
      com.imseam.raptor.startup.Raptor "$@" start -config ../config/engine.config
      
  
    eval $_RUNJAVA -Djava.util.logging.config.file=$LOGGING_CONFIG_FILE $COMMONS_LOGGING_LOGGER $JAVA_OPTS $RAPTOR_OPTS \
      -Djava.endorsed.dirs=$JAVA_ENDORSED_DIRS -classpath $CLASSPATH \
      -DRAPTOR.base=$RAPTOR_BASE \
      -DRAPTOR.home=$RAPTOR_HOME \
      -Djava.io.tmpdir=$RAPTOR_TMPDIR \
      com.imseam.raptor.startup.Raptor "$@" start -config ../config/engine.config \
      >> "$RAPTOR_OUT" 2>&1 "&"

  

  if [ ! -z "$RAPTOR_PID" ]; then
    echo $! > "$RAPTOR_PID"
  fi

elif [ "$1" = "stop" ] ; then

  shift

  SLEEP=5
  if [ ! -z "$1" ]; then
    echo $1 | grep "[^0-9]" >/dev/null 2>&1
    if [ $? -gt 0 ]; then
      SLEEP=$1
      shift
    fi
  fi

  FORCE=0
  if [ "$1" = "-force" ]; then
    shift
    FORCE=1
  fi

  if [ ! -z "$RAPTOR_PID" ]; then
    if [ -f "$RAPTOR_PID" ]; then
      if [ -s "$RAPTOR_PID" ]; then
        kill -0 `cat "$RAPTOR_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
          echo "PID file found but no matching process was found. Stop aborted."
          exit 1
        fi
      else
        echo "PID file is empty and has been ignored."
      fi
    else
      echo "\$RAPTOR_PID was set but the specified file does not exist. Is Tomcat running? Stop aborted."
      exit 1
    fi
  fi

  eval \"$_RUNJAVA\" $LOGGING_MANAGER $JAVA_OPTS \
    -Djava.endorsed.dirs=\"$JAVA_ENDORSED_DIRS\" -classpath \"$CLASSPATH\" \
    -DRAPTOR.base=\"$RAPTOR_BASE\" \
    -DRAPTOR.home=\"$RAPTOR_HOME\" \
    -Djava.io.tmpdir=\"$RAPTOR_TMPDIR\" \
    org.apache.RAPTOR.startup.Bootstrap "$@" stop

  if [ ! -z "$RAPTOR_PID" ]; then
    if [ -f "$RAPTOR_PID" ]; then
      while [ $SLEEP -ge 0 ]; do
        kill -0 `cat "$RAPTOR_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
          rm -f "$RAPTOR_PID" >/dev/null 2>&1
          if [ $? != 0 ]; then
            if [ -w "$RAPTOR_PID" ]; then
              cat /dev/null > "$RAPTOR_PID"
            else
              echo "Tomcat stopped but the PID file could not be removed or cleared."
            fi
          fi
          break
        fi
        if [ $SLEEP -gt 0 ]; then
          sleep 1
        fi
        if [ $SLEEP -eq 0 ]; then
          if [ $FORCE -eq 0 ]; then
            echo "Tomcat did not stop in time. PID file was not removed."
          fi
        fi
        SLEEP=`expr $SLEEP - 1 `
      done
    fi
  fi

  if [ $FORCE -eq 1 ]; then
    if [ -z "$RAPTOR_PID" ]; then
      echo "Kill failed: \$RAPTOR_PID not set"
    else
      if [ -f "$RAPTOR_PID" ]; then
        PID=`cat "$RAPTOR_PID"`
        echo "Killing Tomcat with the PID: $PID"
        kill -9 $PID
        rm -f "$RAPTOR_PID" >/dev/null 2>&1
        if [ $? != 0 ]; then
          echo "Tomcat was killed but the PID file could not be removed."
        fi
      fi
    fi
  fi

elif [ "$1" = "configtest" ] ; then

    eval \"$_RUNJAVA\" $LOGGING_MANAGER $JAVA_OPTS \
      -Djava.endorsed.dirs=\"$JAVA_ENDORSED_DIRS\" -classpath \"$CLASSPATH\" \
      -DRAPTOR.base=\"$RAPTOR_BASE\" \
      -DRAPTOR.home=\"$RAPTOR_HOME\" \
      -Djava.io.tmpdir=\"$RAPTOR_TMPDIR\" \
      org.apache.RAPTOR.startup.Bootstrap configtest
    result=$?
    if [ $result -ne 0 ]; then
        echo "Configuration error detected!"
    fi
    exit $result

elif [ "$1" = "version" ] ; then

    "$_RUNJAVA"   \
      -classpath "$RAPTOR_HOME/lib/RAPTOR.jar" \
      org.apache.RAPTOR.util.ServerInfo

else

  echo "Usage: RAPTOR.sh ( commands ... )"
  echo "commands:"
  if $os400; then
    echo "  debug             Start RAPTOR in a debugger (not available on OS400)"
    echo "  debug -security   Debug RAPTOR with a security manager (not available on OS400)"
  else
    echo "  debug             Start RAPTOR in a debugger"
    echo "  debug -security   Debug RAPTOR with a security manager"
  fi
  echo "  jpda start        Start RAPTOR under JPDA debugger"
  echo "  run               Start RAPTOR in the current window"
  echo "  run -security     Start in the current window with security manager"
  echo "  start             Start RAPTOR in a separate window"
  echo "  start -security   Start in a separate window with security manager"
  echo "  stop              Stop RAPTOR, waiting up to 5 seconds for the process to end"
  echo "  stop n            Stop RAPTOR, waiting up to n seconds for the process to end"
  echo "  stop -force       Stop RAPTOR, wait up to 5 seconds and then use kill -KILL if still running"
  echo "  stop n -force     Stop RAPTOR, wait up to n seconds and then use kill -KILL if still running"
  echo "  configtest        Run a basic syntax check on server.xml - check exit code for result"
  echo "  version           What version of tomcat are you running?"
  echo "Note: Waiting for the process to end and use of the -force option require that \$RAPTOR_PID is defined"
  exit 1

fi
