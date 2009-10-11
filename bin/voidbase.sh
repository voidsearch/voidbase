#!/bin/sh

# BUILD CLASSPATH
base_dir=$(dirname $0)/..
CLASSPATH=$(echo $base_dir/target/*.jar $base_dir/lib/*.jar | tr " " :)

# OTHER PARAMETERS
USER_ARGS="$@"
JVM_ARGS="-Xmx1024m -Xms256m"
EXTERNAL_LIBS="/usr/local/lib"
MAIN_CLASS="com.voidsearch.voidbase.VoidBase"

# RUN APPLICATION
echo "Starting: java -server -cp $CLASSPATH -Djava.library.path=${EXTERNAL_LIBS} ${JVM_ARGS} ${MAIN_CLASS} ${USER_ARGS}"
java -server -cp $CLASSPATH -Djava.library.path=${EXTERNAL_LIBS} ${JVM_ARGS} ${MAIN_CLASS} ${USER_ARGS}
