#! /bin/sh
# There is no need to call this if you set the MULE_HOME in your environment
exec ../../../bin/sethome

#Set the main class to run, this is not necessay if you are using org.mule.MuleServer
export MULE_MAIN=org.mule.samples.loanbroker.LoanConsumer

# Set your application specific classpath like this
SET CLASSPATH=$MULE_HOME/samples/loanbroker/conf:$MULE_HOME/samples/loanbroker/classes

exec $MULE_HOME/bin/mule
export MULE_MAIN=
export CLASSPATH=